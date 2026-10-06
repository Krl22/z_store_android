// Mascota de Zeta Dorada: hongo de sombrero dorado brillante, cuerpo crema gordito con carita, en acuarela.
// Referencia de Carlos: sombrero en cúpula con lunares claros y brillos, laminillas visibles, corona, capa dorada,
// pañuelo verde con estrella y botitas doradas. Diseño propio (nada de personajes con derechos de autor).
// Local: pies en y=0, ~275 u hasta la cúpula (la corona sube más). pose: hop, sx, sy, rot, flip, look,
// eyes ('dot'|'happy'|'closed'|'wide'), mouth ('open'|'smile'|'o'), armL, armR (grados, + = arriba), shake, wand, outfit.
import * as L from '../engine/lib.js';
import { Z } from './zeta.js';
export { Z };

const boil = K => Math.floor(K.t * 8);
export const SH = { gold: '#E9B43A', goldDeep: '#C98A20', goldLight: '#FFEFA0', spot: '#FBE08A', body: '#FBF1DD', bodyShade: '#EAD6B0', gill: '#F1DFB8',
  line: '#6B4521', blush: '#F4A9A0', eye: '#3A2618', mouth: '#8B3A2A', kraft: '#CDA678', straw: '#E6C681', boot: '#EDB93F' };
export const OUTFITS = ['rey', 'natural', 'chef', 'jardinero', 'chullo', 'repartidor'];
const CAP = { rx: 126, ry: 16, cy: -166, top: -284 }, NECK = -50;

const rr = (x, y, w, h, r) => { const p = new Path2D(); p.roundRect(x, y, w, h, r); return p; };
const ell = (x, y, rx, ry, a = 0) => { const p = new Path2D(); p.ellipse(x, y, rx, ry, a, 0, 7); return p; };
function star(ctx, x, y, r, n = 5, k = 0.45) { ctx.beginPath(); for (let i = 0; i < n * 2; i++) { const a = -Math.PI / 2 + i * Math.PI / n, q = i % 2 ? r * k : r; ctx.lineTo(x + Math.cos(a) * q, y + Math.sin(a) * q); } ctx.closePath(); }

export function drawShroomy(ctx, F, S, P = {}, t = 0) {
  const o = P.outfit || 'rey', lk = P.look || [0, 0], sway = (P.shake || 0) * Math.sin(t * 30) * 3;
  ctx.lineJoin = ctx.lineCap = 'round';
  // capa (detrás de todo)
  if (o === 'rey') { const fl = Math.sin(t * 2.4) * 5, c = new Path2D();
    c.moveTo(-46, -128); c.quadraticCurveTo(-92 - fl, -70, -98 - fl, -8); c.quadraticCurveTo(-50, 2, 0, 0); c.quadraticCurveTo(50, 2, 98 + fl, -8); c.quadraticCurveTo(92 + fl, -70, 46, -128); c.closePath();
    F(c, SH.gold); ctx.save(); ctx.clip(c); ctx.globalAlpha = 0.35; ctx.fillStyle = SH.goldDeep; ctx.fillRect(-120, -60, 240, 70); ctx.restore(); S(c, 0.9); }
  const { rx, ry, cy, top } = CAP;
  // cuerpo gordito
  // cuerpo: la parte de arriba queda dentro del sombrero
  const body = new Path2D(); body.moveTo(-60, -205); body.bezierCurveTo(-20, -212, 20, -212, 60, -205); body.bezierCurveTo(80, -150, 84, -80, 76, -46); body.bezierCurveTo(70, -12, 42, -3, 0, -3);
  body.bezierCurveTo(-42, -3, -70, -12, -76, -46); body.bezierCurveTo(-84, -80, -80, -150, -60, -205); body.closePath();
  F(body, SH.body); ctx.save(); ctx.clip(body); ctx.globalAlpha = 0.45; ctx.fillStyle = SH.bodyShade; ctx.beginPath(); ctx.ellipse(34, -10, 70, 50, 0, 0, 7); ctx.fill();
  { const sh = ctx.createLinearGradient(0, CAP.cy + CAP.ry - 2, 0, CAP.cy + CAP.ry + 22); sh.addColorStop(0, 'rgba(150,110,50,.45)'); sh.addColorStop(1, 'rgba(150,110,50,0)'); ctx.globalAlpha = 1; ctx.fillStyle = sh; ctx.fillRect(-90, CAP.cy + CAP.ry - 4, 180, 30); }
  ctx.restore(); S(body, 1);
  // pies o botitas
  for (const fx of [-30, 30]) {
    if (o === 'rey') { const b = ell(fx, -4, 27, 15); F(b, SH.boot); S(b, 0.8); const cf = new Path2D(); cf.moveTo(fx - 22, -12); cf.quadraticCurveTo(fx, -4, fx + 22, -12); ctx.save(); ctx.lineWidth = 6; ctx.strokeStyle = SH.goldLight; ctx.stroke(cf); ctx.restore(); }
    else { const f = ell(fx, -7, 23, 12); F(f, SH.bodyShade); S(f, 0.8); } }
  // ropa del cuerpo
  clothes(ctx, F, S, o, t);
  // sombrero dorado (cúpula brillante con lunares claros)
  const cap = new Path2D(); cap.moveTo(sway - rx, cy); cap.bezierCurveTo(sway - rx - 6, cy - 84, sway - rx * 0.55, top, sway, top); cap.bezierCurveTo(sway + rx * 0.55, top, sway + rx + 6, cy - 84, sway + rx, cy);
  cap.ellipse(sway, cy, rx, ry, 0, 0, Math.PI, false); cap.closePath();
  F(cap, SH.gold);
  ctx.save(); ctx.clip(cap);
  const gr = ctx.createRadialGradient(sway - 45, top + 40, 10, sway - 20, top + 70, 190); gr.addColorStop(0, SH.goldLight); gr.addColorStop(0.45, '#F2C64A'); gr.addColorStop(1, SH.goldDeep);
  ctx.globalAlpha = 0.85; ctx.fillStyle = gr; ctx.fillRect(sway - rx - 20, top - 10, rx * 2 + 40, cy - top + 40);
  ctx.globalAlpha = 0.95; ctx.globalAlpha = 0.7; for (const [x, y, a, b] of [[-62, -222, 15, 10], [8, -262, 11, 7], [60, -226, 16, 10], [96, -192, 8, 6], [-92, -190, 8, 6]]) { const sp = ell(sway + x, y, a, b, 0.2); ctx.fillStyle = SH.spot; ctx.fill(sp); }
  ctx.globalAlpha = 0.65; ctx.fillStyle = '#FFFFFF'; ctx.beginPath(); ctx.ellipse(sway - 52, top + 34, 28, 11, -0.55, 0, 7); ctx.fill(); ctx.beginPath(); ctx.arc(sway - 18, top + 22, 5, 0, 7); ctx.fill();
  ctx.restore();
  { const rim = new Path2D(); rim.ellipse(sway, cy, rx, ry, 0, 0.05, Math.PI - 0.05); ctx.save(); ctx.lineWidth = 9; ctx.strokeStyle = SH.goldDeep; ctx.globalAlpha = 0.85; ctx.stroke(rim); ctx.restore(); }
  S(cap, 1.1);
  // brillitos
  ctx.save(); ctx.fillStyle = '#FFFFFF'; for (const [x, y, r, ph] of [[42, -270, 10, 0], [-96, -204, 8, 2], [104, -230, 7, 4]]) { ctx.globalAlpha = 0.55 + 0.45 * Math.sin(t * 4 + ph); star(ctx, sway + x, y, r, 4, 0.3); ctx.fill(); } ctx.restore();
  // varita con un honguito (debajo de la mano)
  const ay = -100, armAt = (side, deg) => { const sx = side * 60, a = side * (-(deg || 0) * Math.PI / 180) + side * 0.5; return { sx, a, hand: [sx - Math.sin(a) * 46, ay + Math.cos(a) * 46] }; };
  const AL = armAt(-1, P.armL), AR = armAt(1, P.armR);
  if (P.wand) { const [hx, hy] = AR.hand, st = new Path2D(); st.moveTo(hx, hy + 14); st.lineTo(hx + 10, hy - 74); ctx.save(); ctx.lineWidth = 7; ctx.strokeStyle = SH.line; ctx.stroke(st); ctx.restore();
    const mc = new Path2D(); mc.moveTo(hx - 14, hy - 72); mc.quadraticCurveTo(hx + 10, hy - 112, hx + 34, hy - 76); mc.closePath(); F(mc, SH.gold); S(mc, 0.7);
    ctx.save(); ctx.globalAlpha = 0.6 + 0.4 * Math.sin(t * 5); ctx.fillStyle = '#FFFFFF'; star(ctx, hx + 36, hy - 104, 9, 4, 0.3); ctx.fill(); ctx.restore(); }
  // bracitos
  for (const A of [AL, AR]) { ctx.save(); ctx.translate(A.sx, ay); ctx.rotate(A.a); const p = rr(-12, 0, 24, 44, 12); F(p, SH.body); S(p, 0.8); const hd = ell(0, 46, 14, 14); F(hd, SH.body); S(hd, 0.7); ctx.restore(); }
  // carita en el cuerpo
  const ex = sway + lk[0] * 7, ey = lk[1] * 6, blink = P.blink !== false && ((t + (P.seed || 0)) % 3.3) < 0.13, es = (P.eyes || 'dot') === 'dot' && blink ? 'closed' : (P.eyes || 'dot');
  ctx.fillStyle = SH.eye; ctx.strokeStyle = SH.eye;
  for (const x0 of [-30, 30]) { const x = x0 + ex, y = -114 + ey; ctx.beginPath();
    if (es === 'happy') { ctx.lineWidth = 6; ctx.moveTo(x - 11, y + 4); ctx.quadraticCurveTo(x, y - 10, x + 11, y + 4); ctx.stroke(); continue; }
    if (es === 'closed') { ctx.lineWidth = 5.5; ctx.moveTo(x - 11, y); ctx.quadraticCurveTo(x, y + 8, x + 11, y); ctx.stroke(); continue; }
    const m = es === 'wide' ? 1.2 : 1; ctx.ellipse(x, y, 12 * m, 15 * m, 0, 0, 7); ctx.fill(); ctx.save(); ctx.fillStyle = '#fff'; ctx.beginPath(); ctx.arc(x + 4, y - 5, 4.5 * m, 0, 7); ctx.fill(); ctx.beginPath(); ctx.arc(x - 3.5, y + 5, 2 * m, 0, 7); ctx.fill(); ctx.restore(); }
  { const br = (P.brow || 0) * 4 + (es === 'wide' ? 4 : 0); ctx.save(); ctx.globalAlpha = 0.75; ctx.strokeStyle = SH.eye; ctx.lineWidth = 4.5; ctx.lineCap = 'round';
    for (const s of [-1, 1]) { const x = s * 30 + ex, y = -136 + ey - br; ctx.beginPath(); ctx.moveTo(x - 9, y + 2); ctx.quadraticCurveTo(x, y - 4 - br * 0.3, x + 9, y + 2); ctx.stroke(); } ctx.restore(); }
  ctx.save(); ctx.globalAlpha = 0.55; ctx.fillStyle = SH.blush; for (const x of [-52, 52]) { ctx.beginPath(); ctx.ellipse(x + ex, -94 + ey, 14, 8, 0, 0, 7); ctx.fill(); } ctx.restore();
  { const my = -94 + ey, talking = typeof P.talk === 'number' && P.talk >= 0.08, mo = talking ? 'open' : (P.mouth || 'open'), k = talking ? 0.35 + 0.8 * P.talk : 1, mw = talking ? 12 + 6 * P.talk : 16, m = new Path2D();
    if (mo === 'o') { m.ellipse(ex, my + 4, 8, 10, 0, 0, 7); ctx.fillStyle = SH.mouth; ctx.fill(m); }
    else if (mo === 'smile') { m.moveTo(ex - 12, my); m.quadraticCurveTo(ex, my + 12, ex + 12, my); }
    else { m.moveTo(ex - mw, my - 2); m.quadraticCurveTo(ex, my + 24 * k, ex + mw, my - 2); m.closePath(); ctx.fillStyle = SH.mouth; ctx.fill(m);
      ctx.save(); ctx.clip(m); ctx.fillStyle = '#E98C86'; ctx.beginPath(); ctx.ellipse(ex, my + 14 * k, 9, 6, 0, 0, 7); ctx.fill(); ctx.restore(); }
    S(m, 0.8); }
  // ropa de la cabeza
  const hatTop = hats(ctx, F, S, o, sway, t);
  return { handL: AL.hand, handR: AR.hand, top: [0, hatTop], center: [0, -150] };
}

function clothes(ctx, F, S, o, t) {
  if (o === 'rey' || o === 'chullo') { // pañuelo verde con estrella dorada (rey) o chalina con flecos (chullo)
    const sc = new Path2D(); sc.moveTo(-66, NECK - 12); sc.quadraticCurveTo(0, NECK + 8, 66, NECK - 12); sc.lineTo(68, NECK + 4); sc.quadraticCurveTo(0, NECK + 26, -68, NECK + 4); sc.closePath(); F(sc, Z.green); S(sc, 0.8);
    if (o === 'rey') { for (const s of [-1, 1]) { const tip = new Path2D(); tip.moveTo(s * 60, NECK - 10); tip.lineTo(s * 96, NECK + 18 + Math.sin(t * 3 + s) * 3); tip.lineTo(s * 54, NECK + 6); tip.closePath(); F(tip, Z.green); S(tip, 0.7); }
      ctx.save(); star(ctx, 0, NECK + 8, 16); ctx.fillStyle = SH.boot; ctx.fill(); ctx.restore(); const sp = new Path2D(); for (let i = 0; i < 10; i++) { const a = -Math.PI / 2 + i * Math.PI / 5, q = i % 2 ? 7.2 : 16; sp[i ? 'lineTo' : 'moveTo'](Math.cos(a) * q, NECK + 8 + Math.sin(a) * q); } sp.closePath(); S(sp, 0.6); }
    else { const tail = rr(22, NECK, 26, 58, 8); F(tail, Z.green); S(tail, 0.7); ctx.save(); ctx.strokeStyle = SH.boot; ctx.lineWidth = 4; for (const y of [NECK + 20, NECK + 36]) { ctx.beginPath(); ctx.moveTo(25, y); ctx.lineTo(45, y); ctx.stroke(); } ctx.restore(); } }
  if (o === 'chef') { const a = rr(-50, -50, 100, 42, 10); F(a, '#FFFFFF'); S(a, 0.8); const ti = new Path2D(); ti.moveTo(-50, -46); ti.lineTo(-66, -56); ti.moveTo(50, -46); ti.lineTo(66, -56); S(ti, 0.6); }
  if (o === 'jardinero') { const a = rr(-52, -56, 104, 50, 12); F(a, Z.green); S(a, 0.8); ctx.fillStyle = SH.boot; for (const x of [-38, 38]) { ctx.beginPath(); ctx.arc(x, -48, 5, 0, 7); ctx.fill(); } const pk = rr(-16, -42, 32, 22, 5); F(pk, Z.deep); S(pk, 0.5); }
  if (o === 'repartidor') { const p = new Path2D(); p.moveTo(-62, -66); p.lineTo(46, -22); ctx.save(); ctx.lineWidth = 8; ctx.strokeStyle = Z.deep; ctx.stroke(p); ctx.restore(); const b = rr(26, -40, 56, 44, 8); F(b, SH.kraft); S(b, 0.8); const lb = rr(36, -28, 36, 16, 4); ctx.fillStyle = Z.green; ctx.fill(lb); }
}

function hats(ctx, F, S, o, x, t) {
  const { rx, top } = CAP;
  if (o === 'rey') { ctx.save(); ctx.translate(x + 36, top + 8); ctx.rotate(0.22); ctx.scale(0.72, 0.72);
    const c = new Path2D(); c.moveTo(-36, 0); c.lineTo(-38, -40); c.lineTo(-18, -20); c.lineTo(0, -50); c.lineTo(18, -20); c.lineTo(38, -40); c.lineTo(36, 0); c.closePath(); F(c, SH.boot); S(c, 0.9);
    const b = rr(-38, -6, 76, 14, 5); F(b, SH.goldLight); S(b, 0.7); for (const [px, py] of [[-38, -40], [0, -50], [38, -40]]) { const ball = ell(px, py - 4, 6, 6); F(ball, SH.goldLight); S(ball, 0.5); }
    ctx.fillStyle = '#EAF4F2'; ctx.beginPath(); ctx.moveTo(0, -24); ctx.lineTo(8, -14); ctx.lineTo(0, -4); ctx.lineTo(-8, -14); ctx.closePath(); ctx.fill();
    { const g = (t % 2.6) / 0.6; if (g < 1) { ctx.globalAlpha = Math.sin(g * Math.PI); ctx.fillStyle = '#FFFFFF'; star(ctx, -34 + 68 * g, -26 - Math.sin(g * Math.PI) * 10, 12, 4, 0.28); ctx.fill(); ctx.globalAlpha = 1; } }
    ctx.restore(); return top - 56; }
  if (o === 'chef') { const b = top + 20, h = new Path2D(); h.moveTo(x - 46, b); h.bezierCurveTo(x - 80, b - 10, x - 76, b - 70, x - 40, b - 66); h.bezierCurveTo(x - 34, b - 100, x + 34, b - 102, x + 42, b - 68); h.bezierCurveTo(x + 78, b - 72, x + 82, b - 10, x + 46, b); h.closePath(); F(h, '#FFFFFF'); S(h);
    const bd = rr(x - 48, b - 12, 96, 26, 6); F(bd, '#FFFFFF'); S(bd, 0.9); return b - 100; }
  if (o === 'jardinero') { const y = top + 30, br = ell(x, y, rx * 0.9, 20); F(br, SH.straw); S(br);
    const cr = new Path2D(); cr.moveTo(x - rx * 0.45, y); cr.bezierCurveTo(x - rx * 0.47, y - 64, x + rx * 0.47, y - 64, x + rx * 0.45, y); cr.closePath(); F(cr, SH.straw); S(cr);
    const band = new Path2D(); band.moveTo(x - rx * 0.45, y - 4); band.quadraticCurveTo(x, y - 12, x + rx * 0.45, y - 4); ctx.save(); ctx.lineWidth = 11; ctx.strokeStyle = Z.green; ctx.stroke(band); ctx.restore(); return y - 60; }
  if (o === 'chullo') { const y = top + 50, cp = new Path2D(); cp.moveTo(x - rx * 0.66, y); cp.bezierCurveTo(x - rx * 0.68, top - 30, x + rx * 0.68, top - 30, x + rx * 0.66, y); cp.closePath(); F(cp, Z.deep); S(cp);
    ctx.save(); ctx.clip(cp); ctx.lineWidth = 6; ctx.strokeStyle = SH.body; ctx.beginPath(); for (let i = 0; i <= 14; i++) ctx.lineTo(x - rx * 0.7 + i * rx * 0.1, y - 24 - (i % 2) * 14); ctx.stroke();
    ctx.strokeStyle = SH.boot; ctx.beginPath(); for (let i = 0; i <= 14; i++) ctx.lineTo(x - rx * 0.7 + i * rx * 0.1, top + 4 - (i % 2) * 12); ctx.stroke(); ctx.restore(); S(cp);
    for (const s of [-1, 1]) { const f = new Path2D(); f.moveTo(x + s * rx * 0.66, y - 6); f.lineTo(x + s * rx * 0.68, y + 34); f.lineTo(x + s * rx * 0.46, y - 2); f.closePath(); F(f, Z.deep); S(f, 0.7);
      const pm = ell(x + s * rx * 0.68, y + 44, 9, 9); F(pm, SH.boot); S(pm, 0.5); }
    const pp = ell(x, top - 30, 18, 18); F(pp, SH.boot); S(pp, 0.7); return top - 48; }
  if (o === 'repartidor') { const y = top + 34, cp = new Path2D(); cp.moveTo(x - rx * 0.54, y); cp.bezierCurveTo(x - rx * 0.56, top - 26, x + rx * 0.56, top - 26, x + rx * 0.54, y); cp.closePath(); F(cp, Z.green); S(cp);
    const v = new Path2D(); v.moveTo(x + rx * 0.2, y - 2); v.quadraticCurveTo(x + rx * 0.75, y - 10, x + rx * 0.92, y + 8); v.quadraticCurveTo(x + rx * 0.55, y + 14, x + rx * 0.2, y + 6); v.closePath(); F(v, Z.deep); S(v, 0.8);
    L.text(ctx, 'Z', x - rx * 0.1, y - 22, { font: '700 34px Fraunces', color: SH.boot, align: 'center' }); return top - 26; }
  return top;
}

export function shroomyAt(K, x, y, hgt, P = {}) {
  const ctx = K.ctx, s = hgt / 275, wf = L.washFill(ctx, 6, { alpha: 0.35, gran: 0.08, pool: 0.22 });
  const F = (p, c) => { ctx.fillStyle = '#FFFEFA'; ctx.fill(p); wf(p, c); }, S = L.wobbleStroke(ctx, SH.line, 4, s, boil(K));
  if (!P.noShadow) { ctx.save(); ctx.globalAlpha = 0.14 * (1 - L.clamp((P.hop || 0) / 250)); ctx.fillStyle = Z.ink; ctx.beginPath(); ctx.ellipse(x, y + 2, 105 * s, 13 * s, 0, 0, 7); ctx.fill(); ctx.restore(); }
  ctx.save(); ctx.translate(x, y - (P.hop || 0)); ctx.rotate(P.rot || 0); ctx.scale(s * (P.sx || 1) * (P.flip ? -1 : 1), s * (P.sy || 1));
  const a = drawShroomy(ctx, F, S, P, K.t);
  const m = ctx.getTransform(), tc = ([u, v]) => [m.a * u + m.c * v + m.e, m.b * u + m.d * v + m.f];
  ctx.restore();
  return { handL: tc(a.handL), handR: tc(a.handR), top: tc(a.top), center: tc(a.center), s };
}
