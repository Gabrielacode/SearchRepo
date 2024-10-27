plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    kotlin("plugin.serialization") version "2.0.20"
    id ("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.solt.practiv"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.solt.practiv"
        minSdk = 26
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
    buildFeatures{
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    val nav_version = "2.7.7"


    // Views/Fragments integration
    implementation("androidx.navigation:navigation-fragment:$nav_version")
    implementation("androidx.navigation:navigation-ui:$nav_version")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    // https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp
    implementation("com.squareup.okhttp3:okhttp:4.10.0")

    // https://mvnrepository.com/artifact/com.squareup.retrofit2/retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")

// https://mvnrepository.com/artifact/com.squareup.okhttp3/logging-interceptor
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
    // https://mvnrepository.com/artifact/com.github.bumptech.glide/glide
    implementation("com.github.bumptech.glide:glide:4.13.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.13.1")

    val paging_version = "3.3.2"

    implementation("androidx.paging:paging-runtime:$paging_version")
    val room_version = "2.6.1"

    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-paging:$room_version")

    // To use Kotlin annotation processing tool (kapt)
    kapt("androidx.room:room-compiler:$room_version")
    // To use Kotlin Symbol Processing (KSP)
    //ksp("androidx.room:room-compiler:$room_version")

    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$room_version")

    //Dagger
    val dagger_version = "2.46"
    implementation ("com.google.dagger:dagger:$dagger_version")
    kapt ("com.google.dagger:dagger-compiler:$dagger_version")
    //Dagger Hilt
    implementation("com.google.dagger:hilt-android:$dagger_version")
    kapt("com.google.dagger:hilt-android-compiler:$dagger_version")
}