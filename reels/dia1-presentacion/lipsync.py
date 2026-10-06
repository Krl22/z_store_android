"""Envolvente de la voz para mover la boca: audio/vo.wav → audio/env.json (un valor 0–1 por cuadro a 30 fps).
Uso: python3 lipsync.py"""
import json, wave, numpy as np
w = wave.open('audio/vo.wav'); sr, ch = w.getframerate(), w.getnchannels(); x = np.frombuffer(w.readframes(w.getnframes()), dtype=np.int16).reshape(-1, ch).mean(1) / 32768; w.close()
hop = sr // 30; n = len(x) // hop
rms = np.array([np.sqrt(np.mean(x[i * hop:(i + 1) * hop] ** 2)) for i in range(n)])
v = np.clip((rms - 0.01) / (np.percentile(rms, 95) - 0.01), 0, 1)
v = np.convolve(v, [0.25, 0.5, 0.25], 'same')  # suaviza sin perder el ritmo de las sílabas
json.dump({'fps': 30, 'v': [round(float(a), 3) for a in v]}, open('audio/env.json', 'w'))
print(f'{n} cuadros · hablando {np.mean(v > 0.15) * 100:.0f}% del tiempo')
