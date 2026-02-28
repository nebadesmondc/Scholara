package com.scholara.shared.domain;

/**
 * User roles within the Scholara platform.
 *
 * <p>Roles define the authorization level and available features for users.
 * The role hierarchy is: ADMIN > TEACHER > STUDENT
 */
public enum Role {

    /**
     * Student role - can enroll in courses, take exams, view progress.
     */
    STUDENT,

    /**
     * Teacher role - can create courses, manage content, grade exams.
     */
    TEACHER,

    /**
     * Administrator role - full system access, user management.
     */
    ADMIN;

    /**
     * Returns the Spring Security authority name for this role.
     *
     * @return the authority string prefixed with "ROLE_"
     */
    public String toAuthority() {
        return "ROLE_" + this.name();
    }
}
