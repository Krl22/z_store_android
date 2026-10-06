"""Voz en off con ElevenLabs + tiempos por palabra (endpoint with-timestamps) → audio/vo.mp3 + audio/words.json.
Uso: python tts_eleven.py <voice_id> guion.txt [offset_s=0.25]"""
import base64, json, os, subprocess, sys, urllib.request
vid, path = sys.argv[1], sys.argv[2]; off = float(sys.argv[3]) if len(sys.argv) > 3 else 0.25
key = os.environ.get('ELEVENLABS_API_KEY') or [l.split('=', 1)[1].strip() for l in open(os.path.expanduser('~/.config/video-pizarra/keys.env')) if l.startswith('ELEVENLABS_API_KEY=')][0]
text = open(path, encoding='utf-8').read().strip()
body = json.dumps({'text': text, 'model_id': 'eleven_multilingual_v2', 'language_code': 'es',
                   'voice_settings': {'stability': 0.55, 'similarity_boost': 0.8, 'style': 0.2, 'speed': 1.0}}).encode()
r = json.load(urllib.request.urlopen(urllib.request.Request(f'https://api.elevenlabs.io/v1/text-to-speech/{vid}/with-timestamps?output_format=mp3_44100_128',
    data=body, headers={'xi-api-key': key, 'content-type': 'application/json'})))
open('audio/_vo_raw.mp3', 'wb').write(base64.b64decode(r['audio_base64']))
# silencio inicial para que la primera palabra caiga después del primer cuadro
subprocess.run(['ffmpeg', '-y', '-v', 'error', '-i', 'audio/_vo_raw.mp3', '-af', f'adelay={int(off*1000)}:all=1', '-ar', '44100', 'audio/vo.wav'], check=True)
a = r['alignment']; ch, st, en = a['characters'], a['character_start_times_seconds'], a['character_end_times_seconds']
words, cur, s0, e0 = [], '', None, None
for c, s, e in zip(ch, st, en):
    if c.isspace():
        if cur: words.append({'w': cur, 's': round(s0 + off, 3), 'e': round(e0 + off, 3)}); cur = ''
        continue
    if not cur: s0 = s
    cur += c; e0 = e
if cur: words.append({'w': cur, 's': round(s0 + off, 3), 'e': round(e0 + off, 3)})
json.dump(words, open('audio/words.json', 'w', encoding='utf-8'), ensure_ascii=False)
print(f'{len(words)} palabras · voz termina en {words[-1]["e"]:.2f}s')
line, start = [], None
for w in words:
    if start is None: start = w['s']
    line.append(w['w'])
    if w['w'].endswith(('.', '?', '!', '…')): print(f'{start:6.2f}–{w["e"]:5.2f}s  {" ".join(line)}'); line, start = [], None
