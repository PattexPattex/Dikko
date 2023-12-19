import org.jetbrains.dokka.gradle.DokkaTask
import java.net.URL

plugins {
    `java-library`
    `maven-publish`
    id("org.jetbrains.dokka") version "1.8.20"
}

dependencies {
    api("net.dv8tion:JDA:5.0.0-beta.18")
    api("com.github.minndevelopment:jda-ktx:9370cb1")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")

    implementation("io.github.classgraph:classgraph:4.8.161")
    implementation(kotlin("reflect"))

    testImplementation(kotlin("test"))
    testRuntimeOnly("ch.qos.logback:logback-classic:1.3.8")
}

tasks {
    withType<DokkaTask>().configureEach {
        moduleName.set("Dikko")
        failOnWarning.set(true)

        dokkaSourceSets.configureEach {
            includes.from(projectDir.resolve("README.md"))
            jdkVersion.set(8)

            sourceRoots.from(project.projectDir)
            samples.from(project(":samples").projectDir)
            skipEmptyPackages.set(true)

            sourceLink {
                localDirectory.set(project.projectDir)
                remoteUrl.set(URL("https://github.com/PattexPattex/Dikko/tree/master/${project.name}"))
                remoteLineSuffix.set("#L")
            }

            externalDocumentationLink(
                "https://ci.dv8tion.net/job/JDA5/javadoc/",
                "https://ci.dv8tion.net/job/JDA5/javadoc/element-list"
            )
            externalDocumentationLink("https://minndevelopment.github.io/jda-ktx/")
            externalDocumentationLink("https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/")
        }
    }

    build {
        dependsOn(jar)
        dependsOn(sourcesJar)
    }
}

val sourcesJar = task<Jar>("sourcesJar") {
    from(sourceSets["main"].allSource)
    archiveClassifier.set("sources")
}

publishing.publications {
    register<MavenPublication>("Release") {
        from(components["java"])
        groupId = project.group as String
        artifactId = project.name
        version = project.version as String

        artifact(sourcesJar)
    }
}
