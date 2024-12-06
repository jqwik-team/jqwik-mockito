plugins {
    id("java-library")
    id("maven-publish")
    id("signing")
    id("com.diffplug.spotless") version "6.13.0"
}

fun isSnapshotRelease(versionString: String): Boolean {
    return versionString.endsWith("SNAPSHOT")
}

val githubProjectName = "jqwik-team"
val artifactId = "jqwik-mockito"
val moduleGroupId = "net.jqwik"
val moduleName = "net.jqwik.mockito"
val junitPlatformVersion = "1.11.2"
val junitJupiterVersion = "5.11.2"
val opentest4jVersion = "1.3.0"
val assertJVersion = "3.26.3"
val mockitoVersion = "4.11.0" // Mockito 5+ no longer supports Java 8
val jqwikVersion = "1.9.1"
val lombokVersion = "1.18.34"
val jqwikMockitoVersion = "0.0.1-SNAPSHOT"
val isSnapshotRelease = isSnapshotRelease(jqwikMockitoVersion)

group = moduleName
version = jqwikMockitoVersion
description = "Jqwik Mockito support module"

repositories {
    mavenCentral()
    maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots")
}

tasks.jar {
    archiveBaseName.set(artifactId)
    archiveVersion.set(jqwikMockitoVersion)
    manifest {
        attributes("Automatic-Module-Name" to moduleName)
    }
}

spotless {
    java {
        palantirJavaFormat("1.1.0")

        importOrder("", "java|javax", "\\#")
        removeUnusedImports()
    }
}

java {
    withJavadocJar()
    withSourcesJar()
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
}

tasks.compileTestJava {
    options.compilerArgs.add("-parameters")
    options.encoding = "UTF-8"
}

tasks.named("check") {
    dependsOn(tasks.named("spotlessApply"))
}

tasks.test {
    useJUnitPlatform {
        includeEngines("jqwik")
    }
}

dependencies {
    api("org.opentest4j:opentest4j:${opentest4jVersion}")
    api("net.jqwik:jqwik:${jqwikVersion}")
    compileOnly("org.mockito:mockito-core:${mockitoVersion}")
    compileOnly("org.mockito:mockito-junit-jupiter:${mockitoVersion}")

    testCompileOnly("org.projectlombok:lombok:${lombokVersion}")
    testAnnotationProcessor("org.projectlombok:lombok:${lombokVersion}")

    testImplementation("org.junit.jupiter:junit-jupiter:${junitJupiterVersion}")
    testImplementation("org.assertj:assertj-core:${assertJVersion}")
    testImplementation("org.mockito:mockito-junit-jupiter:${mockitoVersion}")
}

publishing {
    repositories {
        maven {
            // hint: password is in ~/.gradle/gradle.properties
            val ossrhUsername: String = project.findProperty("ossrhUsername") as? String ?: ""
            val ossrhPassword: String = project.findProperty("ossrhPassword") as? String ?: ""

            credentials {
                username = ossrhUsername
                password = ossrhPassword
            }

            // change URLs to point to your repos, e.g. http://my.org/repo
            val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
            val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
            url = uri(if (isSnapshotRelease) { snapshotsRepoUrl } else { releasesRepoUrl })
        }
    }
    publications {
        create<MavenPublication>("jqwikMockito") {
            groupId = moduleGroupId
            artifactId = artifactId
            from(components["java"])
            pom {
                groupId = moduleGroupId
                name = artifactId
                description = "Jqwik Mockito support module"
                url = "https://github.org/$githubProjectName/$artifactId"
                licenses {
                    license {
                        name = "Eclipse Public License - v 2.0"
                        url = "http://www.eclipse.org/legal/epl-v20.html"
                    }
                }
                developers {
                    developer {
                        id = "agustafson-atl"
                        name = "Andrew Gustafson"
                        email = "agustafson@atlassian.com"
                    }
                    developer {
                        id = githubProjectName
                        name = "Johannes Link"
                        email = "business@johanneslink.net"
                    }
                }
                scm {
                    connection = "scm:git:git://github.com/$githubProjectName/$artifactId.git"
                    developerConnection = "scm:git:git://github.com/$githubProjectName/$artifactId.git"
                    url = "https://github.com/$githubProjectName/$artifactId"
                }
            }
        }
    }
}

signing {
    if (!isSnapshotRelease) {
        sign(publishing.publications["jqwikMockito"])
    }
}

tasks.wrapper {
    gradleVersion = "8.11.1" // upgrade with: ./gradlew wrapper
}
