package com.thenillow.game.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.thenillow.game.entities.Player
import com.thenillow.game.systems.PhysicsSystem
import com.thenillow.game.utils.Assets
import com.thenillow.game.utils.Constants

/**
 * HUD — интерфейс поверх игры (фиксирован на экране, не в мировых координатах).
 *
 * Отображает:
 *  - HP-бар игрока (слева сверху)
 *  - Счёт (справа сверху)
 *  - Таймер до смены гравитации + предупреждение
 *  - Два виртуальных джойстика (снизу)
 *  - Подсказка «Подойди к Кириллу» когда NPC рядом
 */
class HUD(
    private val assets: Assets,
    private val screenW: Float,
    private val screenH: Float
) {
    private val m = Constants.HUD_MARGIN

    /** Рендер всего HUD в экранных координатах */
    fun render(
        batch: SpriteBatch,
        shape: ShapeRenderer,
        player: Player,
        physics: PhysicsSystem,
        leftJoy: VirtualJoystick,
        rightJoy: VirtualJoystick,
        kirillNear: Boolean,
        solvedQuests: Int
    ) {
        // ── HP бар ─────────────────────────────────────────────────────
        val hpFraction = player.hp.toFloat() / player.maxHp
        val barW = 220f; val barH = 22f
        val barX = m; val barY = screenH - m - barH

        batch.end()
        shape.begin(ShapeRenderer.ShapeType.Filled)

        // Фон бара
        shape.setColor(0.15f, 0.15f, 0.15f, 0.75f)
        shape.rect(barX, barY, barW, barH)

        // HP fill — цвет от зелёного к красному
        val r = 1f - hpFraction; val g = hpFraction
        shape.setColor(r, g, 0.1f, 1f)
        shape.rect(barX, barY, barW * hpFraction, barH)

        // Гравитационный таймер (снизу по центру)
        val gFrac = 1f - physics.timeUntilFlip / Constants.GRAVITY_CHANGE_INTERVAL
        val gtW = 260f; val gtH = 14f
        val gtX = screenW / 2f - gtW / 2f; val gtY = m + 60f
        shape.setColor(0.1f, 0.1f, 0.1f, 0.6f)
        shape.rect(gtX, gtY, gtW, gtH)
        val gColor = if (physics.timeUntilFlip < 10f) Color(1f, 0.2f, 0.2f, 1f)
                     else Color(0.2f, 0.7f, 1f, 1f)
        shape.setColor(gColor)
        shape.rect(gtX, gtY, gtW * gFrac, gtH)

        shape.end()
        batch.begin()

        // ── Текст поверх HP бара ──────────────────────────────────────
        assets.fontSmall.color = Color.WHITE
        assets.fontSmall.draw(batch, "HP  ${player.hp} / ${player.maxHp}", barX + 6f, barY + barH - 4f)

        // Счёт
        assets.fontMed.color = Color(1f, 0.95f, 0.3f, 1f)
        assets.fontMed.draw(batch, "★ ${player.score}", screenW - 160f, screenH - m - 4f)

        // Квесты Кирилла
        assets.fontSmall.color = Color(0.7f, 1f, 0.7f, 1f)
        assets.fontSmall.draw(batch, "Шифры: $solvedQuests / 3", m, screenH - m - barH - 28f)

        // Гравитационная подпись
        val gravLabel = if (physics.gravityFlipped) "↑ ГРАВИТАЦИЯ ПЕРЕВЁРНУТА ↑" else "↓ Гравитация в норме"
        assets.fontSmall.color = if (physics.gravityFlipped) Color(1f, 0.3f, 0.3f, 1f) else Color(0.5f, 0.9f, 1f, 1f)
        assets.fontSmall.draw(batch, gravLabel, screenW / 2f - 120f, m + 90f)

        // Предупреждение за 10 секунд до смены
        if (physics.timeUntilFlip < 10f) {
            assets.fontMed.color = Color(1f, 0.2f, 0.2f, 0.9f)
            val warn = "⚠ СМЕНА ЧЕРЕЗ ${physics.timeUntilFlip.toInt() + 1} СЕК"
            assets.fontMed.draw(batch, warn, screenW / 2f - 160f, screenH / 2f + 40f)
        }

        // Подсказка Кирилла
        if (kirillNear) {
            assets.fontSmall.color = Color(1f, 0.9f, 0.3f, 1f)
            assets.fontSmall.draw(batch, "[ Нажми правый джойстик → для разговора с Кириллом ]",
                screenW / 2f - 200f, m + 110f)
        }

        // Джойстики
        leftJoy.render(batch)
        rightJoy.render(batch)
    }

    /** Большое сообщение по центру (смерть, переворот гравитации и т.д.) */
    fun renderCenterMessage(batch: SpriteBatch, text: String, color: Color = Color.WHITE) {
        assets.fontBig.color = color
        assets.fontBig.draw(batch, text, screenW / 2f - text.length * 9f, screenH / 2f)
    }
}
