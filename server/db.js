const Database = require('better-sqlite3');
const path = require('path');

const dbPath = path.join(__dirname, 'conways.db');
const db = new Database(dbPath);

function initDb() {
  db.exec(`
    CREATE TABLE IF NOT EXISTS maps (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      name TEXT NOT NULL,
      width INTEGER NOT NULL,
      height INTEGER NOT NULL,
      cells TEXT NOT NULL DEFAULT '',
      created_at TEXT NOT NULL DEFAULT (datetime('now')),
      updated_at TEXT NOT NULL DEFAULT (datetime('now'))
    )
  `);
}

function listMaps() {
  const stmt = db.prepare(`
    SELECT id, name, width, height, cells, created_at, updated_at
    FROM maps
    ORDER BY updated_at DESC
  `);
  return stmt.all();
}

function getMap(id) {
  const stmt = db.prepare(`
    SELECT id, name, width, height, cells, created_at, updated_at
    FROM maps
    WHERE id = ?
  `);
  return stmt.get(id);
}

function createMap({ name, width, height, cells = '' }) {
  const stmt = db.prepare(`
    INSERT INTO maps (name, width, height, cells)
    VALUES (?, ?, ?, ?)
  `);
  const result = stmt.run(name, width, height, cells);
  return getMap(result.lastInsertRowid);
}

function updateMap(id, { name, width, height, cells }) {
  const map = getMap(id);
  if (!map) return null;
  const updates = [];
  const values = [];
  if (name !== undefined) { updates.push('name = ?'); values.push(name); }
  if (width !== undefined) { updates.push('width = ?'); values.push(width); }
  if (height !== undefined) { updates.push('height = ?'); values.push(height); }
  if (cells !== undefined) { updates.push('cells = ?'); values.push(cells); }
  if (updates.length === 0) return map;
  updates.push("updated_at = datetime('now')");
  values.push(id);
  const stmt = db.prepare(`
    UPDATE maps SET ${updates.join(', ')} WHERE id = ?
  `);
  stmt.run(...values);
  return getMap(id);
}

function deleteMap(id) {
  const stmt = db.prepare('DELETE FROM maps WHERE id = ?');
  const result = stmt.run(id);
  return result.changes > 0;
}

initDb();

module.exports = {
  listMaps,
  getMap,
  createMap,
  updateMap,
  deleteMap,
};
