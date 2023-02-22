package project.base

import loader.PropertyLoader
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.plugins.PluginContainer
import org.gradle.api.tasks.TaskContainer
import project.dependency.DependencyType
import project.dependency.resolveMavenDependency
import java.util.*

abstract class SimpleProject<T : WrappedProject> : GradleProject<T> {
	
	/* -----------------------------------------------------------------------
	 * Properties
	 * -----------------------------------------------------------------------*/
	
	/**
	 * Real gradle project
	 */
	private lateinit var wrappedProjectImpl: Project
	
	/**
	 * Real gradle project
	 */
	override val wrappedProject: Project
		get() = wrappedProjectImpl
	
	/* -----------------------------------------------------------------------
	 * Methods
	 * -----------------------------------------------------------------------*/
	
	/**
	 * Configure default project configuration
	 *
	 * @param project the project to configure
	 */
	@Suppress("UNCHECKED_CAST")
	override fun configure(project: Project, action: ((self: T) -> Unit)?) = with(project) {
		// Define project instance
		if (!::wrappedProjectImpl.isInitialized) {
			wrappedProjectImpl = project
		}
		
		// Load properties/environment items
		PropertyLoader.loadProjects(rootProject, wrappedProject)
		
		// Configure base elements
		this@SimpleProject.groupId?.let(this::setGroup)
		this@SimpleProject.version?.let(this::setVersion)
		
		// Configure plugins, tasks and other configurations
		plugins.let {
			basePluginConfiguration(it)
			customPluginConfiguration(it)
		}
		
		extensions.let {
			baseExtensionConfiguration(it)
			customExtensionConfiguration(it)
		}
		
		tasks.let {
			baseTaskConfiguration(it)
			customTaskConfiguration(it)
		}
		
		// Last use custom configuration
		customConfiguration()
		
		// Resolve dependencies
		resolveDependencies(dependencies)
		
		// Call action
		action?.invoke(this@SimpleProject as T) ?: Unit
	}
	
	/**
	 * Resolve project environment variable
	 *
	 * @param key environment/property identifier
	 * @param default the value if environment/property not exists
	 * @return the environment/property value or `null` if default is an empty string
	 */
	fun env(key: String, default: String = ""): String? {
		// Get all environment/property elements
		val items = PropertyLoader.getProjectItems(wrappedProject)
		var result: String? = null
		
		// Inspect all elements for the key
		for (item in items) {
			if (item.containsKey(key)) {
				result = item.getProperty(key)
			}
		}
		
		return if (result == null) {
			default.ifBlank { null }
		} else {
			result
		}
	}
	
	/* -----------------------------------------------------------------------
	 * Utility methods
	 * -----------------------------------------------------------------------*/
	
	/**
	 * Internal plugin configuration
	 *
	 * @param container plugin container
	 */
	protected open fun basePluginConfiguration(container: PluginContainer) = Unit
	
	/**
	 * Internal extension configuration
	 *
	 * @param container extension container
	 */
	protected open fun baseExtensionConfiguration(container: ExtensionContainer) = Unit
	
	/**
	 * Internal task configuration
	 *
	 * @param container task container
	 */
	protected open fun baseTaskConfiguration(container: TaskContainer) = Unit
	
	/**
	 * Gets the project reference
	 *
	 * @param root super project instance
	 * @param query child project name
	 */
	protected fun project(root: Project, query: String): Optional<Project> {
		// Check if project exists
		return try {
			Optional.of(root.project(query))
		} catch (e: Exception) {
			e.printStackTrace()
			Optional.empty<Project>()
		}
	}
	
	/**
	 * Gets the project reference
	 *
	 * @param query child project name
	 */
	protected fun project(query: String): Optional<Project> {
		return project(wrappedProject, query)
	}
	
	/**
	 * Gets the project reference
	 *
	 * @param query child project name
	 */
	protected fun globalProject(query: String): Optional<Project> {
		var current = wrappedProject
		while (current.parent != null) {
			current = current.parent!!
		}
		
		return project(current, query)
	}
	
	/* -----------------------------------------------------------------------
	 * Internal methods
	 * -----------------------------------------------------------------------*/
	
	/**
	 * Configure project dependencies
	 *
	 * @param handler current project dependency handler
	 */
	private fun resolveDependencies(handler: DependencyHandler) = with(handler) {
		// Store valid dependency map
		val validDependencyMap = dependencyMap ?: return@with
		
		// Iterate all elements
		for ((k, v) in validDependencyMap.entries) {
			// Check if value is a valid dependency
			if (v == null) continue
			// Iterate all dependencies
			for (d in v) {
				// Ignore null dependencies
				if (d == null) continue
				
				// Check dependency type
				when (d) {
					is Optional<*> -> if (d.isPresent) addDependency(handler, k, d.get()) else continue
					else -> addDependency(handler, k, d)
				}
			}
		}
	}
	
	/**
	 * Insert the dependency to current project
	 *
	 * @param handler project dependency handler
	 * @param configuration dependency configuration identifier
	 * @param dependency dependency object
	 */
	private fun addDependency(handler: DependencyHandler, configuration: Any, dependency: Any) = with(handler) {
		// Extract the configuration name
		val configurationId = when (configuration) {
			is CharSequence -> configuration.toString()
			is DependencyType -> configuration.id
			else -> return@with
		}
		// Resolve dependency string
		val realDependency = when (dependency) {
			is CharSequence -> resolveMavenDependency(dependency.toString())
			else -> dependency
		}
		// Add dependency to project
		add(configurationId, realDependency)
	}
	
}