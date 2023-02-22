package project.dependency.maven

import kotlinx.serialization.Serializable

/**
 * Artifact object structure
 *
 * @param artifact the artifact
 * @param g the artifact group
 * @param a the artifact name
 * @param v the latest artifact version
 * @param p the artifact file type
 * @param timestamp the artifact creation timestamp
 * @param ec the artifact files
 * @param tags the artifact resource tags
 */
@Serializable
class MavenArtifact(
	val id: String,
	val g: String,
	val a: String,
	val v: String,
	val p: String,
	val timestamp: Long,
	val ec: List<String>?,
	val tags: List<String>?
)

/**
 * Artifact response header
 *
 * @param status response status
 * @param QTime response time
 * @param params request params
 */
@Serializable
class MavenHeader(
	val status: Int,
	val QTime: Int,
	val params: Map<String, String>
)

/**
 * Maven partial content response
 *
 * @param numFound total of artifacts found
 * @param start the index with the fist artifact
 * @param docs Array with all artifacts
 */
@Serializable
class MavenPartialResponse(
	val numFound: Int,
	val start: Int,
	val docs: Array<MavenArtifact>
)

/**
 * Maven complete content response
 *
 * @param responseHeader response header
 * @param response response body
 */
@Serializable
class MavenResponse(
	val responseHeader: MavenHeader,
	val response: MavenPartialResponse?
)