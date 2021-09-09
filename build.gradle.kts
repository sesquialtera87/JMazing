plugins {
    kotlin("multiplatform") version "1.4.21"
}

group = "mth"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    flatDir {
        dir("/home/mattia/IdeaProjects/json-simple/out/artifacts/json_simple_jar")
    }
}


kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        testRuns["test"].executionTask.configure {
            useJUnit()
        }
    }
    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(":json-simple")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("org.junit.jupiter:junit-jupiter:5.4.2")
            }
        }
    }
}
