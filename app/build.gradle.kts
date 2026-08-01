plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.positiveparenting"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.positiveparenting"
        minSdk = 33
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
        @Suppress("DEPRECATION")
        jvmTarget = "11"
    }
    buildFeatures {
        // buildConfig bleibt an: das secrets-gradle-plugin reicht Keys über BuildConfig durch.
        buildConfig = true
    }
}

ksp {
    // Schema-Historie einchecken: Migrationen statt Datenverlust (ADR-004).
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.appcompat)
    implementation("com.airbnb.android:lottie:6.6.7")
    implementation(libs.material)
    // Explizit statt nur transitiv über Material: die Übersicht (A-2) nutzt RecyclerView direkt.
    implementation(libs.androidx.recyclerview)
    // Room 2.7+ enthält die früheren KTX-APIs (suspend-DAOs) direkt in room-runtime.
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}