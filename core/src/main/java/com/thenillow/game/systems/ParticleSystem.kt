package com.thenillow.game.systems

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils

/** Одна частица взрыва / спаркл */
data class Particle(
    var x: Float, var y: Float,
    var vx: Float, var vy: Float,
    var life: Float, var maxLife: Float,
    var size: Float,
    val r: Float, val g: Float, val b: Float
)

/**
 * ParticleSystem — пул частиц для взрывов при уничтожении врагов,
 * и sparkle при смене гравитации.
 */
class ParticleSystem(private val tex: Texture) {

    private val particles = ArrayList<Particle>(256)

    /** Взрыв при уничтожении врага */
    fun explode(x: Float, y: Float, count: Int = 12,
                r: Float = 0.3f, g: Float = 1f, b: Float = 0.5f) {
        repeat(count) {
            val angle = MathUtils.random(360f)
            val speed = MathUtils.random(80f, 320f)
            particles.add(Particle(
                x = x, y = y,
                vx = MathUtils.cosDeg(angle) * speed,
                vy = MathUtils.sinDeg(angle) * speed,
                life = MathUtils.random(0.3f, 0.7f),
                maxLife = 0.7f,
                size = MathUtils.random(6f, 18f),
                r = r, g = g, b = b
            ))
        }
    }

    /** Gravity flip — большой экранный burst */
    fun gravityFlash(screenCx: Float, screenCy: Float) {
        repeat(30) {
            val angle = MathUtils.random(360f)
            val speed = MathUtils.random(120f, 500f)
            particles.add(Particle(
                x = screenCx + MathUtils.random(-100f, 100f),
                y = screenCy + MathUtils.random(-60f, 60f),
                vx = MathUtils.cosDeg(angle) * speed,
                vy = MathUtils.sinDeg(angle) * speed,
                life = MathUtils.random(0.5f, 1.2f),
                maxLife = 1.2f,
                size = MathUtils.random(8f, 22f),
                r = 0.3f, g = 0.8f, b = 1f
            ))
        }
    }

    fun update(delta: Float) {
        val iter = particles.iterator()
        while (iter.hasNext()) {
            val p = iter.next()
            p.x += p.vx * delta
            p.y += p.vy * delta
            p.vx *= 0.92f; p.vy *= 0.92f   // затухание
            p.life -= delta
            if (p.life <= 0f) iter.remove()
        }
    }

    fun render(batch: SpriteBatch) {
        for (p in particles) {
            val alpha = (p.life / p.maxLife).coerceIn(0f, 1f)
            batch.setColor(p.r, p.g, p.b, alpha)
            batch.draw(tex, p.x - p.size / 2f, p.y - p.size / 2f, p.size, p.size)
        }
        batch.setColor(Color.WHITE)
    }

    fun clear() = particles.clear()
}
