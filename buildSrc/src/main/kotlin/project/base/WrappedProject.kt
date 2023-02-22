package project.base

import org.gradle.api.Project

interface WrappedProject {
	
	/**
	 * Real gradle project
	 */
	val wrappedProject: Project
	
}