plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "app.justfocus"
    compileSdk = 37

    defaultConfig {
        applicationId = "app.justfocus"
        minSdk = 26
        targetSdk = 36
        // CI passes the build number, so every release installs as an update of the previous one.
        val buildNumber = providers.environmentVariable("BUILD_NUMBER").orNull?.toInt() ?: 1
        versionCode = buildNumber
        versionName = "1.0.$buildNumber"
    }

    // The release key is private: CI gets it from GitHub Secrets, it is never stored in the repository.
    // Without these variables the release APK is built unsigned.
    val releaseKeystorePath = providers.environmentVariable("SIGNING_KEYSTORE_PATH").orNull
    val releaseKeystorePassword = providers.environmentVariable("SIGNING_KEYSTORE_PASSWORD").orNull

    signingConfigs {
        if (releaseKeystorePath != null && releaseKeystorePassword != null) {
            create("release") {
                storeFile = file(releaseKeystorePath)
                storePassword = releaseKeystorePassword
                keyAlias = "just-focus"
                keyPassword = releaseKeystorePassword
            }
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.findByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.kotlinx.coroutines.android)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)

    testImplementation(libs.junit)
}
