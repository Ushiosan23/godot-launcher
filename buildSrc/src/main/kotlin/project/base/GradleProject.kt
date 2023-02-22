package project.base

import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.plugins.PluginContainer
import org.gradle.api.tasks.TaskContainer

interface GradleProject<T : WrappedProject> : WrappedProject {
	
	/* -----------------------------------------------------------------------
	 * Properties
	 * -----------------------------------------------------------------------*/
	
	/**
	 * Project name
	 */
	val name: String?
	
	/**
	 * Project group id
	 */
	val groupId: String?
	
	/**
	 * Project version
	 */
	val version: String?
	
	/**
	 * Project dependencies
	 */
	val dependencyMap: Map<Any, Iterable<*>?>?
		get() = null
	
	/* -----------------------------------------------------------------------
	 * Methods
	 * -----------------------------------------------------------------------*/
	
	/**
	 * Configure default project configuration
	 *
	 * @param project the project to configure
	 */
	fun configure(project: Project, action: ((self: T) -> Unit)? = null)
	
	/**
	 * Custom project configuration
	 */
	fun customConfiguration() = Unit
	
	/**
	 * Custom plugin configuration
	 *
	 * @param container project plugin container
	 */
	fun customPluginConfiguration(container: PluginContainer) = Unit
	
	/**
	 * Custom extension configuration
	 *
	 * @param container project extension container
	 */
	fun customExtensionConfiguration(container: ExtensionContainer) = Unit
	
	/**
	 * Custom task configuration
	 *
	 * @param container project task container
	 */
	fun customTaskConfiguration(container: TaskContainer) = Unit
	
}