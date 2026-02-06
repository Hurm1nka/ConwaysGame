# Conway's Game of Life — API

REST API для хранения карт на сервере (Часть 2 задания).

## Запуск

```bash
cd server
npm install
npm start
```

Сервер: `http://localhost:3000`

## Эндпоинты

| Метод | URL | Описание |
|-------|-----|----------|
| GET | `/api/maps` | Список всех карт |
| GET | `/api/maps/:id` | Одна карта по id |
| POST | `/api/maps` | Создать карту |
| PUT | `/api/maps/:id` | Обновить карту |
| DELETE | `/api/maps/:id` | Удалить карту |

## Модель карты

- **id** — число (авто)
- **name** — строка
- **width**, **height** — размеры поля
- **cells** — строка из `0` и `1` (например `"0001010110000..."`), по строкам сетки
- **created_at**, **updated_at** — даты в SQLite datetime

## Примеры

**Создать карту:**
```http
POST /api/maps
Content-Type: application/json

{"name": "Glider", "width": 5, "height": 5, "cells": "0010001010111000000000000"}
```

**Список карт:**
```http
GET /api/maps
```

**Обновить клетки:**
```http
PUT /api/maps/1
Content-Type: application/json

{"cells": "0001010110000..."}
```

Для теста с телефона укажи в приложении URL сервера (например `http://192.168.1.x:3000`). В манифесте Android нужен cleartext traffic или использование HTTPS.
