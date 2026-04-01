package com.thenillow.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.viewport.FitViewport
import com.thenillow.game.TheNillowGame
import com.thenillow.game.entities.*
import com.thenillow.game.systems.ParticleSystem
import com.thenillow.game.systems.PhysicsSystem
import com.thenillow.game.systems.SpawnSystem
import com.thenillow.game.ui.*
import com.thenillow.game.utils.*

/**
 * GameScreen — основной игровой экран.
 *
 * Содержит:
 *  - Двойной джойстик (Brawl Stars-стиль)
 *  - Открытый мир с камерой
 *  - Врагов (треугольники + формулы)
 *  - Систему лазеров игрока
 *  - NPC Кирилл с диалогом шифров
 *  - Физику с инверсией гравитации
 *  - Частицы взрывов
 *  - HUD
 */
class GameScreen(private val game: TheNillowGame) : ScreenAdapter() {

    // ── Рендер ────────────────────────────────────────────────────────
    private lateinit var worldCam: OrthographicCamera    // камера мира
    private lateinit var uiCam: OrthographicCamera       // камера HUD (фиксирована)
    private lateinit var viewport: FitViewport

    private val SW get() = Gdx.graphics.width.toFloat()
    private val SH get() = Gdx.graphics.height.toFloat()

    // ── Игровые системы ───────────────────────────────────────────────
    private lateinit var physics: PhysicsSystem
    private lateinit var spawn: SpawnSystem
    private lateinit var particles: ParticleSystem
    private lateinit var camHelper: CameraHelper
    private lateinit var worldRenderer: WorldRenderer

    // ── Сущности ──────────────────────────────────────────────────────
    private lateinit var player: Player
    private lateinit var kirill: Kirill
    private val lasers      = ArrayList<Laser>(64)
    private val enemyBullets = ArrayList<EnemyBullet>(32)

    // ── UI ────────────────────────────────────────────────────────────
    private lateinit var leftJoy: VirtualJoystick
    private lateinit var rightJoy: VirtualJoystick
    private lateinit var touchInput: TouchInputHandler
    private lateinit var hud: HUD
    private lateinit var cipherDialog: CipherDialog

    // ── Состояние ─────────────────────────────────────────────────────
    private var solvedQuests = 0
    private var nextQuestId  = 1
    private var gameOver     = false
    private var gameOverTimer = 0f
    private var gravFlipFlash = 0f   // белая вспышка при смене гравитации

    override fun show() {
        val assets = game.assets

        // Камеры
        worldCam = OrthographicCamera()
        uiCam    = OrthographicCamera()
        viewport = FitViewport(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT, worldCam)
        uiCam.setToOrtho(false, SW, SH)

        // Системы
        physics  = PhysicsSystem()
        spawn    = SpawnSystem(assets)
        particles = ParticleSystem(assets.particleTex)
        camHelper = CameraHelper(worldCam)
        worldRenderer = WorldRenderer(assets, game.shapeRenderer)

        // Игрок в центре мира
        val startX = Constants.WORLD_WIDTH  / 2f - Constants.PLAYER_WIDTH  / 2f
        val startY = Constants.WORLD_HEIGHT / 2f - Constants.PLAYER_HEIGHT / 2f
        player = Player(startX, startY, assets)
        player.onShoot = { ox, oy, dx, dy ->
            lasers.add(Laser(ox, oy, dx, dy, assets))
        }

        // NPC Кирилл рядом со стартом
        kirill = Kirill(startX + 200f, startY - 40f, assets)

        // Камера стартует на игроке
        worldCam.position.set(startX, startY, 0f)
        worldCam.update()

        // UI
        leftJoy  = VirtualJoystick(assets)
        rightJoy = VirtualJoystick(assets)
        touchInput = TouchInputHandler(leftJoy, rightJoy, SW, SH)
        hud = HUD(assets, SW, SH)

        cipherDialog = CipherDialog(assets, SW, SH)
        cipherDialog.onSolved = { questId ->
            solvedQuests++
            nextQuestId = questId + 1
            // Спавним бонусных врагов после решения квеста
            repeat(3) { spawn.update(0f, player.centerX(), player.centerY()) }
        }

        // Спавн стреляет снарядами
        spawn.onFormulaShoot = { ox, oy, dx, dy ->
            enemyBullets.add(EnemyBullet(ox, oy, dx, dy, assets))
        }

        // Callback смены гравитации
        physics.onFlip = { flipped ->
            gravFlipFlash = 0.4f
            camHelper.shake(18f, 0.5f)
            // Частицы по центру экрана (в мировых координатах)
            particles.gravityFlash(player.centerX(), player.centerY())
            player.gravityFlipped = flipped
        }

        // Ввод
        Gdx.input.inputProcessor = touchInput
    }

    override fun render(delta: Float) {
        val dt = delta.coerceAtMost(0.05f)   // cap чтобы не прыгало при лагах

        // ── Update ────────────────────────────────────────────────────
        if (!gameOver && !cipherDialog.visible) {
            updateGame(dt)
        }
        cipherDialog.update(dt)

        if (gameOver) {
            gameOverTimer += dt
            if (gameOverTimer > 3f) game.setScreen(MenuScreen(game))
            return
        }

        // ── Clear ─────────────────────────────────────────────────────
        Gdx.gl.glClearColor(0.04f, 0.06f, 0.12f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // ── Мир (world camera) ────────────────────────────────────────
        game.batch.projectionMatrix = worldCam.combined
        game.batch.begin()

        worldRenderer.render(game.batch, worldCam, physics.gravityFlipped)

        // Вспышка при смене гравитации
        if (gravFlipFlash > 0f) {
            val alpha = (gravFlipFlash / 0.4f) * 0.55f
            game.batch.setColor(0.3f, 0.8f, 1f, alpha)
            game.batch.draw(game.assets.pixelTex,
                worldCam.position.x - Constants.VIEWPORT_WIDTH,
                worldCam.position.y - Constants.VIEWPORT_HEIGHT,
                Constants.VIEWPORT_WIDTH * 2f, Constants.VIEWPORT_HEIGHT * 2f)
            game.batch.setColor(Color.WHITE)
            gravFlipFlash -= dt
        }

        // Сущности
        kirill.render(game.batch)
        lasers.forEach { if (it.alive) it.render(game.batch) }
        enemyBullets.forEach { if (it.alive) it.render(game.batch) }
        spawn.triangles.forEach { if (it.alive) renderTriangle(it) }
        spawn.formulas.forEach  { if (it.alive) renderFormula(it) }
        player.render(game.batch)
        particles.render(game.batch)

        game.batch.end()

        // ── HUD (ui camera) ───────────────────────────────────────────
        game.batch.projectionMatrix = uiCam.combined
        game.batch.begin()
        hud.render(game.batch, game.shapeRenderer,
            player, physics, leftJoy, rightJoy,
            kirill.isNearPlayer, solvedQuests)

        cipherDialog.render(game.batch, game.shapeRenderer)

        if (gameOver) {
            hud.renderCenterMessage(game.batch, "GAME OVER", Color(1f, 0.3f, 0.3f, 1f))
        }
        game.batch.end()
    }

    // ── Внутренние update ─────────────────────────────────────────────

    private fun updateGame(dt: Float) {
        physics.update(dt)
        camHelper.update(dt, player.centerX(), player.centerY())

        // Джойстики → ввод игрока
        player.handleInput(leftJoy.direction, rightJoy.direction, dt)
        player.update(dt)

        // Границы мира для игрока
        player.x = player.x.coerceIn(0f, Constants.WORLD_WIDTH  - player.width)
        player.y = player.y.coerceIn(0f, Constants.WORLD_HEIGHT - player.height)

        // Кирилл
        kirill.update(dt, player.centerX(), player.centerY())

        // Нажатие правого джойстика = открыть диалог Кирилла
        // (определяем по tap — rightJoy.direction близко к нулю, но active)
        if (kirill.isNearPlayer && rightJoy.active && rightJoy.direction.len() < 0.15f) {
            if (nextQuestId <= 3) {
                cipherDialog.show(nextQuestId)
            }
        }
        // Нажатие левого джойстика когда диалог открыт = закрыть / открыть клавиатуру
        if (cipherDialog.visible) {
            if (leftJoy.active && leftJoy.direction.len() < 0.15f) cipherDialog.hide()
            if (rightJoy.active && rightJoy.direction.len() < 0.15f) cipherDialog.openKeyboard()
        }

        // Лазеры
        lasers.forEach { it.update(dt) }
        lasers.removeAll { !it.alive }

        // Снаряды врагов
        enemyBullets.forEach { it.update(dt) }
        enemyBullets.removeAll { !it.alive }

        // Враги
        spawn.update(dt, player.centerX(), player.centerY())

        particles.update(dt)

        checkCollisions()

        if (!player.alive) {
            gameOver = true
        }
    }

    private fun checkCollisions() {
        // Лазер vs Треугольники
        val laserIter = lasers.iterator()
        while (laserIter.hasNext()) {
            val laser = laserIter.next()
            if (!laser.alive) continue
            for (tri in spawn.triangles) {
                if (tri.alive && laser.overlaps(tri)) {
                    tri.takeDamage(Constants.LASER_DAMAGE)
                    laser.alive = false
                    if (!tri.alive) {
                        player.score += 10
                        particles.explode(tri.centerX(), tri.centerY(),
                            r = 1f, g = 0.3f, b = 0.3f)
                        camHelper.shake(6f, 0.15f)
                    }
                    break
                }
            }
            if (!laser.alive) continue
            // Лазер vs Формулы
            for (form in spawn.formulas) {
                if (form.alive && laser.overlaps(form)) {
                    form.takeDamage(Constants.LASER_DAMAGE)
                    laser.alive = false
                    if (!form.alive) {
                        player.score += 20
                        particles.explode(form.centerX(), form.centerY(),
                            r = 1f, g = 0.85f, b = 0.1f)
                        camHelper.shake(6f, 0.15f)
                    }
                    break
                }
            }
        }

        // Треугольник vs Игрок
        for (tri in spawn.triangles) {
            if (tri.alive && tri.overlaps(player)) {
                player.onHit(tri.damage)
                camHelper.shake(10f, 0.25f)
            }
        }

        // Снаряд формулы vs Игрок
        val bulletIter = enemyBullets.iterator()
        while (bulletIter.hasNext()) {
            val b = bulletIter.next()
            if (b.alive && b.overlaps(player)) {
                player.onHit(b.damage)
                b.alive = false
                camHelper.shake(8f, 0.2f)
            }
        }
    }

    // ── Рендер врагов с текстом ───────────────────────────────────────

    private fun renderTriangle(t: Triangle) {
        game.batch.draw(
            t.texture,
            t.x, t.y,
            t.width / 2f, t.height / 2f,
            t.width, t.height,
            1f, 1f,
            t.rotation,
            0, 0, t.texture.width, t.texture.height,
            false, false
        )
        // HP бар над врагом
        renderEntityHpBar(t.x, t.y + t.height + 4f, t.width, t.hp, t.maxHp)
    }

    private fun renderFormula(f: Formula) {
        game.batch.draw(f.texture, f.x, f.y, f.width, f.height)
        // Текст формулы
        game.assets.fontSmall.color = Color(0.1f, 0.1f, 0.1f, 1f)
        game.assets.fontSmall.draw(game.batch, f.formulaText, f.x + 4f, f.y + f.height - 6f)
        renderEntityHpBar(f.x, f.y + f.height + 4f, f.width, f.hp, f.maxHp)
    }

    private fun renderEntityHpBar(x: Float, y: Float, w: Float, hp: Int, maxHp: Int) {
        game.batch.end()
        game.shapeRenderer.projectionMatrix = worldCam.combined
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        game.shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 0.6f)
        game.shapeRenderer.rect(x, y, w, 5f)
        val frac = hp.toFloat() / maxHp
        game.shapeRenderer.setColor(1f - frac, frac, 0.1f, 1f)
        game.shapeRenderer.rect(x, y, w * frac, 5f)
        game.shapeRenderer.end()
        game.batch.begin()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
        uiCam.setToOrtho(false, width.toFloat(), height.toFloat())
    }

    override fun hide() {
        Gdx.input.inputProcessor = null
    }

    override fun dispose() {
        particles.clear()
    }
}
