import java.util.Properties

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.dokar.upnextgpt"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.dokar.upnextgpt"
        minSdk = 21
        targetSdk = 33
        versionCode = 14
        versionName = "1.13"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release").apply {
            val properties = Properties()
            val localProps = rootProject.file("local.properties")
            if (localProps.exists()) {
                localProps.inputStream().use {
                    properties.load(it)
                }
            }
            val path = envOrProp(properties, "KEYSTORE_PATH")
            var keystoreFile = rootProject.file(path)
            if (!keystoreFile.exists()) {
                keystoreFile = file(path)
            }
            if (keystoreFile.exists()) {
                storeFile = keystoreFile
                storePassword = envOrProp(properties, "KEYSTORE_PASSWORD")
                keyAlias = "key0"
                keyPassword = envOrProp(properties, "KEYSTORE_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs["release"]
        }

        release {
            isMinifyEnabled = true
            signingConfig = signingConfigs["release"]
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
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(projects.base)
    implementation(projects.data)
    implementation(projects.player)
    implementation(projects.ui.shared)
    implementation(projects.ui.home)
    implementation(projects.ui.settings)

    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.navigation)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom.alpha))
    androidTestImplementation(libs.compose.ui.test.junit4)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)
}

fun envOrProp(props: Properties, propName: String): String {
    return System.getenv(propName) ?: props.getProperty(propName)
}
