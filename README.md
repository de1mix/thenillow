# TheNillow & Восстание Геометрии
## Полный туториал: сборка Android APK

---

## ШАГ 1 — Установка Android Studio

1. Перейди на https://developer.android.com/studio
2. Скачай **Android Studio Hedgehog** (2023.1.1) или новее
3. Запусти установщик, нажимай Next/Next/Finish
4. При первом запуске Android Studio скачает SDK автоматически

**Что нужно выбрать при установке SDK:**
- Android SDK Platform 34
- Android SDK Build-Tools 34.0.0
- Android Emulator (опционально — для теста без телефона)

---

## ШАГ 2 — Открыть проект

1. Запусти Android Studio
2. Нажми **"Open"** (не "New Project")
3. Выбери папку `TheNillow` (эту папку, где лежит settings.gradle)
4. Нажми **OK**
5. Подожди пока Gradle скачает зависимости (~2-5 минут, нужен интернет)

Если видишь ошибку "SDK not found":
- File → Project Structure → SDK Location
- Укажи путь к Android SDK (обычно `C:\Users\ИМЯ\AppData\Local\Android\Sdk`)

---

## ШАГ 3 — Исправить native libs (ВАЖНО!)

LibGDX требует нативные .so файлы. Открой **Терминал** внутри Android Studio (снизу):

```bash
# Windows:
gradlew.bat :android:copyNatives

# Mac/Linux:
./gradlew :android:copyNatives
```

Или просто сразу сборка — Gradle сделает всё сам:
```bash
# Windows:
gradlew.bat assembleDebug

# Mac/Linux:
./gradlew assembleDebug
```

---

## ШАГ 4 — Сборка APK

**Вариант A — через GUI:**
1. Меню сверху: **Build → Build Bundle(s) / APK(s) → Build APK(s)**
2. Внизу справа появится уведомление "APK(s) generated"
3. Нажми **"locate"** — откроется папка с APK

**Вариант B — через терминал:**
```bash
# Windows:
gradlew.bat assembleDebug

# Mac/Linux:
./gradlew assembleDebug
```

APK будет тут:
```
TheNillow/android/build/outputs/apk/debug/android-debug.apk
```

---

## ШАГ 5 — Установка на Android телефон

### Метод 1: USB (быстрый, нужен кабель)

1. На телефоне: **Настройки → О телефоне → Номер сборки** (нажать 7 раз) → активирован Режим разработчика
2. **Настройки → Для разработчиков → Отладка по USB** → включить
3. Подключи телефон кабелем к компу
4. В Android Studio нажми **▶ Run** (зелёный треугольник) — выбери свой телефон
5. Игра автоматически установится и запустится

### Метод 2: Файлом (без компа-телефонной связи)

1. Скинь APK файл на телефон (в Telegram себе, Google Drive, USB как накопитель)
2. На телефоне открой файл через файловый менеджер
3. Если спросит "Разрешить установку из неизвестных источников" → разреши
4. Нажми "Установить"

### Метод 3: ADB команда

```bash
adb install android/build/outputs/apk/debug/android-debug.apk
```

---

## ШАГ 6 — Управление в игре

| Действие | Управление |
|----------|-----------|
| Движение | Левый джойстик (левая половина экрана) |
| Прицел + стрельба лазером | Правый джойстик (правая половина) |
| Разговор с Кириллом | Подойди к нему + тап правым джойстиком |
| Ввод ответа на шифр | Тап правым джойстиком в диалоге |
| Закрыть диалог | Тап левым джойстиком в диалоге |

---

## Шифры Кирилла (подсказки для разработчика)

### Квест 1 — Шифр Цезаря
Ответ: `иди на восток найди пещеру`
Метод: каждая буква сдвинута на +4 вперёд по русскому алфавиту

### Квест 2 — Двоичный код
Ответ: `north tower`
Метод: 01001011 = K (75), 01001100 = L (76), 01000001 = A (65), 01000100 = D (68), и т.д.

### Квест 3 — Шифр Виженера
Ответ: `boss lair`
Ключ: NILLOW

---

## Структура проекта

```
TheNillow/
├── core/                          # Весь игровой код (платформонезависимый)
│   └── src/main/java/com/thenillow/game/
│       ├── TheNillowGame.kt       # Точка входа LibGDX
│       ├── entities/              # Игровые объекты
│       │   ├── Entity.kt          # Базовый класс
│       │   ├── Player.kt          # Клавиатура TheNillow
│       │   ├── Triangle.kt        # Враг-треугольник
│       │   ├── Formula.kt         # Враг-формула
│       │   ├── Laser.kt           # Снаряд игрока
│       │   ├── EnemyBullet.kt     # Снаряд врага
│       │   └── Kirill.kt          # NPC Кирилл
│       ├── screens/               # Экраны
│       │   ├── GameScreen.kt      # Основной геймплей
│       │   └── MenuScreen.kt      # Главное меню
│       ├── systems/               # Игровые системы
│       │   ├── PhysicsSystem.kt   # Сломанная физика
│       │   ├── SpawnSystem.kt     # Спавн врагов
│       │   └── ParticleSystem.kt  # Взрывы и частицы
│       ├── ui/                    # Интерфейс
│       │   ├── VirtualJoystick.kt # Один джойстик
│       │   ├── TouchInputHandler.kt # Мультитач
│       │   ├── HUD.kt             # Весь интерфейс
│       │   └── CipherDialog.kt    # Диалог шифров
│       ├── cipher/
│       │   └── CipherSystem.kt    # Шифры Цезаря, двоичный, Виженера
│       └── utils/
│           ├── Assets.kt          # Текстуры и шрифты
│           ├── Constants.kt       # Все константы
│           ├── CameraHelper.kt    # Плавная камера + тряска
│           └── WorldRenderer.kt   # Рендер мира
│
├── android/                       # Android-специфичный код
│   └── src/main/java/com/thenillow/game/
│       └── AndroidLauncher.kt     # Activity
│
└── assets/                        # Ресурсы (сейчас пусто — всё генерируется кодом)
```

---

## Частые ошибки и решения

**"Could not resolve com.badlogicgames.gdx"**
→ Нет интернета или Maven-репозиторий недоступен. Подключись к интернету и нажми "Sync Now"

**"Kotlin not configured"**
→ File → Project Structure → Modules → выбери модуль → Dependencies → убедись что Kotlin Runtime добавлен

**"No target device found"**
→ Телефон не подключён или нет эмулятора. Создай AVD: Tools → Device Manager → Create Device

**"Installation failed: INSTALL_FAILED_NO_MATCHING_ABIS"**
→ Нативные либы не скопировались. Запусти `gradlew :android:copyNatives` вручную

**Игра вылетает сразу**
→ Logcat (снизу в Android Studio) покажет стектрейс. Скинь его и я помогу разобраться.

---

## Технологии

- **LibGDX 1.12.1** — 2D игровой фреймворк
- **Kotlin** — основной язык
- **Android API 26+** (Android 8.0 Oreo и новее)
- **OpenGL ES 2.0** — рендер через LibGDX
- Архитектура: **Screen-based** (MenuScreen → GameScreen)
- Все текстуры **генерируются программно** (Pixmap API) — не нужны PNG-файлы
