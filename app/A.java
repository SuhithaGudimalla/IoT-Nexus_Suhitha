plugins {
    id("com.android.application")
    id("com.google.gms.google-services") // Apply the Google services plugin for Firebase
}

android {
    namespace = "com.example.iotnexus"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.iotnexus"
        minSdk = 21
        targetSdk = 34
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Firebase dependencies
    implementation("com.google.firebase:firebase-auth:21.0.5")
    implementation("com.google.firebase:firebase-database:20.0.5")

    // Other dependencies can be added as needed
}
