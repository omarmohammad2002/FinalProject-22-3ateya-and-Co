import requests
from faker import Faker
import random
import time

fake = Faker()

BASE_URL = "http://127.0.0.1:65348"
USER_BASE = f"{BASE_URL}/userapi"
POST_BASE = f"{BASE_URL}/posts"
SONG_BASE = f"{BASE_URL}/songs"
PLAYLIST_BASE = f"{BASE_URL}/playlists"
FOLLOW_BASE = f"{BASE_URL}/userapi/follows"
LIKE_BASE = f"{BASE_URL}/likes"

def wait(): time.sleep(0.1)

def extract_user_id_from_text(text):
    try:
        return text.strip().split("ID:")[1].strip()
    except Exception:
        print("❌ Failed to extract USER_ID from text:", text)
        return fake.uuid4()

def extract_session_id(set_cookie):
    for part in set_cookie.split(";"):
        if "SESSION_ID=" in part:
            return part.strip().split("=")[1]
    return None

def get_auth_headers(user):
    session_id = user.get("session_id")
    user_id = user.get("id")
    cookie = f"SESSION_ID={session_id}; USER_ID={user_id}"
    return {"Cookie": cookie}

def register_and_login_users(n=10):
    users = []
    for _ in range(n):
        username = fake.user_name()
        email = fake.email()
        password = "1234"
        user_type = random.choice(["FREE", "PREMIUM"])
        register_data = {
            "username": username,
            "email": email,
            "password": password,
            "bio": fake.sentence(),
            "user_type": user_type
        }

        try:
            reg = requests.post(f"{USER_BASE}/user/register", json=register_data)
            if reg.status_code == 201:
                user_id = extract_user_id_from_text(reg.text)

                login = requests.post(f"{USER_BASE}/sessions/login", json={
                    "username": username,
                    "password": password
                })

                if login.status_code == 200:
                    set_cookie = login.headers.get("Set-Cookie", "")
                    session_id = extract_session_id(set_cookie)
                    if session_id:
                        users.append({
                            "id": user_id,
                            "username": username,
                            "session_id": session_id
                        })
                        print(f"✅ Registered + Logged in: {username} ({user_id})")
                    else:
                        print("⚠️ SESSION_ID not found in login response.")
                else:
                    print("⚠️ Login failed:", login.text)
            else:
                print("⚠️ Registration failed:", reg.text)
        except Exception as e:
            print("❌ Connection error:", e)
        wait()
    return users

def create_songs(users, n=10):
    song_ids = []
    for _ in range(n):
        user = random.choice(users)
        data = {
            "title": fake.sentence(nb_words=3).replace(".", ""),
            "genre": random.choice(["Pop", "Rock", "Jazz", "Classical"]),
            "duration": random.randint(120, 400)
        }
        res = requests.post(f"{SONG_BASE}/", json=data, headers=get_auth_headers(user))
        print("🎵 Song:", res.status_code)
        if res.ok:
            try:
                song_id = res.json().get("id")
                if song_id:
                    song_ids.append(song_id)
            except:
                pass
        wait()
    return song_ids

def create_playlists(users, n=5):
    playlist_ids = []
    for _ in range(n):
        user = random.choice(users)
        data = {
            "name": fake.catch_phrase(),
            "private": random.choice([True, False])
        }
        res = requests.post(f"{PLAYLIST_BASE}", json=data, headers=get_auth_headers(user))
        print("📀 Playlist:", res.status_code)
        if res.ok:
            try:
                pid = res.json().get("id")
                if pid:
                    playlist_ids.append((pid, user))
            except:
                pass
        wait()
    return playlist_ids

def create_posts(users, n=10):
    post_ids = []
    for _ in range(n):
        user = random.choice(users)
        data = {
            "content": fake.sentence(),
            "visibility": random.choice(["public", "private"])
        }
        res = requests.post(f"{POST_BASE}", data=data, headers=get_auth_headers(user))
        print("📝 Post:", res.status_code)
        if res.ok:
            try:
                post_id = res.json().get("id", fake.uuid4())
                post_ids.append((post_id, user))
            except:
                pass
        wait()
    return post_ids

def add_songs_to_playlists(playlist_ids, song_ids):
    for pid, user in playlist_ids:
        for sid in random.sample(song_ids, min(3, len(song_ids))):
            res = requests.post(f"{PLAYLIST_BASE}/{pid}/add", json=sid, headers=get_auth_headers(user))
            print("➕ Added Song:", res.status_code)
            wait()

def create_follows(users):
    for i in range(len(users)):
        for j in range(i + 1, len(users)):
            f1 = users[i]
            f2 = users[j]
            url = f"{FOLLOW_BASE}/follow?followerId={f1['id']}&followedId={f2['id']}"
            res = requests.post(url, headers=get_auth_headers(f1))
            print(f"🤝 {f1['username']} follows {f2['username']}: {res.status_code}")
            wait()

def like_posts(users, post_ids):
    for pid, _ in post_ids:
        for _ in range(random.randint(1, 3)):
            user = random.choice(users)
            res = requests.post(f"{LIKE_BASE}/{pid}/like", headers=get_auth_headers(user))
            print(f"❤️ {user['username']} liked post {pid}: {res.status_code}")
            wait()

def like_songs(song_ids, users):
    for sid in song_ids:
        for _ in range(random.randint(1, 4)):
            user = random.choice(users)
            res = requests.put(f"{SONG_BASE}/{sid}/like", headers=get_auth_headers(user))
            print(f"🎶 {user['username']} liked song {sid}: {res.status_code}")
            wait()

# MAIN
if __name__ == "__main__":
    print("🚀 Seeding started...")
    users = register_and_login_users(10)
    if users:
        songs = create_songs(users, 10)
        playlists = create_playlists(users, 5)
        posts = create_posts(users, 10)
        add_songs_to_playlists(playlists, songs)
        create_follows(users)
        like_posts(users, posts)
        like_songs(songs, users)
        print("✅ Seeding complete.")
    else:
        print("❌ Aborting: No users created.")
