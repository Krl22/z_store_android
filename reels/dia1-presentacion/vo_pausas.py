"""Agrega silencio entre frases de audio/vo.wav y corre los tiempos de audio/words.json.
Uso: python vo_pausas.py "1:0.15,2:0.35,..."  (índice de frase terminada → segundos extra)"""
import json, sys, wave, numpy as np
extra = {int(k): float(v) for k, v in (p.split(':') for p in sys.argv[1].split(','))}
w = wave.open('audio/vo.wav'); sr, ch, sw = w.getframerate(), w.getnchannels(), w.getsampwidth(); x = np.frombuffer(w.readframes(w.getnframes()), dtype=np.int16).reshape(-1, ch); w.close()
words = json.load(open('audio/words.json', encoding='utf-8'))
# fin de cada frase = palabra con . ? ! …
ends = [i for i, wd in enumerate(words) if wd['w'].endswith(('.', '?', '!', '…'))]
cuts = []  # (tiempo de corte, segundos a insertar)
for k, i in enumerate(ends, 1):
    if k in extra and i + 1 < len(words): cuts.append(((words[i]['e'] + words[i + 1]['s']) / 2, extra[k]))
out, prev, shift = [], 0, 0.0
for t, d in cuts:
    n = int(t * sr); out += [x[prev:n], np.zeros((int(d * sr), ch), np.int16)]; prev = n
out.append(x[prev:])
for wd in words:
    s = sum(d for t, d in cuts if t < wd['s']); wd['s'] = round(wd['s'] + s, 3); wd['e'] = round(wd['e'] + s, 3)
y = np.concatenate(out); o = wave.open('audio/vo.wav', 'wb'); o.setnchannels(ch); o.setsampwidth(sw); o.setframerate(sr); o.writeframes(y.tobytes()); o.close()
json.dump(words, open('audio/words.json', 'w', encoding='utf-8'), ensure_ascii=False)
line, st = [], None
for wd in words:
    if st is None: st = wd['s']
    line.append(wd['w'])
    if wd['w'].endswith(('.', '?', '!', '…')): print(f'{st:6.2f}-{wd["e"]:5.2f}  {" ".join(line)}'); line, st = [], None
print('total', round(len(y) / sr, 2))
