package com.ghostlead.api;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
/** CI overrides H2 with PostgreSQL and Flyway to verify the actual schema. */
@SpringBootTest
class MigrationTests {
    @Test void applicationStartsWithValidatedSchema() {}
}
