package project.java

import org.gradle.api.JavaVersion
import org.gradle.api.Task
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.external.javadoc.StandardJavadocDocletOptions
import project.base.SimpleProject
import project.base.WrappedProject
import project.java.doc.JavaDocInfo

abstract class BaseJavaProject<T: WrappedProject> : SimpleProject<T>() {
	
	/* -----------------------------------------------------------------------
	 * Properties
	 * -----------------------------------------------------------------------*/
	
	/**
	 * Java source version
	 */
	open val sourceCompatibility: JavaVersion
		get() = JavaVersion.VERSION_17
	
	/**
	 * Target source version
	 */
	open val targetCompatibility: JavaVersion
		get() = sourceCompatibility
	
	/**
	 * Project javadoc configuration
	 */
	open val javadoc: JavaDocInfo
		get() = JavaDocInfo()
	
	/* -----------------------------------------------------------------------
	 * Methods
	 * -----------------------------------------------------------------------*/
	
	/**
	 * Internal extension configuration
	 *
	 * @param container extension container
	 */
	override fun baseExtensionConfiguration(container: ExtensionContainer) {
		container.findByType(JavaPluginExtension::class.java)?.let(::configureBaseJava)
	}
	
	/**
	 * Internal task configuration
	 *
	 * @param container task container
	 */
	override fun baseTaskConfiguration(container: TaskContainer) {
		container.findByName("javadoc")?.let(::configureJavadocTask)
	}
	
	/* -----------------------------------------------------------------------
	 * Internal methods
	 * -----------------------------------------------------------------------*/
	
	/**
	 * Configure base java elements
	 *
	 * @param ext the java extension
	 */
	private fun configureBaseJava(ext: JavaPluginExtension) {
		ext.sourceCompatibility = sourceCompatibility
		ext.targetCompatibility = targetCompatibility
	}
	
	/**
	 * Configure javadoc task
	 *
	 * @param task the javadoc tasks
	 */
	private fun configureJavadocTask(task: Task) {
		// Convert the task object to valid javadoc
		val javadocT = task as Javadoc
		val options = javadocT.options as StandardJavadocDocletOptions
		
		// Configure all javadoc options
		(javadoc.title ?: name)?.let(options::setDocTitle)
		(javadoc.windowTitle ?: name)?.let(options::setWindowTitle)
		javadoc.docUrls?.let(options::setLinks)
		
		// Defined configurations
		options.outputLevel = javadoc.outputLevel
	}
	
}