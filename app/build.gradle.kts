import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.api.JavaVersion.VERSION_1_8
import java.util.Properties

plugins {
  id("com.android.application")
  kotlin("android")
  kotlin("kapt")
  id("com.diffplug.spotless") version "6.8.0"
  id("io.gitlab.arturbosch.detekt").version("1.21.0-RC2")
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
    kotlinCompilerExtensionVersion = Libs.AndroidX.Compose.version
  }

  kotlinOptions {
    jvmTarget = "$VERSION_1_8"
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
  implementation(kotlin("reflect"))

  implementation(Libs.AndroidX.Compose.material)
  implementation(Libs.AndroidX.Compose.materialIcons)
  compileOnly(Libs.AndroidX.Compose.uiTooling)

  implementation(Libs.AndroidX.activityCompose)
  implementation(Libs.AndroidX.navigationCompose)

  implementation(Libs.KoHttp.kohttp)
  implementation(Libs.KoHttp.jackson)
  implementation(Libs.Jackson.jsr310)

  testImplementation(Libs.Kotest.junit5Runner)
  testImplementation(Libs.Kotest.Extensions.mockServer)
  testImplementation(Libs.Slf4J.simple)

  androidTestImplementation(Libs.AndroidX.Compose.Test.uiTestJunit4)
  debugImplementation(Libs.AndroidX.Compose.Test.uiTestManifest)
}


spotless {
  kotlin {
    licenseHeaderFile(rootProject.file("LICENSE_HEADER"))
    target("src/*/java/**/*.kt")
  }
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(11))
  }
}

tasks.withType<Detekt> {
  buildUponDefaultConfig = true

  reports {
    html.required.set(true)
    sarif.required.set(true)
    txt.required.set(true)
    xml.required.set(true)
  }
}
