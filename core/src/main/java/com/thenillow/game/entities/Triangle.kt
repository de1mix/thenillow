package com.thenillow.game.entities

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.thenillow.game.utils.Assets
import com.thenillow.game.utils.Constants

/**
 * Агрессивный равнобедренный треугольник.
 * Медленно преследует игрока, при касании наносит урон.
 * Вращается вокруг своей оси (это раздражает — как и задумано).
 */
class Triangle(
    startX: Float,
    startY: Float,
    assets: Assets
) : Entity(startX, startY, Constants.TRIANGLE_SIZE, Constants.TRIANGLE_SIZE, assets.triangleTex) {

    var rotation: Float = MathUtils.random(360f)
    private val rotSpeed: Float = MathUtils.random(60f, 180f) * if (MathUtils.randomBoolean()) 1f else -1f
    val damage = Constants.TRIANGLE_DAMAGE
    private var aggroRange = 500f
    private var isAggro = false

    init {
        hp = Constants.TRIANGLE_HP
        maxHp = Constants.TRIANGLE_HP
    }

    fun update(delta: Float, playerX: Float, playerY: Float) {
        rotation += rotSpeed * delta

        val dx = playerX - centerX()
        val dy = playerY - centerY()
        val dist = Vector2.len(dx, dy)

        if (dist < aggroRange) isAggro = true
        if (dist > aggroRange * 1.5f) isAggro = false

        if (isAggro && dist > 30f) {
            val nx = dx / dist; val ny = dy / dist
            velocity.x = nx * Constants.TRIANGLE_SPEED
            velocity.y = ny * Constants.TRIANGLE_SPEED
        } else {
            // Дрейф в случайном направлении когда не агрессивен
            velocity.x *= 0.95f; velocity.y *= 0.95f
        }

        x += velocity.x * delta
        y += velocity.y * delta
    }
}
