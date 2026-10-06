// Día 3 · vie 9 oct · reel 15 s (9:16), sin voz · «Cómo se usa tu kit» en 5 pasos (datos de Carlos y su socio; valen para todos los kits).
// Música: audio/music_dia3_15.mp3 (ElevenLabs Music, ~96 BPM, 1 beat = 0,627 s). Nada crece en pantalla: la cosecha aparece ya lista.
// Render: node render.mjs _dia3.mp4 --query spec=semana/dia3.js
import { L, Z, SH, W, LOGO, HL, hand, SK, table, sparkle, leaf, ctaChip, logo, badge, oysters, sprayer, scissors, knife, sunIcon, drop, rr, shroomyAt } from './common.js';

const H = 1920, B = 0.627, cl = L.clamp, E = L.E;
const camB = z => ({ z, x: W / 2, y: H - H / 2 / z });
const MJ = (K, x, y, h, P = {}) => shroomyAt(K, x, y, h, Object.assign({ outfit: 'jardinero' }, P));
const pop = (t, a, d = 0.35) => E.back(cl((t - a) / d));
// caja del kit de frente; state: 'mark' (línea punteada), 'cut' (abierta), 'harvest' (con setas listas)
function kitFront(K, sk, x, y, w, h, state, t = 0) { const ctx = K.ctx;
  const b = rr(x, y, w, h, 14); sk.fill(b, SH.kraft); sk.stroke(b);
  const lb = rr(x + w * 0.12, y + h * 0.08, w * 0.76, h * 0.26, 12); ctx.fillStyle = Z.surface; ctx.fill(lb); ctx.lineWidth = 4; ctx.strokeStyle = Z.green; ctx.stroke(lb);
  L.text(ctx, 'Zeta Dorada', x + w / 2, y + h * 0.2, { font: `600 ${Math.round(w * 0.1)}px Fraunces`, color: Z.deep, align: 'center' });
  L.text(ctx, 'Kit de cultivo', x + w / 2, y + h * 0.29, { font: `600 ${Math.round(w * 0.065)}px Poppins`, color: Z.green, align: 'center' });
  const ox = x + w * 0.22, oy = y + h * 0.5, ow = w * 0.56, oh = h * 0.3;
  if (state === 'mark') { ctx.save(); ctx.setLineDash([18, 12]); ctx.lineWidth = 6; ctx.strokeStyle = Z.ink; ctx.strokeRect(ox, oy, ow, oh); ctx.restore();
    L.text(ctx, 'corta aquí', x + w / 2, oy + oh + 52, { font: K.S.type.hand(46), color: Z.deep, align: 'center' }); return { ox, oy, ow, oh }; }
  const hole = rr(ox, oy, ow, oh, 10); ctx.fillStyle = '#5C4630'; ctx.fill(hole); ctx.save(); ctx.clip(hole); ctx.fillStyle = '#EDE3CC'; ctx.globalAlpha = 0.85; ctx.fillRect(ox + 8, oy + 8, ow - 16, oh - 16); ctx.restore(); sk.stroke(hole, 0.8);
  return { ox, oy, ow, oh }; }
const STEPS = [
  'Corta donde está *marcado* en el empaque.',
  'Rocía agua limpia con atomizador, *3 veces al día.*',
  'Mantenlo en la *sombra.*',
  'En unas 2 semanas, *cosecha:* corta desde la base de las setas.',
  'Vuelve a rociar y tendrás una *nueva cosecha.*',
];
function stepHead(K, s, h, n, t) { h.cue('pop', 0.05); badge(K, 540, 300, n, 56 * Math.max(0.05, Math.min(1, pop(t, 0.05))));
  HL(K, s, STEPS[n - 1], { x: 60, y: 380, w: 960, h: 360 }, h.A(0.15, 0.5), { max: 100 }); }

export default {
  style: 'zetadia1', format: '9:16', fps: 30, camera: false, chrome: false, captions: false, person: false, actor: null,
  scenes: [
    // título
    { type: 'story', dur: 3 * B, bg: 'cream', cam: t => camB(1 + 0.04 * E.inOut(cl(t / 2))),
      render(K, s, h) { const sk = SK(K, 61), t = s.t; h.cue('title', 0.05); h.cue('pop', 0.6);
        table(K, sk, 1500, '#E5EDCB');
        HL(K, s, 'Cómo se usa *tu kit*', { x: 60, y: 300, w: 960, h: 320 }, h.A(0.05, 0.5), { max: 140 });
        hand(K, 'en 5 pasos', 540, 720, 90, Z.green, cl((t - 0.5) / 0.3));
        kitFront(K, sk, 250, 1110, 380, 390, 'mark');
        const m = MJ(K, 820, 1500, 400, { armR: 60 + Math.sin(t * 9) * 15, armL: 20, eyes: 'happy', mouth: 'open', look: [-0.4, 0] });
        sparkle(K, 220, 1060, 28, cl((t - 0.6) / 0.6)); } },
    // 1 · cortar
    { type: 'story', dur: 3.5 * B, bg: 'sage', trans: { type: 'pan', dur: 0.35 },
      render(K, s, h) { const sk = SK(K, 62), t = s.t; stepHead(K, s, h, 1, t); h.cue('whoosh', 0.7);
        table(K, sk, 1560); const o = kitFront(K, sk, 230, 1060, 460, 500, 'mark');
        // la tijera recorre la línea punteada de arriba
        const u = cl((t - 0.5) / 1.4); scissors(K, o.ox + o.ow * u, o.oy, 1, 0.25 + Math.abs(Math.sin(t * 14)) * 0.25, 0);
        MJ(K, 880, 1560, 340, { look: [-0.7, -0.2], eyes: 'wide', mouth: 'o', armL: 40 }); } },
    // 2 · rociar
    { type: 'story', dur: 3.5 * B, bg: 'sun', trans: { type: 'pan', dur: 0.35 },
      render(K, s, h) { const sk = SK(K, 63), ctx = K.ctx, t = s.t; stepHead(K, s, h, 2, t); [0.6, 1.1, 1.6].forEach(a => h.cue('whoosh', a));
        table(K, sk, 1560); kitFront(K, sk, 120, 1060, 440, 500, 'cut');
        const m = MJ(K, 820, 1560, 420, { armL: 70, armR: 15, eyes: 'happy', mouth: 'smile', look: [-0.6, 0] });
        const q = (Math.sin(t * 6) * 0.5 + 0.5); sprayer(K, sk, m.handL[0] - 10, m.handL[1] - 70, 1.1, 0.35, q, true);
        [0, 1, 2].forEach(k => { const p = pop(t, 0.8 + k * 0.25); if (p > 0.01) { ctx.save(); ctx.translate(380 + k * 110, 860); ctx.scale(p, p); drop(K, 0, 0, 26); ctx.restore(); } });
        if (t > 1.4) L.text(ctx, '3 veces al día', 490, 1000, { font: '700 52px Poppins', color: Z.deep, align: 'center', alpha: cl((t - 1.4) / 0.3) }); } },
    // 3 · sombra
    { type: 'story', dur: 3.5 * B, bg: 'cream', trans: { type: 'pan', dur: 0.35 },
      render(K, s, h) { const sk = SK(K, 64), ctx = K.ctx, t = s.t; stepHead(K, s, h, 3, t);
        // ventana con sol a la izquierda; el kit queda en la sombra a la derecha
        const win = rr(70, 820, 330, 420, 16); sk.fill(win, '#EAF3F5'); sk.stroke(win); ctx.save(); ctx.globalAlpha = 0.5; ctx.strokeStyle = Z.ink; ctx.lineWidth = 6; ctx.beginPath(); ctx.moveTo(235, 820); ctx.lineTo(235, 1240); ctx.moveTo(70, 1030); ctx.lineTo(400, 1030); ctx.stroke(); ctx.restore();
        sunIcon(K, 170, 920, 40 + Math.sin(t * 3) * 2);
        ctx.save(); ctx.globalAlpha = 0.28; ctx.fillStyle = Z.gold; ctx.beginPath(); ctx.moveTo(400, 860); ctx.lineTo(560, 1560); ctx.lineTo(240, 1560); ctx.lineTo(400, 1240); ctx.closePath(); ctx.fill(); ctx.restore();
        table(K, sk, 1560);
        ctx.save(); ctx.globalAlpha = 0.22; ctx.fillStyle = Z.ink; ctx.fillRect(600, 900, 480, 660); ctx.restore();
        kitFront(K, sk, 640, 1160, 340, 400, 'cut');
        MJ(K, 480, 1560, 300, { look: [0.7, -0.1], eyes: 'happy', mouth: 'smile', armR: 60 });
        hand(K, 'sombra', 810, 1110, 54, Z.deep, cl((t - 0.6) / 0.3)); } },
    // 4 · cosechar (las setas ya están listas: no se anima el crecimiento)
    { type: 'story', dur: 3.5 * B, bg: 'sage', trans: { type: 'pan', dur: 0.35 },
      render(K, s, h) { const sk = SK(K, 65), ctx = K.ctx, t = s.t; stepHead(K, s, h, 4, t); h.cue('pop', 1.2);
        table(K, sk, 1560); const o = kitFront(K, sk, 200, 1060, 460, 500, 'harvest');
        oysters(K, sk, o.ox + o.ow / 2, o.oy + o.oh * 0.8, 1.35);
        // cuchillo que corta en la base
        const kx = o.ox + o.ow / 2 + 170 - cl((t - 0.7) / 0.8) * 60; knife(K, kx, o.oy + o.oh * 0.85, 1.1, Math.PI);
        // calendario «unas 2 semanas»
        const c = rr(780, 860, 220, 200, 16); sk.fill(c, Z.surface); sk.stroke(c); const top = rr(780, 860, 220, 50, 16); ctx.fillStyle = Z.green; ctx.fill(top);
        L.text(ctx, 'unas', 890, 960, { font: K.S.type.hand(44), color: Z.deep, align: 'center' }); L.text(ctx, '2 semanas', 890, 1020, { font: '700 36px Poppins', color: Z.deep, align: 'center' });
        MJ(K, 880, 1560, 320, { eyes: 'happy', mouth: 'open', armL: 60, armR: 60 }); } },
    // 5 · volver a rociar
    { type: 'story', dur: 3.5 * B, bg: 'sun', trans: { type: 'pan', dur: 0.35 },
      render(K, s, h) { const sk = SK(K, 66), ctx = K.ctx, t = s.t; stepHead(K, s, h, 5, t); [0.6, 1.2].forEach(a => h.cue('whoosh', a));
        table(K, sk, 1560); kitFront(K, sk, 120, 1060, 440, 500, 'cut');
        // flechas en círculo: «otra vez»
        ctx.save(); ctx.strokeStyle = Z.green; ctx.lineWidth = 10; ctx.lineCap = 'round'; const a0 = t * 2; for (const off of [0, Math.PI]) { ctx.beginPath(); ctx.arc(340, 900, 80, a0 + off, a0 + off + 2.3); ctx.stroke();
          const ax = 340 + Math.cos(a0 + off + 2.3) * 80, ay = 900 + Math.sin(a0 + off + 2.3) * 80, d = a0 + off + 2.3 + Math.PI / 2; ctx.beginPath(); ctx.moveTo(ax + Math.cos(d - 2.5) * 24, ay + Math.sin(d - 2.5) * 24); ctx.lineTo(ax, ay); ctx.lineTo(ax + Math.cos(d + 2.5) * 24, ay + Math.sin(d + 2.5) * 24); ctx.stroke(); } ctx.restore();
        const m = MJ(K, 820, 1560, 420, { armL: 70, armR: 15, eyes: 'happy', mouth: 'open', look: [-0.6, 0] });
        sprayer(K, sk, m.handL[0] - 10, m.handL[1] - 70, 1.1, 0.35, Math.sin(t * 6) * 0.5 + 0.5, true); } },
    // cierre: logo + «Escríbenos al inbox»
    { type: 'story', dur: 3.5 * B, bg: 'cream', image: LOGO, trans: { type: 'fade', dur: 0.3 },
      render(K, s, h) { const sk = SK(K, 67), t = s.t; h.cue('enter', 0.05); h.cue('ding', 0.5);
        table(K, sk, 1480, '#E5EDCB');
        [[110, 300, 0.2], [970, 330, 2.6], [90, 1180, -0.4], [990, 1120, 0.6]].forEach(([x, y, a], i) => leaf(K, sk, x, y + Math.sin(t * 1.5 + i) * 6, 1.1, a, i % 2 ? '#8DB07A' : '#6E9A5B'));
        logo(K, 250, 760);
        HL(K, s, '¿Dudas con *tu kit?*', { x: 60, y: 520, w: 960, h: 170 }, h.A(0.1, 0.4), { max: 110 });
        ctaChip(K, 540, 820, 'Escríbenos al inbox', Math.max(0.01, pop(t, 0.45, 0.35)));
        MJ(K, 540, 1480, 380, { armR: 60 + Math.sin(t * 9) * 18, armL: 15, eyes: 'happy', mouth: 'open' }); } },
  ],
};
