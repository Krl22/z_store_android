// Piezas comunes para los posts de la semana (días 2–7). Formato 4:5 (1080x1350) recortado del lienzo 9:16: y 285–1635.
// Render de láminas: node render.mjs --scenes --query spec=semana/diaN.js --outdir qa/diaN  →  python3 semana/recortar.py
import * as L from '../engine/lib.js';
import { para } from '../engine/base.js';
import { Z, SH, shroomyAt } from '../styles/shroomy.js';
import { SK, table, leaf, sparkle, kitBox, mug, plant, leafFall } from '../video.js';
import { pouch } from '../video-publicidad.js';
export { L, Z, SH, shroomyAt, SK, table, leaf, sparkle, kitBox, mug, plant, leafFall, pouch, para };

export const W = 1080, Y0 = 285, Y1 = 1635, LOGO = 'assets/logo-zeta.png';
export const cl = L.clamp, E = L.E;
export const HL = (K, s, str, box, p = 1, o = {}) => K.S.headline(K, str, box, p, s, o);
export function hand(K, str, x, y, size, color = Z.green, alpha = 1) { L.text(K.ctx, str, x, y, { font: K.S.type.hand(size), color, align: 'center', alpha }); }
export function body(K, str, x, y, size = 44, color = Z.ink, align = 'center', weight = 500) { L.text(K.ctx, str, x, y, { font: `${weight} ${size}px Poppins`, color, align }); }
export const rr = (x, y, w, h, r) => { const p = new Path2D(); p.roundRect(x, y, w, h, r); return p; };
export const ell = (x, y, rx, ry, a = 0) => { const p = new Path2D(); p.ellipse(x, y, rx, ry, a, 0, 7); return p; };
export const M = (K, x, y, h, P = {}) => shroomyAt(K, x, y, h, Object.assign({ outfit: 'natural' }, P));

export function logo(K, y, w = 380) { const img = K.images[LOGO]; if (!img) return; K.ctx.drawImage(img, (W - w) / 2, y, w, w * img.height / img.width); }
// botón dorado con globo de mensaje genérico
export function ctaChip(K, x, y, text, s = 1) { const ctx = K.ctx; ctx.save(); ctx.translate(x, y); ctx.scale(s, s); ctx.font = '800 50px Poppins';
  const tw = ctx.measureText(text).width, w = tw + 170, chip = rr(-w / 2, -62, w, 124, 62);
  ctx.shadowColor = 'rgba(35,48,31,.22)'; ctx.shadowBlur = 22; ctx.shadowOffsetY = 8; ctx.fillStyle = Z.gold; ctx.fill(chip); ctx.shadowColor = 'transparent';
  const bx = -w / 2 + 44, bub = new Path2D(); bub.roundRect(bx, -28, 60, 46, 15); bub.moveTo(bx + 13, 18); bub.lineTo(bx + 6, 34); bub.lineTo(bx + 28, 18); ctx.lineWidth = 5; ctx.strokeStyle = Z.ink; ctx.stroke(bub);
  ctx.fillStyle = Z.ink; for (const dx of [16, 30, 44]) { ctx.beginPath(); ctx.arc(bx + dx, -5, 4.2, 0, 7); ctx.fill(); }
  L.text(ctx, text, bx + 84 + tw / 2, 17, { font: '800 50px Poppins', color: Z.ink, align: 'center' }); ctx.restore(); }
// puntitos del carrusel + «desliza»
export function dots(K, i, n, swipe = i === 0) { const ctx = K.ctx, y = Y1 - 48;
  for (let k = 0; k < n; k++) { ctx.beginPath(); ctx.arc(W / 2 + (k - (n - 1) / 2) * 34, y, k === i ? 10 : 7, 0, 7); ctx.fillStyle = k === i ? Z.deep : 'rgba(40,75,46,.28)'; ctx.fill(); }
  if (swipe) { L.text(ctx, 'desliza →', W - 60, y + 14, { font: K.S.type.hand(44), color: Z.green, align: 'right' }); } }
export function badge(K, x, y, n, r = 46) { const ctx = K.ctx; ctx.beginPath(); ctx.arc(x, y, r, 0, 7); ctx.fillStyle = Z.deep; ctx.fill(); ctx.lineWidth = 6; ctx.strokeStyle = Z.gold; ctx.stroke();
  L.text(ctx, String(n), x, y + r * 0.36, { font: `700 ${Math.round(r * 1.05)}px Fraunces`, color: Z.surface, align: 'center' }); }
// marco suave de la zona 4:5 (para que las láminas respiren igual)
export function frameLeaves(K, sk, t = 0) { [[90, Y0 + 70, 0.3], [990, Y0 + 90, 2.6], [70, Y1 - 160, -0.5], [1010, Y1 - 190, 0.7]].forEach(([x, y, a], i) => leaf(K, sk, x, y + Math.sin(t * 1.5 + i) * 5, 1, a, i % 2 ? '#8DB07A' : '#6E9A5B')); }

// ---------- ilustraciones ----------
// melena de león (el hongo, sin carita): bola crema con filas de espinas colgantes
export function lionsMane(K, sk, x, y, s = 1, t = 0) { const ctx = K.ctx; ctx.save(); ctx.translate(x, y); ctx.scale(s, s);
  const b = new Path2D(), N = 22; for (let i = 0; i <= N; i++) { const a = i / N * Math.PI * 2, r = i % 2 ? 1.06 : 1, px = Math.cos(a) * 110 * r, py = Math.sin(a) * 96 * r; i ? b.lineTo(px, py) : b.moveTo(px, py); } b.closePath();
  sk.fill(b, '#F6EEDC'); sk.stroke(b, 0.9);
  [[10, 26, 0.6], [36, 34, 0.75], [62, 40, 0.9]].forEach(([ry, len, wk], j) => { const half = 110 * Math.sqrt(Math.max(0, 1 - (ry / 100) ** 2)) * 0.95, n = Math.round(half / 13);
    for (let i = 0; i <= n * 2; i++) { const bx = -half + i * (half / n), sw = Math.sin(t * 2 + i + j) * 2, l = len + ((i * 7 + j * 3) % 5) * 3, w = 9 * wk + 3, p = new Path2D();
      p.moveTo(bx - w, ry - 4); p.quadraticCurveTo(bx - w * 0.7, ry + l * 0.7, bx - w * 0.3 + sw, ry + l); p.quadraticCurveTo(bx + sw, ry + l + w * 0.7, bx + w * 0.3 + sw, ry + l); p.quadraticCurveTo(bx + w * 0.7, ry + l * 0.7, bx + w, ry - 4); p.closePath();
      ctx.fillStyle = j === 2 ? '#F0E4CB' : '#F7EFDF'; ctx.fill(p); ctx.save(); ctx.strokeStyle = 'rgba(35,48,31,.3)'; ctx.lineWidth = 2; ctx.stroke(p); ctx.restore(); } });
  ctx.restore(); }
// racimo de setas ostra (abanicos), color crema o pardo
export function oysters(K, sk, x, y, s = 1, col = '#EFE6D2', edge = '#CDBB97') { const ctx = K.ctx; ctx.save(); ctx.translate(x, y); ctx.scale(s, s);
  [[-60, -20, -0.5, 0.9], [50, -30, 0.45, 1], [-5, -70, 0.05, 1.1], [-40, -95, -0.3, 0.8], [45, -100, 0.35, 0.85]].forEach(([dx, dy, a, k]) => { ctx.save(); ctx.translate(dx, dy); ctx.rotate(a); ctx.scale(k, k);
    const st = rr(-10, 0, 20, 60, 10); sk.fill(st, '#F4ECDC'); sk.stroke(st, 0.6);
    const f = new Path2D(); f.moveTo(-8, 4); f.bezierCurveTo(-90, -10, -80, -80, 0, -78); f.bezierCurveTo(80, -80, 90, -10, 8, 4); f.closePath(); sk.fill(f, col); sk.stroke(f, 0.8);
    ctx.save(); ctx.globalAlpha = 0.5; ctx.strokeStyle = edge; ctx.lineWidth = 2.5; for (let k2 = -3; k2 <= 3; k2++) { ctx.beginPath(); ctx.moveTo(0, 0); ctx.lineTo(k2 * 18, -60); ctx.stroke(); } ctx.restore(); ctx.restore(); });
  ctx.restore(); }
// ganoderma (repisa rojiza-barniz) – solo forma, sin claims
export function ganoderma(K, sk, x, y, s = 1) { const ctx = K.ctx; ctx.save(); ctx.translate(x, y); ctx.scale(s, s);
  const st = rr(-12, -10, 24, 70, 10); sk.fill(st, '#8A5A36'); sk.stroke(st, 0.7);
  const c = new Path2D(); c.moveTo(-90, 0); c.bezierCurveTo(-100, -60, 100, -60, 90, 0); c.quadraticCurveTo(0, 20, -90, 0); c.closePath(); sk.fill(c, '#9B4A2E'); sk.stroke(c);
  ctx.save(); ctx.clip(c); ctx.globalAlpha = 0.6; ctx.strokeStyle = '#E8B86A'; ctx.lineWidth = 6; ctx.beginPath(); ctx.moveTo(-88, -2); ctx.quadraticCurveTo(0, 16, 88, -2); ctx.stroke(); ctx.restore(); ctx.restore(); }
// atomizador (q: 0–1 neblina)
export function sprayer(K, sk, x, y, s = 1, ang = 0, q = 0) { const ctx = K.ctx; ctx.save(); ctx.translate(x, y); ctx.rotate(ang); ctx.scale(s, s);
  const b = rr(-34, -10, 68, 110, 16); sk.fill(b, '#CFE3D2'); sk.stroke(b); const lv = rr(-28, 30, 56, 64, 12); ctx.save(); ctx.globalAlpha = 0.5; ctx.fillStyle = '#9FC7D6'; ctx.fill(lv); ctx.restore();
  const n = rr(-18, -40, 36, 32, 6); sk.fill(n, Z.green); sk.stroke(n, 0.8); const h = new Path2D(); h.moveTo(-18, -40); h.lineTo(-30, -40); h.lineTo(-30, -52); h.lineTo(26, -52); h.lineTo(26, -40); sk.stroke(h, 0.8);
  const tr = new Path2D(); tr.moveTo(10, -24); tr.quadraticCurveTo(26, -6, 14, 6); sk.stroke(tr, 0.7);
  if (q > 0) { ctx.save(); ctx.fillStyle = '#CFE6F0'; for (let i = 0; i < 26; i++) { const a = -0.35 + (i % 7) * 0.1, d = 30 + ((i * 37) % 120) * q; ctx.globalAlpha = (1 - d / 170) * 0.9; ctx.beginPath(); ctx.arc(30 + Math.cos(a) * d, -46 + Math.sin(a) * d, 3 + (i % 3), 0, 7); ctx.fill(); } ctx.restore(); }
  ctx.restore(); }
export function scissors(K, x, y, s = 1, open = 0.3, ang = 0) { const ctx = K.ctx; ctx.save(); ctx.translate(x, y); ctx.rotate(ang); ctx.scale(s, s); ctx.lineWidth = 6; ctx.strokeStyle = Z.ink;
  for (const sg of [-1, 1]) { ctx.save(); ctx.rotate(sg * open); ctx.fillStyle = '#C9CFC4'; ctx.beginPath(); ctx.moveTo(0, 0); ctx.lineTo(90, sg * -6); ctx.lineTo(0, sg * 10); ctx.closePath(); ctx.fill(); ctx.stroke();
    ctx.beginPath(); ctx.ellipse(-36, sg * 16, 22, 14, 0, 0, 7); ctx.fillStyle = Z.green; ctx.fill(); ctx.stroke(); ctx.restore(); } ctx.restore(); }
export function knife(K, x, y, s = 1, ang = 0) { const ctx = K.ctx; ctx.save(); ctx.translate(x, y); ctx.rotate(ang); ctx.scale(s, s);
  const h = rr(-90, -12, 70, 24, 10); ctx.fillStyle = '#7A5A3C'; ctx.fill(h); ctx.lineWidth = 4; ctx.strokeStyle = Z.ink; ctx.stroke(h);
  const b = new Path2D(); b.moveTo(-20, -14); b.lineTo(80, -6); b.quadraticCurveTo(96, 0, 80, 10); b.lineTo(-20, 12); b.closePath(); ctx.fillStyle = '#D9DED5'; ctx.fill(b); ctx.stroke(b); ctx.restore(); }
export function sunIcon(K, x, y, r = 46, rays = true) { const ctx = K.ctx; ctx.save(); ctx.fillStyle = Z.gold; ctx.beginPath(); ctx.arc(x, y, r, 0, 7); ctx.fill();
  if (rays) { ctx.strokeStyle = Z.gold; ctx.lineWidth = 7; ctx.lineCap = 'round'; for (let k = 0; k < 8; k++) { const a = k * Math.PI / 4; ctx.beginPath(); ctx.moveTo(x + Math.cos(a) * (r + 14), y + Math.sin(a) * (r + 14)); ctx.lineTo(x + Math.cos(a) * (r + 34), y + Math.sin(a) * (r + 34)); ctx.stroke(); } } ctx.restore(); }
export function drop(K, x, y, r = 22, col = '#8EC3D6') { const ctx = K.ctx; ctx.save(); ctx.beginPath(); ctx.moveTo(x, y - r * 1.6); ctx.quadraticCurveTo(x + r * 1.1, y - r * 0.2, x, y + r); ctx.quadraticCurveTo(x - r * 1.1, y - r * 0.2, x, y - r * 1.6); ctx.fillStyle = col; ctx.fill(); ctx.lineWidth = 3; ctx.strokeStyle = Z.ink; ctx.stroke(); ctx.restore(); }
// sartén / plancha vista desde arriba-lado
export function pan(K, sk, cx, cy, s = 1) { const ctx = K.ctx; ctx.save(); ctx.translate(cx, cy); ctx.scale(s, s);
  const h = rr(-470, -22, 200, 40, 20); sk.fill(h, '#5B4433'); sk.stroke(h);
  const side = new Path2D(); side.ellipse(0, 30, 285, 95, 0, 0, Math.PI); side.lineTo(-285, 0); side.ellipse(0, 0, 285, 95, 0, Math.PI, 0, true); side.closePath(); ctx.fillStyle = '#2E3A2A'; ctx.fill(side); sk.stroke(side);
  const top = ell(0, 0, 285, 95); ctx.fillStyle = '#3B4836'; ctx.fill(top); sk.stroke(top); const inn = ell(0, 4, 252, 76); ctx.fillStyle = '#46543F'; ctx.fill(inn); ctx.restore(); }
export function garlic(K, sk, x, y, s = 1) { const ctx = K.ctx; ctx.save(); ctx.translate(x, y); ctx.scale(s, s);
  const b = new Path2D(); b.moveTo(0, -70); b.bezierCurveTo(20, -40, 70, -30, 60, 10); b.bezierCurveTo(50, 50, -50, 50, -60, 10); b.bezierCurveTo(-70, -30, -20, -40, 0, -70); b.closePath(); sk.fill(b, '#F5EFE3'); sk.stroke(b);
  ctx.save(); ctx.globalAlpha = 0.5; for (const dx of [-25, 0, 25]) { const l = new Path2D(); l.moveTo(dx * 0.4, -55); l.quadraticCurveTo(dx * 1.3, -5, dx * 0.9, 38); sk.stroke(l, 0.5); } ctx.restore(); ctx.restore(); }
export function lemon(K, sk, x, y, s = 1) { const ctx = K.ctx; ctx.save(); ctx.translate(x, y); ctx.scale(s, s);
  const l = new Path2D(); l.moveTo(-75, 0); l.bezierCurveTo(-60, -55, 60, -55, 75, 0); l.bezierCurveTo(60, 55, -60, 55, -75, 0); l.closePath(); sk.fill(l, '#E9D35A'); sk.stroke(l);
  const lf = new Path2D(); lf.moveTo(40, -36); lf.quadraticCurveTo(70, -70, 96, -50); lf.quadraticCurveTo(70, -28, 40, -36); sk.fill(lf, '#6E9A5B'); sk.stroke(lf, 0.6); ctx.restore(); }
export function bottle(K, sk, x, y, s = 1, col = '#E3C25A', label = 'aceite') { const ctx = K.ctx; ctx.save(); ctx.translate(x, y); ctx.scale(s, s);
  const b = new Path2D(); b.moveTo(-40, 0); b.lineTo(-40, -120); b.quadraticCurveTo(-40, -150, -16, -165); b.lineTo(-16, -200); b.lineTo(16, -200); b.lineTo(16, -165); b.quadraticCurveTo(40, -150, 40, -120); b.lineTo(40, 0); b.closePath();
  sk.fill(b, col); sk.stroke(b); const c = rr(-18, -218, 36, 22, 5); sk.fill(c, Z.green); sk.stroke(c, 0.7);
  const lb = rr(-34, -100, 68, 52, 8); ctx.fillStyle = Z.surface; ctx.fill(lb); L.text(ctx, label, 0, -66, { font: '600 20px Poppins', color: Z.deep, align: 'center' }); ctx.restore(); }
export function saltShaker(K, sk, x, y, s = 1) { const ctx = K.ctx; ctx.save(); ctx.translate(x, y); ctx.scale(s, s);
  const b = rr(-36, -110, 72, 110, 18); sk.fill(b, '#F7F4EC'); sk.stroke(b); const t = rr(-30, -138, 60, 34, 14); sk.fill(t, '#C9CFC4'); sk.stroke(t, 0.8);
  ctx.fillStyle = Z.ink; for (const [dx, dy] of [[-10, -126], [0, -120], [10, -126]]) { ctx.beginPath(); ctx.arc(dx, dy, 3, 0, 7); ctx.fill(); } L.text(ctx, 'sal', 0, -46, { font: '600 26px Poppins', color: Z.deep, align: 'center' }); ctx.restore(); }
export function phone(K, sk, x, y, s = 1, draw) { const ctx = K.ctx; ctx.save(); ctx.translate(x, y); ctx.scale(s, s);
  const b = rr(-110, -200, 220, 400, 34); sk.fill(b, Z.deep); sk.stroke(b); const sc = rr(-94, -176, 188, 352, 22); ctx.fillStyle = Z.surface; ctx.fill(sc);
  if (draw) { ctx.save(); ctx.clip(sc); draw(ctx); ctx.restore(); } ctx.restore(); }
export function bubble(ctx, x, y, w, h, col, mine = false) { const p = new Path2D(); p.roundRect(x, y, w, h, 18); if (mine) { p.moveTo(x + w - 20, y + h); p.lineTo(x + w + 8, y + h + 14); p.lineTo(x + w - 40, y + h); } else { p.moveTo(x + 20, y + h); p.lineTo(x - 8, y + h + 14); p.lineTo(x + 40, y + h); } ctx.fillStyle = col; ctx.fill(p); }
// caja de delivery con líneas de movimiento
export function deliveryBox(K, sk, x, y, s = 1) { const ctx = K.ctx; ctx.save(); ctx.translate(x, y); ctx.scale(s, s);
  const b = rr(-110, -150, 220, 150, 10); sk.fill(b, SH.kraft); sk.stroke(b); const tape = rr(-18, -150, 36, 150, 2); ctx.save(); ctx.globalAlpha = 0.6; ctx.fillStyle = '#B98E62'; ctx.fill(tape); ctx.restore();
  const lb = rr(-80, -110, 70, 44, 6); ctx.fillStyle = Z.surface; ctx.fill(lb); ctx.lineWidth = 3; ctx.strokeStyle = Z.green; ctx.stroke(lb);
  ctx.strokeStyle = Z.green; ctx.lineWidth = 7; ctx.lineCap = 'round'; for (const [yy, l] of [[-120, 70], [-80, 100], [-40, 60]]) { ctx.beginPath(); ctx.moveTo(-140, yy); ctx.lineTo(-140 - l, yy); ctx.stroke(); } ctx.restore(); }
