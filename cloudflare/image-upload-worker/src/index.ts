// Recuperado del Worker desplegado `zeta-image-upload-worker` (versión del 01-jul-2026).
// Sube imágenes de productos y promos a R2 (solo admins de Supabase) y las sirve en /images/*.

interface Env {
  STORE_IMAGES: R2Bucket;
  SUPABASE_URL: string;
  SUPABASE_ANON_KEY: string;
  ALLOWED_ORIGIN?: string;
}

const MAX_IMAGE_BYTES = 5 * 1024 * 1024;
const ALLOWED_IMAGE_TYPES = new Set(["image/jpeg", "image/png", "image/webp", "image/gif"]);

export default {
  async fetch(request: Request, env: Env): Promise<Response> {
    const corsHeaders = buildCorsHeaders(env);
    if (request.method === "OPTIONS") {
      return new Response(null, { headers: corsHeaders });
    }

    const url = new URL(request.url);
    if (url.pathname.startsWith("/images/") && request.method === "GET") {
      const objectPath = sanitizeObjectPath(url.pathname.replace(/^\/images\//, ""));
      if (!objectPath) {
        return new Response("Not found", { status: 404, headers: corsHeaders });
      }
      const object = await env.STORE_IMAGES.get(objectPath);
      if (!object) {
        return new Response("Not found", { status: 404, headers: corsHeaders });
      }
      return new Response(object.body, {
        headers: {
          ...corsHeaders,
          "Content-Type": object.httpMetadata?.contentType ?? "application/octet-stream",
          "Cache-Control": object.httpMetadata?.cacheControl ?? "public, max-age=31536000, immutable",
          ETag: object.httpEtag,
        },
      });
    }

    if (url.pathname !== "/upload" || request.method !== "POST") {
      return json({ error: "Not found" }, 404, corsHeaders);
    }

    try {
      const authHeader = request.headers.get("Authorization") ?? "";
      const accessToken = authHeader.replace(/^Bearer\s+/i, "").trim();
      if (!accessToken) {
        return json({ error: "Missing session" }, 401, corsHeaders);
      }

      const isAdmin = await verifySupabaseAdmin(env, accessToken);
      if (!isAdmin) {
        return json({ error: "Admin access required" }, 403, corsHeaders);
      }

      const contentType = request.headers.get("Content-Type")?.split(";")[0]?.toLowerCase() ?? "";
      if (!ALLOWED_IMAGE_TYPES.has(contentType)) {
        return json({ error: "Only JPG, PNG, WebP, or GIF images are allowed" }, 415, corsHeaders);
      }

      const objectPath = sanitizeObjectPath(url.searchParams.get("path") ?? "");
      if (!objectPath) {
        return json({ error: "Invalid image path" }, 400, corsHeaders);
      }

      const bytes = await request.arrayBuffer();
      if (bytes.byteLength <= 0 || bytes.byteLength > MAX_IMAGE_BYTES) {
        return json({ error: "Image must be between 1 byte and 5 MB" }, 413, corsHeaders);
      }

      await env.STORE_IMAGES.put(objectPath, bytes, {
        httpMetadata: {
          contentType,
          cacheControl: "public, max-age=31536000, immutable",
        },
      });

      return json({ url: `${url.origin}/images/${encodePath(objectPath)}` }, 200, corsHeaders);
    } catch (error) {
      const message = error instanceof Error ? error.message : "Upload failed";
      return json({ error: message }, 500, corsHeaders);
    }
  },
};

async function verifySupabaseAdmin(env: Env, accessToken: string): Promise<boolean> {
  const baseUrl = env.SUPABASE_URL.replace(/\/+$/, "");
  const userResponse = await fetch(`${baseUrl}/auth/v1/user`, {
    headers: {
      apikey: env.SUPABASE_ANON_KEY,
      Authorization: `Bearer ${accessToken}`,
    },
  });
  if (!userResponse.ok) return false;

  const user = (await userResponse.json()) as { id?: string };
  if (!user.id) return false;

  const profileResponse = await fetch(
    `${baseUrl}/rest/v1/profiles?id=eq.${encodeURIComponent(user.id)}&select=role&limit=1`,
    {
      headers: {
        apikey: env.SUPABASE_ANON_KEY,
        Authorization: `Bearer ${accessToken}`,
      },
    },
  );
  if (!profileResponse.ok) return false;

  const profiles = (await profileResponse.json()) as Array<{ role?: string }>;
  return profiles[0]?.role === "admin";
}

function sanitizeObjectPath(value: string): string {
  const decoded = decodeURIComponent(value).replace(/\\/g, "/").replace(/^\/+/, "");
  const segments = decoded.split("/").map((segment) => segment.trim()).filter(Boolean);
  if (segments.length < 2 || segments.some((segment) => segment === "." || segment === "..")) {
    return "";
  }

  const root = segments[0];
  if (root !== "products" && root !== "promos") {
    return "";
  }

  return segments.map((segment) => segment.replace(/[^a-zA-Z0-9._-]/g, "-")).join("/");
}

function encodePath(path: string): string {
  return path.split("/").map(encodeURIComponent).join("/");
}

function buildCorsHeaders(env: Env): Record<string, string> {
  return {
    "Access-Control-Allow-Origin": env.ALLOWED_ORIGIN || "*",
    "Access-Control-Allow-Methods": "POST, OPTIONS",
    "Access-Control-Allow-Headers": "Authorization, Content-Type",
    "Access-Control-Max-Age": "86400",
  };
}

function json(body: unknown, status: number, headers: Record<string, string>): Response {
  return Response.json(body, {
    status,
    headers: {
      ...headers,
      "Content-Type": "application/json",
    },
  });
}
