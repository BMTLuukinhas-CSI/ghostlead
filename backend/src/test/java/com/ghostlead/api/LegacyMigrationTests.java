package com.ghostlead.api;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest
@EnabledIfSystemProperty(named="legacyMigration",matches="true")
class LegacyMigrationTests {
    @Autowired JdbcTemplate jdbc;
    @Test void preservesExistingRecordsWithoutGrantingOwnership() {
        assertThat(jdbc.queryForObject("select count(*) from companies where name='Empresa legada' and owner_id is null",Long.class)).isEqualTo(1);
        assertThat(jdbc.queryForObject("select count(*) from leads where name='Lead legado' and status='NEW' and deal_value=0",Long.class)).isEqualTo(1);
        assertThat(jdbc.queryForObject("select count(*) from flyway_schema_history where version='2' and success=true",Long.class)).isEqualTo(1);
    }
}
