package com.thenillow.game.entities

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2

/**
 * Базовый класс всех игровых объектов.
 */
abstract class Entity(
    var x: Float,
    var y: Float,
    val width: Float,
    val height: Float,
    var texture: Texture
) {
    val velocity = Vector2(0f, 0f)
    var hp: Int = 1
    var maxHp: Int = 1
    var alive: Boolean = true
    val bounds: Rectangle get() = Rectangle(x, y, width, height)

    open fun update(delta: Float) {}

    open fun render(batch: SpriteBatch) {
        if (alive) batch.draw(texture, x, y, width, height)
    }

    fun takeDamage(amount: Int) {
        hp -= amount
        if (hp <= 0) { hp = 0; alive = false }
    }

    fun overlaps(other: Entity): Boolean = bounds.overlaps(other.bounds)

    fun centerX() = x + width / 2f
    fun centerY() = y + height / 2f
}
