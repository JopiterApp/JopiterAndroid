#!/usr/bin/env kotlin
@file:Repository("https://repo1.maven.org/maven2/")
@file:Repository("https://central.sonatype.com/repository/maven-snapshots/")
@file:DependsOn("io.github.typesafegithub:github-workflows-kt:4.0.0-SNAPSHOT")

@file:Repository("https://bindings.krzeminski.it")
@file:DependsOn("actions:checkout:v4")
@file:DependsOn("actions:setup-java:v5")
@file:DependsOn("gradle:actions__setup-gradle:v4")
@file:DependsOn("entrostat:git-secret-action:v4")
@file:DependsOn("ruby:setup-ruby:v1")
@file:DependsOn("softprops:action-gh-release:v2")

import io.github.typesafegithub.workflows.actions.actions.Checkout
import io.github.typesafegithub.workflows.actions.actions.SetupJava
import io.github.typesafegithub.workflows.actions.entrostat.GitSecretAction
import io.github.typesafegithub.workflows.actions.gradle.ActionsSetupGradle
import io.github.typesafegithub.workflows.actions.ruby.SetupRuby
import io.github.typesafegithub.workflows.actions.softprops.ActionGhRelease
import io.github.typesafegithub.workflows.domain.Mode
import io.github.typesafegithub.workflows.domain.Permission
import io.github.typesafegithub.workflows.domain.RunnerType.UbuntuLatest
import io.github.typesafegithub.workflows.domain.triggers.Push
import io.github.typesafegithub.workflows.dsl.expressions.Contexts
import io.github.typesafegithub.workflows.dsl.expressions.expr
import io.github.typesafegithub.workflows.dsl.workflow

// Secrets (configure in the repository settings):
//   GPG_KEY     — armored GPG private key that decrypts the git-secret files
//                 (jopiter-key.jks, keystore.properties, fastlane/google-play.json)
//   RELEASE_KEY — SSH private key of a deploy key with write access, to push the version bump
val GPG_KEY by Contexts.secrets
val RELEASE_KEY by Contexts.secrets

workflow(
  name = "Release",
  on = listOf(Push(branches = listOf("main"))),
  sourceFile = __FILE__,
) {
  job(
    id = "release",
    runsOn = UbuntuLatest,
    permissions = mapOf(Permission.Contents to Mode.Write),
  ) {
    run(
      name = "Configure Git identity",
      command = """
        git config --global user.name "github-actions[bot]"
        git config --global user.email "41898282+github-actions[bot]@users.noreply.github.com"
      """.trimIndent(),
    )

    uses(name = "Set up JDK", action = SetupJava(javaVersion = "17", distribution = SetupJava.Distribution.Adopt))
    uses(name = "Set up Gradle", action = ActionsSetupGradle())

    uses(
      name = "Checkout",
      action = Checkout(sshKey = expr { RELEASE_KEY }, fetchDepth = Checkout.FetchDepth.Infinite),
    )

    uses(name = "Reveal secrets", action = GitSecretAction(gpgPrivateKey = expr { GPG_KEY }))

    run(
      name = "Determine bump type and changelog",
      command = """
        message=${'$'}(git log -1 --pretty=%B)
        case "${'$'}message" in
          *'#major'*) echo "type=major" >> "${'$'}GITHUB_ENV" ;;
          *'#minor'*) echo "type=minor" >> "${'$'}GITHUB_ENV" ;;
          *)          echo "type=patch" >> "${'$'}GITHUB_ENV" ;;
        esac
        echo "changelog=${'$'}(git log -1 --pretty=%s)" >> "${'$'}GITHUB_ENV"
      """.trimIndent(),
    )

    val bump = run(
      name = "Bump version, tag and push",
      command = "kotlin app/bump_version.main.kts \"${'$'}type\" \"${'$'}changelog\"",
    )

    run(
      name = "Build signed APK for the GitHub release",
      command = "./gradlew assembleOfficialRelease",
    )

    uses(
      name = "Create GitHub release",
      action = ActionGhRelease(
        tagName = expr { bump.outputs["version"] },
        name = expr { bump.outputs["version"] },
        draft = false,
        files = listOf(
          "app/build/outputs/apk/official/release/app-official-release.apk",
          "app/build/outputs/mapping/officialRelease/mapping.txt",
          "app/build/outputs/mapping/officialRelease/configuration.txt",
          "app/build/outputs/mapping/officialRelease/seeds.txt",
          "app/build/outputs/mapping/officialRelease/usage.txt",
        ),
      ),
    )

    uses(name = "Set up Ruby", action = SetupRuby(rubyVersion = "3.2.3"))

    run(
      name = "Publish to Play Store",
      workingDirectory = "fastlane",
      command = """
        bundle install --jobs 4 --retry 3
        bundle exec fastlane playstore
      """.trimIndent(),
    )
  }
}
