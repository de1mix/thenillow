package com.thenillow.game.entities

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.thenillow.game.utils.Assets
import com.thenillow.game.utils.Constants

/** Снаряд, выпущенный врагом-формулой */
class EnemyBullet(
    startX: Float,
    startY: Float,
    dirX: Float,
    dirY: Float,
    assets: Assets
) : Entity(startX, startY, 14f, 14f, assets.particleTex) {

    private var lifetime = 2.2f
    val damage = 10

    init {
        hp = 1; maxHp = 1
        val speed = 280f
        velocity.x = dirX * speed
        velocity.y = dirY * speed
    }

    override fun update(delta: Float) {
        x += velocity.x * delta
        y += velocity.y * delta
        lifetime -= delta
        if (lifetime <= 0f) alive = false
    }

    override fun render(batch: SpriteBatch) {
        if (!alive) return
        batch.setColor(1f, 0.9f, 0.1f, 1f)
        batch.draw(texture, x, y, width, height)
        batch.setColor(Color.WHITE)
    }
}
