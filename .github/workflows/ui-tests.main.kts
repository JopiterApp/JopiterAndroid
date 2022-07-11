#!/usr/bin/env kotlin

@file:DependsOn("it.krzeminski:github-actions-kotlin-dsl:0.21.0")

import it.krzeminski.githubactions.actions.CustomAction
import it.krzeminski.githubactions.actions.actions.CheckoutV3
import it.krzeminski.githubactions.domain.RunnerType
import it.krzeminski.githubactions.domain.triggers.Push
import it.krzeminski.githubactions.dsl.workflow
import it.krzeminski.githubactions.yaml.writeToFile

workflow(
  "Run interface tests",
  on = listOf(Push()),
  sourceFile = __FILE__.toPath()
) {

  job("ui-tests", runsOn = RunnerType.MacOSLatest) {
    uses(CheckoutV3())

    val androidAction = CustomAction(
      "reactivecircus",
      "android-emulator-runner",
      "v2",
      mapOf("api-level" to "31", "script" to "./gradlew connectedCheck")
    )

    uses(androidAction)
  }
}.writeToFile()
