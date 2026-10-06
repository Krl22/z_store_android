// Zeta Dorada · Día 1 «Presentación de Zeta Dorada» · reel de ~18 s (9:16).
// Mascota dorada (styles/shroomy.js). Sin precios, sin salud, sin crecimiento animado. Storyboard: STORYBOARD.md
// Hasta tener la voz, los tiempos de escena son estimados; después se ajustan a audio/words.json.
import * as L from './engine/lib.js';
import { Z, SH } from './styles/shroomy.js';
import { CHAR } from './styles/zetadia1.js';

// ---- decisiones pendientes de Carlos (nombre, forma, color, vestuario) ----
export const NAME = 'Zetito';            // provisional
Object.assign(CHAR, { shape: 'clasico', tone: 'dorado', outfit: 'normal' });
const PHONE = '+51 928 817 018';

const W = 1080, H = 1920, FLOOR = 1420, LOGO = 'assets/logo-zeta.png';
const boil = K => Math.floor(K.t * 8);
const cl = L.clamp, E = L.E;
function SK(K, seed = 21) { const ctx = K.ctx, wf = L.washFill(ctx, seed, { alpha: 0.42, gran: 0.1, pool: 0.25 });
  return { fill: (p, c) => { ctx.fillStyle = '#FFFEF8'; ctx.fill(p); wf(p, c); }, stroke: L.wobbleStroke(ctx, Z.ink, 4, 1, boil(K)), ink: Z.ink }; }
const HL = (K, s, str, box, p, o = {}) => K.S.headline(K, str, box, p, s, o);
function hand(K, str, x, y, size, color = Z.green, alpha = 1) { L.text(K.ctx, str, x, y, { font: K.S.type.hand(size), color, align: 'center', alpha }); }
const rr = (x, y, w, h, r) => { const p = new Path2D(); p.roundRect(x, y, w, h, r); return p; };
const ell = (x, y, rx, ry) => { const p = new Path2D(); p.ellipse(x, y, rx, ry, 0, 0, 7); return p; };
const pop = (t, a, d = 0.35) => E.back(cl((t - a) / d));
const squash = (t, a, d = 0.3, k = 0.22) => (t > a && t < a + d) ? Math.sin((t - a) / d * Math.PI) * k : 0;
const wave = (t, a) => t > a ? 60 + Math.sin((t - a) * 9) * 20 : 15;

// ---------- piezas ----------
function table(K, sk, y = FLOOR, col = '#D9B48A') { const top = new Path2D(); top.rect(-40, y - 6, W + 80, H - y + 60); sk.fill(top, col); const ln = new Path2D(); ln.moveTo(-20, y); ln.lineTo(W + 20, y); sk.stroke(ln, 0.9);
  for (let k = 1; k < 6; k++) { const yy = y + k * k * 14, g = new Path2D(); g.moveTo(-20, yy); g.lineTo(W + 20, yy); K.ctx.save(); K.ctx.globalAlpha = 0.18; sk.stroke(g, 0.5); K.ctx.restore(); } }
function leaf(K, sk, x, y, s, a, col = '#6E9A5B') { const ctx = K.ctx; ctx.save(); ctx.translate(x, y); ctx.rotate(a); ctx.scale(s, s); const p = new Path2D(); p.moveTo(0, -40); p.quadraticCurveTo(26, -6, 0, 40); p.quadraticCurveTo(-26, -6, 0, -40); sk.fill(p, col);
  const v = new Path2D(); v.moveTo(0, -34); v.lineTo(0, 36); ctx.save(); ctx.globalAlpha = 0.5; sk.stroke(v, 0.5); ctx.restore(); ctx.restore(); }
function sparkle(K, x, y, r, p) { if (p <= 0) return; const ctx = K.ctx; ctx.save(); ctx.globalAlpha = cl(1.4 - p); ctx.fillStyle = Z.gold; L.star(ctx, x, y, r * E.out(cl(p * 2)), 4, 0.35); ctx.fill(); ctx.restore(); }
function kitBox(K, sk, x, y, w, h, label) { // caja kraft genérica con etiqueta verde (el empaque real no está confirmado)
  const ctx = K.ctx; const fl = new Path2D(); fl.moveTo(x, y); fl.lineTo(x - w * 0.12, y - h * 0.22); fl.lineTo(x + w * 0.45, y - h * 0.2); fl.lineTo(x + w * 0.5, y); fl.closePath(); sk.fill(fl, '#B98E62'); sk.stroke(fl);
  const fr = new Path2D(); fr.moveTo(x + w, y); fr.lineTo(x + w * 1.12, y - h * 0.22); fr.lineTo(x + w * 0.55, y - h * 0.2); fr.lineTo(x + w * 0.5, y); fr.closePath(); sk.fill(fr, '#B98E62'); sk.stroke(fr);
  const b = rr(x, y, w, h, 10); sk.fill(b, SH.kraft); sk.stroke(b);
  const lb = rr(x + w * 0.1, y + h * 0.26, w * 0.8, h * 0.5, 12); ctx.fillStyle = Z.surface; ctx.fill(lb); ctx.lineWidth = 4; ctx.strokeStyle = Z.green; ctx.stroke(lb);
  L.text(ctx, 'Zeta Dorada', x + w / 2, y + h * 0.47, { font: `600 ${Math.round(w * 0.11)}px Fraunces`, color: Z.deep, align: 'center' });
  L.text(ctx, label, x + w / 2, y + h * 0.64, { font: `600 ${Math.round(w * 0.075)}px Poppins`, color: Z.green, align: 'center' }); }
function mug(K, sk, x, y, t) { const ctx = K.ctx; // taza de cerámica verde con vapor
  const hd = new Path2D(); hd.ellipse(x + 78, y - 70, 30, 36, 0, -1.4, 1.4); ctx.save(); ctx.lineWidth = 16; ctx.strokeStyle = Z.green; ctx.stroke(hd); ctx.restore();
  const b = rr(x - 70, y - 150, 140, 150, 24); sk.fill(b, Z.green); sk.stroke(b); const rim = ell(x, y - 150, 70, 14); sk.fill(rim, '#7A5A3C'); sk.stroke(rim, 0.7);
  ctx.save(); ctx.strokeStyle = 'rgba(255,255,255,.8)'; ctx.lineCap = 'round';
  for (let k = 0; k < 3; k++) { const ph = (t * 0.45 + k / 3) % 1; ctx.globalAlpha = Math.sin(ph * Math.PI) * 0.8; ctx.lineWidth = 8 - ph * 4; ctx.beginPath();
    for (let i = 0; i <= 12; i++) { const u = i / 12, yy = y - 170 - ph * 180 - u * 90, xx = x + (k - 1) * 34 + Math.sin(u * 5 + t * 2 + k) * 14; i ? ctx.lineTo(xx, yy) : ctx.moveTo(xx, yy); } ctx.stroke(); } ctx.restore(); }
function plant(K, sk, x, y, t) { const pot = new Path2D(); pot.moveTo(x - 60, y); pot.lineTo(x - 74, y - 120); pot.lineTo(x + 74, y - 120); pot.lineTo(x + 60, y); pot.closePath();
  for (let k = 0; k < 7; k++) leaf(K, sk, x + (k - 3) * 24, y - 170 - (3 - Math.abs(k - 3)) * 22, 1.05, (k - 3) * 0.32 + Math.sin(t * 1.2 + k) * 0.05, k % 2 ? '#8DB07A' : '#6E9A5B');
  sk.fill(pot, '#E9DFC6'); sk.stroke(pot); const rim = rr(x - 82, y - 136, 164, 24, 8); sk.fill(rim, '#E9DFC6'); sk.stroke(rim, 0.8); }
function waButton(K, x, y, c) { if (c <= 0.01) return; const ctx = K.ctx; ctx.save(); ctx.translate(x, y); ctx.scale(c, c);
  const chip = rr(-300, -62, 600, 124, 62); ctx.shadowColor = 'rgba(35,48,31,.22)'; ctx.shadowBlur = 22; ctx.shadowOffsetY = 8; ctx.fillStyle = Z.gold; ctx.fill(chip); ctx.shadowColor = 'transparent';
  const bub = new Path2D(); bub.roundRect(-262, -26, 54, 44, 14); bub.moveTo(-250, 18); bub.lineTo(-258, 34); bub.lineTo(-236, 18); ctx.lineWidth = 5; ctx.strokeStyle = Z.ink; ctx.stroke(bub);
  L.text(ctx, 'Pide por WhatsApp', 40, 16, { font: '800 48px Poppins', color: Z.ink, align: 'center' }); ctx.restore(); }

export default {
  style: 'zetadia1', format: '9:16', fps: 30, camera: false, chrome: false, captions: false,
  person: false, mascot: 'shroomy', actor: { x: 540, y: FLOOR, h: 400 },
  scenes: [
    // 1 · HOLA — entra rebotando y saluda
    { type: 'story', dur: 4.2, bg: 'cream', say: `¡Hola! Soy ${NAME}. Te doy la bienvenida a Zeta Dorada.`,
      render(K, s, h) { const sk = SK(K), t = s.t;
        h.cue('title', 0.15); [0.45, 0.8, 1.1].forEach(a => h.cue('hop', a)); h.cue('pop', 1.5); h.cue('title', 2.0);
        table(K, sk, FLOOR, '#E5EDCB');
        leaf(K, sk, 820 + Math.sin(t * 1.6) * 50, -80 + t * 230, 1.1, Math.sin(t * 2) * 0.8);
        leaf(K, sk, 220 + Math.sin(t * 1.3 + 1) * 40, -240 + t * 210, 0.85, Math.sin(t * 1.7 + 1) * 0.9, '#8DB07A');
        const box = { x: 60, y: 300, w: 960, h: 260 };
        L.writeOn(K.ctx, () => HL(K, s, `¡Hola! Soy *${NAME}*`, box, 1, { max: 140 }), box, cl((t - 0.15) / 0.9), { tool: 'brush', lines: 1 });
        hand(K, 'te doy la bienvenida', 540, 640, 66, Z.green, cl((t - 2.0) / 0.4));
        hand(K, 'a Zeta Dorada', 540, 720, 66, Z.green, cl((t - 2.4) / 0.4));
        sparkle(K, 820, 930, 32, cl((t - 1.5) / 0.6)); sparkle(K, 260, 1000, 24, cl((t - 1.65) / 0.6)); },
      actor(t) { const q = cl((t - 0.15) / 1.1), x = L.lerp(1350, 540, E.out(q)), b = t < 1.25 ? Math.abs(Math.sin(q * Math.PI * 3)) * (1 - q * 0.7) * 160 : 0;
        const land = [0.45, 0.8, 1.1, 1.26].reduce((m, a) => m + squash(t, a - 0.05, 0.16, 0.16), 0);
        return { x, y: FLOOR, h: 430, pose: { hop: b, sx: 1 + land, sy: 1 - land, armR: wave(t, 1.5), armL: 12, eyes: t > 1.4 ? 'happy' : 'dot', mouth: t > 1.4 ? 'grin' : 'smile', look: t < 1.3 ? [-0.5, 0] : [0, 0.1] } }; } },

    // 2 · QUÉ HACEMOS — kits de autocultivo de hongos comestibles
    { type: 'story', dur: 4.8, bg: 'sage', trans: { type: 'pan', dur: 0.6 }, say: 'Hacemos kits para cultivar hongos comestibles en casa.',
      render(K, s, h) { const sk = SK(K, 23), ctx = K.ctx, t = s.t;
        h.cue('title', 0.2); h.cue('pop', 0.9); h.cue('pop', 1.15); h.cue('pop', 1.4); h.cue('title', 2.4);
        table(K, sk, 1220);
        [[90, 1000, 250, 220, 'Ostra blanca', 0.9], [395, 940, 290, 280, 'Melena de león', 1.15], [735, 1000, 250, 220, 'Ostra parda', 1.4]].forEach(([x, y, w, hh, lab, a]) => {
          const q = pop(t, a, 0.4); if (q <= 0.01) return; ctx.save(); ctx.translate(x + w / 2, y + hh); ctx.scale(q, q); ctx.translate(-(x + w / 2), -(y + hh)); kitBox(K, sk, x, y, w, hh, lab); ctx.restore(); });
        HL(K, s, 'Kits de hongos *comestibles*', { x: 60, y: 260, w: 960, h: 300 }, h.A(0.2, 0.6), { max: 120 });
        hand(K, 'para cultivar en casa', 540, 640, 70, Z.green, cl((t - 2.4) / 0.4));
        [[200, 900, 1.6], [880, 880, 1.75]].forEach(([x, y, a]) => sparkle(K, x, y, 28, cl((t - a) / 0.6))); },
      actor(t) { return { x: 540, y: 1700, h: 380, pose: { armL: t > 1.0 ? 85 : 20, armR: 20, eyes: t > 2.4 ? 'happy' : 'dot', look: [-0.2, -0.7], hop: t > 2.3 && t < 2.6 ? Math.sin((t - 2.3) / 0.3 * Math.PI) * 24 : 0 } }; } },

    // 3 · CON CALMA — taza y planta; respira tranquilo (nada crece)
    { type: 'story', dur: 3.8, bg: 'sun', trans: { type: 'iris', x: 0.5, y: 1200 / H, dur: 0.6 }, say: 'Hechos con calma, como nos gusta.',
      render(K, s, h) { const sk = SK(K, 25), t = s.t;
        h.cue('title', 0.15); h.cue('title', 1.6);
        table(K, sk, 1380);
        mug(K, sk, 200, 1384, t); plant(K, sk, 880, 1384, t);
        HL(K, s, 'Hechos con *calma*', { x: 60, y: 300, w: 960, h: 260 }, h.A(0.15, 0.7), { max: 140 });
        hand(K, 'como nos gusta', 540, 650, 70, Z.green, cl((t - 1.6) / 0.5));
        leaf(K, sk, 560 + Math.sin(t * 1.1) * 120, 760 + t * 70, 0.7, Math.sin(t * 1.4) * 0.7, '#8DB07A'); },
      actor(t) { const br = Math.sin(t * 2.2) * 0.03; // respira lento
        return { x: 540, y: 1384, h: 380, pose: { sx: 1 + br, sy: 1 - br, armL: 8, armR: 8, eyes: t > 0.6 ? 'closed' : 'dot', mouth: 'smile' } }; } },

    // 4 · CIERRE — logo, «Pide por WhatsApp» y número (sin precio)
    { type: 'story', dur: 5.6, bg: 'cream', image: LOGO, trans: { type: 'fade', dur: 0.6 }, say: '¿Quieres el tuyo? Pide por WhatsApp.',
      render(K, s, h) { const sk = SK(K, 31), ctx = K.ctx, t = s.t, img = K.images[LOGO];
        h.cue('enter', 0.2); h.cue('title', 0.6); h.cue('impact', 1.6); h.cue('ding', 1.7); h.cue('pop', 2.3);
        table(K, sk, FLOOR, '#E5EDCB');
        [[110, 300, 0.2], [970, 330, 2.6], [90, 1180, -0.4], [990, 1120, 0.6]].forEach(([x, y, a], i) => leaf(K, sk, x, y + Math.sin(t * 1.5 + i) * 6, 1.1, a, i % 2 ? '#8DB07A' : '#6E9A5B'));
        if (img) { const e = E.out(cl((t - 0.1) / 0.6)), w = 760, hh = w * img.height / img.width; ctx.save(); ctx.globalAlpha = e; ctx.drawImage(img, (W - w) / 2, 250 - (1 - e) * 30, w, hh); ctx.restore(); }
        HL(K, s, '¿Quieres *el tuyo?*', { x: 60, y: 500, w: 960, h: 170 }, h.A(0.6, 0.6), { max: 110 });
        waButton(K, 540, 820, pop(t, 1.6, 0.45));
        const n = cl((t - 2.3) / 0.4); if (n > 0) L.text(ctx, PHONE, 540, 990, { font: '600 70px Poppins', color: Z.deep, align: 'center', alpha: n }); },
      actor(t) { return { x: 540, y: FLOOR, h: 380, pose: { armR: wave(t, 2.6), armL: 12, eyes: 'happy', mouth: t > 2.6 ? 'grin' : 'smile', hop: t > 1.6 && t < 1.9 ? Math.sin((t - 1.6) / 0.3 * Math.PI) * 24 : 0 } }; } },
  ],
};
