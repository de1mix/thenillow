package com.thenillow.game.utils

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2

/**
 * CameraHelper — плавно следит за игроком (lerp-интерполяция),
 * ограничивает камеру рамками мира, добавляет тряску при ударе.
 */
class CameraHelper(private val camera: OrthographicCamera) {

    private val halfW = Constants.VIEWPORT_WIDTH  / 2f
    private val halfH = Constants.VIEWPORT_HEIGHT / 2f

    // Тряска камеры
    private var shakeTimer  = 0f
    private var shakePower  = 0f

    fun update(delta: Float, targetX: Float, targetY: Float) {
        // Плавное следование (lerp коэффициент = 5)
        val lerpFactor = 1f - Math.exp((-5.0 * delta)).toFloat()
        camera.position.x += (targetX - camera.position.x) * lerpFactor
        camera.position.y += (targetY - camera.position.y) * lerpFactor

        // Ограничение камеры в пределах мира
        camera.position.x = MathUtils.clamp(
            camera.position.x,
            halfW,
            Constants.WORLD_WIDTH - halfW
        )
        camera.position.y = MathUtils.clamp(
            camera.position.y,
            halfH,
            Constants.WORLD_HEIGHT - halfH
        )

        // Тряска
        if (shakeTimer > 0f) {
            shakeTimer -= delta
            val dx = MathUtils.random(-shakePower, shakePower)
            val dy = MathUtils.random(-shakePower, shakePower)
            camera.position.x += dx
            camera.position.y += dy
        }

        camera.update()
    }

    /** Добавить тряску камеры (при ударе, смене гравитации) */
    fun shake(power: Float = 12f, duration: Float = 0.35f) {
        shakePower = power
        shakeTimer = duration
    }
}
