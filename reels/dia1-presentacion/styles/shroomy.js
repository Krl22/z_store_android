// Mascota de Zeta Dorada: un champiñón de sombrero dorado, robusto y redondo, con carita en el sombrero.
// Forma de champiñón a propósito: nada de sombrero cónico con tallo delgado (no debe parecer un hongo no comestible),
// sin puntos en el sombrero (ni Amanita ni personajes con derechos de autor).
// Local: pies en y=0, ~235 u de alto. pose (igual que Pompón): hop, sx, sy, rot, flip, look, eyes ('dot'|'happy'|'closed'|'wide'),
// mouth ('smile'|'o'|'grin'), armL, armR (grados, + = arriba), shake, shape ('clasico'|'portobello'|'bebe'), outfit.
import * as L from '../engine/lib.js';
import { Z } from './zeta.js';
export { Z };

const boil = K => Math.floor(K.t * 8);
export const SH = { gold: '#E8BD47', goldDeep: '#C9962B', goldLight: '#FBE9A6', stem: '#FBF4E2', stemShade: '#EADFC6', gill: '#EBDCB9', straw: '#E6C681', kraft: '#CDA678' };
const SHAPES = {
  clasico: { rx: 112, top: -238, bot: -104, sw: 44 },
  portobello: { rx: 140, top: -206, bot: -100, sw: 40 },
  bebe: { rx: 100, top: -226, bot: -84, sw: 48 },
};
export const OUTFITS = ['normal', 'chef', 'jardinero', 'chullo', 'repartidor', 'elegante'];

const rr = (x, y, w, h, r) => { const p = new Path2D(); p.roundRect(x, y, w, h, r); return p; };
const ell = (x, y, rx, ry, a = 0) => { const p = new Path2D(); p.ellipse(x, y, rx, ry, a, 0, 7); return p; };

export function drawShroomy(ctx, F, S, P = {}, t = 0) {
  const g = SHAPES[P.shape] || SHAPES.clasico, { rx, top, bot, sw } = g, o = P.outfit || 'normal';
  const lk = P.look || [0, 0], sway = (P.shake || 0) * Math.sin(t * 30) * 3;
  ctx.lineJoin = ctx.lineCap = 'round';
  // piecitos
  for (const fx of [-sw * 0.6, sw * 0.6]) F(ell(fx, -7, 21, 11), SH.stemShade), S(ell(fx, -7, 21, 11), 0.8);
  // tallo-cuerpo: barrilito
  const st = new Path2D(); st.moveTo(-sw * 0.92, -2); st.bezierCurveTo(-sw * 1.18, bot * 0.4, -sw * 1.08, bot * 0.8, -sw * 0.82, bot - 4);
  st.lineTo(sw * 0.82, bot - 4); st.bezierCurveTo(sw * 1.08, bot * 0.8, sw * 1.18, bot * 0.4, sw * 0.92, -2); st.quadraticCurveTo(0, 8, -sw * 0.92, -2); st.closePath();
  F(st, SH.stem); S(st, 1);
  // ropa del cuerpo (debajo de los brazos)
  body(ctx, F, S, o, g);
  // laminillas bajo el borde
  const gl = ell(sway, bot + 6, rx * 0.84, 17); F(gl, SH.gill); S(gl, 0.6);
  ctx.save(); ctx.clip(gl); ctx.globalAlpha = 0.35; for (let k = -10; k <= 10; k++) { const p = new Path2D(); p.moveTo(sway + k * rx * 0.02, bot + 4); p.lineTo(sway + k * rx * 0.085, bot + 24); S(p, 0.4); } ctx.restore();
  // sombrero dorado (cúpula lisa)
  const cap = new Path2D(), h = bot - top;
  cap.moveTo(sway - rx, bot); cap.bezierCurveTo(sway - rx * 1.03, bot - h * 0.78, sway - rx * 0.56, top, sway, top);
  cap.bezierCurveTo(sway + rx * 0.56, top, sway + rx * 1.03, bot - h * 0.78, sway + rx, bot);
  cap.quadraticCurveTo(sway + rx * 0.98, bot + 14, sway + rx * 0.6, bot + 13); cap.quadraticCurveTo(sway, bot + 20, sway - rx * 0.6, bot + 13); cap.quadraticCurveTo(sway - rx * 0.98, bot + 14, sway - rx, bot); cap.closePath();
  const cream = P.tone === 'crema', cc = cream ? { base: '#F4EAD0', deep: '#C9B48A', light: '#FFFDF4' } : { base: SH.gold, deep: SH.goldDeep, light: SH.goldLight };
  F(cap, cc.base);
  ctx.save(); ctx.clip(cap);
  ctx.globalAlpha = 0.32; ctx.fillStyle = cc.deep; ctx.beginPath(); ctx.ellipse(sway, bot + 26, rx * 1.15, h * 0.42, 0, 0, 7); ctx.fill();
  ctx.globalAlpha = 0.75; ctx.fillStyle = cc.light; ctx.beginPath(); ctx.ellipse(sway - rx * 0.42, top + h * 0.3, rx * 0.26, h * 0.12, -0.5, 0, 7); ctx.fill();
  ctx.globalAlpha = 0.9; ctx.beginPath(); ctx.arc(sway - rx * 0.12, top + h * 0.17, 6, 0, 7); ctx.fill();
  if (cream) { ctx.globalAlpha = 1; ctx.strokeStyle = SH.gold; ctx.lineWidth = 16; ctx.beginPath(); ctx.moveTo(sway - rx, bot); ctx.quadraticCurveTo(sway - rx * 0.98, bot + 14, sway - rx * 0.6, bot + 13); ctx.quadraticCurveTo(sway, bot + 20, sway + rx * 0.6, bot + 13); ctx.quadraticCurveTo(sway + rx * 0.98, bot + 14, sway + rx, bot); ctx.stroke(); }
  ctx.restore(); S(cap, 1.1);
  if (cream && !P.outfit || cream && P.outfit === 'normal') { ctx.save(); ctx.fillStyle = SH.gold; L.star(ctx, sway + rx * 0.3, top + h * 0.2, 16, 4, 0.38); ctx.fill(); ctx.restore(); }
  // brazos
  const ay = bot * 0.62, arm = (side, deg) => { const sx = side * sw * 1.02, a = side * (-(deg || 0) * Math.PI / 180) + side * 0.45; ctx.save(); ctx.translate(sx, ay); ctx.rotate(a);
    const p = rr(-11, 0, 22, 48, 11); F(p, sleeve(o)); S(p, 0.8); const hd = ell(0, 48, 13, 13); F(hd, SH.stem); S(hd, 0.7); ctx.restore(); return [sx - Math.sin(a) * 52, ay + Math.cos(a) * 52]; };
  const hL = arm(-1, P.armL), hR = arm(1, P.armR);
  // carita en el sombrero
  const fy = bot - h * 0.36, k = rx / 112, ex = sway + lk[0] * 7, ey = fy + lk[1] * 6, es = P.eyes || 'dot';
  ctx.fillStyle = Z.ink; ctx.strokeStyle = Z.ink;
  for (const x0 of [-32 * k, 32 * k]) { const x = x0 + ex, y = ey; ctx.beginPath();
    if (es === 'happy') { ctx.lineWidth = 5.5; ctx.moveTo(x - 10, y + 3); ctx.quadraticCurveTo(x, y - 9, x + 10, y + 3); ctx.stroke(); continue; }
    if (es === 'closed') { ctx.lineWidth = 5; ctx.moveTo(x - 10, y); ctx.quadraticCurveTo(x, y + 7, x + 10, y); ctx.stroke(); continue; }
    const m = es === 'wide' ? 1.3 : 1; ctx.ellipse(x, y, 8 * m, 10 * m, 0, 0, 7); ctx.fill(); ctx.save(); ctx.fillStyle = '#fff'; ctx.beginPath(); ctx.arc(x + 2.5, y - 3.5, 2.8 * m, 0, 7); ctx.fill(); ctx.restore(); }
  ctx.save(); ctx.globalAlpha = 0.3; ctx.fillStyle = Z.terra; for (const x of [-60 * k, 60 * k]) { ctx.beginPath(); ctx.ellipse(x + ex, ey + 22, 15, 9, 0, 0, 7); ctx.fill(); } ctx.restore();
  { const m = new Path2D(), my = ey + 20; if (P.mouth === 'o') m.ellipse(ex, my + 4, 8, 10, 0, 0, 7); else if (P.mouth === 'grin') { m.moveTo(ex - 15, my); m.quadraticCurveTo(ex, my + 20, ex + 15, my); m.closePath(); ctx.fillStyle = '#7A3B2A'; ctx.fill(m); } else { m.moveTo(ex - 12, my); m.quadraticCurveTo(ex, my + 12, ex + 12, my); } S(m, 0.9); }
  // ropa de la cabeza (encima del sombrero)
  const hatTop = head(ctx, F, S, o, g, sway);
  return { handL: hL, handR: hR, top: [0, hatTop], center: [0, (top + bot) / 2] };
}

function sleeve(o) { return { jardinero: Z.green, repartidor: Z.green, chullo: SH.stem }[o] || SH.stem; }

function body(ctx, F, S, o, g) {
  const { bot, sw } = g;
  if (o === 'chef') { const a = rr(-sw * 0.78, bot * 0.72, sw * 1.56, -bot * 0.66, 10); F(a, '#FFFFFF'); S(a, 0.8); const pk = rr(-14, bot * 0.42, 28, 18, 4); S(pk, 0.5); }
  if (o === 'jardinero') { const a = rr(-sw * 0.8, bot * 0.62, sw * 1.6, -bot * 0.62 - 4, 12); F(a, Z.green); S(a, 0.8);
    for (const s of [-1, 1]) { const p = new Path2D(); p.moveTo(s * sw * 0.6, bot * 0.62); p.lineTo(s * sw * 0.55, bot + 2); ctx.save(); ctx.lineWidth = 9; ctx.strokeStyle = Z.green; ctx.stroke(p); ctx.restore(); ctx.fillStyle = Z.gold; ctx.beginPath(); ctx.arc(s * sw * 0.6, bot * 0.6, 5, 0, 7); ctx.fill(); }
    const pk = rr(-16, bot * 0.5, 32, 22, 5); F(pk, Z.deep); S(pk, 0.5); }
  if (o === 'chullo') { // chalina verde con flecos
    const sc = rr(-sw * 0.95, bot + 22, sw * 1.9, 24, 12); F(sc, Z.green); S(sc, 0.8); const tail = rr(sw * 0.2, bot + 34, 24, 56, 8); F(tail, Z.green); S(tail, 0.7);
    ctx.save(); ctx.strokeStyle = Z.gold; ctx.lineWidth = 4; for (const y of [bot + 54, bot + 70]) { ctx.beginPath(); ctx.moveTo(sw * 0.2 + 3, y); ctx.lineTo(sw * 0.2 + 21, y); ctx.stroke(); } ctx.restore();
    ctx.save(); ctx.strokeStyle = Z.green; ctx.lineWidth = 3; for (let i = 0; i < 4; i++) { ctx.beginPath(); ctx.moveTo(sw * 0.2 + 4 + i * 5.5, bot + 90); ctx.lineTo(sw * 0.2 + 4 + i * 5.5, bot + 102); ctx.stroke(); } ctx.restore(); }
  if (o === 'repartidor') { // bolso kraft cruzado
    const p = new Path2D(); p.moveTo(-sw * 0.7, bot + 6); p.lineTo(sw * 0.75, bot * 0.3); ctx.save(); ctx.lineWidth = 8; ctx.strokeStyle = Z.deep; ctx.stroke(p); ctx.restore();
    const b = rr(sw * 0.35, bot * 0.42, 52, 42, 8); F(b, SH.kraft); S(b, 0.8); const lb = rr(sw * 0.35 + 10, bot * 0.42 + 12, 32, 16, 4); ctx.fillStyle = Z.green; ctx.fill(lb); }
  if (o === 'elegante') { // moño verde con botón dorado
    for (const s of [-1, 1]) { const w = new Path2D(); w.moveTo(0, bot + 40); w.lineTo(s * 34, bot + 26); w.lineTo(s * 34, bot + 54); w.closePath(); F(w, Z.green); S(w, 0.7); }
    const kn = ell(0, bot + 40, 9, 9); F(kn, Z.deep); S(kn, 0.6); ctx.fillStyle = Z.gold; for (const y of [bot * 0.42, bot * 0.2]) { ctx.beginPath(); ctx.arc(0, y, 5, 0, 7); ctx.fill(); } }
}

function head(ctx, F, S, o, g, x) {
  const { rx, top } = g;
  if (o === 'chef') { const b = top + 18, h = new Path2D(); h.moveTo(x - 44, b); h.bezierCurveTo(x - 76, b - 10, x - 72, b - 66, x - 38, b - 62); h.bezierCurveTo(x - 32, b - 96, x + 32, b - 98, x + 40, b - 64); h.bezierCurveTo(x + 74, b - 68, x + 78, b - 10, x + 44, b); h.closePath(); F(h, '#FFFFFF'); S(h);
    const bd = rr(x - 46, b - 12, 92, 26, 6); F(bd, '#FFFFFF'); S(bd, 0.9); return b - 96; }
  if (o === 'jardinero') { const y = top + 24, br = ell(x, y, rx * 0.98, 20); F(br, SH.straw); S(br);
    const cr = new Path2D(); cr.moveTo(x - rx * 0.48, y); cr.bezierCurveTo(x - rx * 0.5, y - 64, x + rx * 0.5, y - 64, x + rx * 0.48, y); cr.closePath(); F(cr, SH.straw); S(cr);
    const band = new Path2D(); band.moveTo(x - rx * 0.48, y - 4); band.quadraticCurveTo(x, y - 12, x + rx * 0.48, y - 4); ctx.save(); ctx.lineWidth = 11; ctx.strokeStyle = Z.green; ctx.stroke(band); ctx.restore();
    const lf = new Path2D(); lf.moveTo(x + rx * 0.32, y - 10); lf.quadraticCurveTo(x + rx * 0.5, y - 44, x + rx * 0.62, y - 30); lf.quadraticCurveTo(x + rx * 0.5, y - 14, x + rx * 0.32, y - 10); F(lf, '#6E9A5B'); S(lf, 0.6); return y - 60; }
  if (o === 'chullo') { const y = top + 44; // gorro tejido con orejeras y pompón
    const cp = new Path2D(); cp.moveTo(x - rx * 0.72, y); cp.bezierCurveTo(x - rx * 0.74, top - 30, x + rx * 0.74, top - 30, x + rx * 0.72, y); cp.closePath(); F(cp, Z.deep); S(cp);
    ctx.save(); ctx.clip(cp); ctx.lineWidth = 6; ctx.strokeStyle = SH.stem; ctx.beginPath(); for (let i = 0; i <= 14; i++) { const xx = x - rx * 0.75 + i * rx * 0.107; ctx.lineTo(xx, y - 22 - (i % 2) * 14); } ctx.stroke();
    ctx.strokeStyle = Z.gold; ctx.beginPath(); for (let i = 0; i <= 14; i++) { const xx = x - rx * 0.75 + i * rx * 0.107; ctx.lineTo(xx, top + 4 - (i % 2) * 12); } ctx.stroke(); ctx.restore(); S(cp);
    for (const s of [-1, 1]) { const f = new Path2D(); f.moveTo(x + s * rx * 0.72, y - 6); f.lineTo(x + s * rx * 0.74, y + 34); f.lineTo(x + s * rx * 0.5, y - 2); f.closePath(); F(f, Z.deep); S(f, 0.7);
      const c = new Path2D(); c.moveTo(x + s * rx * 0.73, y + 34); c.lineTo(x + s * rx * 0.74, y + 66); ctx.save(); ctx.lineWidth = 4; ctx.strokeStyle = Z.ink; ctx.stroke(c); ctx.restore(); const pm = ell(x + s * rx * 0.74, y + 72, 9, 9); F(pm, Z.gold); S(pm, 0.5); }
    const pp = ell(x, top - 30, 18, 18); F(pp, Z.gold); S(pp, 0.7); return top - 48; }
  if (o === 'repartidor') { const y = top + 30; const cp = new Path2D(); cp.moveTo(x - rx * 0.58, y); cp.bezierCurveTo(x - rx * 0.6, top - 26, x + rx * 0.6, top - 26, x + rx * 0.58, y); cp.closePath(); F(cp, Z.green); S(cp);
    const v = new Path2D(); v.moveTo(x + rx * 0.2, y - 2); v.quadraticCurveTo(x + rx * 0.8, y - 10, x + rx * 0.98, y + 8); v.quadraticCurveTo(x + rx * 0.6, y + 14, x + rx * 0.2, y + 6); v.closePath(); F(v, Z.deep); S(v, 0.8);
    const bt = ell(x, top - 18, 7, 5); F(bt, Z.gold); L.text(ctx, 'Z', x - rx * 0.12, y - 22, { font: '700 34px Fraunces', color: Z.gold, align: 'center' }); return top - 26; }
  return top;
}

export function shroomyAt(K, x, y, hgt, P = {}) {
  const ctx = K.ctx, s = hgt / 235, wf = L.washFill(ctx, 6, { alpha: 0.35, gran: 0.08, pool: 0.22 });
  const F = (p, c) => { ctx.fillStyle = '#FFFEFA'; ctx.fill(p); wf(p, c); }, S = L.wobbleStroke(ctx, Z.ink, 4, s, boil(K));
  const rx = (SHAPES[P.shape] || SHAPES.clasico).rx;
  if (!P.noShadow) { ctx.save(); ctx.globalAlpha = 0.14 * (1 - L.clamp((P.hop || 0) / 250)); ctx.fillStyle = Z.ink; ctx.beginPath(); ctx.ellipse(x, y + 2, rx * 0.9 * s, 13 * s, 0, 0, 7); ctx.fill(); ctx.restore(); }
  ctx.save(); ctx.translate(x, y - (P.hop || 0)); ctx.rotate(P.rot || 0); ctx.scale(s * (P.sx || 1) * (P.flip ? -1 : 1), s * (P.sy || 1));
  const a = drawShroomy(ctx, F, S, P, K.t);
  const m = ctx.getTransform(), tc = ([u, v]) => [m.a * u + m.c * v + m.e, m.b * u + m.d * v + m.f];
  ctx.restore();
  return { handL: tc(a.handL), handR: tc(a.handR), top: tc(a.top), center: tc(a.center), s };
}
