// Portada 1080x1080 de la publicidad Día 1 (CTA: Comenta QUIERO, sin WhatsApp). Se dibuja en y 420–1500 y se recorta.
// Render: node render.mjs --stills 0.5 --query spec=portada-publicidad.js --outdir qa
import * as L from './engine/lib.js';
import { Z, shroomyAt } from './styles/shroomy.js';
import { SK, kitBox } from './video.js';
import { pouch, commentButton } from './video-publicidad.js';

const LOGO = 'assets/logo-zeta.png', Y0 = 420;
export default {
  style: 'zetadia1', format: '9:16', fps: 30, camera: false, chrome: false, captions: false, person: false, actor: null,
  scenes: [{ type: 'story', dur: 1, bg: 'cream', image: LOGO, render(K, s) { const ctx = K.ctx, img = K.images[LOGO], sk = SK(K, 41);
    if (img) { const w = 460, h = w * img.height / img.width; ctx.drawImage(img, (1080 - w) / 2, Y0 + 40, w, h); }
    K.S.headline(K, 'Cultiva tus propios *hongos*', { x: 50, y: Y0 + 175, w: 980, h: 130 }, 1, s, { max: 96 });
    L.text(ctx, 'en casa', 540, Y0 + 370, { font: K.S.type.hand(80), color: Z.green, align: 'center' });
    const f = new Path2D(); f.rect(-20, Y0 + 900, 1120, 200); sk.fill(f, '#E5EDCB');
    kitBox(K, sk, 60, Y0 + 690, 240, 210, 'Ostra'); kitBox(K, sk, 320, Y0 + 650, 270, 250, 'Melena de león'); pouch(K, sk, 610, Y0 + 640, 220, 260);
    shroomyAt(K, 950, Y0 + 900, 270, { outfit: 'natural', armR: 60, eyes: 'happy', mouth: 'open', look: [-0.3, 0] });
    commentButton(K, 540, Y0 + 520, 0.95); } }],
};
