// Día 3 · carrusel 4:5 (1080x1350) · portada + 5 pasos + cierre «¿Dudas con tu kit? Escríbenos al inbox».
// Usa las mismas escenas del reel (semana/dia3.js) sin cámara ni transiciones; recorte y 235–1585.
// Render: node render.mjs --scenes --query spec=semana/dia3-carrusel.js --outdir qa/dia3c  →  python3 semana/recortar.py qa/dia3c ../../imagenes/dia3-como-se-usa 235
import reel from './dia3.js';
import { dots } from './common.js';

const n = reel.scenes.length;
export default { ...reel, scenes: reel.scenes.map((sc, i) => ({ ...sc, dur: 3, trans: undefined, cam: undefined,
  render(K, s, h) { sc.render(K, s, h); const ctx = K.ctx; ctx.save(); ctx.translate(0, 235 - 285); dots(K, i, n); ctx.restore(); } })) };
