package com.thenillow.game

import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration

class AndroidLauncher : AndroidApplication() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val config = AndroidApplicationConfiguration().apply {
            useImmersiveMode = true   // полноэкранный режим без кнопок
            useAccelerometer = false  // экономим батарею
            useCompass = false
            useGyroscope = false
            numSamples = 2           // MSAA x2 антиалиасинг
        }
        initialize(TheNillowGame(), config)
    }
}
