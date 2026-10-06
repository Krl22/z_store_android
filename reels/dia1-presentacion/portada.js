// Portada 1080x1080 para Facebook: se dibuja en el centro del lienzo 9:16 (y 420–1500) y se recorta con PIL.
// Render: node render.mjs --stills 0.5 --query spec=portada.js --outdir qa  →  recorte en videos/
import * as L from './engine/lib.js';
import { Z, shroomyAt } from './styles/shroomy.js';
import { NAME } from './video.js';

const LOGO = 'assets/logo-zeta.png', Y0 = 420;
export default {
  style: 'zetadia1', format: '9:16', fps: 30, camera: false, chrome: false, captions: false, person: false,
  scenes: [{ type: 'story', dur: 1, bg: 'cream', image: LOGO, render(K, s) { const ctx = K.ctx, img = K.images[LOGO];
    if (img) { const w = 520, h = w * img.height / img.width; ctx.drawImage(img, (1080 - w) / 2, Y0 + 50, w, h); }
    K.S.headline(K, `¡Hola! Soy *${NAME}*`, { x: 60, y: Y0 + 210, w: 960, h: 170 }, 1, s, { max: 120 });
    const f = new Path2D(); f.rect(-20, Y0 + 960, 1120, 200); ctx.fillStyle = '#E5EDCB'; ctx.fill(f);
    ctx.save(); ctx.globalAlpha = 0.3; ctx.strokeStyle = Z.green; ctx.lineWidth = 3; ctx.beginPath(); ctx.moveTo(0, Y0 + 960); ctx.lineTo(1080, Y0 + 960); ctx.stroke(); ctx.restore();
    shroomyAt(K, 540, Y0 + 980, 500, { outfit: 'rey', wand: true, armR: 30, armL: 70, eyes: 'dot', mouth: 'open' });
    L.text(ctx, 'Pide por WhatsApp', 540, Y0 + 1050, { font: '800 50px Poppins', color: Z.deep, align: 'center' }); } }],
};
