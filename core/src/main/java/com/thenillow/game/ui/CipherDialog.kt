package com.thenillow.game.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.thenillow.game.cipher.CipherSystem
import com.thenillow.game.utils.Assets

/**
 * CipherDialog — появляется поверх игры когда игрок подходит к Кириллу.
 * Показывает зашифрованное задание, подсказку и поле для ввода ответа.
 *
 * На Android ввод через Gdx.input.getTextInput() (нативная клавиатура).
 */
class CipherDialog(
    private val assets: Assets,
    private val screenW: Float,
    private val screenH: Float
) {
    var visible: Boolean = false
        private set

    private var currentQuestId: Int = 1
    var onSolved: ((Int) -> Unit)? = null  // callback когда решили шифр

    private var feedbackMsg: String = ""
    private var feedbackColor: Color = Color.WHITE
    private var feedbackTimer: Float = 0f

    private var currentInput: String = ""

    fun show(questId: Int) {
        currentQuestId = questId
        visible = true
        feedbackMsg = ""
        currentInput = ""
    }

    fun hide() {
        visible = false
    }

    /** Открывает нативную Android-клавиатуру для ввода */
    fun openKeyboard() {
        val quest = CipherSystem.quests.find { it.id == currentQuestId } ?: return
        Gdx.input.getTextInput(object : com.badlogic.gdx.Input.TextInputListener {
            override fun input(text: String) {
                currentInput = text
                checkAnswer(text)
            }
            override fun canceled() {}
        }, "Введи ответ", currentInput, "Расшифруй послание Кирилла...")
    }

    private fun checkAnswer(input: String) {
        if (CipherSystem.checkAnswer(currentQuestId, input)) {
            val quest = CipherSystem.quests.find { it.id == currentQuestId }!!
            feedbackMsg = "✓ ВЕРНО! ${quest.reward}"
            feedbackColor = Color(0.3f, 1f, 0.5f, 1f)
            feedbackTimer = 3f
            onSolved?.invoke(currentQuestId)
        } else {
            feedbackMsg = "✗ Неверно. Попробуй ещё раз."
            feedbackColor = Color(1f, 0.3f, 0.3f, 1f)
            feedbackTimer = 2f
        }
    }

    fun update(delta: Float) {
        if (feedbackTimer > 0f) {
            feedbackTimer -= delta
            if (feedbackTimer <= 0f && feedbackMsg.startsWith("✓")) {
                hide()
            }
        }
    }

    fun render(batch: SpriteBatch, shape: ShapeRenderer) {
        if (!visible) return

        val quest = CipherSystem.quests.find { it.id == currentQuestId } ?: return

        // Затемнение фона
        batch.end()
        shape.begin(ShapeRenderer.ShapeType.Filled)
        shape.setColor(0f, 0f, 0f, 0.72f)
        shape.rect(0f, 0f, screenW, screenH)

        // Панель диалога
        val pw = 680f; val ph = 380f
        val px = screenW / 2f - pw / 2f; val py = screenH / 2f - ph / 2f
        shape.setColor(0.06f, 0.08f, 0.14f, 0.97f)
        shape.rect(px, py, pw, ph)
        shape.setColor(0.4f, 0.8f, 1f, 0.6f)
        // Рамка (4 линии)
        shape.rectLine(px, py, px + pw, py, 2f)
        shape.rectLine(px, py + ph, px + pw, py + ph, 2f)
        shape.rectLine(px, py, px, py + ph, 2f)
        shape.rectLine(px + pw, py, px + pw, py + ph, 2f)
        shape.end()
        batch.begin()

        // Заголовок
        assets.fontMed.color = Color(1f, 0.85f, 0.2f, 1f)
        assets.fontMed.draw(batch, "[ КИРИЛЛ — КВЕСТ ${quest.id} / 3 ]", px + 20f, py + ph - 20f)

        // Зашифрованное послание
        assets.fontSmall.color = Color(0.7f, 0.9f, 1f, 1f)
        assets.fontSmall.draw(batch, "Зашифрованное послание:", px + 20f, py + ph - 60f)
        assets.fontCipher.color = Color(1f, 0.9f, 0.2f, 1f)
        // Длинный текст переносим (упрощённо — первые 40 символов и остальные)
        val enc = quest.encoded
        val line1 = if (enc.length > 42) enc.substring(0, 42) else enc
        val line2 = if (enc.length > 42) enc.substring(42) else ""
        assets.fontCipher.draw(batch, line1, px + 20f, py + ph - 95f)
        if (line2.isNotEmpty()) assets.fontCipher.draw(batch, line2, px + 20f, py + ph - 122f)

        // Тип шифра
        val typeLabel = when (quest.cipherType) {
            CipherSystem.CipherType.CAESAR   -> "Тип: Шифр Цезаря (сдвиг +4)"
            CipherSystem.CipherType.BINARY   -> "Тип: Двоичный код → ASCII"
            CipherSystem.CipherType.VIGENERE -> "Тип: Шифр Виженера (ключ: NILLOW)"
        }
        assets.fontSmall.color = Color(0.5f, 0.8f, 0.6f, 1f)
        assets.fontSmall.draw(batch, typeLabel, px + 20f, py + ph - 160f)

        // Подсказка
        assets.fontSmall.color = Color(0.6f, 0.6f, 0.7f, 1f)
        assets.fontSmall.draw(batch, "Подсказка: ${quest.hint.take(60)}", px + 20f, py + ph - 195f)
        if (quest.hint.length > 60)
            assets.fontSmall.draw(batch, quest.hint.drop(60).take(60), px + 20f, py + ph - 218f)

        // Текущий ввод
        assets.fontSmall.color = Color.WHITE
        assets.fontSmall.draw(batch, "Твой ответ: $currentInput▌", px + 20f, py + ph - 265f)

        // Кнопка ввода
        assets.fontSmall.color = Color(0.3f, 1f, 0.7f, 1f)
        assets.fontSmall.draw(batch, "[ Нажми ПРАВЫЙ джойстик = открыть клавиатуру ]", px + 20f, py + 60f)
        assets.fontSmall.color = Color(0.8f, 0.5f, 0.5f, 1f)
        assets.fontSmall.draw(batch, "[ Нажми ЛЕВЫЙ джойстик = закрыть ]", px + 20f, py + 30f)

        // Feedback
        if (feedbackTimer > 0f) {
            assets.fontMed.color = feedbackColor
            assets.fontMed.draw(batch, feedbackMsg.take(55), px + 20f, py + ph - 310f)
        }
    }
}
