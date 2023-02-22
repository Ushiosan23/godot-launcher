package project.java.doc

import org.gradle.external.javadoc.JavadocOutputLevel

/**
 * Javadoc project configuration
 *
 * @param title javadoc project title
 * @param windowTitle javadoc browser window title
 * @param outputLevel javadoc construction output level
 * @param docUrls external javadoc urls
 */
data class JavaDocInfo(
	val title: String? = null,
	val windowTitle: String? = null,
	val outputLevel: JavadocOutputLevel = JavadocOutputLevel.VERBOSE,
	val docUrls: List<String>? = null
)
