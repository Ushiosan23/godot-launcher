package project.dependency


/* -----------------------------------------------------------------------
 * Global properties
 * -----------------------------------------------------------------------*/

val IMPLEMENTATION = DependencyType("implementation")
val COMPILE_ONLY = DependencyType("compileOnly")
val TEST_IMPLEMENTATION = DependencyType("testImplementation")
val TEST_COMPILE_ONLY = DependencyType("testCompileOnly")

/* -----------------------------------------------------------------------
 * Types
 * -----------------------------------------------------------------------*/

/**
 * Dependency configuration type
 *
 * @param id dependency configuration name
 */
data class DependencyType(
	val id: String
) {
	
	/**
	 * Generate dependency pair with from instance
	 *
	 * @param dependencies All project dependencies
	 * @return dependency pair
	 */
	fun make(vararg dependencies: Any): Pair<DependencyType, List<Any>> =
		Pair(this, dependencies.toList())
	
	/**
	 * Generate dependency pair with from instance
	 *
	 * @param dependencies All project dependencies
	 * @return dependency pair
	 */
	operator fun plus(dependencies: Iterable<Any>): Pair<DependencyType, List<Any>> =
		make(dependencies)
	
}
