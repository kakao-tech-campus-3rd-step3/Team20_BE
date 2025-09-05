package com.example.kspot;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
class KspotApplicationTests {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Test
  void printAllTables() {
    String[] tables = {"locations", "contents"};

    for (String table : tables) {
      System.out.println("=== Table: " + table + " ===");
      List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM " + table);

      for (Map<String, Object> row : rows) {
        System.out.println(row);
      }

      System.out.println(); // 줄바꿈
    }
  }
}
