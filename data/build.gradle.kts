@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.ksp)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.compose.compiler)
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("io.upnextgpt")
        }
    }
}

android {
    namespace = "io.upnextgpt.data"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    ksp(libs.moshi.codegen)

    implementation(projects.base)

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.compose.runtime)
    implementation(libs.moshi)
    api(libs.retrofit)
    api(libs.retrofit.converter.moshi)
    api(libs.androidx.datastore)
    api(libs.sqldelight.driver)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}