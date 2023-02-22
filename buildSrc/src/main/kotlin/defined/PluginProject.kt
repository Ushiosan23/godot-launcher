package defined

import project.dependency.IMPLEMENTATION
import project.java.BaseJavaProject

/**
 * Plugin system project instance
 */
object PluginSystemProject : BaseJavaProject<PluginSystemProject>() {
	
	/**
	 * Project name
	 */
	override val name: String
		get() = "plugin-system"
	
	/**
	 * Project group id
	 */
	override val groupId: String
		get() = "com.github.ushiosan23"
	
	/**
	 * Project version
	 */
	override val version: String
		get() = "0.0.1"
	
	/**
	 * Project dependencies
	 */
	override val dependencyMap: Map<Any, Iterable<*>?>
		get() = dependenciesOf(
			IMPLEMENTATION.make(globalProject(":plugin-api"))
		)
	
}

/**
 * Plugin api project instance
 */
object PluginApiProject : BaseJavaProject<PluginApiProject>() {
	
	/**
	 * Project name
	 */
	override val name: String
		get() = "plugin-api"
	
	/**
	 * Project group id
	 */
	override val groupId: String
		get() = "com.github.ushiosan23"
	
	/**
	 * Project version
	 */
	override val version: String
		get() = "0.0.1"
	
	/**
	 * Project dependencies
	 */
	override val dependencyMap: Map<Any, Iterable<*>?>
		get() = dependenciesOf()
	
}