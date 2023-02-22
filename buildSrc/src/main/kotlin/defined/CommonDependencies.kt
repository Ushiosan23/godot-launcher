package defined

import project.base.WrappedProject
import project.dependency.*

/**
 * Common dependency map
 */
private val commonDependencyMap: Map<Any, List<Any>> = mapOf(
	IMPLEMENTATION.make("com.github.ushiosan23:jvm-utilities:#latest#"),
	COMPILE_ONLY.make("org.jetbrains:annotations:#latest#"),
	// Testing
	TEST_IMPLEMENTATION.make("org.junit.jupiter:junit-jupiter:#latest#"),
	TEST_COMPILE_ONLY.make("org.jetbrains:annotations:#latest#")
)

/* -----------------------------------------------------------------------
 * Methods
 * -----------------------------------------------------------------------*/

/**
 * Generate dependency map
 *
 * @param dependencies all extra dependencies
 */
fun dependenciesOf(
	vararg dependencies: Pair<DependencyType, Iterable<Any>>
): Map<Any, List<Any>> {
	// Generate a copy of common dependencies
	val result = commonDependencyMap.toMutableMap()
	
	// Iterate all dependencies
	for ((k, v) in dependencies) {
		if (k !in result) {
			result[k] = v.toList()
		} else {
			result[k] = result[k]!! + v.toList()
		}
	}
	
	// Gets the resulted map
	return result
}

/**
 * Generate dependency map
 *
 * @param dependencies all extra dependencies
 */
fun <T : WrappedProject> T.dependenciesOf(
	vararg dependencies: Pair<DependencyType, Iterable<Any>>
): Map<Any, List<Any>> = defined.dependenciesOf(*dependencies)