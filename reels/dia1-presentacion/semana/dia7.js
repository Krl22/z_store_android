// Día 7 · mar 13 oct · carrusel 4 láminas 4:5 · «Cómo pedir». Sin plazos ni zonas. Mushie repartidor. CTA inbox.
import { L, Z, SH, W, Y0, Y1, LOGO, HL, hand, body, SK, table, kitBox, sparkle, frameLeaves, ctaChip, logo, dots, badge, phone, bubble, deliveryBox, rr, M } from './common.js';
const N = 4, base = { style: 'zetadia1', format: '9:16', fps: 30, camera: false, chrome: false, captions: false, person: false, actor: null };
const MR = (K, x, y, h, P = {}) => M(K, x, y, h, Object.assign({ outfit: 'repartidor' }, P));
function head(K, s, n, txt) { hand(K, 'Cómo pedir', 540, Y0 + 110, 64); badge(K, 540, Y0 + 210, n, 54); HL(K, s, txt, { x: 60, y: Y0 + 290, w: 960, h: 260 }, 1, { max: 120 }); }
export default { ...base, scenes: [
  { type: 'story', dur: 1, bg: 'cream', render(K, s) { const sk = SK(K, 101); frameLeaves(K, sk); head(K, s, 1, 'Escríbenos *al inbox*');
    phone(K, sk, 400, 1200, 1.05, ctx => { bubble(ctx, -80, -140, 150, 60, '#E5EDCB'); bubble(ctx, -60, -50, 140, 60, Z.gold, true); bubble(ctx, -80, 40, 120, 60, '#E5EDCB');
      ctx.fillStyle = Z.deep; ctx.font = '600 26px Poppins'; ctx.fillText('¡Hola!', -60, -100); ctx.fillText('Quiero', -40, -10); });
    MR(K, 790, 1460, 330, { armL: 60, eyes: 'happy', mouth: 'open', look: [-0.5, 0] }); dots(K, 0, N); } },
  { type: 'story', dur: 1, bg: 'sage', render(K, s) { const sk = SK(K, 102); frameLeaves(K, sk); head(K, s, 2, 'Te contamos *productos y precios*');
    phone(K, sk, 540, 1210, 1.05, ctx => { for (let k = 0; k < 4; k++) { const y = -140 + k * 80; const b = rr(-80, y, 44, 44, 6); ctx.fillStyle = SH.kraft; ctx.fill(b); ctx.fillStyle = 'rgba(40,75,46,.55)'; ctx.fillRect(-20, y + 8, 90 - (k % 2) * 20, 10); ctx.fillStyle = 'rgba(40,75,46,.3)'; ctx.fillRect(-20, y + 26, 60, 8); } });
    MR(K, 880, 1460, 260, { eyes: 'happy', mouth: 'smile', look: [-0.6, 0] }); dots(K, 1, N); } },
  { type: 'story', dur: 1, bg: 'sun', render(K, s) { const sk = SK(K, 103); frameLeaves(K, sk); head(K, s, 3, 'Coordinamos *el delivery*');
    table(K, sk, 1460); deliveryBox(K, sk, 420, 1460, 1.3); MR(K, 820, 1460, 380, { armL: 40, armR: 40, eyes: 'happy', mouth: 'open', look: [-0.4, 0] }); dots(K, 2, N); } },
  { type: 'story', dur: 1, bg: 'cream', image: LOGO, render(K, s) { const sk = SK(K, 104); frameLeaves(K, sk); head(K, s, 4, 'Pagas *por Yape*');
    phone(K, sk, 380, 1230, 0.95, ctx => { ctx.fillStyle = Z.green; ctx.beginPath(); ctx.arc(0, -40, 60, 0, 7); ctx.fill(); ctx.strokeStyle = Z.surface; ctx.lineWidth = 12; ctx.lineCap = 'round'; ctx.beginPath(); ctx.moveTo(-26, -40); ctx.lineTo(-6, -18); ctx.lineTo(30, -62); ctx.stroke();
      ctx.fillStyle = Z.deep; ctx.font = '700 40px Poppins'; ctx.textAlign = 'center'; ctx.fillText('Yape', 0, 70); });
    ctaChip(K, 540, 1505, 'Escríbenos al inbox', 0.8);
    MR(K, 790, 1400, 300, { armR: 60, eyes: 'happy', mouth: 'open', look: [-0.4, 0] }); dots(K, 3, N, false); } },
] };
