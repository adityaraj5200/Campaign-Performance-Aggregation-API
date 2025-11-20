package com.growthfusion.repository;

import com.growthfusion.model.MetaCost;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for querying the meta_cost table using JdbcTemplate.
 * Provides methods to fetch all rows inside a UTC window for later filtering.
 */
@Repository
public class MetaCostRepository {

    private final JdbcTemplate jdbcTemplate;

    public MetaCostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<MetaCost> findByUtcWindow(LocalDateTime utcStart, LocalDateTime utcEnd) {
        String sql = """
            SELECT campaign_id, campaign_name, status, spend, impressions, 
                   event_time_la, ingested_at
            FROM meta_cost
            WHERE event_time_la BETWEEN ? AND ?
            """;

        return jdbcTemplate.query(sql, new Object[]{utcStart, utcEnd}, new MetaCostRowMapper());
    }

    private static class MetaCostRowMapper implements RowMapper<MetaCost> {
        @Override
        public MetaCost mapRow(ResultSet rs, int rowNum) throws SQLException {
            MetaCost m = new MetaCost();
            m.setCampaignId(rs.getString("campaign_id"));
            m.setCampaignName(rs.getString("campaign_name"));
            m.setStatus(rs.getString("status"));
            m.setSpend(rs.getDouble("spend"));
            m.setImpressions(rs.getLong("impressions"));
            m.setEventTimeLa(rs.getTimestamp("event_time_la").toLocalDateTime());
            m.setIngestedAt(rs.getTimestamp("ingested_at").toLocalDateTime());
            return m;
        }
    }
}
