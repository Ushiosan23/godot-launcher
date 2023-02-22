package loader

import org.gradle.api.Project
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.stream.Stream

object PropertyLoader {
	
	/* -----------------------------------------------------------------------
	 * Properties
	 * -----------------------------------------------------------------------*/
	
	/**
	 * Regular expression used to detect a property/environment files
	 */
	private val fileFilter = Regex(".*\\.(properties|env)")
	
	/**
	 * All property/environment loaded files
	 */
	private val loaded = mutableListOf<Path>()
	
	/**
	 * Property object from each project
	 */
	private val cache = mutableMapOf<Project, Properties>()
	
	/* -----------------------------------------------------------------------
	 * Methods
	 * -----------------------------------------------------------------------*/
	
	/**
	 * Current loader configurations
	 */
	val properties: List<Properties>
		get() = cache.values.toList()
	
	/**
	 * Load project properties/environment elements
	 *
	 * @param project the project to inspect
	 * @return current loader configurations
	 */
	@JvmStatic
	fun loadProject(project: Project): Properties {
		// Check if project already exists
		if (project in cache.keys) return cache[project]!!
		
		// Create property project instance
		cache[project] = Properties()
		
		// Store properties on the cache
		val projectItems = getAllValidProjectItems(project)
		projectItems.forEach { addItem(project, it) }
		
		// Update system environment variables.
		// These properties have higher precedence than other properties
		updateEnvironment(project)
		
		// Get the result
		return cache[project]!!
	}
	
	/**
	 * Load multiple projects
	 *
	 * @param projects the projects to inspect
	 */
	@JvmStatic
	fun loadProjects(vararg projects: Project) = projects.forEach(::loadProject)
	
	/**
	 * Returns all elements in hierarchical order
	 *
	 * @return all project elements
	 */
	@JvmStatic
	fun getProjectItems(project: Project): List<Properties> {
		var result = arrayListOf<Properties>()
		var current: Project? = project
		
		do {
			// Check if current current project exists
			if (current in cache.keys) {
				result.add(cache[current]!!)
			} else {
				result.add(loadProject(current!!))
			}
			
			// Change to next project
			current = current?.parent
		} while (current != null)
		
		return result.reversed()
	}
	
	/* -----------------------------------------------------------------------
	 * Internal methods
	 * -----------------------------------------------------------------------*/
	
	/**
	 * Get all valid property/environment files from project
	 *
	 * @param project the project to inspect
	 * @return All valid items
	 */
	@JvmStatic
	fun getAllValidProjectItems(project: Project): Stream<Path> {
		// Get project directory
		val directory = project.projectDir.toPath()
		// Search all valid property/environment files
		return Files.walk(directory, 1)
			.filter(Files::isRegularFile)
			.filter { fileFilter.containsMatchIn(it.fileName.toString()) }
	}
	
	/**
	 * Insert new item to properties
	 *
	 * @param item the project to check
	 * @param path the location load
	 */
	@JvmStatic
	fun addItem(item: Project, path: Path) {
		// Check if path location exists
		if (!Files.exists(path) || loaded.contains(path)) return
		
		// Show debug information
		println("Loading: \"$path\" [${item.name}]")
		
		// Try to load configuration
		try {
			// Temporal variables
			val tmpProps = Properties()
			val tmpStream = Files.newInputStream(path)
			
			// Load properties/environment items
			tmpStream.use(tmpProps::load)
			
			// Add all properties/environment to cache
			cache[item]!!.putAll(tmpProps.map { Pair(it.key, it.value) })
			// Add file to list of items
			loaded.add(path)
		} catch (e: Exception) {
			println("\"$path\" failed to load. ${e.message}")
		}
	}
	
	/**
	 * Update system environment variables
	 *
	 * @param item the project to check
	 */
	@JvmStatic
	fun updateEnvironment(item: Project) {
		// Store environment variables
		val tmpEnv = System.getenv()
		// Update environment variables
		cache[item]!!.putAll(tmpEnv)
	}
	
}