import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("maven-publish")
    id("signing")
}

android {
    namespace = "com.seo4d696b75.compose.pager"
    compileSdk = 35

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        val reportDir = layout.buildDirectory.dir("compose_compiler").get().asFile.absolutePath
        freeCompilerArgs += listOf(
            "-P",
            "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=$reportDir",
            "-P",
            "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=$reportDir"
        )
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

afterEvaluate {
    publishing {
        publications {
            register<MavenPublication>("maven") {
                groupId = "com.seo4d696b75.compose"
                artifactId = "loop-pager"
                version = libs.versions.loopPager.get()
                from(components["release"])

                pom {
                    name = "Compose Loop Pager"
                    description = "Android Jetpack Compose loop pager scrollable infinitely"
                    url = "https://github.com/Seo-4d696b75/compose-loop-pager"
                    licenses {
                        license {
                            name = "MIT License"
                            url = "https://opensource.org/license/mit"
                            distribution = "repo"
                        }
                    }
                    developers {
                        developer {
                            id = "seo4d696b75"
                            name = "Seo-4d696b75"
                            email = "s.kaoru509@gmail.com"
                        }
                    }
                    scm {
                        connection =
                            "scm:git:https://github.com/Seo-4d696b75/compose-loop-pager.git"
                        developerConnection =
                            "scm:git:ssh://github.com/Seo-4d696b75/compose-loop-pager.git"
                        url = "https://github.com/Seo-4d696b75/compose-loop-pager"
                    }
                }
            }
        }
        repositories {
            maven {
                // maven-publish plugin is not used for publishing,
                // but only for generating artifact files
                name = "tmp"
                url = uri(layout.buildDirectory.dir("tmp"))
            }
        }
    }
    signing {
        sign(publishing.publications["maven"])
    }
}

// zip all the files to be published before curl POST
tasks.register<Zip>("archivePublication") {
    dependsOn("publishMavenPublicationToTmpRepository")
    from(layout.buildDirectory.dir("tmp/com/seo4d696b75/compose/loop-pager/${libs.versions.loopPager.get()}"))
    into("/com/seo4d696b75/compose/loop-pager/${libs.versions.loopPager.get()}")
}

// TODO find official gradle plugin
// publish via Central Publisher API
// @see https://central.sonatype.com/api-doc
tasks.register("publishMavenCentralAPI") {
    dependsOn("archivePublication")
    doLast {
        val username = project.properties["SONATYPE_USERNAME"].toString()
        val password = project.properties["SONATYPE_PASSWORD"].toString()

        @OptIn(ExperimentalEncodingApi::class)
        val auth = Base64.encode("$username:$password".encodeToByteArray())

        exec {
            commandLine = listOf(
                "curl",
                "--request", "POST",
                "--verbose",
                "--header", "Authorization: Bearer $auth",
                "--form", "bundle=@build/distributions/lib.zip",
                "https://central.sonatype.com/api/v1/publisher/upload?publishingType=AUTOMATIC",
            )
        }
    }
}
