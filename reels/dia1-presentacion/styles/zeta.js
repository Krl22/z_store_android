// Acuarela con la marca Zeta Dorada: crema, verde bosque, dorado solo de acento, terracota solo para precio.
// Mascota: «Pompón», una melena de león (Hericium erinaceus) blanca y peluda con carita.
import * as L from '../engine/lib.js';
import acuarela from './acuarela.js';
import { title } from '../engine/base.js';

export const Z = { deep: '#284B2E', green: '#426B35', ink: '#23301F', muted: '#6C735D', cream: '#F3F0E6', surface: '#FFFDF4', soft: '#FAF7EC', strong: '#EEF4D8',
  lime: '#DCEBB1', border: '#D8DDC8', terra: '#D96B42', gold: '#EAC64A', goldSoft: '#F8E4B2', sage: '#B9CDA8', wood: '#B88A5E', woodDark: '#8A5A36', fur: '#F6EEDC', furShade: '#E6D7B8' };
const boil = K => Math.floor(K.t * 8);
let paperC, washes = {};

// ---------- Pompón ----------
// Local: pies en y=0, ~215 u de alto. pose: hop, sx, sy, rot, look, eyes ('dot'|'happy'|'closed'|'wide'), mouth ('smile'|'o'), armL, armR (grados, + = arriba), hat, shake
const SP = (() => { const r = L.rng(11), out = []; for (let i = 0; i < 26; i++) { const a = Math.PI * (0.06 + 0.88 * i / 25); out.push({ a, len: 26 + r() * 26, w: 7 + r() * 4, ph: r() * 6 }); } return out; })();
const TUFTS = (() => { const r = L.rng(5), out = []; for (let i = 0; i < 22; i++) { const a = r() * Math.PI * 2, d = Math.sqrt(r()) * 0.8; out.push([Math.cos(a) * 88 * d, -112 + Math.sin(a) * 80 * d, (r() - 0.5) * 1.2]); } return out; })();
export function drawPompon(ctx, F, S, P = {}, t = 0) {
  const lk = P.look || [0, 0], sway = (P.shake || 0) * Math.sin(t * 30);
  ctx.lineJoin = ctx.lineCap = 'round';
  // piecitos
  for (const fx of [-34, 34]) { const f = new Path2D(); f.ellipse(fx, -8, 22, 12, 0, 0, 7); F(f, Z.furShade); S(f, 0.8); }
  // cuerpo esponjoso (borde con bultitos)
  const body = new Path2D(), N = 22;
  for (let i = 0; i <= N; i++) { const a0 = i / N * Math.PI * 2, a1 = (i + 0.5) / N * Math.PI * 2, r0 = 1, r1 = 1.07;
    const x0 = sway * 3 + Math.cos(a0) * 96 * r0, y0 = -118 + Math.sin(a0) * 90 * r0, x1 = sway * 3 + Math.cos(a1) * 96 * r1, y1 = -118 + Math.sin(a1) * 90 * r1;
    if (i === 0) body.moveTo(x0, y0); else body.quadraticCurveTo(xp, yp, x0, y0); var xp = x1, yp = y1; }
  body.closePath(); F(body, Z.fur); S(body, 1.1);
  // mechoncitos de textura arriba
  ctx.save(); ctx.globalAlpha = 0.35; for (const [x, y, a] of TUFTS) { if (y > -150 || Math.abs(x) < 46 && y > -175) continue; const p = new Path2D(); p.moveTo(x - 6, y - 6); p.quadraticCurveTo(x + Math.sin(a) * 6, y + 2, x + 2, y + 10); S(p, 0.45); } ctx.restore();
  // la «melena»: filas de espinas que cuelgan sobre la mitad de abajo
  [[-84, 22, 0.55], [-58, 30, 0.7], [-32, 38, 0.85]].forEach(([ry, len, wk], j) => {
    const half = 96 * Math.sqrt(Math.max(0, 1 - ((ry + 118) / 92) ** 2)) * 0.96, n = Math.max(2, Math.round(half / 13));
    for (let i = 0; i <= n * 2; i++) { const bx = sway * 3 + -half + i * (half / n), ph = i * 1.7 + j, sw = Math.sin(t * 2.2 + ph) * 2.5 + sway * 5, l = len + ((i * 7 + j * 3) % 5) * 3, w = 9 * wk + 3, p = new Path2D();
      p.moveTo(bx - w, ry - 4); p.quadraticCurveTo(bx - w * 0.7, ry + l * 0.7, bx - w * 0.3 + sw, ry + l); p.quadraticCurveTo(bx + sw, ry + l + w * 0.7, bx + w * 0.3 + sw, ry + l); p.quadraticCurveTo(bx + w * 0.7, ry + l * 0.7, bx + w, ry - 4);
      const edge = new Path2D(p); p.closePath(); ctx.fillStyle = j === 2 ? '#F0E4CB' : j === 1 ? '#F5ECDA' : '#F8F1E3'; ctx.fill(p); ctx.save(); ctx.strokeStyle = 'rgba(35,48,31,.3)'; ctx.lineWidth = 2.2; ctx.stroke(edge); ctx.restore(); } });
  // bracitos (por delante)
  const arm = (side, deg) => { const sx = side * 84, sy = -118, a = side * (-(deg || 0) * Math.PI / 180) + side * 0.5; ctx.save(); ctx.translate(sx, sy); ctx.rotate(a); const p = new Path2D(); p.roundRect(-12, 0, 24, 54, 12); F(p, Z.fur); S(p, 0.8); ctx.restore();
    return [sx - Math.sin(a) * 50, sy + Math.cos(a) * 50]; };
  const hL = arm(-1, P.armL), hR = arm(1, P.armR);
  // cara
  const ex = lk[0] * 6, ey = lk[1] * 6, es = P.eyes || 'dot';
  ctx.fillStyle = Z.ink; ctx.strokeStyle = Z.ink;
  for (const x0 of [-30, 30]) { const x = x0 + ex, y = -128 + ey; ctx.beginPath();
    if (es === 'happy') { ctx.lineWidth = 5.5; ctx.moveTo(x - 10, y + 3); ctx.quadraticCurveTo(x, y - 9, x + 10, y + 3); ctx.stroke(); continue; }
    if (es === 'closed') { ctx.lineWidth = 5; ctx.moveTo(x - 10, y); ctx.quadraticCurveTo(x, y + 7, x + 10, y); ctx.stroke(); continue; }
    const k = es === 'wide' ? 1.3 : 1; ctx.ellipse(x, y, 8 * k, 10 * k, 0, 0, 7); ctx.fill(); ctx.save(); ctx.fillStyle = '#fff'; ctx.beginPath(); ctx.arc(x + 2.5, y - 3.5, 2.8 * k, 0, 7); ctx.fill(); ctx.restore(); }
  ctx.save(); ctx.globalAlpha = 0.35; ctx.fillStyle = Z.terra; for (const x of [-56, 56]) { ctx.beginPath(); ctx.ellipse(x + ex, -100 + ey, 15, 9, 0, 0, 7); ctx.fill(); } ctx.restore();
  { const m = new Path2D(); if (P.mouth === 'o') m.ellipse(ex, -96 + ey, 8, 10, 0, 0, 7); else { m.moveTo(-12 + ex, -102 + ey); m.quadraticCurveTo(ex, -90 + ey, 12 + ex, -102 + ey); } S(m, 0.9); }
  // gorrito de chef
  if (P.hat) { const h = new Path2D(); h.moveTo(-46, -196); h.bezierCurveTo(-78, -206, -74, -262, -40, -258); h.bezierCurveTo(-34, -292, 30, -294, 38, -260); h.bezierCurveTo(76, -264, 80, -206, 46, -196); h.closePath(); F(h, '#FFFFFF'); S(h);
    const b = new Path2D(); b.roundRect(-48, -206, 96, 26, 6); F(b, '#FFFFFF'); S(b, 0.9); }
  return { handL: hL, handR: hR, top: [0, P.hat ? -290 : -208], center: [0, -118] };
}
export function pomponAt(K, x, y, hgt, P = {}) {
  const ctx = K.ctx, s = hgt / 215, wf = L.washFill(ctx, 6, { alpha: 0.35, gran: 0.08, pool: 0.22 });
  const F = (p, c) => { ctx.fillStyle = '#FFFEFA'; ctx.fill(p); wf(p, c); }, S = L.wobbleStroke(ctx, Z.ink, 4, s, boil(K));
  if (!P.noShadow) { ctx.save(); ctx.globalAlpha = 0.14 * (1 - L.clamp((P.hop || 0) / 250)); ctx.fillStyle = Z.ink; ctx.beginPath(); ctx.ellipse(x, y + 2, 100 * s, 13 * s, 0, 0, 7); ctx.fill(); ctx.restore(); }
  ctx.save(); ctx.translate(x, y - (P.hop || 0)); ctx.rotate(P.rot || 0); ctx.scale(s * (P.sx || 1) * (P.flip ? -1 : 1), s * (P.sy || 1));
  const a = drawPompon(ctx, F, S, P, K.t);
  const m = ctx.getTransform(), tc = ([u, v]) => [m.a * u + m.c * v + m.e, m.b * u + m.d * v + m.f];
  ctx.restore();
  return { handL: tc(a.handL), handR: tc(a.handR), top: tc(a.top), center: tc(a.center), s };
}

export default {
  ...acuarela, id: 'zeta', name: 'Zeta acuarela',
  fonts: 'Fraunces:ital,opsz,wght@0,9..144,600;0,9..144,700;1,9..144,600&family=Poppins:wght@500;600;800&family=Caveat:wght@600;700',
  fontLoads: ['600 60px Fraunces', '700 60px Fraunces', 'italic 600 60px Fraunces', '500 30px Poppins', '600 30px Poppins', '800 30px Poppins', '700 40px Caveat'],
  palette: { bg: Z.cream, ink: Z.ink, accent: Z.green, muted: Z.muted, panel: Z.surface, line: 'rgba(35,48,31,.18)', good: Z.green, bad: Z.terra, mascot: Z.fur },
  type: { display: s => `600 ${s}px Fraunces`, em: s => `italic 600 ${s}px Fraunces`, body: s => `500 ${s}px Poppins`, bodyEm: s => `600 ${s}px Poppins`, label: s => `700 ${s}px Caveat`, mono: s => `500 ${s}px Poppins`, hand: s => `700 ${s}px Caveat` },
  lh: 1.04, ls: -0.01, sfx: 'zeta', transDur: 0.6, push: 0.02,
  async setup(K) {
    const { W, H } = K;
    paperC = L.canvas(W, H); L.paper(paperC.getContext('2d'), Z.cream, { seed: 9, grain: 0.045, blotch: 0.05 });
    const vars = { cream: [Z.strong, Z.goldSoft, '#F6F0DC'], sage: [Z.lime, Z.strong, '#E6EFD0'], sun: [Z.goldSoft, Z.lime, '#F7E9C6'], terra: ['#F7D7BF', Z.strong, '#FBE7D6'] };
    for (const [k, cols] of Object.entries(vars)) {
      const c = L.canvas(W, H), x = c.getContext('2d'); x.drawImage(paperC, 0, 0);
      L.watercolor(x, L.rectPoly(-80, -60, W + 160, H * 0.7), cols[0], { seed: 3 + k.length, layers: 26, alpha: 0.03, amp: 0.2 });
      L.watercolor(x, L.circlePoly(W * 0.7, H * 0.28, W * 0.42, H * 0.2), cols[2], { seed: 6 + k.length, layers: 22, alpha: 0.04 });
      washes[k] = c;
    }
  },
  background(K, s) { K.ctx.drawImage(washes[s.d.bg || 'cream'] || paperC, 0, 0); },
  headline(K, str, box, p, s, o = {}) { title(K, str, box, p, { align: o.align || 'center', size: o.size, color: o.color || Z.deep, emColor: o.emColor || Z.green, reveal: 'wipe', emStyle: 'highlight', emBg: o.emBg || 'rgba(234,198,74,.30)', valign: 'middle', max: o.max }); },
  mascot(K, box, p, s, mood) { if (p <= 0) return; const P = L.POSE || {}; const a = pomponAt(K, box.x + box.w / 2, box.y + box.h, box.h, P); Object.assign(L.MLAST, { handL: a.handL, handR: a.handR, top: a.top, center: a.center, s: a.s }); },
  caption(K, words, act, box, p) {
    const { ctx, u } = K; ctx.save(); ctx.globalAlpha = p; ctx.font = K.S.type.bodyEm(37 * u); ctx.textAlign = 'left'; ctx.letterSpacing = '0px';
    const w = ctx.measureText(words.join(' ')).width + 76 * u, x = box.x + (box.w - w) / 2;
    L.rrect(ctx, x, box.y, w, box.h, box.h / 2); ctx.fillStyle = 'rgba(40,75,46,.9)'; ctx.fill();
    let cx = x + 38 * u; words.forEach((wd, i) => { ctx.fillStyle = i <= act ? Z.lime : 'rgba(255,253,244,.62)'; ctx.fillText(wd, cx, box.y + box.h * 0.66); cx += ctx.measureText(wd + ' ').width; });
    ctx.restore();
  },
  overlay(K) { const ctx = K.ctx; ctx.save(); ctx.globalCompositeOperation = 'multiply'; ctx.globalAlpha = 0.28; ctx.drawImage(paperC, 0, 0); ctx.restore(); },
};
