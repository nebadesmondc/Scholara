/**
 * Content Module - Academic Content Management.
 *
 * <p>This module is responsible for:
 * <ul>
 *   <li>Course management and organization</li>
 *   <li>Subject and lesson management</li>
 *   <li>Learning resource management</li>
 *   <li>Content publishing and versioning</li>
 * </ul>
 *
 * <p>Module Boundaries:
 * <ul>
 *   <li>Depends on identity for author/instructor information</li>
 *   <li>Depends on shared for common types and events</li>
 *   <li>Publishes domain events for content lifecycle changes</li>
 * </ul>
 *
 * @see com.scholara.shared
 * @see com.scholara.identity
 */
@org.springframework.modulith.ApplicationModule(
    allowedDependencies = {"shared", "identity"}
)
package com.scholara.content;
