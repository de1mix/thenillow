package com.thenillow.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.thenillow.game.TheNillowGame
import com.thenillow.game.utils.Constants

class MenuScreen(private val game: TheNillowGame) : ScreenAdapter() {

    private lateinit var cam: OrthographicCamera
    private var animTimer = 0f

    // Декоративные треугольники на фоне
    data class BgTri(var x: Float, var y: Float, var rot: Float, var spd: Float, var sz: Float)
    private val bgTris = List(14) {
        BgTri(
            MathUtils.random(0f, 1f),
            MathUtils.random(0f, 1f),
            MathUtils.random(360f),
            MathUtils.random(20f, 80f),
            MathUtils.random(30f, 90f)
        )
    }

    override fun show() {
        cam = OrthographicCamera()
        cam.setToOrtho(false, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())

        Gdx.input.inputProcessor = object : InputAdapter() {
            override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                game.startGame()
                return true
            }
        }
    }

    override fun render(delta: Float) {
        animTimer += delta

        Gdx.gl.glClearColor(0.04f, 0.05f, 0.14f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        val W = Gdx.graphics.width.toFloat()
        val H = Gdx.graphics.height.toFloat()

        // Фоновые треугольники
        game.shapeRenderer.projectionMatrix = cam.combined
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        for (t in bgTris) {
            t.rot += t.spd * delta
            val alpha = 0.06f + MathUtils.sin(animTimer + t.x * 5f) * 0.04f
            game.shapeRenderer.setColor(0.3f, 0.6f, 1f, alpha)
            val cx = t.x * W; val cy = t.y * H; val s = t.sz
            game.shapeRenderer.triangle(cx, cy - s/2, cx - s/2, cy + s/2, cx + s/2, cy + s/2)
        }
        game.shapeRenderer.end()

        // Текст
        game.batch.projectionMatrix = cam.combined
        game.batch.begin()

        val pulse = 0.85f + MathUtils.sin(animTimer * 2.5f) * 0.15f

        // Заголовок
        game.assets.fontBig.color = Color(0.3f, 1f * pulse, 0.6f * pulse, 1f)
        game.assets.fontBig.draw(game.batch, "THENILLOW", W / 2f - 160f, H / 2f + 100f)

        game.assets.fontMed.color = Color(0.7f, 0.85f, 1f, 0.9f)
        game.assets.fontMed.draw(game.batch, "& Восстание Геометрии", W / 2f - 145f, H / 2f + 55f)

        // Подзаголовок
        game.assets.fontSmall.color = Color(0.5f, 0.6f, 0.5f, 0.7f)
        game.assets.fontSmall.draw(game.batch, "Всратый Open-World RPG", W / 2f - 100f, H / 2f + 10f)

        // Старт
        val tapAlpha = 0.5f + MathUtils.sin(animTimer * 3f) * 0.5f
        game.assets.fontMed.color = Color(1f, 1f, 1f, tapAlpha)
        game.assets.fontMed.draw(game.batch, "► Коснись экрана для старта ◄", W / 2f - 185f, H / 2f - 50f)

        // Описание
        game.assets.fontSmall.color = Color(0.6f, 0.6f, 0.65f, 0.8f)
        game.assets.fontSmall.draw(game.batch, "Левый джойстик = движение", W / 2f - 105f, H / 2f - 100f)
        game.assets.fontSmall.draw(game.batch, "Правый джойстик = прицел + стрельба лазером", W / 2f - 190f, H / 2f - 124f)
        game.assets.fontSmall.draw(game.batch, "Найди Кирилла → реши шифры → выживи", W / 2f - 160f, H / 2f - 148f)

        // Версия
        game.assets.fontSmall.color = Color(0.3f, 0.3f, 0.35f, 0.6f)
        game.assets.fontSmall.draw(game.batch, "v1.0  |  LibGDX  |  Kotlin", W / 2f - 80f, 28f)

        game.batch.end()
    }

    override fun resize(width: Int, height: Int) {
        cam.setToOrtho(false, width.toFloat(), height.toFloat())
    }

    override fun hide() {
        Gdx.input.inputProcessor = null
    }
}
