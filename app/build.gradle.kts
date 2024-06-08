plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("kotlin-kapt")
}

android {
    namespace = "com.maxcred.orderservice"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.maxcred.orderservice"
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

    packagingOptions {
        exclude("META-INF/NOTICE.md")
        exclude("META-INF/LICENSE.md")
        exclude("META-INF/DEPENDENCIES")
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

    // Dependências de email
    implementation(libs.mail.android.mail)
    implementation(libs.android.activation.v167)
    implementation(libs.google.api.services.gmail) {
        exclude(group = "com.google.guava", module = "guava-jdk5")
    }

    implementation(libs.google.auth.library.oauth2.http) {
        exclude(group = "com.google.guava", module = "guava-jdk5")
    }
    implementation(libs.android.activation)

    // Dependências do Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.legacy.support.v4)
    kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.common)

    // Dependências do iText
    implementation(libs.itext7.core)
    implementation(libs.itext7.html2pdf)

    // Outras dependências
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.constraintlayout.v213)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Versão correta da Guava
    implementation ("com.google.guava:guava:29.0-android")

    // Dependências de teste
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
