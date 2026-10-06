// Hoja de modelos de la mascota: 1) tres formas de sombrero, 2) seis vestuarios. Render: node render.mjs --stills 1,3 --query spec=modelos.js
import * as L from './engine/lib.js';
import { Z, shroomyAt } from './styles/shroomy.js';

const W = 1080, NAME = 'Mascota';
const label = (K, str, x, y, size = 40, color = Z.ink) => L.text(K.ctx, str, x, y, { font: `600 ${size}px Poppins`, color, align: 'center' });
const head = (K, a, b) => { L.text(K.ctx, a, W / 2, 190, { font: '600 92px Fraunces', color: Z.deep, align: 'center' }); L.text(K.ctx, b, W / 2, 270, { font: '700 54px Caveat', color: Z.green, align: 'center' }); };
const floor = (K, y) => { const c = K.ctx; c.save(); c.globalAlpha = 0.25; c.strokeStyle = Z.green; c.lineWidth = 3; c.beginPath(); c.moveTo(60, y + 4); c.lineTo(W - 60, y + 4); c.stroke(); c.restore(); };

export default {
  style: 'zeta', format: '9:16', fps: 30, camera: false, chrome: false, captions: false, person: false,
  scenes: [
    { type: 'story', dur: 2, bg: 'cream', render(K) {
      head(K, `${NAME} · formas`, 'elige la silueta');
      [['A · Clásico', 'clasico', 'dot'], ['B · Portobello', 'portobello', 'happy'], ['C · Bebé', 'bebe', 'wide']].forEach(([n, shape, eyes], i) => {
        const y = 760 + i * 0; const x = 190 + i * 350; floor(K, 760);
        shroomyAt(K, x, 760, i === 2 ? 290 : 310, { shape, eyes, mouth: i === 2 ? 'o' : 'smile', armR: i === 1 ? 60 : 10 });
        label(K, n, x, 840, 38); });
      // grande: el recomendado, saludando
      floor(K, 1640); shroomyAt(K, W / 2, 1640, 640, { shape: 'clasico', eyes: 'happy', mouth: 'grin', armR: 75, armL: 10 });
      label(K, 'A en grande, saludando', W / 2, 1730, 42, Z.green); } },
    { type: 'story', dur: 2, bg: 'sage', render(K) {
      head(K, `${NAME} · vestuario`, 'una pinta para cada post');
      [['Natural', 'normal', {}], ['Chef', 'chef', { armR: 40, eyes: 'happy' }], ['Jardinero', 'jardinero', { armL: 60, eyes: 'happy' }],
       ['Chullo y chalina', 'chullo', { mouth: 'grin', eyes: 'happy' }], ['Repartidor', 'repartidor', { armR: 75, look: [0.4, -0.1] }], ['Elegante', 'elegante', { mouth: 'grin' }]]
        .forEach(([n, outfit, P], i) => { const x = 190 + (i % 3) * 350, y = 900 + Math.floor(i / 3) * 760; floor(K, y);
          shroomyAt(K, x, y, 330, Object.assign({ outfit }, P)); label(K, n, x, y + 80, 38); }); } },
    { type: 'story', dur: 2, bg: 'cream', render(K) {
      head(K, `${NAME} · color`, '¿dorado entero o crema con dorado?');
      floor(K, 1000); shroomyAt(K, 290, 1000, 520, { eyes: 'happy', mouth: 'grin', armR: 70 }); label(K, '1 · Dorado entero', 290, 1090, 40);
      shroomyAt(K, 790, 1000, 520, { tone: 'crema', eyes: 'happy', mouth: 'grin', armR: 70 }); label(K, '2 · Crema + ribete dorado', 790, 1090, 40);
      floor(K, 1700); [['chef', 140], ['jardinero', 400], ['chullo', 660], ['repartidor', 920]].forEach(([outfit, x]) => shroomyAt(K, x, 1700, 300, { tone: 'crema', outfit, eyes: 'happy' }));
      label(K, 'la opción 2 con vestuario', W / 2, 1790, 38, Z.green); } },
  ],
};
