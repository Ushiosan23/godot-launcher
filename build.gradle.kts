// Configure all project repositories
allprojects {
	repositories {
		mavenCentral()
		maven {
			setUrl("https://plugins.gradle.org/m2/")
		}
		mavenLocal()
	}
}