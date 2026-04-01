package com.thenillow.game.utils

object Constants {
    // Мир
    const val WORLD_WIDTH = 2400f
    const val WORLD_HEIGHT = 1600f
    const val VIEWPORT_WIDTH = 800f
    const val VIEWPORT_HEIGHT = 480f

    // Физика (PPM = pixels per meter)
    const val PPM = 32f
    const val GRAVITY_NORMAL = -9.8f
    const val GRAVITY_FLIPPED = 9.8f
    const val GRAVITY_CHANGE_INTERVAL = 120f   // секунд

    // Игрок
    const val PLAYER_SPEED = 260f
    const val PLAYER_HP = 100
    const val PLAYER_WIDTH = 96f
    const val PLAYER_HEIGHT = 52f
    const val LASER_SPEED = 620f
    const val LASER_COOLDOWN = 0.18f           // секунд между выстрелами
    const val LASER_DAMAGE = 12

    // Враги
    const val TRIANGLE_HP = 30
    const val TRIANGLE_SPEED = 120f
    const val TRIANGLE_DAMAGE = 8
    const val TRIANGLE_SIZE = 56f
    const val FORMULA_HP = 50
    const val FORMULA_SPEED = 80f
    const val FORMULA_DAMAGE = 15
    const val FORMULA_WIDTH = 80f
    const val FORMULA_HEIGHT = 40f

    // Спавн
    const val ENEMY_SPAWN_INTERVAL = 3.5f
    const val MAX_ENEMIES = 18

    // UI
    const val JOYSTICK_RADIUS = 80f
    const val JOYSTICK_KNOB_RADIUS = 44f
    const val HUD_MARGIN = 24f

    // Шифры Кирилла
    const val CIPHER_QUEST_COUNT = 3
}
