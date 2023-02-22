plugins {
	`kotlin-dsl`
	kotlin("plugin.serialization") version "1.6.21"
}

repositories {
	mavenCentral()
	maven {
		setUrl("https://plugins.gradle.org/m2/")
	}
}

dependencies {
	implementation("io.github.gradle-nexus:publish-plugin:1.1.0")
	implementation(kotlin("serialization"))
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
}