package com.thenillow.game

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.thenillow.game.screens.GameScreen
import com.thenillow.game.screens.MenuScreen
import com.thenillow.game.utils.Assets

class TheNillowGame : Game() {

    lateinit var batch: SpriteBatch
    lateinit var shapeRenderer: ShapeRenderer
    lateinit var assets: Assets

    override fun create() {
        batch = SpriteBatch()
        shapeRenderer = ShapeRenderer()
        assets = Assets()
        assets.loadAll()

        // Запускаем с меню
        setScreen(MenuScreen(this))
    }

    fun startGame() {
        setScreen(GameScreen(this))
    }

    override fun dispose() {
        batch.dispose()
        shapeRenderer.dispose()
        assets.dispose()
    }
}
