@file:Suppress("DEPRECATION")

import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        load(localPropertiesFile.inputStream())
    }
}

android {
    namespace = "com.aefyr.sai"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.aefyr.sai"
        minSdk = 24
        targetSdk = 37
        versionCode = 67
        versionName = "4.12"
        resourceConfigurations += setOf("zh-rCN", "zh-rTW")
        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas",
                    "room.incremental" to "true",
                    "room.expandProjection" to "true"
                )
            }
        }
    }

    signingConfigs {
        create("release") {
            val keystorePath = System.getenv("KEYSTORE_PATH")
                ?: localProperties.getProperty("KEYSTORE_PATH", "")
            if (keystorePath.isNotEmpty()) {
                storeFile = file(keystorePath)
                storePassword =  System.getenv("KEYSTORE_PASSWORD")
                    ?: localProperties.getProperty("KEYSTORE_PASSWORD", "")
                keyAlias = System.getenv("KEY_ALIAS")
                    ?: localProperties.getProperty("KEY_ALIAS", "")
                keyPassword = System.getenv("KEY_PASSWORD")
                    ?: localProperties.getProperty("KEY_PASSWORD", "")
            }
            enableV1Signing = false
            enableV2Signing = true
            enableV3Signing = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    lint {
        // Library modules are not linted by :app:lint unless this is enabled.
        checkDependencies = true
        abortOnError = true
    }

    packaging {
        resources {
            excludes += setOf(
                "META-INF/DEPENDENCIES",
                "META-INF/ASL2.0",
                "META-INF/CHANGES",
                "META-INF/README.md",
                "META-INF/*.version"
            )
        }
    }
}

dependencies {
    implementation(project(":libs:flexfilter"))
    implementation(project(":libs:filepicker"))
    implementation(project(":libs:tooltips"))
    implementation(project(":libs:pseudoapksigner"))

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.documentfile)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.common.java8)
    implementation(libs.androidx.preference)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.room.runtime)

    annotationProcessor(libs.androidx.room.compiler) {
        exclude(group = "com.intellij", module = "annotations")
    }

    implementation(libs.material)
    implementation(libs.glide)
    implementation(libs.flexbox)
    implementation(libs.gson)
    implementation(libs.shimmer.android)

    // Shizuku/Sui
    implementation(libs.shizuku.api)
    implementation(libs.shizuku.provider)

    debugImplementation(libs.leakcanary.android)
}
