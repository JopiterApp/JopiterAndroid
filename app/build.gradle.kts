import org.gradle.api.JavaVersion.VERSION_1_8
import java.util.Properties

plugins {
  id("com.android.application")
  kotlin("android")
  id("com.diffplug.spotless") version "6.8.0"
}

android {
  compileSdk = 32

  defaultConfig {
    targetSdk = 31
    minSdk = 26
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
  implementation(Libs.AndroidX.Compose.material)
  implementation(Libs.AndroidX.Compose.materialIcons)
  compileOnly(Libs.AndroidX.Compose.uiTooling)

  implementation(Libs.AndroidX.activityCompose)
  implementation(Libs.AndroidX.navigationCompose)
}

spotless {
  kotlin {
    licenseHeaderFile(rootProject.file("LICENSE_HEADER"))
    target("src/*/java/**/*.kt")
  }
}