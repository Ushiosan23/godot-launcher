package project.dependency

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import project.dependency.maven.MavenResponse
import java.net.HttpURLConnection
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers
import java.time.Duration

/* -----------------------------------------------------------------------
 * Types
 * -----------------------------------------------------------------------*/

/**
 * Dependency cache information
 *
 * @param configuration dependency configuration
 * @param resolved real dependency result
 * @param magic configuration version
 */
data class DependencyCache(
	val configuration: String,
	val resolved: String,
	val magic: String
)

/* -----------------------------------------------------------------------
 * Properties
 * -----------------------------------------------------------------------*/

/**
 * Url to check if artifact is valid
 *
 * @param g the artifact group
 * @param a the artifact name
 * @param rows total of elements to show
 * @param wt the response format (json|xml)
 */
private val urlArtifactChecker =
	"https://search.maven.org/solrsearch/select?q=g:%s+AND+a:%s&core=gav&rows=%d&wt=%s"

/**
 * Regular expression used to check if artifact special notation is valid
 */
private val configurationChecker =
	Regex("#(.+)#")

/**
 * Json serialization object
 */
private val jsonSerializer = Json {
	ignoreUnknownKeys = true
	prettyPrint = true
	useArrayPolymorphism = true
}

/**
 * Dependency checker http client
 */
private val httpDepsClient = HttpClient.newBuilder()
	.version(HttpClient.Version.HTTP_2)
	.connectTimeout(Duration.ofSeconds(5L))
	.build()

/**
 * Dependency cache container
 */
private val dependencyCacheContainer = mutableListOf<DependencyCache>()

/* -----------------------------------------------------------------------
 * Methods
 * -----------------------------------------------------------------------*/

/**
 * Resolve dependency configuration
 *
 * @param configuration target configuration
 */
fun resolveMavenDependency(configuration: String): String {
	// Check if configuration is valid
	val chunks = configuration.split(":")
	if (chunks.size != 3) return configuration
	
	// Validate configuration content
	val found = configurationChecker.find(configuration) ?: return configuration
	val magic = found.groups[1] ?: return configuration
	
	// Check if dependency already exists on cache
	val cacheFound = dependencyCacheContainer.filter { it.configuration == configuration && it.magic == magic.value }
		.firstOrNull()
	if (cacheFound != null) {
		println("Resolved dependency: (${cacheFound.magic}) ${cacheFound.resolved} [cached]")
		return cacheFound.resolved
	}
	
	// Resolve dependencies from HTTP
	val artifactVersion = when (magic.value) {
		"latest" -> resolveLatestArtifact(chunks[0], chunks[1])
		else -> resolveLatestArtifact(chunks[0], chunks[1])
	}
	
	// Resolve dependency
	val resolved = configuration.substring(0, found.range.first) + artifactVersion
	println("Resolved dependency: (${magic.value}) $resolved")
	dependencyCacheContainer.add(DependencyCache(configuration, resolved, magic.value))
	
	// Get the dependency value
	return resolved
}

/* -----------------------------------------------------------------------
 * Internal methods
 * -----------------------------------------------------------------------*/

/**
 * Resolve latest artifact version
 *
 * @param group artifact group
 * @param artifact id
 * @return latest artifact version
 */
@Suppress("OPT_IN_USAGE")
private fun resolveLatestArtifact(group: String, artifact: String): String {
	// Make http request
	val validUri = urlArtifactChecker.format(group, artifact, 5, "json")
	val request = HttpRequest.newBuilder()
		.uri(URI.create(validUri))
		.setHeader("User-Agent", "Java/HttpClient")
		.GET()
		.build()
	
	// Send request
	val response = httpDepsClient.send(request, BodyHandlers.ofInputStream())
	if (response.statusCode() != HttpURLConnection.HTTP_OK) {
		throw RuntimeException("Error to make request. Error ${response.statusCode()}")
	}
	
	// Parse response content
	val jsonContent = jsonSerializer.decodeFromStream<MavenResponse>(response.body())
	if (jsonContent.response == null || jsonContent.response.numFound <= 0) {
		throw RuntimeException("Invalid response content. Content cannot be null or configuration not exists")
	}
	
	return jsonContent.response.docs.first().v
}