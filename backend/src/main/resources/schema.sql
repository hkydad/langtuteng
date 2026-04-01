CREATE TABLE IF NOT EXISTS player (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    phone TEXT,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS football_match (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    match_date TEXT NOT NULL,
    location TEXT,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS attendance (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    match_id INTEGER NOT NULL,
    player_id INTEGER NOT NULL,
    status TEXT NOT NULL,
    remark TEXT,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (match_id) REFERENCES football_match(id),
    FOREIGN KEY (player_id) REFERENCES player(id),
    UNIQUE(match_id, player_id)
);
