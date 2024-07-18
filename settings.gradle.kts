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

rootProject.name = "Secure Notes"
include(":app")
include(":feat:home")
include(":feat:settings")
include(":feat:authentication")
include(":domain:settings")
include(":domain:authentication")
include(":data:settings")
include(":data:authentication")
include(":core:ui")
include(":core:common")
include(":core:navigation")
include(":data:home")
