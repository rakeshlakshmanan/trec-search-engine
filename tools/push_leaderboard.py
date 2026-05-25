import json
import csv
import requests
import os

CONFIG_FILE = "tools/leaderboard_config.json"
CSV_FILE = "standings.csv"

# ----------------------------------
# Load config
# ----------------------------------
if not os.path.exists(CONFIG_FILE):
    print("❌ ERROR: leaderboard_config.json not found.")
    exit(1)

with open(CONFIG_FILE, "r") as f:
    cfg = json.load(f)

API_URL = cfg.get("LEADERBOARD_API_URL")
TOKEN = cfg.get("LEADERBOARD_API_TOKEN")
TEAM_NAME = cfg.get("TEAM_NAME", "Local Team")
TEAM_MEMBERS = cfg.get("TEAM_MEMBERS", "Local User")

if not API_URL or not TOKEN:
    print("❌ Missing LEADERBOARD_API_URL or LEADERBOARD_API_TOKEN")
    exit(1)

# ----------------------------------
# Load evaluation results
# ----------------------------------
if not os.path.exists(CSV_FILE):
    print("❌ ERROR: out/standings.csv not found.")
    exit(1)

with open(CSV_FILE, "r") as f:
    reader = csv.DictReader(f)
    row = next(reader)

MAP = float(row["MAP"])
P20 = float(row["P@20"])
NDCG20 = float(row["nDCG@20"])

print("\n📊 Extracted Metrics:")
print(f"MAP      = {MAP}")
print(f"P@20     = {P20}")
print(f"nDCG@20  = {NDCG20}")

# ----------------------------------
# Build leaderboard payload
# ----------------------------------
payload = {
    "team_name": TEAM_NAME,
    "student_id": TEAM_NAME,
    "team_members": TEAM_MEMBERS,
    "map": MAP,
    "p20": P20,
    "ndcg20": NDCG20
}

headers = {
    "Authorization": f"Bearer {TOKEN}",
    "Content-Type": "application/json"
}

# ----------------------------------
# POST to leaderboard
# ----------------------------------
print("\n🚀 Sending scores to leaderboard...")

response = requests.post(f"{API_URL}/api/results", json=payload, headers=headers)

if response.status_code == 200:
    print("✅ Success! Your score was submitted.")
    print(response.json())
else:
    print(f"❌ Failed with status {response.status_code}")
    print(response.text)
