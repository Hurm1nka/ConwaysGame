const express = require('express');
const cors = require('cors');
const db = require('./db');

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(express.json({ limit: '1mb' }));

// GET /api/maps — список карт
app.get('/api/maps', (req, res) => {
  try {
    const maps = db.listMaps();
    res.json(maps);
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// GET /api/maps/:id — одна карта
app.get('/api/maps/:id', (req, res) => {
  try {
    const id = parseInt(req.params.id, 10);
    if (Number.isNaN(id)) {
      return res.status(400).json({ error: 'Invalid id' });
    }
    const map = db.getMap(id);
    if (!map) {
      return res.status(404).json({ error: 'Map not found' });
    }
    res.json(map);
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// POST /api/maps — создать карту
app.post('/api/maps', (req, res) => {
  try {
    const { name, width, height, cells } = req.body;
    if (!name || width == null || height == null) {
      return res.status(400).json({
        error: 'Required: name, width, height. Optional: cells (string of 0/1)',
      });
    }
    const w = parseInt(width, 10);
    const h = parseInt(height, 10);
    if (Number.isNaN(w) || Number.isNaN(h) || w < 1 || h < 1) {
      return res.status(400).json({ error: 'width and height must be positive integers' });
    }
    const cellStr = typeof cells === 'string' ? cells : '';
    const map = db.createMap({
      name: String(name).trim() || 'Unnamed',
      width: w,
      height: h,
      cells: cellStr,
    });
    res.status(201).json(map);
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// PUT /api/maps/:id — обновить карту
app.put('/api/maps/:id', (req, res) => {
  try {
    const id = parseInt(req.params.id, 10);
    if (Number.isNaN(id)) {
      return res.status(400).json({ error: 'Invalid id' });
    }
    const map = db.getMap(id);
    if (!map) {
      return res.status(404).json({ error: 'Map not found' });
    }
    const { name, width, height, cells } = req.body;
    const updates = {};
    if (name !== undefined) updates.name = String(name).trim();
    if (width !== undefined) {
      const w = parseInt(width, 10);
      if (Number.isNaN(w) || w < 1) {
        return res.status(400).json({ error: 'width must be a positive integer' });
      }
      updates.width = w;
    }
    if (height !== undefined) {
      const h = parseInt(height, 10);
      if (Number.isNaN(h) || h < 1) {
        return res.status(400).json({ error: 'height must be a positive integer' });
      }
      updates.height = h;
    }
    if (cells !== undefined) updates.cells = typeof cells === 'string' ? cells : '';
    const updated = db.updateMap(id, updates);
    res.json(updated);
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// DELETE /api/maps/:id — удалить карту
app.delete('/api/maps/:id', (req, res) => {
  try {
    const id = parseInt(req.params.id, 10);
    if (Number.isNaN(id)) {
      return res.status(400).json({ error: 'Invalid id' });
    }
    const deleted = db.deleteMap(id);
    if (!deleted) {
      return res.status(404).json({ error: 'Map not found' });
    }
    res.status(204).send();
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

app.listen(PORT, () => {
  console.log(`Conway's Game of Life API: http://localhost:${PORT}/api/maps`);
});
