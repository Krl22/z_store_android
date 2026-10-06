// Día 5 · dom 11 oct · imagen 4:5 · «Domingo con calma» · kit de ganoderma, taza y planta. Mushie con chullo y chalina, tranquilo.
import { Z, W, Y0, Y1, LOGO, HL, hand, body, SK, table, kitBox, mug, plant, sparkle, frameLeaves, ctaChip, logo, ganoderma, M } from './common.js';
export default { style: 'zetadia1', format: '9:16', fps: 30, camera: false, chrome: false, captions: false, person: false, actor: null,
  scenes: [{ type: 'story', dur: 1, bg: 'sun', image: LOGO, render(K, s) { const sk = SK(K, 81); frameLeaves(K, sk); logo(K, Y0 + 50, 300);
    HL(K, s, 'Domingo *con calma*', { x: 60, y: Y0 + 150, w: 960, h: 200 }, 1, { max: 140 });
    body(K, 'Un ratito para ti y tu kit de ganoderma.', 540, Y0 + 420, 42, Z.ink);
    table(K, sk, 1330);
    kitBox(K, sk, 90, 1060, 340, 270, 'Ganoderma'); ganoderma(K, sk, 260, 1050, 0.75);
    M(K, 600, 1330, 330, { outfit: 'chullo', eyes: 'closed', mouth: 'smile', armL: 8, armR: 8 });
    mug(K, sk, 850, 1334, 2); plant(K, sk, 990, 1334, 0);
    ctaChip(K, 540, 1455, 'Comenta QUIERO', 0.95); hand(K, 'o escríbenos al inbox', 540, 1565, 56); } }] };
