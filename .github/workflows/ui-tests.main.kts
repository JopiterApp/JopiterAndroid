#!/usr/bin/env kotlin

@file:DependsOn("it.krzeminski:github-actions-kotlin-dsl:0.21.0")

import it.krzeminski.githubactions.actions.CustomAction
import it.krzeminski.githubactions.actions.actions.CheckoutV3
import it.krzeminski.githubactions.actions.actions.SetupJavaV3
import it.krzeminski.githubactions.domain.RunnerType
import it.krzeminski.githubactions.domain.triggers.Push
import it.krzeminski.githubactions.dsl.workflow
import it.krzeminski.githubactions.yaml.writeToFile

workflow(
  "Run interface tests",
  on = listOf(Push()),
  sourceFile = __FILE__.toPath()
) {

  job("ui-tests", runsOn = RunnerType.UbuntuLatest) {
    uses(CheckoutV3())
    uses(SetupJavaV3("17", SetupJavaV3.Distribution.Adopt))

    run(
      name = "Enable KVM",
      command = """
        echo 'KERNEL=="kvm", GROUP="kvm", MODE="0666", OPTIONS+="static_node=kvm"' | sudo tee /etc/udev/rules.d/99-kvm4all.rules
        sudo udevadm control --reload-rules
        sudo udevadm trigger --name-match=kvm
      """.trimIndent()
    )

    val androidAction = CustomAction(
      "reactivecircus",
      "android-emulator-runner",
      "v2",
      mapOf("api-level" to "29", "script" to "./gradlew connectedCheck")
    )

    uses(androidAction)
  }
}.writeToFile()
