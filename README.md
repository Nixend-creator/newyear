<p align="center">
  <img src="https://dummyimage.com/600x180/14213d/ffffff&text=NewYear+Minecraft+Plugin" alt="NewYear Banner">
</p>

<h1 align="center">🎄 NewYear v2 — Новогодний плагин для Minecraft</h1>

<p align="center">
  <b>Подарки, Санта, снег, квесты, ежедневные задания, защита от спама и дюпа</b>
</p>

<p align="center">
  <a href="https://github.com/n1xend/NewYear/actions">
    <img src="https://img.shields.io/github/actions/workflow/status/n1xend/NewYear/build.yml?branch=main&label=build" alt="Build Status">
  </a>
  <a href="#">
    <img src="https://img.shields.io/badge/Java-17-blue" alt="Java 17">
  </a>
  <a href="#">
    <img src="https://img.shields.io/badge/Paper-1.21.1-brightgreen" alt="Paper 1.21.1">
  </a>
  <a href="https://github.com/n1xend/NewYear/releases">
    <img src="https://img.shields.io/github/v/release/n1xend/NewYear?label=latest" alt="Latest Release">
  </a>
</p>

---

## ✨ Описание

**NewYear v2** — это продвинутый новогодний плагин для Paper / Spigot, который добавляет:

- 🎁 Продвинутую систему подарков с редкостями  
- 🎅 Летающего Санту на "санях" (ArmorStand)  
- ❄ Визуальный и реальный снег  
- 🎄 Постройку новогодних ёлок  
- 🧩 Систему квестов (обычных и ежедневных)  
- 🛡 Анти-спам, анти-дюп и анти-логаут защиту  
- 🤖 GitHub Actions для автосборки и релизов  

Плагин написан под **Java 17** и **Paper 1.21.1**, использует **Vault** (опционально) для денежных наград.

---

## 🧱 Основные фичи

### 🎁 Подарки и редкости

- 3D-подарок (куб из блоков) появляется над игроком  
- После "приземления" выпадает награда  
- Система редкостей:
  - `common` — обычные призы  
  - `rare` — редкие предметы  
  - `epic` — топовые награды  

Все настраивается в `config.yml`:

```yaml
gifts:
  common:
    chance: 60
    items:
      - "STONE:32"
      - "TORCH:16"
  rare:
    chance: 30
    items:
      - "DIAMOND:2"
      - "EMERALD:3"
  epic:
    chance: 10
    items:
      - "NETHERITE_SCRAP:1"
      - "TOTEM_OF_UNDYING:1"
