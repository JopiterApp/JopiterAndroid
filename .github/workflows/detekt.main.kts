#!/usr/bin/env kotlin

@file:DependsOn("it.krzeminski:github-actions-kotlin-dsl:0.21.0")

import it.krzeminski.githubactions.actions.CustomAction
import it.krzeminski.githubactions.actions.actions.CheckoutV3
import it.krzeminski.githubactions.actions.actions.UploadArtifactV3
import it.krzeminski.githubactions.actions.gradle.GradleBuildActionV2
import it.krzeminski.githubactions.domain.RunnerType
import it.krzeminski.githubactions.domain.triggers.Push
import it.krzeminski.githubactions.dsl.expressions.contexts.GitHubContext
import it.krzeminski.githubactions.dsl.expressions.expr
import it.krzeminski.githubactions.dsl.workflow
import it.krzeminski.githubactions.yaml.writeToFile

workflow(
  "Detekt",
  on = listOf(Push()),
  sourceFile = __FILE__.toPath()
) {

  job("detekt", runsOn = RunnerType.UbuntuLatest) {
    uses(CheckoutV3())
    uses(GradleBuildActionV2(arguments = "app:detekt", generateJobSummary = false))

    uses(
      CustomAction(
        "github/codeql-action",
        "upload-sarif",
        "v2",
        mapOf("sarif_file" to "app/build/reports/detekt/detekt.sarif")
      ),
    )

    run("cat app/build/reports/detekt/detekt.md >> ${expr { GitHubContext.step_summary }}")
  }
}.writeToFile()