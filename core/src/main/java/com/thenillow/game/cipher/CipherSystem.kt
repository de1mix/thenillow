package com.thenillow.game.cipher

/**
 * Система шифров Кирилла.
 * Квесты выдаются зашифрованными — игрок РЕАЛЬНО решает шифры.
 *
 * Шифры:
 *  1. Шифр Цезаря (сдвиг +4 по русскому алфавиту)
 *  2. Двоичный код → ASCII
 *  3. Шифр Виженера (ключ: NILLOW, по русскому)
 */
object CipherSystem {

    data class CipherQuest(
        val id: Int,
        val kirillSpeech: String,          // что говорит Кирилл (зашифрованно)
        val encoded: String,               // зашифрованное задание
        val hint: String,                  // подсказка
        val answer: String,                // правильный ответ (нижний регистр)
        val reward: String,                // что открывается после решения
        val cipherType: CipherType
    )

    enum class CipherType { CAESAR, BINARY, VIGENERE }

    private val RU = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя"

    // ── Шифр Цезаря (русский, сдвиг +4) ──────────────────────────────

    fun caesarEncode(text: String, shift: Int = 4): String {
        return text.lowercase().map { c ->
            val idx = RU.indexOf(c)
            if (idx >= 0) RU[(idx + shift + RU.length) % RU.length] else c
        }.joinToString("")
    }

    fun caesarDecode(text: String, shift: Int = 4): String {
        return caesarEncode(text, RU.length - shift)
    }

    // ── Двоичный код (ASCII) ──────────────────────────────────────────

    fun textToBinary(text: String): String =
        text.uppercase().map { c ->
            c.code.toString(2).padStart(8, '0')
        }.joinToString(" ")

    fun binaryToText(binary: String): String =
        binary.trim().split(" ").map { byte ->
            byte.toInt(2).toChar()
        }.joinToString("")

    // ── Шифр Виженера (ключ NILLOW, на транслите для простоты) ────────

    private val LAT = "abcdefghijklmnopqrstuvwxyz"

    fun vigenereEncode(text: String, key: String = "nillow"): String {
        val t = text.lowercase().filter { it.isLetter() }
        val k = key.lowercase()
        var ki = 0
        return t.map { c ->
            val ci = LAT.indexOf(c)
            if (ci >= 0) {
                val kc = LAT.indexOf(k[ki % k.length])
                ki++
                LAT[(ci + kc) % 26]
            } else c
        }.joinToString("")
    }

    fun vigenereDecode(text: String, key: String = "nillow"): String {
        val t = text.lowercase()
        val k = key.lowercase()
        var ki = 0
        return t.map { c ->
            val ci = LAT.indexOf(c)
            if (ci >= 0) {
                val kc = LAT.indexOf(k[ki % k.length])
                ki++
                LAT[(ci - kc + 26) % 26]
            } else c
        }.joinToString("")
    }

    // ── Квесты ────────────────────────────────────────────────────────

    val quests: List<CipherQuest> = listOf(
        CipherQuest(
            id = 1,
            kirillSpeech = "Хм-хм... ${caesarEncode("ПЩЗЙВМ ЖЗЙКЗОЁЛ ЗЙ ЩКРЩГ")}",
            encoded = caesarEncode("иди на восток найди пещеру"),
            hint = "Шифр Цезаря. Каждая буква сдвинута на 4 вперёд по алфавиту. Сдвинь назад.",
            answer = "иди на восток найди пещеру",
            reward = "Открыта локация: Пещера Треугольников",
            cipherType = CipherType.CAESAR
        ),
        CipherQuest(
            id = 2,
            kirillSpeech = "Ты не сдался? Тогда вот следующее...",
            encoded = textToBinary("NORTH TOWER"),
            hint = "Двоичный код. Каждые 8 бит = 1 символ ASCII. Переведи в буквы.",
            answer = "north tower",
            reward = "Открыта локация: Северная Башня Формул",
            cipherType = CipherType.BINARY
        ),
        CipherQuest(
            id = 3,
            kirillSpeech = "Последний... если осмелишься.",
            encoded = vigenereEncode("boss lair"),
            hint = "Шифр Виженера. Ключ: NILLOW. Расшифруй каждую букву вычитая ключ.",
            answer = "boss lair",
            reward = "Открыта финальная локация: Логово Финального Босса",
            cipherType = CipherType.VIGENERE
        )
    )

    /** Проверяет ответ игрока (регистронезависимо, игнорирует пробелы в начале/конце) */
    fun checkAnswer(questId: Int, playerInput: String): Boolean {
        val quest = quests.find { it.id == questId } ?: return false
        return playerInput.trim().lowercase() == quest.answer.lowercase()
    }
}
