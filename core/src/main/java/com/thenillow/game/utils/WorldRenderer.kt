package com.thenillow.game.utils

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.thenillow.game.utils.Assets
import com.thenillow.game.utils.Constants

/**
 * WorldRenderer — рисует задний план открытого мира:
 *   - Градиентный фон (синий / фиолетовый при перевёрнутой гравитации)
 *   - Тайловая земля (снизу и сверху при flip)
 *   - Декоративные перевёрнутые деревья
 *   - Координатная сетка (лёгкая)
 */
class WorldRenderer(
    private val assets: Assets,
    private val shape: ShapeRenderer
) {
    // Позиции деревьев (генерируются один раз)
    private val trees: List<Pair<Float, Float>> = List(22) {
        Pair(
            (it * 117f + 80f) % Constants.WORLD_WIDTH,
            Constants.WORLD_HEIGHT * 0.5f + (if (it % 2 == 0) 180f else -180f)
        )
    }

    fun render(batch: SpriteBatch, camera: OrthographicCamera, gravFlipped: Boolean) {
        // ── Фон ───────────────────────────────────────────────────────
        batch.end()
        shape.projectionMatrix = camera.combined
        shape.begin(ShapeRenderer.ShapeType.Filled)

        if (!gravFlipped) {
            shape.setColor(0.05f, 0.08f, 0.18f, 1f)
        } else {
            // Перевёрнутая гравитация — мрачно-фиолетовый
            shape.setColor(0.12f, 0.04f, 0.22f, 1f)
        }
        shape.rect(0f, 0f, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT)

        // Земля (повторяющиеся плиты снизу и сверху)
        val groundH = 64f
        shape.setColor(if (gravFlipped) 0.35f else 0.18f, 0.48f, 0.12f, 1f)
        shape.rect(0f, 0f, Constants.WORLD_WIDTH, groundH)
        shape.rect(0f, Constants.WORLD_HEIGHT - groundH, Constants.WORLD_WIDTH, groundH)

        // Лёгкая сетка для ощущения движения
        shape.setColor(1f, 1f, 1f, 0.03f)
        var gx = 0f
        while (gx <= Constants.WORLD_WIDTH) {
            shape.rectLine(gx, 0f, gx, Constants.WORLD_HEIGHT, 1f)
            gx += 160f
        }
        var gy = 0f
        while (gy <= Constants.WORLD_HEIGHT) {
            shape.rectLine(0f, gy, Constants.WORLD_WIDTH, gy, 1f)
            gy += 160f
        }

        shape.end()
        batch.begin()

        // ── Деревья с корнями ВВЕРХУ ───────────────────────────────────
        for ((tx, ty) in trees) {
            batch.draw(assets.treeTex, tx, ty, 48f, 80f)
        }

        // ── Земляные плиты ─────────────────────────────────────────────
        var x = 0f
        while (x < Constants.WORLD_WIDTH) {
            batch.draw(assets.groundTex, x, 0f, 128f, 32f)
            batch.draw(assets.groundTex, x, Constants.WORLD_HEIGHT - 32f, 128f, 32f)
            x += 128f
        }
    }
}
