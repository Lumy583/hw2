import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  	id("org.springframework.boot") version "3.1.0"
  	id("io.spring.dependency-management") version "1.1.0"
  	kotlin("jvm") version "1.8.21"
  	kotlin("plugin.spring") version "1.8.21"
	kotlin("plugin.serialization") version "1.8.21"
}

group = "academy.softserve"
version = "0.0.1-SNAPSHOT"
java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}


val isWindows = System.getProperty("os.name").startsWith("Windows")
val npm = if (System.getProperty("os.name").startsWith("Windows")) "npm.cmd" else "npm"

val npmInstall by tasks.registering(Exec::class) {
    workingDir = file("ui")
    commandLine(npm, "install")
}

val compileUi by tasks.registering(Exec::class) {
    dependsOn(npmInstall)
    workingDir = file("ui")
    commandLine(npm, "run", "build")
}

val copyUi by tasks.registering(Copy::class) {
    dependsOn(compileUi)
    from("ui/dist/index.html")
    from("ui/dist/app.js")
    from("ui/dist/css") { into("css") }
    into("src/main/resources")
}

tasks.named("processResources") { dependsOn(copyUi) }

tasks.named("bootRun") {
    dependsOn(copyUi)
}