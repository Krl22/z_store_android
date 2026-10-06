"""Página de entrega de un día: copia los archivos a entregas/<dia>/ y escribe index.html.
Uso: python3 semana/pagina.py <dia> "<título>" "<bajada>" "<notas html>" archivo1 [archivo2 …]  (png/jpg o un mp4)"""
import html, os, shutil, sys
dia, title, lead, notes, files = sys.argv[1], sys.argv[2], sys.argv[3], sys.argv[4], sys.argv[5:]
d = f'entregas/{dia}'; os.makedirs(d, exist_ok=True)
items = []
for i, f in enumerate(files, 1):
    name = os.path.basename(f); shutil.copy(f, os.path.join(d, name))
    if name.endswith('.mp4'):
        items.append(f'<video src="{name}" controls playsinline preload="metadata"></video>')
    else:
        cap = f'<figcaption>Lámina {i} de {len(files)}</figcaption>' if len(files) > 1 else ''
        items.append(f'<figure><img src="{name}" alt="{html.escape(title)} – lámina {i}" loading="lazy">{cap}</figure>')
page = f'''<title>{html.escape(title)}</title>
<link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Fraunces:opsz,wght@9..144,600&family=Poppins:wght@400;600&display=swap">
<style>
/* Layout: una columna para celular; láminas o reel uno debajo de otro, notas al final */
:root{{--bg:#F3F0E6;--card:#FFFDF4;--ink:#23301F;--deep:#284B2E;--green:#426B35;--gold:#EAC64A;--line:#D8DDC8;
--display:"Fraunces",Georgia,serif;--body:"Poppins",system-ui,sans-serif}}
@media (prefers-color-scheme:dark){{:root:not([data-theme="light"]){{--bg:#1B2219;--card:#232C20;--ink:#EDEBDD;--deep:#CFE0B8;--green:#A9C98F;--line:#3A4535;color-scheme:dark}}}}
:root[data-theme="dark"]{{--bg:#1B2219;--card:#232C20;--ink:#EDEBDD;--deep:#CFE0B8;--green:#A9C98F;--line:#3A4535;color-scheme:dark}}
body{{background:var(--bg);color:var(--ink);font-family:var(--body);line-height:1.55}}
main{{max-width:540px;margin:0 auto;padding-inline:16px;padding-block:24px 48px;display:grid;gap:18px}}
h1{{font-family:var(--display);color:var(--deep);font-size:1.8rem;line-height:1.1;margin:0;text-wrap:balance}}
h2{{font-family:var(--display);color:var(--deep);font-size:1.2rem;margin:0}}
p,ul{{margin:0}} .lead{{color:var(--green)}}
figure{{margin:0;display:grid;gap:6px}} figcaption{{font-size:.85rem;opacity:.75}}
img,video{{width:100%;max-width:100%;border-radius:12px;border:1px solid var(--line);display:block}}
video{{aspect-ratio:9/16;background:#000}}
.note{{background:var(--card);border:1px solid var(--line);border-left:4px solid var(--gold);border-radius:10px;padding:14px 16px;display:grid;gap:8px}}
ul{{padding-left:1.2em;display:grid;gap:4px}} small{{opacity:.75}}
</style>
<main>
<h1>{html.escape(title)}</h1>
<p class="lead">{html.escape(lead)}</p>
{chr(10).join(items)}
<div class="note"><h2>Notas</h2>{notes}</div>
<p><small>Borrador para revisión. No se publicó nada en Facebook.</small></p>
</main>
'''
open(os.path.join(d, 'index.html'), 'w').write(page); print(d, [os.path.basename(f) for f in files])
