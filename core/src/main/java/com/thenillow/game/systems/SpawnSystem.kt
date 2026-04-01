package com.thenillow.game.systems

import com.badlogic.gdx.math.MathUtils
import com.thenillow.game.entities.Formula
import com.thenillow.game.entities.Triangle
import com.thenillow.game.utils.Assets
import com.thenillow.game.utils.Constants

/**
 * SpawnSystem — спавнит врагов за экраном вокруг игрока,
 * как в Brawl Stars (враги появляются за краем видимой области).
 */
class SpawnSystem(private val assets: Assets) {

    private var timer: Float = 0f
    private var wave: Int = 1

    val triangles = ArrayList<Triangle>(32)
    val formulas  = ArrayList<Formula>(16)

    var onFormulaShoot: ((Float, Float, Float, Float) -> Unit)? = null

    fun update(delta: Float, playerX: Float, playerY: Float) {
        timer += delta
        if (timer >= Constants.ENEMY_SPAWN_INTERVAL) {
            timer = 0f
            spawnWave(playerX, playerY)
            // Каждые 5 спавнов — новая волна (чуть быстрее)
            if ((triangles.size + formulas.size) % 5 == 0) wave++
        }

        // Обновляем всех врагов
        val ti = triangles.iterator()
        while (ti.hasNext()) {
            val t = ti.next()
            t.update(delta, playerX, playerY)
            if (!t.alive) ti.remove()
        }

        val fi = formulas.iterator()
        while (fi.hasNext()) {
            val f = fi.next()
            f.update(delta, playerX, playerY)
            if (!f.alive) fi.remove()
        }
    }

    private fun spawnWave(px: Float, py: Float) {
        val total = triangles.size + formulas.size
        if (total >= Constants.MAX_ENEMIES) return

        // Спавн за краем экрана (300–600px от игрока)
        val count = MathUtils.random(1, 3)
        repeat(count) {
            val angle = MathUtils.random(360f)
            val dist  = MathUtils.random(320f, 580f)
            val sx = px + MathUtils.cosDeg(angle) * dist
            val sy = py + MathUtils.sinDeg(angle) * dist

            // С увеличением волн появляется больше формул
            if (MathUtils.random() < 0.65f - wave * 0.05f) {
                triangles.add(Triangle(sx, sy, assets))
            } else {
                val f = Formula(sx, sy, assets)
                f.onShoot = onFormulaShoot
                formulas.add(f)
            }
        }
    }

    fun reset() {
        triangles.clear(); formulas.clear()
        timer = 0f; wave = 1
    }

    fun totalEnemies() = triangles.size + formulas.size
}
