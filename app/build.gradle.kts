import io.gitlab.arturbosch.detekt.Detekt
import java.util.Properties

plugins {
  id("com.android.application")
  kotlin("android")
  kotlin("kapt")
  id("io.gitlab.arturbosch.detekt") version "1.23.4"
  id("app.cash.sqldelight") version "2.0.1"
  id("com.diffplug.spotless") version "6.25.0"
}

spotless {
  kotlin {
    target("src/**/*.kt")
    licenseHeaderFile(rootProject.file("LICENSE_HEADER"))
  }
}

android {
  namespace = "app.jopiter"
  compileSdk = 34

  defaultConfig {
    applicationId = "app.jopiter"
    targetSdk = 34
    minSdk = 26
    versionCode = 3000000
    versionName = "3.0.0"

    testApplicationId = "$applicationId.test"
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    testFunctionalTest = true
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
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

    if (!keyFile.exists() || !propertyFile.exists()) {
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

    if (signingConfigs.findByName("production") == null) return@productFlavors
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
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
}

dependencies {
  // Compose
  implementation(libs.bundles.compose)
  implementation(libs.compose.ui.tooling.preview)
  implementation("androidx.compose.material:material:1.6.1")
  implementation("androidx.compose.ui:ui:1.6.1")


  debugImplementation(libs.compose.ui.tooling)
  androidTestImplementation(libs.compose.ui.test.junit4)
  androidTestImplementation(libs.compose.ui.test.manifest)


  // SQL Delight
  implementation(libs.sqldelight.android.driver)
  implementation(libs.sqldelight.coroutines.extensions)
  testImplementation(libs.sqdelight.sqlite.driver)

  // Kotest
  testImplementation(libs.bundles.kotest)
  testImplementation(libs.kotlinx.coroutines.test)

  // Fuel
  implementation(libs.bundles.fuel)

  // OkHttp — used directly for the long-running JupiterWeb timetable import
  implementation(libs.okhttp)

  // WorkManager — schedules presence/appointment reminder notifications
  implementation(libs.work.runtime)

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


sqldelight {
  databases {
    create("Database") {
      packageName.set("app.jopiter")
    }
  }
}
