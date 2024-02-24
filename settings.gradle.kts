pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositories {
        repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            authentication {
                create<BasicAuthentication>("basic")
            }
            credentials {
                // Do not change the username below.
                // This should always be `mapbox` (not your username).
                username = "mapbox"
                // Use the secret token you stored in gradle.properties as the password
                password = "sk.eyJ1Ijoia2llbjExMTk5OSIsImEiOiJjbHN4bm9vejUwM2liMmtvNWlkeDFuMWdtIn0.C8JjrSIqbMl_rBMcNfSseg"
            }
            google()
            mavenCentral()
        }
    }
}



rootProject.name = "Landmark Remark"
include(":app")
 