package project.java

import org.gradle.api.Task
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.plugins.JavaApplication
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.TaskContainer
import project.base.WrappedProject

abstract class JavaApplicationProject<T : WrappedProject> : BaseJavaProject<T>() {
	
	/* -----------------------------------------------------------------------
	 * Properties
	 * -----------------------------------------------------------------------*/
	
	/**
	 * Application main class
	 */
	open val mainClass: String?
		get() = null
	
	/**
	 * Application executable directory
	 */
	open val executableDir: String?
		get() = null
	
	/**
	 * Application JVM arguments
	 */
	open val jvmArgs: Iterable<String>?
		get() = null
	
	/**
	 * Application arguments
	 */
	open val applicationArgs: Iterable<String>?
		get() = null
	
	/**
	 * Use default java inputs and outputs in Gradle
	 */
	open val useStandardIO: Boolean
		get() = true
	
	/* -----------------------------------------------------------------------
	 * Methods
	 * -----------------------------------------------------------------------*/
	
	/**
	 * Internal extension configuration
	 *
	 * @param container extension container
	 */
	override fun baseExtensionConfiguration(container: ExtensionContainer) {
		super.baseExtensionConfiguration(container)
		// Configure java application extension
		container.findByType(JavaApplication::class.java)?.let(::configureJavaApplication)
	}
	
	/**
	 * Internal task configuration
	 *
	 * @param container task container
	 */
	override fun baseTaskConfiguration(container: TaskContainer) {
		super.baseTaskConfiguration(container)
		// Configure java executable task
		container.findByName("run")?.let(::configureApplicationExecution)
	}
	
	/* -----------------------------------------------------------------------
	 * Internal methods
	 * -----------------------------------------------------------------------*/
	
	/**
	 * Configure the java application extension
	 *
	 * @param ext the java application extension
	 */
	private fun configureJavaApplication(ext: JavaApplication) {
		mainClass?.let(ext.mainClass::set)
		
		name?.let(ext::setApplicationName)
		executableDir?.let(ext::setExecutableDir)
		jvmArgs?.let(ext::setApplicationDefaultJvmArgs)
	}
	
	/**
	 * Change application execution configuration
	 *
	 * @param task application executable task
	 */
	private fun configureApplicationExecution(task: Task) {
		// Change task type
		val execution = task as Exec
		// Configure execution elements
		applicationArgs?.let(execution::setArgs)
		
		// Use standard java I/O
		if (!useStandardIO) return
		execution.standardOutput = System.`out`
		execution.standardInput = System.`in`
	}
	
}