#!/usr/bin/env kotlin

@file:DependsOn("it.krzeminski:github-actions-kotlin-dsl:0.21.0")

import it.krzeminski.githubactions.actions.actions.CheckoutV3
import it.krzeminski.githubactions.actions.gradle.GradleBuildActionV2
import it.krzeminski.githubactions.domain.RunnerType
import it.krzeminski.githubactions.domain.triggers.Push
import it.krzeminski.githubactions.dsl.workflow
import it.krzeminski.githubactions.yaml.writeToFile

workflow(
  "Run Detekt checks",
  on = listOf(Push()),
  sourceFile = __FILE__.toPath()
) {

  job("run-detekt", runsOn = RunnerType.UbuntuLatest) {
    uses(CheckoutV3())
    uses(GradleBuildActionV2(arguments = "app:detekt"))
  }
}.writeToFile()