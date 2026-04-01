plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

val gdxVersion = "1.12.1"

android {
    namespace = "com.thenillow.game"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.thenillow.game"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    // ДОБАВЬ ЭТОТ БЛОК НИЖЕ:
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
        jniLibs {
            useLegacyPackaging = true
            pickFirsts += "lib/**/*.so" // Это решит проблему с дубликатами
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    // ... остальной код (compileOptions, kotlinOptions и т.д.) без изменений
}

    buildTypes {
        getByName("release") {
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
            jniLibs.srcDirs("src/main/jniLibs")
        }
    }
}

configurations {
    create("natives")
}

dependencies {
    implementation(project(":core"))
    api("com.badlogicgames.gdx:gdx-backend-android:$gdxVersion")
    
    val platforms = listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
    platforms.forEach { platform ->
        "natives"("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-$platform")
        "natives"("com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-$platform")
    }
}

tasks.register("copyNatives") {
    doLast {
        configurations.getByName("natives").resolvedConfiguration.resolvedArtifacts.forEach { artifact ->
            val name = artifact.classifier
            if (name != null) {
                copy {
                    from(zipTree(artifact.file))
                    into("src/main/jniLibs/$name")
                    include("*.so")
                }
            }
        }
    }
}

project.afterEvaluate {
    tasks.all {
        if (name.contains("merge") && name.contains("NativeLibs")) {
            dependsOn("copyNatives")
        }
    }
}

// Глобальные фиксы для всех зависимостей
configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "com.android.support") {
            useVersion("28.0.0")
        }
    }
}
