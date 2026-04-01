package com.thenillow.game.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.thenillow.game.utils.Assets
import com.thenillow.game.utils.Constants
import kotlin.math.min
import kotlin.math.sqrt

/**
 * VirtualJoystick — один виртуальный джойстик.
 * Используется дважды: левый (движение) и правый (прицел/стрельба).
 *
 * Логика как в Brawl Stars:
 *  - Появляется там, где ткнул пальцем (dynamic mode)
 *  - Нормализованный вектор [−1..1] по X и Y
 *  - Палец может выходить за радиус — нолик фиксируется на краю
 */
class VirtualJoystick(
    private val assets: Assets,
    private val baseRadius: Float = Constants.JOYSTICK_RADIUS,
    private val knobRadius: Float = Constants.JOYSTICK_KNOB_RADIUS
) {
    // Позиция центра базы (где ткнул палец)
    var baseX: Float = 0f
    var baseY: Float = 0f

    // Позиция нолика
    var knobX: Float = 0f
    var knobY: Float = 0f

    // Активен ли сейчас
    var active: Boolean = false
        private set

    // Индекс пальца (pointer id)
    var pointer: Int = -1
        private set

    // Результирующий нормализованный вектор [-1..1]
    val direction = Vector2(0f, 0f)

    /** Нажатие пальца — активируем джойстик */
    fun touchDown(screenX: Float, screenY: Float, ptr: Int) {
        active = true
        pointer = ptr
        baseX = screenX
        baseY = screenY
        knobX = screenX
        knobY = screenY
        direction.set(0f, 0f)
    }

    /** Движение пальца — двигаем нолик, считаем направление */
    fun touchDragged(screenX: Float, screenY: Float, ptr: Int) {
        if (!active || pointer != ptr) return

        val dx = screenX - baseX
        val dy = screenY - baseY
        val dist = sqrt(dx * dx + dy * dy)

        if (dist <= baseRadius) {
            knobX = screenX
            knobY = screenY
        } else {
            // Ограничиваем нолик краем базы
            val nx = dx / dist
            val ny = dy / dist
            knobX = baseX + nx * baseRadius
            knobY = baseY + ny * baseRadius
        }

        // Нормализованный вектор (0 в центре, 1 на краю)
        val clampedDist = min(dist, baseRadius)
        direction.x = (knobX - baseX) / baseRadius
        direction.y = -(knobY - baseY) / baseRadius  // Y инвертирован (экран vs мир)
    }

    /** Отпускание пальца — деактивируем */
    fun touchUp(ptr: Int) {
        if (pointer != ptr) return
        active = false
        pointer = -1
        direction.set(0f, 0f)
    }

    /** Рендер базы и нолика */
    fun render(batch: SpriteBatch) {
        if (!active) return

        val baseDiam = baseRadius * 2f
        val knobDiam = knobRadius * 2f

        // База (полупрозрачный круг)
        batch.setColor(1f, 1f, 1f, 0.22f)
        batch.draw(
            assets.joystickBaseTex,
            baseX - baseRadius, baseY - baseRadius,
            baseDiam, baseDiam
        )

        // Нолик
        batch.setColor(1f, 1f, 1f, 0.65f)
        batch.draw(
            assets.joystickKnobTex,
            knobX - knobRadius, knobY - knobRadius,
            knobDiam, knobDiam
        )

        batch.setColor(Color.WHITE)
    }
}
