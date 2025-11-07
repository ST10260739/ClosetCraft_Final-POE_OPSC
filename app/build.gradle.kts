plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.closetcraft"
    compileSdk = 35
    // Use the current stable SDK version, not a future one.

    defaultConfig {
        applicationId = "com.example.closetcraft"
        minSdk = 24
        // Target SDK should also be 34
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
    kotlinOptions {
        // Align JVM target with Java version
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {
    // --- Default and Google Auth Dependencies (from your libs catalog) ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // --- Firebase Dependencies ---
    // Import the Firebase Bill of Materials (BoM). This manages all Firebase library versions.
    // I've updated it to a recent stable version.
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))

    // CORRECTED: Declare the Firestore dependency WITHOUT the -ktx suffix or a version number.
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-auth")

    // --- Other Dependencies (cleaned up duplicates) ---
    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:21.2.0")
    // For image loading (Coil) and circular images
    implementation("io.coil-kt:coil:2.5.0")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    // Lifecycle, Coroutines, Retrofit, etc.
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.maps.android:android-maps-utils:3.4.0")
    implementation("androidx.work:work-runtime-ktx:2.8.1")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.1")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.android.flexbox:flexbox:3.0.0")
// Firebase Cloud Messaging
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation("com.google.firebase:firebase-database:20.3.1")

}




