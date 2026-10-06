"""Recorta las láminas 9:16 de qa/<dia>/ a 4:5 (1080x1350) y las guarda como <salida>-1.png, -2.png…
Uso: python3 semana/recortar.py qa/dia2 ../../imagenes/dia2-feriado [y0=285]"""
import os, sys
from PIL import Image
src, out = sys.argv[1], sys.argv[2]
y0 = int(sys.argv[3]) if len(sys.argv) > 3 else 285
os.makedirs(os.path.dirname(out) or '.', exist_ok=True)
fs = sorted(f for f in os.listdir(src) if f.startswith('s_') and f.endswith('.jpg'))
for i, f in enumerate(fs, 1):
    p = f'{out}.png' if len(fs) == 1 else f'{out}-{i}.png'
    Image.open(os.path.join(src, f)).crop((0, y0, 1080, y0 + 1350)).save(p); print(p)
