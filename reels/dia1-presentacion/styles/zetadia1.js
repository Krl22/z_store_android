// Estilo del reel Día 1: el mismo «zeta» (acuarela de marca), con la mascota dorada en lugar de Pompón.
import * as L from '../engine/lib.js';
import zeta from './zeta.js';
import { shroomyAt } from './shroomy.js';

// forma, color y vestuario por defecto del personaje (video.js los fija)
export const CHAR = { shape: 'clasico', tone: 'dorado', outfit: 'normal' };

export default {
  ...zeta, id: 'zetadia1', name: 'Zeta acuarela · mascota dorada',
  mascot(K, box, p, s, mood) { if (p <= 0) return; const P = Object.assign({}, CHAR, L.POSE || {});
    const a = shroomyAt(K, box.x + box.w / 2, box.y + box.h, box.h, P); Object.assign(L.MLAST, { handL: a.handL, handR: a.handR, top: a.top, center: a.center, s: a.s }); },
};
