// Día 2 · jue 8 oct (feriado) · imagen 4:5 · «Feriado en casa» · kits de ostra blanca y parda. Mushie no sale.
import { L, Z, W, Y0, Y1, LOGO, HL, hand, body, SK, table, kitBox, mug, plant, sparkle, frameLeaves, ctaChip, logo, oysters } from './common.js';

export default {
  style: 'zetadia1', format: '9:16', fps: 30, camera: false, chrome: false, captions: false, person: false, actor: null,
  scenes: [{ type: 'story', dur: 1, bg: 'sun', image: LOGO, render(K, s) { const sk = SK(K, 51), ctx = K.ctx;
    // luz cálida de feriado: manchas doradas suaves
    ctx.save(); for (const [x, y, r] of [[160, 900, 70], [930, 860, 90], [520, 980, 60], [300, 1060, 40], [800, 1020, 50]]) { const g = ctx.createRadialGradient(x, y, 0, x, y, r); g.addColorStop(0, 'rgba(248,228,178,.75)'); g.addColorStop(1, 'rgba(248,228,178,0)'); ctx.fillStyle = g; ctx.fillRect(x - r, y - r, 2 * r, 2 * r); } ctx.restore();
    frameLeaves(K, sk);
    logo(K, Y0 + 50, 300);
    HL(K, s, 'Feriado *en casa*', { x: 60, y: Y0 + 150, w: 960, h: 180 }, 1, { max: 140 });
    body(K, 'Empieza tu kit de', 540, Y0 + 405, 50, Z.ink); body(K, 'ostra blanca u ostra parda.', 540, Y0 + 470, 50, Z.deep, 'center', 600);
    table(K, sk, 1330);
    kitBox(K, sk, 140, 1070, 320, 260, 'Ostra blanca'); kitBox(K, sk, 520, 1070, 320, 260, 'Ostra parda');
    mug(K, sk, 955, 1334, 1.4);
    oysters(K, sk, 300, 1060, 0.55); oysters(K, sk, 690, 1060, 0.55, '#B9A58A', '#8C7A62');
    sparkle(K, 120, 1000, 26, 0.5); sparkle(K, 880, 980, 22, 0.5);
    ctaChip(K, 540, 1455, 'Comenta QUIERO', 0.95);
    hand(K, 'o escríbenos al inbox', 540, 1565, 56); } }],
};
