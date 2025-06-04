package ru.sskier.tg_assistant_bot.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class TestDatabaseCleaner {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public TestDatabaseCleaner(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public void cleanTablesWithOrder(String schemaName, List<String> tables) {
        for (String tableName : tables) {
            deleteFromTable(schemaName + "." + tableName);
        }
    }

    public void cleanSchema(String schemaName) {
        String sql = "SELECT tablename FROM pg_tables WHERE schemaname = :schemaName";
        List<String> tableNames = namedParameterJdbcTemplate.queryForList(sql, Map.of("schemaName", schemaName), String.class);

        for (String tableName : tableNames) {
            truncateTable(schemaName + "." + tableName);
        }
    }

    public void truncateTable(String tableName) {
        String sql = "TRUNCATE TABLE " + tableName;
        namedParameterJdbcTemplate.getJdbcTemplate().execute(sql);
    }

    public void deleteFromTable(String tableName) {
        String sql = "DELETE FROM " + tableName;
        namedParameterJdbcTemplate.getJdbcTemplate().execute(sql);
    }
}

