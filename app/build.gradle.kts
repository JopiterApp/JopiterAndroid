import io.gitlab.arturbosch.detekt.Detekt
import java.util.Properties

plugins {
  id("com.android.application")
  kotlin("android")
  kotlin("kapt")
  id("io.gitlab.arturbosch.detekt").version("1.23.4")
}

android {
  compileSdk = 32

  defaultConfig {
    applicationId = "app.jopiter"
    targetSdk = 31
    minSdk = 26
    versionCode = 300
    versionName = "3.0.0"

    testApplicationId = "$applicationId.test"
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    testFunctionalTest = true
  }

  buildFeatures {
    compose = true
  }

  composeOptions {
    kotlinCompilerExtensionVersion = libs.versions.compose.get()
  }

  testOptions {
    unitTests {
      isIncludeAndroidResources = true
      all { it.useJUnitPlatform() }
    }
  }

  signingConfigs {
    val keyFile = rootProject.file("jopiter-key.jks")
    val propertyFile = rootProject.file("keystore.properties")

    if(!keyFile.exists() || !propertyFile.exists()) {
      logger.warn("Impossible to create signing configs without signing-key.")
      return@signingConfigs
    }

    val properties = Properties().apply {
      load(propertyFile.inputStream())
    }

    create("production") {
      storeFile = keyFile
      keyPassword = properties.getProperty("KEYSTORE_KEY_PASSWORD")
      storePassword = properties.getProperty("KEYSTORE_PASSWORD")
      keyAlias = properties.getProperty("KEYSTORE_KEY_ALIAS")
    }
  }

  flavorDimensions += "distribution"
  productFlavors {
    create("unofficial") {
      dimension = "distribution"
    }

    if(signingConfigs.findByName("production") == null) return@productFlavors
    create("official") {
      dimension = "distribution"
      signingConfig = signingConfigs.getByName("production")
    }
  }

  buildTypes {
    named("debug") {
      applicationIdSuffix = ".debug"
      isDebuggable = true
    }

    named("release") {
      isMinifyEnabled = true
    }
  }
}

dependencies {
  // Compose
  implementation(libs.bundles.compose)
  compileOnly(libs.compose.ui.tooling)
  androidTestImplementation(libs.compose.ui.test.junit4)
  androidTestImplementation(libs.compose.ui.test.manifest)

  // AndroidX Datastore
  implementation(libs.datastore.preferences)

  // Kotest
  testImplementation(libs.bundles.kotest)

  // Kohttp
  implementation(libs.bundles.kohttp)

  // SL4J
  implementation(libs.slf4j.simple)

  // Koin
  implementation(libs.koin.compose)
  testImplementation(libs.koin.junit4)
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(11))
  }
}

tasks.withType<Detekt> {
  buildUponDefaultConfig = true
}
