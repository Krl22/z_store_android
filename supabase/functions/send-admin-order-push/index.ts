type WebhookPayload = {
  type?: string;
  table?: string;
  record?: {
    id?: string;
    admin_id?: string;
    order_id?: string;
    title?: string;
    body?: string;
    is_read?: boolean;
  };
};

type PushTokenRow = {
  token: string;
};

const supabaseUrl = Deno.env.get("SUPABASE_URL") ?? "";
const serviceRoleKey = Deno.env.get("SUPABASE_SERVICE_ROLE_KEY") ?? "";
const firebaseProjectId = Deno.env.get("FIREBASE_PROJECT_ID") ?? "";
const firebaseClientEmail = Deno.env.get("FIREBASE_CLIENT_EMAIL") ?? "";
const firebasePrivateKey = (Deno.env.get("FIREBASE_PRIVATE_KEY") ?? "").replace(/\\n/g, "\n");
const webhookSecret = Deno.env.get("WEBHOOK_SECRET") ?? "";

Deno.serve(async (request) => {
  if (request.method !== "POST") {
    return json({ error: "Method not allowed" }, 405);
  }

  assertConfigured();
  if (!isAuthorizedWebhook(request)) {
    return json({ error: "Unauthorized" }, 401);
  }

  const payload = await request.json().catch(() => null) as WebhookPayload | null;
  const notification = payload?.record;
  if (!notification?.id || !notification.admin_id || notification.is_read) {
    return json({ skipped: true });
  }

  const tokens = await fetchAdminTokens(notification.admin_id);
  if (tokens.length === 0) {
    return json({ sent: 0, reason: "No active tokens" });
  }

  const accessToken = await getFirebaseAccessToken();
  const results = await Promise.allSettled(
    tokens.map((token) =>
      sendFcmMessage({
        accessToken,
        token,
        title: notification.title || "Nuevo pedido",
        body: notification.body || "Tienes un pedido nuevo para revisar.",
        notificationId: notification.id,
        orderId: notification.order_id || "",
      })
    )
  );

  const sent = results.filter((result) => result.status === "fulfilled").length;
  const failed = results.length - sent;
  return json({ sent, failed });
});

function assertConfigured() {
  const missing = [
    ["SUPABASE_URL", supabaseUrl],
    ["SUPABASE_SERVICE_ROLE_KEY", serviceRoleKey],
    ["FIREBASE_PROJECT_ID", firebaseProjectId],
    ["FIREBASE_CLIENT_EMAIL", firebaseClientEmail],
    ["FIREBASE_PRIVATE_KEY", firebasePrivateKey],
    ["WEBHOOK_SECRET", webhookSecret],
  ].filter(([, value]) => !value).map(([name]) => name);

  if (missing.length > 0) {
    throw new Error(`Missing env vars: ${missing.join(", ")}`);
  }
}

function isAuthorizedWebhook(request: Request): boolean {
  const headerSecret = request.headers.get("x-webhook-secret") ?? "";
  const bearerSecret = (request.headers.get("authorization") ?? "").replace(/^Bearer\s+/i, "").trim();
  return headerSecret === webhookSecret || bearerSecret === webhookSecret;
}

async function fetchAdminTokens(adminId: string): Promise<string[]> {
  const response = await fetch(
    `${supabaseUrl}/rest/v1/device_push_tokens?user_id=eq.${encodeURIComponent(adminId)}&is_active=eq.true&select=token`,
    {
      headers: {
        apikey: serviceRoleKey,
        authorization: `Bearer ${serviceRoleKey}`,
      },
    },
  );

  if (!response.ok) {
    throw new Error(`Could not fetch push tokens: ${await response.text()}`);
  }

  const rows = await response.json() as PushTokenRow[];
  return rows.map((row) => row.token).filter(Boolean);
}

async function getFirebaseAccessToken(): Promise<string> {
  const now = Math.floor(Date.now() / 1000);
  const header = { alg: "RS256", typ: "JWT" };
  const claim = {
    iss: firebaseClientEmail,
    scope: "https://www.googleapis.com/auth/firebase.messaging",
    aud: "https://oauth2.googleapis.com/token",
    iat: now,
    exp: now + 3600,
  };
  const unsignedJwt = `${base64Url(JSON.stringify(header))}.${base64Url(JSON.stringify(claim))}`;
  const signature = await signJwt(unsignedJwt, firebasePrivateKey);
  const jwt = `${unsignedJwt}.${signature}`;

  const response = await fetch("https://oauth2.googleapis.com/token", {
    method: "POST",
    headers: { "content-type": "application/x-www-form-urlencoded" },
    body: new URLSearchParams({
      grant_type: "urn:ietf:params:oauth:grant-type:jwt-bearer",
      assertion: jwt,
    }),
  });

  if (!response.ok) {
    throw new Error(`Firebase auth failed: ${await response.text()}`);
  }

  const body = await response.json() as { access_token?: string };
  if (!body.access_token) throw new Error("Firebase did not return access_token");
  return body.access_token;
}

async function sendFcmMessage(input: {
  accessToken: string;
  token: string;
  title: string;
  body: string;
  notificationId: string;
  orderId: string;
}) {
  const response = await fetch(`https://fcm.googleapis.com/v1/projects/${firebaseProjectId}/messages:send`, {
    method: "POST",
    headers: {
      authorization: `Bearer ${input.accessToken}`,
      "content-type": "application/json",
    },
    body: JSON.stringify({
      message: {
        token: input.token,
        notification: {
          title: input.title,
          body: input.body,
        },
        android: {
          priority: "HIGH",
          notification: {
            channel_id: "admin_orders",
            click_action: "OPEN_ADMIN_ORDERS",
          },
        },
        data: {
          notification_id: input.notificationId,
          order_id: input.orderId,
          type: "admin_order",
        },
      },
    }),
  });

  if (!response.ok) {
    throw new Error(`FCM send failed: ${await response.text()}`);
  }
}

async function signJwt(input: string, pem: string): Promise<string> {
  const keyData = pem
    .replace("-----BEGIN PRIVATE KEY-----", "")
    .replace("-----END PRIVATE KEY-----", "")
    .replace(/\s/g, "");
  const binary = Uint8Array.from(atob(keyData), (char) => char.charCodeAt(0));
  const key = await crypto.subtle.importKey(
    "pkcs8",
    binary,
    { name: "RSASSA-PKCS1-v1_5", hash: "SHA-256" },
    false,
    ["sign"],
  );
  const signature = await crypto.subtle.sign(
    "RSASSA-PKCS1-v1_5",
    key,
    new TextEncoder().encode(input),
  );
  return base64Url(new Uint8Array(signature));
}

function base64Url(input: string | Uint8Array): string {
  const bytes = typeof input === "string" ? new TextEncoder().encode(input) : input;
  let binary = "";
  bytes.forEach((byte) => binary += String.fromCharCode(byte));
  return btoa(binary).replace(/\+/g, "-").replace(/\//g, "_").replace(/=+$/g, "");
}

function json(body: unknown, status = 200): Response {
  return new Response(JSON.stringify(body), {
    status,
    headers: { "content-type": "application/json" },
  });
}
