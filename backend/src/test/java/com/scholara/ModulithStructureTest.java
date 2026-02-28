package com.scholara;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

/**
 * Modulith Structure Verification Tests.
 *
 * These tests verify that module boundaries are respected
 * and generate documentation for the module structure.
 */
class ModulithStructureTest {

    private final ApplicationModules modules = ApplicationModules.of(ScholaraApplication.class);

    @Test
    void verifyModuleStructure() {
        modules.verify();
    }

    @Test
    void generateModuleDocumentation() {
        new Documenter(modules)
            .writeModulesAsPlantUml()
            .writeIndividualModulesAsPlantUml();
    }

}
