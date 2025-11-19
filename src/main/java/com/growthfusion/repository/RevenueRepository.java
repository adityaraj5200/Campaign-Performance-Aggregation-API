package com.growthfusion.repository;

import com.growthfusion.model.RevenueRecord;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for fetching revenue rows within a UTC window.
 * These rows are later aggregated into unified revenue totals per campaign.
 */
@Repository
public class RevenueRepository {

    private final JdbcTemplate jdbcTemplate;

    public RevenueRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<RevenueRecord> findByUtcWindow(LocalDateTime utcStart, LocalDateTime utcEnd) {
        String sql = """
            SELECT campaign_name, clicks, unique_clicks, conversions,
                   lp_views, lp_clicks, revenue, ingested_at
            FROM revenue_table
            WHERE ingested_at BETWEEN ? AND ?
            """;

        return jdbcTemplate.query(sql, new Object[]{utcStart, utcEnd}, new RevenueRowMapper());
    }

    private static class RevenueRowMapper implements RowMapper<RevenueRecord> {
        @Override
        public RevenueRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
            RevenueRecord r = new RevenueRecord();
            r.setCampaignName(rs.getString("campaign_name"));
            r.setClicks(rs.getLong("clicks"));
            r.setUniqueClicks(rs.getLong("unique_clicks"));
            r.setConversions(rs.getLong("conversions"));
            r.setLpViews(rs.getLong("lp_views"));
            r.setLpClicks(rs.getLong("lp_clicks"));
            r.setRevenue(rs.getDouble("revenue"));
            r.setIngestedAt(rs.getTimestamp("ingested_at").toLocalDateTime());
            return r;
        }
    }
}
