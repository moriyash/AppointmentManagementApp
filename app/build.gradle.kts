plugins {
    id("com.android.application")
    id("com.google.gms.google-services") // Google Services Plugin
    id("com.google.firebase.crashlytics") // Firebase Crashlytics (אופציונלי)
    id("com.google.firebase.firebase-perf") // Firebase Performance (אופציונלי)
}

android {
    namespace = "com.example.queuemanagementapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.queuemanagementapp"
        minSdk = 24
        targetSdk = 35
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
    implementation("com.google.firebase:firebase-auth:22.3.1") // Firebase Authentication
    implementation("com.google.firebase:firebase-firestore:24.6.1") // Firestore Database
    implementation("com.google.firebase:firebase-database:20.3.1") // RealTime Database (אופציונלי)
    implementation("com.google.android.gms:play-services-auth:20.7.0") // Google Play Auth Services


    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    }