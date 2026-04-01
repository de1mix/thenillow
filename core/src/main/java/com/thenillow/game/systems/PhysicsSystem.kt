package com.thenillow.game.systems

import com.thenillow.game.utils.Constants

/**
 * PhysicsSystem — управляет гравитацией мира.
 * Каждые GRAVITY_CHANGE_INTERVAL секунд гравитация переворачивается.
 * Это «сломанная физика» по описанию игры.
 */
class PhysicsSystem {

    var gravityFlipped: Boolean = false
        private set

    var timer: Float = 0f
        private set

    /** Сколько секунд до следующего переворота */
    val timeUntilFlip: Float get() = Constants.GRAVITY_CHANGE_INTERVAL - timer

    var onFlip: ((Boolean) -> Unit)? = null

    fun update(delta: Float) {
        timer += delta
        if (timer >= Constants.GRAVITY_CHANGE_INTERVAL) {
            timer = 0f
            gravityFlipped = !gravityFlipped
            onFlip?.invoke(gravityFlipped)
        }
    }

    /** Возвращает направление гравитации: +1 = нормальная (вниз), -1 = перевёрнутая (вверх) */
    fun gravitySign(): Float = if (gravityFlipped) -1f else 1f

    /** Применяет гравитационный дрейф к Y-скорости объекта */
    fun applyGravity(vy: Float, delta: Float, strength: Float = 180f): Float {
        return vy - gravitySign() * strength * delta
    }

    fun reset() {
        timer = 0f
        gravityFlipped = false
    }
}
