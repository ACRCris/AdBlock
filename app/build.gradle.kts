plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.copiloto.addblock"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.copiloto.addblock"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    // Robolectric necesita los recursos Android
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

// Configuración de Mockito como Java Agent (requerido para Java 21+)
val mockitoAgent: Configuration by configurations.creating {
    isTransitive = false
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)
    implementation(libs.core.ktx)

    // Jetpack Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.activity.compose)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons)
    implementation(libs.navigation.compose)
    debugImplementation(libs.compose.ui.tooling)

    // Unit Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.robolectric)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.arch.core.testing)
    testImplementation(libs.turbine)
    testImplementation(libs.androidx.test.core)

    // Mockito agent para Java 21+
    mockitoAgent(libs.mockito.core)

    // Instrumented Testing
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.ui.test.junit4)
    debugImplementation(libs.compose.ui.test.manifest)
}

// Perfil C: Tests con Java 21 (requerido por Robolectric 4.16 + SDK 36)
tasks.withType<Test>().configureEach {
    // Fuerza Java 21 SOLO para los tests
    javaLauncher.set(
        javaToolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    )

    // Mockito como Java Agent (evita warnings en Java 21+)
    // Usamos doFirst para evitar resolver la configuración durante la fase de configuración
    doFirst {
        jvmArgs("-javaagent:${mockitoAgent.singleFile.absolutePath}")
    }

    // Add-opens necesarios para Robolectric/Mockito
    jvmArgs(
        "--add-opens", "java.base/java.lang=ALL-UNNAMED",
        "--add-opens", "java.base/java.lang.reflect=ALL-UNNAMED",
        "--add-opens", "java.base/java.util=ALL-UNNAMED",
        "--add-opens", "java.base/java.io=ALL-UNNAMED"
    )

    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
    }
}
