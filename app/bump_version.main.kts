#!/usr/bin/env kotlin

import java.io.File

// Bumps the app version in app/build.gradle.kts, writes the release changelog, commits, tags and
// pushes. Usage: app/bump_version.main.kts <major|minor|patch> "<changelog>"
// The commit carries [skip ci] so pushing it does not trigger the push-based CI workflows
// (unit tests, detekt, license header, UI tests). Release itself is workflow_dispatch-only.

val buildGradleFile = File("app/build.gradle.kts")
val changelogsDir = File("fastlane/metadata/android/pt-BR/changelogs")
val buildGradleContent = buildGradleFile.readText()

val versionCodeRegex = Regex("""versionCode\s*=\s*(\d+)""")
val versionNameRegex = Regex("""versionName\s*=\s*"([\d.]+)"""")

val currentVersionName = versionNameRegex.find(buildGradleContent)!!.groupValues[1]
println("Current version: $currentVersionName")

val bumpType = args.getOrNull(0) ?: "patch"
check(bumpType in listOf("major", "minor", "patch")) { "bump type must be one of: major, minor, patch" }

val versionParts = currentVersionName.split(".").map { it.toIntOrNull() ?: 0 }
val (newMajor, newMinor, newPatch) = when (bumpType) {
  "major" -> Triple(versionParts[0] + 1, 0, 0)
  "minor" -> Triple(versionParts[0], versionParts[1] + 1, 0)
  else -> Triple(versionParts[0], versionParts[1], versionParts[2] + 1)
}

val newVersionName = "$newMajor.$newMinor.$newPatch"
val newVersionCode = newMajor * 1_000_000 + newMinor * 1_000 + newPatch
println("New version: $newVersionName ($newVersionCode)")

buildGradleFile.writeText(
  buildGradleContent
    .replace(versionCodeRegex, "versionCode = $newVersionCode")
    .replace(versionNameRegex, "versionName = \"$newVersionName\"")
)

changelogsDir.mkdirs()
val changelog = (args.getOrNull(1) ?: "Melhorias e correções.").replace("\\n", "\n")
val changelogFile = File(changelogsDir, "$newVersionCode.txt")
changelogFile.writeText(changelog)
println("Wrote changelog to $changelogFile")

fun git(vararg command: String) =
  ProcessBuilder(listOf("git") + command).inheritIO().start().waitFor()

git("add", buildGradleFile.path, changelogFile.path)
git("commit", "-m", "🔖 Release $newVersionName ($newVersionCode)", "-m", "[skip ci]")
git("tag", "-a", newVersionName, "-m", "Release $newVersionName")
git("push", "origin", "HEAD:main", "--tags")

// Expose the new version to later workflow steps (GitHub release tag/name).
System.getenv("GITHUB_OUTPUT")?.let { File(it).appendText("version=$newVersionName\n") }
