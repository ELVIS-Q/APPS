plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.googleService)
}

android {
    namespace = "com.personal.firebaseapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.personal.firebaseapp"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation("com.google.firebase:firebase-auth-ktx") // Usamos string porque 'libs.firebaseAuth' no existe
    implementation(libs.firebase.database)
    implementation(libs.firebase.messaging)
    implementation ("com.google.firebase:firebase-database:20.3.0")
    implementation ("com.squareup.okhttp3:okhttp:4.10.0")
    implementation ("com.google.firebase:firebase-firestore:24.11.1")




    // Google Maps
    implementation(libs.play.services.maps)
}
