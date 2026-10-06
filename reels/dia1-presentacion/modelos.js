// Hoja de modelos de la mascota (v2, según la referencia de Carlos): 1) personaje y expresiones, 2) vestuario.
// Render: node render.mjs --stills 1,3 --query spec=modelos.js
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
      head(K, `${NAME} dorada`, 'con corona, capa y varita');
      floor(K, 1130); shroomyAt(K, W / 2, 1130, 720, { outfit: 'rey', wand: true, armR: 40, armL: 70, eyes: 'dot', mouth: 'open' });
      [['feliz', { eyes: 'happy', armL: 70, armR: 70 }], ['sorpresa', { eyes: 'wide', mouth: 'o' }], ['tranquila', { eyes: 'closed', mouth: 'smile' }]].forEach(([n, P], i) => {
        const x = 190 + i * 350; floor(K, 1690); shroomyAt(K, x, 1690, 330, Object.assign({ outfit: 'rey' }, P)); label(K, n, x, 1770, 38); }); } },
    { type: 'story', dur: 2, bg: 'sage', render(K) {
      head(K, `${NAME} · vestuario`, 'una pinta para cada post');
      [['Rey (Día 1)', 'rey', { wand: true, armL: 60 }], ['Natural', 'natural', { eyes: 'happy' }], ['Chef', 'chef', { armR: 40, eyes: 'happy' }],
       ['Jardinero', 'jardinero', { armL: 60, eyes: 'happy' }], ['Chullo y chalina', 'chullo', { eyes: 'happy' }], ['Repartidor', 'repartidor', { armR: 75, look: [0.4, -0.1] }]]
        .forEach(([n, outfit, P], i) => { const x = 190 + (i % 3) * 350, y = 960 + Math.floor(i / 3) * 760; floor(K, y);
          shroomyAt(K, x, y, 360, Object.assign({ outfit }, P)); label(K, n, x, y + 80, 38); }); } },
  ],
};
