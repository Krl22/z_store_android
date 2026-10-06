// Zeta Dorada · Día 1 «Publicidad» · 15 s (9:16), SIN voz: música (ElevenLabs Music) + SFX sutiles.
// Mushie es secundario (asoma, reacciona), vestuario natural. CTA: comentar QUIERO o escribir al inbox (sin WhatsApp).
// Cambios de escena en beats de audio/beats_pub.json (~99 BPM, 1 beat = 0,604 s). Render: node render.mjs --query spec=video-publicidad.js
import * as L from './engine/lib.js';
import { Z, SH, shroomyAt } from './styles/shroomy.js';
import { SK, table, leaf, sparkle, kitBox, mug, plant, leafFall } from './video.js';

const W = 1080, H = 1920, LOGO = 'assets/logo-zeta.png', B = 0.604;
const cl = L.clamp, E = L.E;
const HL = (K, s, str, box, p, o = {}) => K.S.headline(K, str, box, p, s, o);
function hand(K, str, x, y, size, color = Z.green, alpha = 1) { L.text(K.ctx, str, x, y, { font: K.S.type.hand(size), color, align: 'center', alpha }); }
const rr = (x, y, w, h, r) => { const p = new Path2D(); p.roundRect(x, y, w, h, r); return p; };
const pop = (t, a, d = 0.35) => E.back(cl((t - a) / d));
const camB = z => ({ z, x: W / 2, y: H - H / 2 / z });
const M = (K, x, y, h, P = {}) => shroomyAt(K, x, y, h, Object.assign({ outfit: 'natural' }, P));
// aparece con pop desde su base
function popIn(K, q, cx, by, draw) { if (q <= 0.01) return; const ctx = K.ctx; ctx.save(); ctx.translate(cx, by); ctx.scale(q, q); ctx.translate(-cx, -by); draw(); ctx.restore(); }
// bolsita kraft de pie para el polvo de melena de león (empaque real no confirmado: genérico)
export function pouch(K, sk, x, y, w, h) { const ctx = K.ctx, p = new Path2D();
  p.moveTo(x + w * 0.08, y); p.lineTo(x + w * 0.92, y); p.quadraticCurveTo(x + w * 1.02, y + h * 0.6, x + w * 0.96, y + h); p.lineTo(x + w * 0.04, y + h); p.quadraticCurveTo(x - w * 0.02, y + h * 0.6, x + w * 0.08, y); p.closePath();
  sk.fill(p, SH.kraft); sk.stroke(p);
  const top = rr(x + w * 0.08, y - 4, w * 0.84, 22, 4); sk.fill(top, '#B98E62'); sk.stroke(top, 0.7);
  const lb = rr(x + w * 0.14, y + h * 0.28, w * 0.72, h * 0.48, 12); ctx.fillStyle = Z.surface; ctx.fill(lb); ctx.lineWidth = 4; ctx.strokeStyle = Z.green; ctx.stroke(lb);
  L.text(ctx, 'Zeta Dorada', x + w / 2, y + h * 0.42, { font: `600 ${Math.round(w * 0.1)}px Fraunces`, color: Z.deep, align: 'center' });
  L.text(ctx, 'Polvo de', x + w / 2, y + h * 0.55, { font: `600 ${Math.round(w * 0.07)}px Poppins`, color: Z.green, align: 'center' });
  L.text(ctx, 'melena de león', x + w / 2, y + h * 0.65, { font: `600 ${Math.round(w * 0.07)}px Poppins`, color: Z.green, align: 'center' }); }
// botón «Comenta QUIERO» con un globo de mensaje genérico (sin logos de terceros)
export function commentButton(K, x, y, c) { if (c <= 0.01) return; const ctx = K.ctx; ctx.save(); ctx.translate(x, y); ctx.scale(c, c);
  const chip = rr(-330, -66, 660, 132, 66); ctx.shadowColor = 'rgba(35,48,31,.22)'; ctx.shadowBlur = 22; ctx.shadowOffsetY = 8; ctx.fillStyle = Z.gold; ctx.fill(chip); ctx.shadowColor = 'transparent';
  const bub = new Path2D(); bub.roundRect(-290, -30, 64, 50, 16); bub.moveTo(-276, 20); bub.lineTo(-284, 38); bub.lineTo(-260, 20); ctx.lineWidth = 5; ctx.strokeStyle = Z.ink; ctx.stroke(bub);
  ctx.fillStyle = Z.ink; for (const dx of [-272, -258, -244]) { ctx.beginPath(); ctx.arc(dx, -5, 4.5, 0, 7); ctx.fill(); }
  L.text(ctx, 'Comenta QUIERO', 40, 18, { font: '800 54px Poppins', color: Z.ink, align: 'center' }); ctx.restore(); }

const PRODUCTS = ['Ostra blanca', 'Ostra parda', 'Melena de león', 'Ganoderma', 'Polvo de melena de león'];

export default {
  style: 'zetadia1', format: '9:16', fps: 30, camera: false, chrome: false, captions: false, person: false, actor: null,
  scenes: [
    // 1 · GANCHO (0–3 s) — «Cultiva tus propios hongos. En casa.» con una pila de kits; Mushie asoma detrás
    { type: 'story', dur: 5 * B, bg: 'cream', cam: t => camB(1 + 0.05 * E.inOut(cl(t / 3))),
      render(K, s, h) { const sk = SK(K), ctx = K.ctx, t = s.t;
        h.cue('title', 0.1); h.cue('pop', 0.3); h.cue('pop', 0.5); h.cue('pop', 0.7); h.cue('title', 1.4); h.cue('hop', 1.7);
        table(K, sk, 1500, '#E5EDCB');
        leafFall(K, sk, t, 4, 5);
        // Mushie asoma por detrás de la pila (se dibuja antes que las cajas)
        const up = E.back(cl((t - 1.6) / 0.45)); if (up > 0) M(K, 790, 1500 - 210 * up + 210, 380, { look: [-0.7, -0.2], eyes: t > 2.3 ? 'happy' : 'wide', mouth: t > 2.3 ? 'open' : 'o', noShadow: true });
        popIn(K, pop(t, 0.3), 330, 1500, () => kitBox(K, sk, 190, 1270, 280, 230, 'Kit de cultivo'));
        popIn(K, pop(t, 0.5), 630, 1500, () => kitBox(K, sk, 490, 1270, 280, 230, 'Kit de cultivo'));
        popIn(K, pop(t, 0.7), 480, 1270, () => kitBox(K, sk, 340, 1050, 280, 220, 'Kit de cultivo'));
        HL(K, s, 'Cultiva tus propios *hongos.*', { x: 50, y: 260, w: 980, h: 360 }, h.A(0.1, 0.6), { max: 140 });
        hand(K, 'En casa.', 540, 760, 110, Z.green, cl((t - 1.4) / 0.35));
        sparkle(K, 200, 980, 30, cl((t - 0.9) / 0.6)); sparkle(K, 880, 1000, 24, cl((t - 1.05) / 0.6)); } },

    // 2 · PRODUCTOS (3–9 s) — uno por uno, con pop y su nombre en grande
    { type: 'story', dur: 10 * B, bg: 'sage', trans: { type: 'pan', dur: 0.5 }, cam: t => camB(1.04 - 0.04 * E.out(cl(t / 1.5))),
      render(K, s, h) { const sk = SK(K, 23), ctx = K.ctx, t = s.t, at = k => 0.15 + k * 2 * B;
        PRODUCTS.forEach((_, k) => { h.cue('pop', at(k)); h.cue('title', at(k) + 0.05); });
        table(K, sk, 1560);
        const items = [
          [() => kitBox(K, sk, 60, 900, 290, 250, 'Ostra blanca'), 205, 1150],
          [() => kitBox(K, sk, 395, 900, 290, 250, 'Ostra parda'), 540, 1150],
          [() => kitBox(K, sk, 730, 900, 290, 250, 'Melena de león'), 875, 1150],
          [() => kitBox(K, sk, 160, 1290, 320, 270, 'Ganoderma'), 320, 1560],
          [() => pouch(K, sk, 620, 1250, 300, 310), 770, 1560],
        ];
        // repisa para la fila de arriba
        const sh = rr(30, 1150, 1020, 26, 8); sk.fill(sh, Z.wood); sk.stroke(sh, 0.8);
        items.forEach(([draw, cx, by], k) => { const q = pop(t, at(k), 0.4); popIn(K, q, cx, by, draw); if (q > 0.6) sparkle(K, cx + 120, by - 260, 22, cl((t - at(k) - 0.1) / 0.6)); });
        // nombre del producto actual, grande
        const k = Math.max(0, Math.min(4, Math.floor((t - 0.15) / (2 * B)))), lt = t - at(k), a = cl(lt / 0.25) * (k < 4 ? cl((2 * B - lt) / 0.2) : 1);
        ctx.save(); ctx.globalAlpha = a; ctx.translate(0, (1 - cl(lt / 0.25)) * 20);
        HL(K, s, k < 4 ? `Kit de *${PRODUCTS[k].toLowerCase()}*` : `*${PRODUCTS[k]}*`, { x: 50, y: 300, w: 980, h: 300 }, 1, { max: 112 }); ctx.restore();
        hand(K, 'kits para cultivar en casa', 540, 700, 62, Z.green, cl((t - 0.3) / 0.4) * (k < 4 ? 1 : 0)); } },

    // 3 · CALMA (9–12 s) — taza y planta; Mushie asoma detrás de la planta
    { type: 'story', dur: 5 * B, bg: 'sun', trans: { type: 'iris', x: 0.5, y: 1250 / H, dur: 0.5 }, cam: t => camB(1 + 0.06 * E.inOut(cl(t / 3))),
      render(K, s, h) { const sk = SK(K, 25), ctx = K.ctx, t = s.t;
        h.cue('title', 0.1); h.cue('whoosh', 0.35); h.cue('hop', 1.2);
        table(K, sk, 1384);
        mug(K, sk, 230, 1384, t);
        const up = E.out(cl((t - 1.0) / 0.5)); ctx.save(); ctx.beginPath(); ctx.rect(0, 0, W, 1384); ctx.clip();
        if (up > 0) M(K, 560, 1384 + 300 * (1 - up), 360, { eyes: t > 1.8 ? 'closed' : 'dot', mouth: 'smile', look: [0, -0.2], noShadow: true }); ctx.restore();
        plant(K, sk, 860, 1384, t);
        HL(K, s, 'Hechos con *calma.*', { x: 50, y: 300, w: 980, h: 280 }, h.A(0.1, 0.7), { max: 150 });
        leafFall(K, sk, t * 0.6, 3, 7, 700, 1380); } },

    // 4 · CIERRE (12–15 s) — logo + «¿Quieres el tuyo?» + «Comenta QUIERO o escríbenos al inbox»
    { type: 'story', dur: 2.92, bg: 'cream', image: LOGO, trans: { type: 'fade', dur: 0.4 }, cam: t => camB(1 + 0.02 * E.inOut(cl(t / 3))),
      render(K, s, h) { const sk = SK(K, 31), ctx = K.ctx, t = s.t, img = K.images[LOGO];
        h.cue('enter', 0.1); h.cue('title', 0.35); h.cue('impact', 0.9); h.cue('ding', 1.0);
        table(K, sk, 1480, '#E5EDCB');
        [[110, 300, 0.2], [970, 330, 2.6], [90, 1180, -0.4], [990, 1120, 0.6]].forEach(([x, y, a], i) => leaf(K, sk, x, y + Math.sin(t * 1.5 + i) * 6, 1.1, a + Math.sin(t * 2 + i) * 0.12, i % 2 ? '#8DB07A' : '#6E9A5B'));
        if (img) { const e = E.out(cl(t / 0.4)), w = 760, hh = w * img.height / img.width; ctx.save(); ctx.globalAlpha = e; ctx.drawImage(img, (W - w) / 2, 250 - (1 - e) * 30, w, hh); ctx.restore(); }
        HL(K, s, '¿Quieres *el tuyo?*', { x: 60, y: 500, w: 960, h: 170 }, h.A(0.3, 0.5), { max: 110 });
        commentButton(K, 540, 790, pop(t, 0.9, 0.4));
        hand(K, 'o escríbenos al inbox', 540, 930, 70, Z.green, cl((t - 1.3) / 0.35));
        M(K, 860, 1480, 300, { armR: t > 1.4 ? 60 + Math.sin((t - 1.4) * 9) * 20 : 15, armL: 12, eyes: 'happy', mouth: 'open', look: [-0.3, 0] }); } },
  ],
};
