package com.thenillow.game.entities

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.thenillow.game.utils.Assets

/**
 * NPC Кирилл — загадочный квестодатель.
 * Выдаёт задания исключительно зашифрованными сообщениями.
 */
class Kirill(startX: Float, startY: Float, assets: Assets) :
    Entity(startX, startY, 52f, 72f, createKirillTexture()) {

    var isNearPlayer = false
    private var floatTimer = 0f

    init { hp = 999; maxHp = 999 }

    fun update(delta: Float, playerX: Float, playerY: Float) {
        floatTimer += delta * 1.5f
        // Левитирует на месте
        y = startY + Math.sin(floatTimer.toDouble()).toFloat() * 8f

        val dx = playerX - centerX()
        val dy = playerY - centerY()
        isNearPlayer = Math.sqrt((dx * dx + dy * dy).toDouble()) < 120.0
    }

    private val startY = startY

    override fun render(batch: SpriteBatch) {
        // Свечение вокруг Кирилла
        if (isNearPlayer) {
            batch.setColor(1f, 0.85f, 0.3f, 0.35f)
            batch.draw(texture, x - 12f, y - 12f, width + 24f, height + 24f)
        }
        batch.setColor(Color.WHITE)
        batch.draw(texture, x, y, width, height)
    }

    companion object {
        private fun createKirillTexture(): Texture {
            val w = 52; val h = 72
            val pm = Pixmap(w, h, Pixmap.Format.RGBA8888)
            // Тело — загадочный силуэт в плаще
            pm.setColor(0.15f, 0.1f, 0.25f, 1f); pm.fill()
            // Голова
            pm.setColor(0.85f, 0.75f, 0.6f, 1f)
            pm.fillCircle(w / 2, h - 14, 11)
            // Плащ
            pm.setColor(0.3f, 0.1f, 0.5f, 1f)
            pm.fillTriangle(0, 0, w, 0, w / 2, h - 22)
            // Знак вопроса
            pm.setColor(1f, 0.9f, 0.1f, 1f)
            pm.drawLine(w / 2 - 1, h / 2, w / 2 + 1, h / 2 + 6)
            pm.fillCircle(w / 2, h / 2 - 2, 3)
            return Texture(pm).also { pm.dispose() }
        }
    }
}
