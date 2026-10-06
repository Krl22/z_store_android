# Zeta Dorada · Día 1 «Presentación de Zeta Dorada» · reel de ~18 s

**Formato:** 9:16, 30 fps · **Estilo:** acuarela de marca (`styles/zetadia1.js` = `zeta` + mascota dorada de `styles/shroomy.js`)
**Voz:** ElevenLabs «Lily» (acento peruano), plan Starter con licencia comercial · **Música:** Suno vía sunoapi.org, marimba ~89 BPM (`audio/beats.json`), sin letra
**Licencias:** verificar los términos comerciales de sunoapi.org antes de publicar.
**Vida:** boca con la voz (`lipsync.py` → `audio/env.json`), parpadeo, cejas, pulso al ritmo, zoom suave anclado abajo, hojas en bucle, destello en la corona; cambios de escena en beats.

## Guion
1. ¡Hola! Soy Mushie. Te doy la bienvenida a Zeta Dorada.
2. Hacemos kits para cultivar hongos comestibles en casa.
3. Hechos con calma, como nos gusta.
4. ¿Quieres el tuyo? Pide por WhatsApp.

## Escenas
| # | Escenario | Texto | Mascota |
|---|---|---|---|
| 1 · Hola | Crema, caen hojas | **¡Hola! Soy *Mushie*** · «te doy la bienvenida a Zeta Dorada» | Entra rebotando y saluda |
| 2 · Qué hacemos | Mesa con 3 cajas kraft genéricas | **Kits de hongos *comestibles*** · «para cultivar en casa» | Señala las cajas |
| 3 · Con calma | Taza humeante y planta | **Hechos con *calma*** · «como nos gusta» | Respira con los ojos cerrados (nada crece) |
| 4 · Cierre | Logo, hojas | **¿Quieres *el tuyo?*** · botón «Pide por WhatsApp» · +51 928 817 018 | Saluda |

Sin precios, sin afirmaciones de salud, sin datos inventados. Empaque de las cajas genérico (no confirmado).

## Pendiente de Carlos
Nombre final (se fija en `video.js`, `NAME`). Personaje v2 según la referencia de Carlos: sombrero dorado brillante, corona, capa, pañuelo verde, botitas y varita (`CHAR.outfit = 'rey'`).

## Render
`CHROME_PATH=/opt/pw-browsers/chromium-1194/chrome-linux/chrome node render.mjs --scenes` (QA) · `node render.mjs _video.mp4` → `python3 sfx_mix.py audio/_mix.wav --music audio/music.mp3 --vo audio/vo.wav` → ffmpeg (loudnorm -14 LUFS) → `videos/dia1-presentacion.mp4`.
