// Día 4 · sáb 10 oct · carrusel 4 láminas 4:5 · Melena de león: portada, kit, polvo, CTA. Sin usos ni beneficios del polvo.
import { Z, W, Y0, Y1, LOGO, HL, hand, body, SK, table, kitBox, pouch, sparkle, frameLeaves, ctaChip, logo, dots, lionsMane, M } from './common.js';
const N = 4, base = { style: 'zetadia1', format: '9:16', fps: 30, camera: false, chrome: false, captions: false, person: false, actor: null };
export default { ...base, scenes: [
  { type: 'story', dur: 1, bg: 'cream', image: LOGO, render(K, s) { const sk = SK(K, 71); frameLeaves(K, sk); logo(K, Y0 + 50, 300);
    HL(K, s, 'Melena *de león*', { x: 60, y: Y0 + 150, w: 960, h: 300 }, 1, { max: 170 });
    table(K, sk, 1400, '#E5EDCB'); lionsMane(K, sk, 540, 1170, 2.1); sparkle(K, 270, 980, 30, 0.5); sparkle(K, 820, 1010, 24, 0.5);
    hand(K, 'en Zeta Dorada', 540, Y0 + 560, 64); dots(K, 0, N); } },
  { type: 'story', dur: 1, bg: 'sage', render(K, s) { const sk = SK(K, 72); frameLeaves(K, sk);
    HL(K, s, 'Kit para cultivar *en casa*', { x: 60, y: Y0 + 90, w: 960, h: 300 }, 1, { max: 130 });
    table(K, sk, 1450); kitBox(K, sk, 270, 1000, 540, 450, 'Melena de león'); lionsMane(K, sk, 540, 990, 0.9);
    sparkle(K, 220, 960, 28, 0.5); dots(K, 1, N); } },
  { type: 'story', dur: 1, bg: 'sun', render(K, s) { const sk = SK(K, 73); frameLeaves(K, sk);
    HL(K, s, 'También, polvo de *melena de león*', { x: 60, y: Y0 + 90, w: 960, h: 320 }, 1, { max: 120 });
    table(K, sk, 1450); pouch(K, sk, 360, 900, 360, 550); sparkle(K, 260, 960, 26, 0.5); sparkle(K, 820, 930, 22, 0.5); dots(K, 2, N); } },
  { type: 'story', dur: 1, bg: 'cream', image: LOGO, render(K, s) { const sk = SK(K, 74); frameLeaves(K, sk); logo(K, Y0 + 90, 520);
    HL(K, s, '¿Quieres *el tuyo?*', { x: 60, y: Y0 + 330, w: 960, h: 200 }, 1, { max: 130 });
    ctaChip(K, 540, Y0 + 680, 'Comenta QUIERO'); hand(K, 'o escríbenos al inbox', 540, Y0 + 820, 64);
    table(K, sk, 1450, '#E5EDCB'); M(K, 860, 1450, 240, { armR: 60, eyes: 'happy', mouth: 'open', look: [-0.3, 0] }); dots(K, 3, N, false); } },
] };
