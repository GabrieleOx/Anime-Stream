pluginManagement {
    repositories {
        google() // Deve essere sempre PRIMO per Compose e AGP
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("com.android.application") version "8.1.1" apply false
        id("org.jetbrains.kotlin.android") version "1.9.10" apply false
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google() // Ancora, PRIMO
        mavenCentral()
    }
}

rootProject.name = "AnimeDownloader"
include(":app")
