package com.thenillow.game.entities

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.thenillow.game.utils.Assets
import com.thenillow.game.utils.Constants

/**
 * Враг-формула: летает зигзагами, стреляет энергетическими импульсами.
 * Отображает настоящие формулы физики 7 класса над собой.
 */
class Formula(
    startX: Float,
    startY: Float,
    private val assets: Assets
) : Entity(startX, startY, Constants.FORMULA_WIDTH, Constants.FORMULA_HEIGHT, assets.formulaTex) {

    val damage = Constants.FORMULA_DAMAGE
    private var zigzagTimer = 0f
    private var zigzagDir = 1f
    private var shootCooldown = MathUtils.random(2f, 4f)

    // Пул формул физики 7 класса (это реальный враг)
    val formulaText: String = FORMULAS.random()

    // Снаряды формулы → callback как в Player
    var onShoot: ((Float, Float, Float, Float) -> Unit)? = null

    init {
        hp = Constants.FORMULA_HP
        maxHp = Constants.FORMULA_HP
    }

    fun update(delta: Float, playerX: Float, playerY: Float) {
        zigzagTimer += delta * 2f
        val dx = playerX - centerX()
        val dy = playerY - centerY()
        val dist = Vector2.len(dx, dy)

        if (dist > 1f) {
            val nx = dx / dist; val ny = dy / dist
            // Зигзаг перпендикулярно к игроку
            val perpX = -ny * MathUtils.sin(zigzagTimer) * 0.6f
            val perpY = nx * MathUtils.sin(zigzagTimer) * 0.6f
            velocity.x = (nx * 0.7f + perpX) * Constants.FORMULA_SPEED
            velocity.y = (ny * 0.7f + perpY) * Constants.FORMULA_SPEED
        }

        x += velocity.x * delta
        y += velocity.y * delta

        // Выстрел в игрока
        shootCooldown -= delta
        if (shootCooldown <= 0f && dist < 400f) {
            val nx = dx / dist; val ny = dy / dist
            onShoot?.invoke(centerX(), centerY(), nx, ny)
            shootCooldown = MathUtils.random(2.5f, 5f)
        }
    }

    override fun render(batch: SpriteBatch) {
        if (!alive) return
        batch.draw(texture, x, y, width, height)
        // Текст формулы рисуется в GameScreen поверх (нужен font)
    }

    companion object {
        val FORMULAS = listOf(
            "F = ma",
            "v = v₀ + at",
            "s = v₀t + at²/2",
            "P = F/S",
            "W = Fs·cos α",
            "E = mc²",
            "ρ = m/V",
            "F = mg",
            "v² = v₀² + 2as",
            "T = 2π√(l/g)"
        )
    }
}
