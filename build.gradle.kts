val arrowKtVersion by extra { "2.2.3" }
val jacksonVersion by extra { "2.22.0" }

// test dependencies
val awaitilityVersion by extra { "4.3.0" }
val junitVersion by extra { "4.6" }
val kotestVersion by extra { "6.1.11" }
val kotestArrowVersion by extra { "2.0.0" }
val mockkVersion by extra { "1.13.11" }

plugins {
    kotlin("jvm") version "2.3.21"
    jacoco
    application
}

group = "com.cyjhuiz"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("io.arrow-kt:arrow-core:$arrowKtVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest.extensions:kotest-assertions-arrow:$kotestArrowVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("org.awaitility:awaitility:$awaitilityVersion")

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }

    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "sudoku.MainKt"
    }

    // extract runtime dependencies
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.runtimeClasspath.get().map {
        if (it.isDirectory) it else zipTree(it)
    })
}

application {
    mainClass.set("sudoku.MainKt")
}

