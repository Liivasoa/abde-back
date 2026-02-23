package mg.msys.abde_back;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class HexagonalArchitectureTest {

    private final JavaClasses importedClasses = new ClassFileImporter().importPackages("mg.msys.abde_back");

    @Disabled
    @Test
    void should_respect_hexagonal_architecture() {
        var architecture = layeredArchitecture().consideringAllDependencies()
                .layer("Domain").definedBy("mg.msys.abde_back.domain..")
                .layer("Application").definedBy("mg.msys.abde_back.application..")
                .layer("Adapters").definedBy("mg.msys.abde_back.adapter..")
                .layer("Ports").definedBy("mg.msys.abde_back.application.port..");

        architecture
                .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Adapters")
                .whereLayer("Application").mayOnlyBeAccessedByLayers("Adapters")
                .whereLayer("Adapters").mayNotBeAccessedByAnyLayer()
                .check(importedClasses);
    }

    @Disabled
    @Test
    void domain_should_not_depend_on_adapters() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("mg.msys.abde_back.domain..")
                .should().dependOnClassesThat().resideInAPackage("mg.msys.abde_back.adapter..");

        rule.check(importedClasses);
    }

    @Disabled
    @Test
    void domain_should_not_depend_on_application() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("mg.msys.abde_back.domain..")
                .should().dependOnClassesThat().resideInAPackage("mg.msys.abde_back.application..");

        rule.check(importedClasses);
    }

    @Disabled
    @Test
    void ports_should_be_interfaces() {
        ArchRule rule = classes()
                .that().resideInAPackage("mg.msys.abde_back.application.port..")
                .should().beInterfaces();

        rule.check(importedClasses);
    }

    @Disabled
    @Test
    void adapters_should_implement_ports() {
        ArchRule rule = classes()
                .that().resideInAPackage("mg.msys.abde_back.adapter.out")
                .should().implement(JavaClass.Predicates.resideInAPackage("mg.msys.abde_back.application.port"));

        rule.check(importedClasses);
    }

    @Disabled
    @Test
    void model_should_not_depend_on_frameworks() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("mg.msys.abde_back.domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "org.springframework..",
                        "jakarta.persistence..",
                        "javax.persistence..");

        rule.check(importedClasses);
    }

}
