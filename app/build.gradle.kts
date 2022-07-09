import org.gradle.api.JavaVersion.VERSION_1_8

plugins {
  id("com.android.application")
  kotlin("android")
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
}

dependencies {
  implementation(Libs.AndroidX.Compose.material)
  implementation(Libs.AndroidX.Compose.materialIcons)
  compileOnly(Libs.AndroidX.Compose.uiTooling)

  implementation(Libs.AndroidX.activityCompose)
  implementation(Libs.AndroidX.navigationCompose)
}