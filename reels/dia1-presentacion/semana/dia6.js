// Día 6 · lun 12 oct · carrusel 5 láminas 4:5 · Receta: «Setas a la plancha con ajo y limón». Cocina corriente, sin claims. Mushie chef. CTA inbox.
import { L, Z, W, Y0, Y1, LOGO, HL, hand, body, SK, table, sparkle, frameLeaves, ctaChip, logo, dots, badge, oysters, pan, garlic, lemon, bottle, saltShaker, rr, M } from './common.js';
const N = 5, base = { style: 'zetadia1', format: '9:16', fps: 30, camera: false, chrome: false, captions: false, person: false, actor: null };
const MC = (K, x, y, h, P = {}) => M(K, x, y, h, Object.assign({ outfit: 'chef' }, P));
function slices(K, sk, cx, cy, gold = 0.8) { const ctx = K.ctx; [[-90, -10, -0.3], [0, 10, 0.1], [95, -6, 0.3], [-30, -40, 0.5], [60, -40, -0.2]].forEach(([dx, dy, r]) => { ctx.save(); ctx.translate(cx + dx, cy + dy); ctx.rotate(r);
  const p = new Path2D(); p.moveTo(-60, 0); p.bezierCurveTo(-60, -40, 60, -40, 60, 0); p.quadraticCurveTo(0, 24, -60, 0); p.closePath(); sk.fill(p, L.mix('#EFE6D2', '#D9A24E', gold)); sk.stroke(p, 0.7); ctx.restore(); }); }
function step(K, s, n, txt, y) { badge(K, 120, y + 40, n, 44); HL(K, s, txt, { x: 190, y: y - 20, w: 830, h: 140 }, 1, { max: 64, align: 'left' }); }
export default { ...base, scenes: [
  { type: 'story', dur: 1, bg: 'cream', image: LOGO, render(K, s) { const sk = SK(K, 91); frameLeaves(K, sk); logo(K, Y0 + 50, 300);
    hand(K, 'receta simple', 540, Y0 + 200, 60);
    HL(K, s, 'Setas a la plancha con *ajo y limón*', { x: 60, y: Y0 + 230, w: 960, h: 330 }, 1, { max: 120 });
    table(K, sk, 1420); pan(K, sk, 470, 1250, 0.9); slices(K, sk, 460, 1240); lemon(K, sk, 140, 1400, 0.55); MC(K, 870, 1420, 330, { armL: 50, eyes: 'happy', mouth: 'open', look: [-0.5, 0.2] }); dots(K, 0, N); } },
  { type: 'story', dur: 1, bg: 'sage', render(K, s) { const sk = SK(K, 92), ctx = K.ctx; frameLeaves(K, sk);
    HL(K, s, '*Ingredientes*', { x: 60, y: Y0 + 80, w: 960, h: 160 }, 1, { max: 130 });
    const cells = [[270, 760, 'Setas de tu kit', () => oysters(K, sk, 270, 760, 1.1)], [810, 760, 'Ajo picado', () => garlic(K, sk, 810, 720, 1.2)],
      [180, 1180, 'Limón', () => lemon(K, sk, 180, 1110, 1)], [540, 1180, 'Aceite', () => bottle(K, sk, 540, 1170, 1)], [900, 1180, 'Sal', () => saltShaker(K, sk, 900, 1160, 1.1)]];
    cells.forEach(([x, y, lab, draw]) => { draw(); body(K, lab, x, y + 80, 40, Z.deep, 'center', 600); }); dots(K, 1, N); } },
  { type: 'story', dur: 1, bg: 'sun', render(K, s) { const sk = SK(K, 93); frameLeaves(K, sk);
    HL(K, s, '*Preparación*', { x: 60, y: Y0 + 70, w: 960, h: 150 }, 1, { max: 110 });
    step(K, s, 1, 'Limpia las setas con un *paño seco.*', Y0 + 260); step(K, s, 2, 'Calienta un poco de *aceite* en la plancha.', Y0 + 450);
    table(K, sk, 1450); pan(K, sk, 600, 1300, 0.8); oysters(K, sk, 200, 1440, 0.9); dots(K, 2, N); } },
  { type: 'story', dur: 1, bg: 'cream', render(K, s) { const sk = SK(K, 94); frameLeaves(K, sk);
    HL(K, s, '*Preparación*', { x: 60, y: Y0 + 70, w: 960, h: 150 }, 1, { max: 110 });
    step(K, s, 3, 'Dóralas por ambos lados y agrega el *ajo al final.*', Y0 + 260); step(K, s, 4, 'Sal y unas gotas de *limón.* ¡Listo!', Y0 + 450);
    table(K, sk, 1450); pan(K, sk, 470, 1300, 0.8); slices(K, sk, 460, 1290, 1); lemon(K, sk, 150, 1410, 0.55); MC(K, 900, 1450, 260, { eyes: 'happy', mouth: 'open', armR: 60 }); dots(K, 3, N); } },
  { type: 'story', dur: 1, bg: 'sage', image: LOGO, render(K, s) { const sk = SK(K, 95); frameLeaves(K, sk); logo(K, Y0 + 90, 520);
    HL(K, s, '¿Quieres cultivar *las tuyas?*', { x: 60, y: Y0 + 320, w: 960, h: 260 }, 1, { max: 120 });
    ctaChip(K, 540, Y0 + 720, 'Escríbenos al inbox');
    table(K, sk, 1450); MC(K, 540, 1450, 300, { armR: 60, eyes: 'happy', mouth: 'open' }); dots(K, 4, N, false); } },
] };
