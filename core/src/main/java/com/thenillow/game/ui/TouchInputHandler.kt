package com.thenillow.game.ui

import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.math.Vector2

/**
 * TouchInputHandler — разделяет экран на левую и правую зоны.
 *
 * Левая половина экрана → левый джойстик (движение)
 * Правая половина экрана → правый джойстик (прицел/стрельба)
 *
 * Поддерживает мультитач — можно держать оба джойстика одновременно.
 */
class TouchInputHandler(
    private val leftJoystick: VirtualJoystick,
    private val rightJoystick: VirtualJoystick,
    private val screenWidth: Float,
    private val screenHeight: Float
) : InputAdapter() {

    // Какой джойстик получил какой pointer
    private val pointerSide = mutableMapOf<Int, Side>()

    enum class Side { LEFT, RIGHT }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val x = screenX.toFloat()
        // LibGDX: Y=0 сверху, нам нужно снизу
        val y = screenHeight - screenY.toFloat()

        return if (x < screenWidth / 2f) {
            pointerSide[pointer] = Side.LEFT
            leftJoystick.touchDown(x, y, pointer)
            true
        } else {
            pointerSide[pointer] = Side.RIGHT
            rightJoystick.touchDown(x, y, pointer)
            true
        }
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        val x = screenX.toFloat()
        val y = screenHeight - screenY.toFloat()

        return when (pointerSide[pointer]) {
            Side.LEFT  -> { leftJoystick.touchDragged(x, y, pointer); true }
            Side.RIGHT -> { rightJoystick.touchDragged(x, y, pointer); true }
            else       -> false
        }
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val side = pointerSide.remove(pointer) ?: return false
        return when (side) {
            Side.LEFT  -> { leftJoystick.touchUp(pointer); true }
            Side.RIGHT -> { rightJoystick.touchUp(pointer); true }
        }
    }
}
