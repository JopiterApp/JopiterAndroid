#!/usr/bin/env kotlin
@file:DependsOn("io.github.typesafegithub:github-workflows-kt:1.8.0")

import io.github.typesafegithub.workflows.actions.actions.CheckoutV4
import io.github.typesafegithub.workflows.actions.actions.SetupJavaV4
import io.github.typesafegithub.workflows.actions.gradle.GradleBuildActionV2
import io.github.typesafegithub.workflows.domain.RunnerType.UbuntuLatest
import io.github.typesafegithub.workflows.domain.triggers.PullRequest
import io.github.typesafegithub.workflows.domain.triggers.Push
import io.github.typesafegithub.workflows.dsl.expressions.contexts.GitHubContext
import io.github.typesafegithub.workflows.dsl.expressions.expr
import io.github.typesafegithub.workflows.dsl.workflow
import io.github.typesafegithub.workflows.yaml.writeToFile


workflow(
    name = "Lint",
    on = listOf(Push(), PullRequest()),
    sourceFile = __FILE__.toPath()
) {
    job(id = "detekt", runsOn = UbuntuLatest) {
        uses(name = "Set up JDK", action = SetupJavaV4(javaVersion = "17", distribution = SetupJavaV4.Distribution.Adopt))
        uses(action = CheckoutV4())
        uses(action = GradleBuildActionV2(arguments = "detekt"))
        run(command = "cat app/build/reporots/detekt/detekt.md >> ${expr { GitHubContext.step_summary } }")
    }
}.writeToFile(generateActionBindings = true)