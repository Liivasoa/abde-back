package mg.msys.abde_back;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@DisplayName("Hexagonal Architecture Tests")
class HexagonalArchitectureTest {

        private static final String BASE_PACKAGE = "mg.msys.abde_back";
        private static final String DOMAIN_PACKAGE = BASE_PACKAGE + ".domain..";
        private static final String APPLICATION_PACKAGE = BASE_PACKAGE + ".application..";
        private static final String PORT_PACKAGE = BASE_PACKAGE + ".application.port..";
        private static final String SERVICE_PACKAGE = BASE_PACKAGE + ".application.service..";
        private static final String USECASE_PACKAGE = BASE_PACKAGE + ".application.usecase..";
        private static final String ADAPTER_PACKAGE = BASE_PACKAGE + ".adapter..";
        private static final String ADAPTER_IN_PACKAGE = BASE_PACKAGE + ".adapter.in..";
        private static final String ADAPTER_OUT_PACKAGE = BASE_PACKAGE + ".adapter.out..";
        private static final String INFRASTRUCTURE_PACKAGE = BASE_PACKAGE + ".infrastructure..";

        private static JavaClasses importedClasses;

        @BeforeAll
        static void setup() {
                importedClasses = new ClassFileImporter()
                                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                                .importPackages(BASE_PACKAGE);
        }

        // =========================================================================
        // DOMAIN LAYER TESTS
        // =========================================================================

        @Nested
        @DisplayName("Domain Layer")
        class DomainLayerTests {

                @Test
                @DisplayName("Domain models should not depend on Spring")
                void domainShouldNotDependOnSpring() {
                        noClasses()
                                        .that().resideInAPackage(DOMAIN_PACKAGE)
                                        .should().dependOnClassesThat().resideInAPackage("org.springframework..")
                                        .as("Domain layer must be independent of Spring framework - " +
                                                        "ensures business logic can be used in any context")
                                        .check(importedClasses);
                }

                @Test
                @DisplayName("Domain models should not depend on adapters")
                void domainShouldNotDependOnAdapters() {
                        noClasses()
                                        .that().resideInAPackage(DOMAIN_PACKAGE)
                                        .should().dependOnClassesThat().resideInAPackage(ADAPTER_PACKAGE)
                                        .as("Domain layer must not know about adapters - " +
                                                        "ensures domain is isolated from infrastructure concerns")
                                        .check(importedClasses);
                }

                @Test
                @DisplayName("Domain models should not depend on JPA or Hibernate")
                void domainShouldNotDependOnPersistence() {
                        noClasses()
                                        .that().resideInAPackage(DOMAIN_PACKAGE)
                                        .should().dependOnClassesThat().resideInAnyPackage(
                                                        "javax.persistence..",
                                                        "jakarta.persistence..",
                                                        "org.hibernate..")
                                        .as("Domain entities must not have persistence annotations (JPA/Hibernate) - " +
                                                        "keeps domain logic independent of persistence framework")
                                        .check(importedClasses);
                }

                @Test
                @DisplayName("Domain models should not depend on application layer")
                void domainShouldNotDependOnApplicationLayer() {
                        noClasses()
                                        .that().resideInAPackage(DOMAIN_PACKAGE)
                                        .should().dependOnClassesThat().resideInAPackage(APPLICATION_PACKAGE)
                                        .as("Domain layer is the innermost layer - " +
                                                        "must not depend on outer layers (application, adapters, infrastructure)")
                                        .check(importedClasses);
                }

                @Test
                @DisplayName("Domain models should not depend on infrastructure")
                void domainShouldNotDependOnInfrastructure() {
                        noClasses()
                                        .that().resideInAPackage(DOMAIN_PACKAGE)
                                        .should().dependOnClassesThat().resideInAPackage(INFRASTRUCTURE_PACKAGE)
                                        .as("Domain layer must not depend on infrastructure - " +
                                                        "keeps database, APIs, and other external systems out of domain")
                                        .check(importedClasses);
                }
        }

        // =========================================================================
        // APPLICATION LAYER TESTS
        // =========================================================================

        @Nested
        @DisplayName("Application Layer")
        class ApplicationLayerTests {

                @Test
                @DisplayName("Application layer should not depend on adapters")
                void applicationShouldNotDependOnAdapters() {
                        noClasses()
                                        .that().resideInAPackage(APPLICATION_PACKAGE)
                                        .should().dependOnClassesThat().resideInAPackage(ADAPTER_PACKAGE)
                                        .as("Application layer must not depend on adapters - " +
                                                        "enforces Dependency Inversion Principle: adapters depend on ports, not vice versa")
                                        .check(importedClasses);
                }

                @Test
                @DisplayName("Application layer should not depend on infrastructure directly")
                void applicationShouldNotDependOnInfrastructure() {
                        noClasses()
                                        .that().resideInAPackage(APPLICATION_PACKAGE)
                                        .should().dependOnClassesThat().resideInAPackage(INFRASTRUCTURE_PACKAGE)
                                        .as("Application layer must not depend on infrastructure - " +
                                                        "loose coupling achieved through ports and dependency injection")
                                        .check(importedClasses);
                }

                @Test
                @DisplayName("Application layer may depend on domain")
                void applicationMayDependOnDomain() {
                        // This is allowed - application layer uses domain models
                        classes()
                                        .that().resideInAPackage(APPLICATION_PACKAGE)
                                        .should().onlyDependOnClassesThat()
                                        .resideInAnyPackage(
                                                        APPLICATION_PACKAGE,
                                                        DOMAIN_PACKAGE,
                                                        "java..",
                                                        "lombok..",
                                                        "org.springframework..",
                                                        "jakarta..",
                                                        "javax..")
                                        .as("Application layer can depend on: domain, other application classes, " +
                                                        "Java standard library, Spring, and annotations")
                                        .check(importedClasses);
                }
        }

        // =========================================================================
        // PORTS TESTS
        // =========================================================================

        @Nested
        @DisplayName("Ports")
        class PortsTests {

                @Test
                @DisplayName("Ports should be interfaces")
                void portsShouldBeInterfaces() {
                        classes()
                                        .that().resideInAPackage(PORT_PACKAGE)
                                        .should().beInterfaces()
                                        .as("Ports are contracts defining what adapters must implement - " +
                                                        "must be interfaces, never concrete classes")
                                        .check(importedClasses);
                }

                @Test
                @DisplayName("Ports should be named with 'Port' suffix")
                void portsShouldBeNamedCorrectly() {
                        classes()
                                        .that().resideInAPackage(PORT_PACKAGE)
                                        .should().haveSimpleNameEndingWith("Port")
                                        .as("Naming convention for ports: *Port (e.g., UserPersistencePort, EmailSenderPort) - "
                                                        +
                                                        "improves code readability and distinguishes ports from other interfaces")
                                        .check(importedClasses);
                }

                @Test
                @DisplayName("Ports should only depend on domain")
                void portsShouldOnlyDependOnDomain() {
                        classes()
                                        .that().resideInAPackage(PORT_PACKAGE)
                                        .should().onlyDependOnClassesThat()
                                        .resideInAnyPackage(
                                                        DOMAIN_PACKAGE,
                                                        PORT_PACKAGE,
                                                        "java..",
                                                        "lombok..",
                                                        "jakarta.validation..",
                                                        "jakarta.annotation..",
                                                        "javax.validation..",
                                                        "javax.annotation..")
                                        .as("Ports define contracts using domain objects - " +
                                                        "must not depend on adapters or services")
                                        .check(importedClasses);
                }

                @Test
                @DisplayName("Ports should not depend on persistence frameworks")
                void portsShouldNotDependOnPersistenceFrameworks() {
                        noClasses()
                                        .that().resideInAPackage(PORT_PACKAGE)
                                        .should().dependOnClassesThat().resideInAnyPackage(
                                                        "jakarta.persistence..",
                                                        "javax.persistence..")
                                        .as("Ports are technology-agnostic contracts - " +
                                                        "must not depend on JPA or any persistence framework")
                                        .check(importedClasses);
                }
        }

        // =========================================================================
        // USE CASES TESTS
        // =========================================================================

        @Nested
        @DisplayName("Use Cases")
        class UseCasesTests {

                @Test
                @DisplayName("Use cases should be interfaces")
                void useCasesShouldBeInterfaces() {
                        classes()
                                        .that().resideInAPackage(USECASE_PACKAGE)
                                        .and().haveSimpleNameEndingWith("UseCase")
                                        .should().beInterfaces()
                                        .as("Use Cases are contracts for business processes - " +
                                                        "must be interfaces to allow multiple implementations and testing")
                                        .check(importedClasses);
                }

                @Test
                @DisplayName("Use cases should be named with 'UseCase' suffix")
                void useCasesShouldBeNamedCorrectly() {
                        classes()
                                        .that().resideInAPackage(USECASE_PACKAGE)
                                        .and().areInterfaces()
                                        .should().haveSimpleNameEndingWith("UseCase")
                                        .as("Naming convention: *UseCase (e.g., CreateUserUseCase) - " +
                                                        "clearly indicates this is a use case definition")
                                        .check(importedClasses);
                }

                @Test
                @DisplayName("Use cases should only depend on domain and ports")
                void useCasesShouldOnlyDependOnDomainAndPorts() {
                        classes()
                                        .that().resideInAPackage(USECASE_PACKAGE)
                                        .should().onlyDependOnClassesThat()
                                        .resideInAnyPackage(
                                                        DOMAIN_PACKAGE,
                                                        PORT_PACKAGE,
                                                        USECASE_PACKAGE,
                                                        "java..",
                                                        "lombok..",
                                                        "jakarta.validation..",
                                                        "jakarta.annotation..",
                                                        "javax.validation..",
                                                        "javax.annotation..")
                                        .as("Use case contracts should only reference domain objects and ports - " +
                                                        "ensures use cases are independent of implementation details")
                                        .check(importedClasses);
                }
        }

        // =========================================================================
        // SERVICES TESTS
        // =========================================================================

        @Nested
        @DisplayName("Services (Application Services)")
        class ServicesTests {

                @Test
                @DisplayName("Services should be named with 'Service' suffix")
                void servicesShouldBeNamedCorrectly() {
                        classes()
                                        .that().resideInAPackage(SERVICE_PACKAGE)
                                        .and().areNotInterfaces()
                                        .should().haveSimpleNameEndingWith("Service")
                                        .as("Naming convention for services: *Service (e.g., CreateUserService) - " +
                                                        "distinguishes services from other classes")
                                        .check(importedClasses);
                }

                @Test
                @DisplayName("Services should depend on ports for external interactions")
                void servicesShouldDependOnPorts() {
                        classes()
                                        .that().resideInAPackage(SERVICE_PACKAGE)
                                        .should().onlyDependOnClassesThat()
                                        .resideInAnyPackage(
                                                        DOMAIN_PACKAGE,
                                                        PORT_PACKAGE,
                                                        SERVICE_PACKAGE,
                                                        USECASE_PACKAGE,
                                                        "java..",
                                                        "org.springframework..",
                                                        "lombok..",
                                                        "jakarta.validation..",
                                                        "jakarta.annotation..",
                                                        "javax.validation..",
                                                        "javax.annotation..")
                                        .as("Services should orchestrate domain logic and communicate with external systems "
                                                        +
                                                        "only through ports (dependency injection)")
                                        .check(importedClasses);
                }

                @Test
                @DisplayName("Services should not depend on adapters")
                void servicesShouldNotDependOnAdapters() {
                        noClasses()
                                        .that().resideInAPackage(SERVICE_PACKAGE)
                                        .should().dependOnClassesThat().resideInAPackage(ADAPTER_PACKAGE)
                                        .as("Services must not know about adapters - " +
                                                        "prevents tight coupling between business logic and infrastructure")
                                        .check(importedClasses);
                }
        }

        // =========================================================================
        // ADAPTER INCOMING (IN) TESTS
        // =========================================================================

        @Nested
        @DisplayName("Input Adapters (Controllers, REST, etc.)")
        class InputAdaptersTests {

                @Test
                @DisplayName("Input adapters should not depend on output adapters")
                void inputAdaptersShouldNotDependOnOutputAdapters() {
                        noClasses()
                                        .that().resideInAPackage(ADAPTER_IN_PACKAGE)
                                        .should().dependOnClassesThat().resideInAPackage(ADAPTER_OUT_PACKAGE)
                                        .as("Input adapters must not directly depend on output adapters - " +
                                                        "communication must flow through application services and ports")
                                        .check(importedClasses);
                }

                @Test
                @DisplayName("Input adapters should depend on application ports for services")
                void inputAdaptersShouldDependOnPortsNotServices() {
                        // Note: Adapters CAN depend on Spring components and services they need to work
                        classes()
                                        .that().resideInAPackage(ADAPTER_IN_PACKAGE)
                                        .should().onlyDependOnClassesThat()
                                        .resideInAnyPackage(
                                                        DOMAIN_PACKAGE,
                                                        PORT_PACKAGE,
                                                        SERVICE_PACKAGE,
                                                        USECASE_PACKAGE,
                                                        ADAPTER_IN_PACKAGE,
                                                        "java..",
                                                        "org.springframework..",
                                                        "com.fasterxml..",
                                                        "io.swagger.v3.oas.annotations..",
                                                        "lombok..")
                                        .as("Input adapters orchestrate incoming requests through use cases/services - "
                                                        +
                                                        "should not directly depend on persistence or other output adapters")
                                        .check(importedClasses);
                }
        }

        // =========================================================================
        // ADAPTER OUTGOING (OUT) TESTS
        // =========================================================================

        @Nested
        @DisplayName("Output Adapters (Repositories, External Services, etc.)")
        class OutputAdaptersTests {

                @Test
                @DisplayName("Output adapters should be named with 'Adapter' suffix")
                void outputAdaptersShouldBeNamedCorrectly() {
                        classes()
                                        .that().resideInAPackage(ADAPTER_OUT_PACKAGE)
                                        .and().areNotInterfaces()
                                        .and().areNotAnnotations()
                                        .and().resideOutsideOfPackage(BASE_PACKAGE + ".adapter.out.entity..")
                                        .and().resideOutsideOfPackage(BASE_PACKAGE + ".adapter.out.mapper..")
                                        .should().haveSimpleNameEndingWith("Adapter")
                                        .as("Naming convention: *Adapter (e.g., UserPersistenceAdapter) - " +
                                                        "clearly indicates this is an adapter implementing a port")
                                        .check(importedClasses);
                }

                @Test
                @DisplayName("Output adapters should not depend on input adapters")
                void outputAdaptersShouldNotAccessInputAdapters() {
                        noClasses()
                                        .that().resideInAPackage(ADAPTER_OUT_PACKAGE)
                                        .should().dependOnClassesThat().resideInAPackage(ADAPTER_IN_PACKAGE)
                                        .as("Output adapters must not depend on input adapters - " +
                                                        "prevents circular dependencies and ensures single responsibility")
                                        .check(importedClasses);
                }

                @Test
                @DisplayName("Output adapter mappers should be named with 'Mapper' or 'MapperImpl' suffix")
                void mappersShouldBeNamedCorrectly() {
                        classes()
                                        .that().resideInAPackage(BASE_PACKAGE + ".adapter.out.mapper..")
                                        .should().haveSimpleNameEndingWith("Mapper").orShould()
                                        .haveSimpleNameEndingWith("MapperImpl")
                                        .as("Mapping utility naming convention: *Mapper or *MapperImpl (e.g., UserEntityMapper, UserEntityMapperImpl) - "
                                                        +
                                                        "indicates responsibility for converting between domain and persistence models. "
                                                        +
                                                        "MapStruct generates implementations with 'Impl' suffix")
                                        .check(importedClasses);
                }

                @Test
                @DisplayName("Output adapter entities should represent persistence model")
                void entitiesShouldOnlyBeInOutAdapter() {
                        // JPA entities should only exist in the output adapter layer
                        classes()
                                        .that().resideInAPackage(BASE_PACKAGE + ".adapter.out.entity..")
                                        .should().onlyDependOnClassesThat()
                                        .resideInAnyPackage(
                                                        BASE_PACKAGE + ".adapter.out.entity..",
                                                        "java..",
                                                        "javax.persistence..",
                                                        "jakarta.persistence..",
                                                        "org.hibernate..",
                                                        "lombok..",
                                                        "com.fasterxml..")
                                        .as("JPA entities (persistence models) should only exist in output adapter layer and "
                                                        +
                                                        "should not depend on domain, services, or other layers")
                                        .check(importedClasses);
                }
        }

        // =========================================================================
        // CROSS-LAYER DEPENDENCY TESTS
        // =========================================================================

        @Nested
        @DisplayName("Cross-Layer Dependencies")
        class CrossLayerDependenciesTests {

                @Test
                @DisplayName("No circular dependencies between adapter layers")
                void noCircularDependenciesBetweenAdapters() {
                        noClasses()
                                        .that().resideInAPackage(ADAPTER_IN_PACKAGE)
                                        .should().dependOnClassesThat().resideInAPackage(ADAPTER_OUT_PACKAGE)
                                        .as("Adapter layers must not have circular dependencies - " +
                                                        "input and output adapters should only communicate through application ports")
                                        .check(importedClasses);
                }

                @Test
                @DisplayName("Infrastructure layer should not be accessed by application layer")
                void infrastructureNotAccessedByApplication() {
                        noClasses()
                                        .that().resideInAPackage(APPLICATION_PACKAGE)
                                        .should().dependOnClassesThat().resideInAPackage(INFRASTRUCTURE_PACKAGE)
                                        .as("Application layer must not directly use infrastructure - " +
                                                        "infrastructure components are managed by adapters")
                                        .check(importedClasses);
                }

                @Test
                @DisplayName("Only adapters should access infrastructure")
                void onlyAdaptersAccessInfrastructure() {
                        classes()
                                        .that().resideInAPackage(INFRASTRUCTURE_PACKAGE)
                                        .should().onlyDependOnClassesThat()
                                        .resideInAnyPackage(
                                                        INFRASTRUCTURE_PACKAGE,
                                                        ADAPTER_PACKAGE,
                                                        DOMAIN_PACKAGE,
                                                        "java..",
                                                        "org.springframework..",
                                                        "javax..",
                                                        "jakarta..",
                                                        "com.fasterxml..",
                                                        "org.hibernate..",
                                                        "org.postgresql..")
                                        .as("Infrastructure layer should only be used by adapters - " +
                                                        "maintains clean separation of concerns")
                                        .check(importedClasses);
                }
        }
}
