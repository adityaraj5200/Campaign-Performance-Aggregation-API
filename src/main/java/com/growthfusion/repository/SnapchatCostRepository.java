package com.growthfusion.repository;

import com.growthfusion.model.SnapchatCost;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for querying the snapchat_cost table via JdbcTemplate.
 * Retrieves all rows falling within a UTC window for snapshot + activity filtering.
 */
@Repository
public class SnapchatCostRepository {

    private final JdbcTemplate jdbcTemplate;

    public SnapchatCostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<SnapchatCost> findByUtcWindow(LocalDateTime utcStart, LocalDateTime utcEnd) {
        String sql = """
            SELECT campaign_id, campaign_name, status, spend, impressions,
                   event_time_la, ingested_at
            FROM snapchat_cost
            WHERE ingested_at BETWEEN ? AND ?
            """;

        return jdbcTemplate.query(sql, new Object[]{utcStart, utcEnd}, new SnapchatCostRowMapper());
    }

    private static class SnapchatCostRowMapper implements RowMapper<SnapchatCost> {
        @Override
        public SnapchatCost mapRow(ResultSet rs, int rowNum) throws SQLException {
            SnapchatCost s = new SnapchatCost();
            s.setCampaignId(rs.getString("campaign_id"));
            s.setCampaignName(rs.getString("campaign_name"));
            s.setStatus(rs.getString("status"));
            s.setSpend(rs.getDouble("spend"));
            s.setImpressions(rs.getLong("impressions"));
            s.setEventTimeLa(rs.getTimestamp("event_time_la").toLocalDateTime());
            s.setIngestedAt(rs.getTimestamp("ingested_at").toLocalDateTime());
            return s;
        }
    }
}
