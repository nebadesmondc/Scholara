/**
 * Assessment Module - Examinations and Grading.
 *
 * <p>This module is responsible for:
 * <ul>
 *   <li>Exam creation and management</li>
 *   <li>Question bank management</li>
 *   <li>High-concurrency exam submissions</li>
 *   <li>Grading logic and result calculation</li>
 * </ul>
 *
 * <p>Module Boundaries:
 * <ul>
 *   <li>Depends on identity for student/teacher information</li>
 *   <li>Depends on content for course/subject associations</li>
 *   <li>Depends on shared for common types and events</li>
 *   <li>Publishes assessment completion events</li>
 * </ul>
 *
 * <p>Concurrency Considerations:
 * <ul>
 *   <li>Uses virtual threads for I/O operations</li>
 *   <li>Implements structured concurrency for result aggregation</li>
 * </ul>
 *
 * @see com.scholara.shared
 * @see com.scholara.identity
 * @see com.scholara.content
 */
@org.springframework.modulith.ApplicationModule(
    allowedDependencies = {"shared", "identity", "content"}
)
package com.scholara.assessment;
