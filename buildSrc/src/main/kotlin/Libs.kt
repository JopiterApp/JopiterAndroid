object Libs {

  object AndroidX {

    object DataStore {
      const val version = "1.0.0"

      val preferences = "androidx.datastore:datastore-preferences:$version"
    }

    object Compose {
      const val version = "1.1.1"

      val material = "androidx.compose.material:material:$version"
      val materialIcons = "androidx.compose.material:material-icons-extended:$version"
      val uiTooling = "androidx.compose.ui:ui-tooling:$version"

      object Test {
        val uiTestJunit4 = "androidx.compose.ui:ui-test-junit4:$version"
        val uiTestManifest = "androidx.compose.ui:ui-test-manifest:$version"
      }
    }

    val activityCompose = "androidx.activity:activity-compose:1.4.0"
    val navigationCompose = "androidx.navigation:navigation-compose:2.5.0"
  }

  object Kotest {
    const val version = "5.3.2"

    val junit5Runner = "io.kotest:kotest-runner-junit5:$version"

    object Extensions {
      val mockServer = "io.kotest.extensions:kotest-extensions-mockserver:1.2.1"
    }
  }

  object KoHttp {
    const val version = "0.12.0"

    val kohttp = "io.github.rybalkinsd:kohttp:${version}"
    val jackson = "io.github.rybalkinsd:kohttp-jackson:${version}"
  }

  object Slf4J {
    const val version = "1.7.36"

    val simple = "org.slf4j:slf4j-simple:$version"
  }

  object Jackson {
    const val version = "2.13.3"

    val jsr310 = "com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$version"
  }

  object Koin {
    const val version = "3.2.0"

    val compose = "io.insert-koin:koin-androidx-compose:$version"

    val junit4 = "io.insert-koin:koin-test-junit4:$version"
  }
}