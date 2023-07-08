pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "UpNextGPT"

include(":app")
include(":base")

file("./ui").listFiles(File::isDirectory)
    ?.forEach { dir ->
        include(":ui:${dir.name}")
        project(":ui:${dir.name}").projectDir = dir
    }

include(":player")
include(":data")
