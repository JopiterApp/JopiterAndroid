object Libs {

  object AndroidX {

    object Compose {
      const val version = "1.1.1"

      val material = "androidx.compose.material:material:$version"
      val materialIcons = "androidx.compose.material:material-icons-extended:$version"
      val uiTooling = "androidx.compose.ui:ui-tooling:$version"
    }

    val activityCompose = "androidx.activity:activity-compose:1.4.0"
    val navigationCompose = "androidx.navigation:navigation-compose:2.5.0"
  }

  object Kotest {
    const val version = "5.3.2"

    val junit5Runner = "io.kotest:kotest-runner-junit5:$version"

  }
}