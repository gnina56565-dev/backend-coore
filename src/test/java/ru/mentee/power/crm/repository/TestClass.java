package ru.mentee.power.crm.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import ru.mentee.power.crm.Application;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
class DatabaseConfigTest {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Test
  void shouldConnectToH2Database() {
    String databaseProductName = jdbcTemplate
        .execute((ConnectionCallback<String>) connection -> connection.getMetaData().getDatabaseProductName());

    assertThat(databaseProductName).isEqualTo("H2");
  }

  @Test
  void shouldHaveLeadsTableCreated() {
    boolean tableExists = jdbcTemplate.execute((ConnectionCallback<Boolean>) connection -> {
      var resultSet = connection.getMetaData().getTables(null, null, "leads", new String[]{"TABLE"});
      boolean exists = resultSet.next();
      resultSet.close();
      return exists;
    });

    assertThat(tableExists).isTrue();
  }
}
