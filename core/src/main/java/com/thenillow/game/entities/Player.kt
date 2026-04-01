package com.thenillow.game.entities

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.thenillow.game.utils.Assets
import com.thenillow.game.utils.Constants

/**
 * TheNillow — летающая механическая клавиатура.
 * Управляется двумя виртуальными джойстиками:
 *   Левый  → движение
 *   Правый → прицеливание + стрельба лазером из пробела
 */
class Player(
    startX: Float,
    startY: Float,
    private val assets: Assets
) : Entity(startX, startY, Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT, assets.keyboardTex) {

    // Состояние
    var score: Int = 0
    var invincibleTimer: Float = 0f     // мигает при уроне
    var laserCooldown: Float = 0f
    var gravityFlipped: Boolean = false

    // Визуал
    private var flashTimer = 0f
    private var bobTimer = 0f           // плавное парение
    private val bobAmplitude = 5f

    // Лазеры спавнятся через callback чтобы не тащить World в Player
    var onShoot: ((Float, Float, Float, Float) -> Unit)? = null

    init {
        hp = Constants.PLAYER_HP
        maxHp = Constants.PLAYER_HP
    }

    /**
     * moveDir — нормализованный вектор от левого джойстика.
     * aimDir  — нормализованный вектор от правого джойстика.
     */
    fun handleInput(moveDir: Vector2, aimDir: Vector2, delta: Float) {
        // Движение
        velocity.x = moveDir.x * Constants.PLAYER_SPEED
        velocity.y = moveDir.y * Constants.PLAYER_SPEED

        // Выстрел лазером когда правый джойстик отклонён
        laserCooldown -= delta
        if (aimDir.len() > 0.25f && laserCooldown <= 0f) {
            val ox = centerX(); val oy = centerY()
            onShoot?.invoke(ox, oy, aimDir.x, aimDir.y)
            laserCooldown = Constants.LASER_COOLDOWN
        }
    }

    override fun update(delta: Float) {
        x += velocity.x * delta
        y += velocity.y * delta

        // Боб-анимация парения
        bobTimer += delta * 3f
        val bobOffset = MathUtils.sin(bobTimer) * bobAmplitude

        // Гравитация (сломанная физика — переворачивает)
        if (!gravityFlipped) {
            y += bobOffset * delta    // мягкое парение вверх/вниз
        } else {
            y -= bobOffset * delta
        }

        // Таймеры
        if (invincibleTimer > 0f) invincibleTimer -= delta
        if (flashTimer > 0f) flashTimer -= delta
    }

    override fun render(batch: SpriteBatch) {
        if (!alive) return
        // Мигание при уроне
        if (invincibleTimer > 0f && (invincibleTimer * 10).toInt() % 2 == 0) return

        val scaleX = if (velocity.x < -10f) -1f else 1f  // отражаем при движении влево
        batch.draw(
            texture,
            x + if (scaleX < 0) width else 0f,
            y,
            width * scaleX,
            height
        )
    }

    fun onHit(damage: Int) {
        if (invincibleTimer > 0f) return
        takeDamage(damage)
        invincibleTimer = 1.2f   // 1.2с неуязвимости после удара
        flashTimer = 1.2f
    }
}
