plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "com.ignaherner.stocky"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.ignaherner.stocky"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Helpers Kolin para Android (extensiones, mas comodo)
    implementation(libs.androidx.core.ktx)

    // lifecycle + coroutines helpers
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // permite setear Compose en una Activity (setContent)
    implementation(libs.androidx.activity.compose)

    // Fija versiones compatibles entre librerías Compose.
    implementation(platform(libs.androidx.compose.bom))

    // ui: base de compose
    implementation(libs.androidx.compose.ui)

    // graphics: cosas de dibujo
    implementation(libs.androidx.compose.ui.graphics)

    // tooling.preview : preview en Android Studio
    implementation(libs.androidx.compose.ui.tooling.preview)

    // material3: componentes Material You
    implementation(libs.androidx.compose.material3)

    // --- NUEVO: Lifecycle para Compose (recomendado) ---
    // Te permite collectAsStateWithLifecycle y mejor manejo de lifecycle en Compose
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.10.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")

    // --- NUEVO: Navigation Compose ---
    implementation("androidx.navigation:navigation-compose:2.9.7")

    // --- NUEVO: Room (DB local) ---
    implementation("androidx.room:room-runtime:2.8.1")
    implementation("androidx.room:room-ktx:2.8.1")
    kapt("androidx.room:room-compiler:2.8.1")

    // JUnit: tests unitarios (local)
    testImplementation(libs.junit)

    // Androidx JUnit + Espresso: test instrumentados
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Comopose UI Tests: testing de Compose
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    // tooling debug: para previews y herramientas solo en debug
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}