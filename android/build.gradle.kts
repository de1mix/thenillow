plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.thenillow.game"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.thenillow.game"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    sourceSets {
        getByName("main") {
            assets.srcDirs("../assets")
            // Мы НЕ указываем jniLibs вручную, чтобы избежать конфликтов
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
        jniLibs {
            // Разрешаем выбирать первую библиотеку при дубликатах
            pickFirsts += "lib/**/*.so"
        }
    }
}

dependencies {
    val gdxVersion = "1.12.1"
    
    implementation(project(":core"))
    implementation("com.badlogicgames.gdx:gdx-backend-android:$gdxVersion")
    
    // Вместо "natives" используем "runtimeOnly", чтобы Android сам их упаковал
    val platforms = listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
    platforms.forEach { platform ->
        runtimeOnly("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-$platform")
        runtimeOnly("com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-$platform")
    }
}
