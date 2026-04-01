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

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
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
    "natives"("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-armeabi-v7a")
    "natives"("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-arm64-v8a")
    "natives"("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-x86")
    "natives"("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-x86_64")
    "natives"("com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-armeabi-v7a")
    "natives"("com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-arm64-v8a")
    "natives"("com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-x86")
    "natives"("com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-x86_64")
}

tasks.register("copyNatives") {
    doLast {
        configurations.getByName("natives").resolvedConfiguration.resolvedArtifacts.forEach { artifact ->
            val name = artifact.classifier ?: return@forEach
            copy {
                from(zipTree(artifact.file))
                into("src/main/jniLibs/$name")
                include("*.so")
            }
        }
    }
}

tasks.whenTaskAdded {
    if (name == "mergeDebugNativeLibs" || name == "mergeReleaseNativeLibs") {
        dependsOn("copyNatives")
    }
}

configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "com.android.support" && !requested.name.contains("multidex")) {
            useVersion("27.1.1")
        }
    }
}
