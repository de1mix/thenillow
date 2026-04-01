package com.thenillow.game.entities

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.thenillow.game.utils.Assets
import com.thenillow.game.utils.Constants

class Laser(
    startX: Float,
    startY: Float,
    private val dirX: Float,
    private val dirY: Float,
    assets: Assets
) : Entity(startX, startY, 8f, 24f, assets.laserTex) {

    private var lifetime = 1.6f
    val angle: Float = MathUtils.atan2(dirY, dirX) * MathUtils.radiansToDegrees

    init {
        hp = 1; maxHp = 1
        velocity.x = dirX * Constants.LASER_SPEED
        velocity.y = dirY * Constants.LASER_SPEED
    }

    override fun update(delta: Float) {
        x += velocity.x * delta
        y += velocity.y * delta
        lifetime -= delta
        if (lifetime <= 0f) alive = false
    }

    override fun render(batch: SpriteBatch) {
        if (!alive) return
        batch.draw(
            texture,
            x, y,
            width / 2f, height / 2f,
            width, height,
            1f, 1f,
            angle - 90f,    // поворачиваем в направлении полёта
            0, 0,
            texture.width, texture.height,
            false, false
        )
    }
}
