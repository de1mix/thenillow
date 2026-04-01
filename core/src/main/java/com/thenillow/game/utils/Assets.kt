package com.thenillow.game.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator

/**
 * Assets — загружает и хранит все текстуры/шрифты.
 * Все текстуры генерируются программно (не нужны PNG-файлы).
 */
class Assets {

    // Текстуры
    lateinit var keyboardTex: Texture
    lateinit var triangleTex: Texture
    lateinit var formulaTex: Texture
    lateinit var laserTex: Texture
    lateinit var particleTex: Texture
    lateinit var groundTex: Texture
    lateinit var treeTex: Texture
    lateinit var joystickBaseTex: Texture
    lateinit var joystickKnobTex: Texture
    lateinit var buttonTex: Texture
    lateinit var pixelTex: Texture       // 1x1 белый пиксель — для ShapeRenderer fallback

    // Шрифты
    lateinit var fontBig: BitmapFont
    lateinit var fontMed: BitmapFont
    lateinit var fontSmall: BitmapFont
    lateinit var fontCipher: BitmapFont  // моноширинный для шифров

    fun loadAll() {
        pixelTex = createPixel(Color.WHITE)
        keyboardTex = createKeyboard()
        triangleTex = createTriangle()
        formulaTex = createFormula()
        laserTex = createLaser()
        particleTex = createCircle(8, Color(0f, 1f, 0.5f, 1f))
        groundTex = createGround()
        treeTex = createTree()
        joystickBaseTex = createCircle(80, Color(1f, 1f, 1f, 0.18f))
        joystickKnobTex = createCircle(44, Color(1f, 1f, 1f, 0.55f))
        buttonTex = createCircle(56, Color(0f, 0.9f, 0.5f, 0.65f))

        fontBig = BitmapFont()
        fontBig.data.setScale(3f)
        fontBig.color = Color.WHITE

        fontMed = BitmapFont()
        fontMed.data.setScale(1.8f)
        fontMed.color = Color.WHITE

        fontSmall = BitmapFont()
        fontSmall.data.setScale(1.2f)
        fontSmall.color = Color(0.8f, 1f, 0.8f, 1f)

        fontCipher = BitmapFont()
        fontCipher.data.setScale(1.5f)
        fontCipher.color = Color(1f, 0.85f, 0.2f, 1f)
    }

    // ── Генераторы текстур ──────────────────────────────────────────────

    private fun createPixel(color: Color): Texture {
        val pm = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pm.setColor(color); pm.fill()
        return Texture(pm).also { pm.dispose() }
    }

    /** Клавиатура — прямоугольник с сеткой клавиш */
    private fun createKeyboard(): Texture {
        val w = 96; val h = 52
        val pm = Pixmap(w, h, Pixmap.Format.RGBA8888)
        pm.setColor(0.15f, 0.15f, 0.22f, 1f); pm.fill()
        pm.setColor(0.3f, 0.8f, 1f, 1f)
        pm.drawRectangle(0, 0, w, h)
        // Ряды клавиш
        val cols = 10; val rows = 3
        val kw = 7; val kh = 8; val gx = 2; val gy = 3
        for (r in 0 until rows) for (c in 0 until cols) {
            val x = 4 + c * (kw + gx); val y = 6 + r * (kh + gy)
            pm.setColor(0.4f, 0.9f, 1f, 0.9f)
            pm.fillRectangle(x, y, kw, kh)
        }
        // Пробел
        pm.setColor(0f, 1f, 0.6f, 1f)
        pm.fillRectangle(20, 38, 56, 9)
        return Texture(pm).also { pm.dispose() }
    }

    /** Треугольник-враг */
    private fun createTriangle(): Texture {
        val sz = 64
        val pm = Pixmap(sz, sz, Pixmap.Format.RGBA8888)
        pm.setColor(1f, 0.25f, 0.25f, 1f)
        // Рисуем равнобедренный треугольник как серию горизонтальных линий
        for (y in 0 until sz) {
            val progress = y.toFloat() / sz
            val halfWidth = (progress * sz / 2).toInt()
            val cx = sz / 2
            pm.drawLine(cx - halfWidth, sz - 1 - y, cx + halfWidth, sz - 1 - y)
        }
        pm.setColor(1f, 0.6f, 0.6f, 1f)
        pm.drawLine(0, sz - 1, sz / 2, 0)
        pm.drawLine(sz - 1, sz - 1, sz / 2, 0)
        pm.drawLine(0, sz - 1, sz - 1, sz - 1)
        return Texture(pm).also { pm.dispose() }
    }

    /** Формула-враг */
    private fun createFormula(): Texture {
        val w = 80; val h = 40
        val pm = Pixmap(w, h, Pixmap.Format.RGBA8888)
        pm.setColor(1f, 0.85f, 0.1f, 1f); pm.fill()
        pm.setColor(0.1f, 0.1f, 0.1f, 1f)
        pm.drawRectangle(0, 0, w, h)
        return Texture(pm).also { pm.dispose() }
    }

    /** Лазерный снаряд */
    private fun createLaser(): Texture {
        val w = 8; val h = 24
        val pm = Pixmap(w, h, Pixmap.Format.RGBA8888)
        for (y in 0 until h) {
            val alpha = 1f - Math.abs(y - h / 2f) / (h / 2f) * 0.3f
            pm.setColor(0.1f, 1f, 0.5f, alpha)
            pm.drawLine(0, y, w - 1, y)
        }
        return Texture(pm).also { pm.dispose() }
    }

    private fun createCircle(diameter: Int, color: Color): Texture {
        val pm = Pixmap(diameter, diameter, Pixmap.Format.RGBA8888)
        pm.setColor(color)
        pm.fillCircle(diameter / 2, diameter / 2, diameter / 2)
        return Texture(pm).also { pm.dispose() }
    }

    private fun createGround(): Texture {
        val w = 128; val h = 32
        val pm = Pixmap(w, h, Pixmap.Format.RGBA8888)
        pm.setColor(0.2f, 0.5f, 0.15f, 1f); pm.fill()
        pm.setColor(0.3f, 0.7f, 0.2f, 1f)
        for (x in 0 until w step 16) pm.fillRectangle(x, 0, 8, 8)
        return Texture(pm).also { pm.dispose() }
    }

    /** Дерево с корнями ВВЕРХУ (сломанная физика) */
    private fun createTree(): Texture {
        val w = 48; val h = 80
        val pm = Pixmap(w, h, Pixmap.Format.RGBA8888)
        // Ствол
        pm.setColor(0.45f, 0.28f, 0.1f, 1f)
        pm.fillRectangle(w / 2 - 4, 20, 8, 45)
        // Крона (снизу, потому что дерево перевёрнуто)
        pm.setColor(0.15f, 0.65f, 0.25f, 1f)
        pm.fillCircle(w / 2, 52, 20)
        // Корни (сверху — мир сломан!)
        pm.setColor(0.5f, 0.32f, 0.12f, 1f)
        pm.drawLine(w / 2, 20, w / 2 - 14, 4)
        pm.drawLine(w / 2, 20, w / 2 + 14, 4)
        pm.drawLine(w / 2, 20, w / 2, 0)
        return Texture(pm).also { pm.dispose() }
    }

    fun dispose() {
        keyboardTex.dispose(); triangleTex.dispose(); formulaTex.dispose()
        laserTex.dispose(); particleTex.dispose(); groundTex.dispose()
        treeTex.dispose(); joystickBaseTex.dispose(); joystickKnobTex.dispose()
        buttonTex.dispose(); pixelTex.dispose()
        fontBig.dispose(); fontMed.dispose(); fontSmall.dispose(); fontCipher.dispose()
    }
}
