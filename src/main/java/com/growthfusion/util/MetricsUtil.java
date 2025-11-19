package com.growthfusion.util;

/**
 * Utility for safely computing all required performance metrics:
 * profit, roas, roi, lpctr, epc, lpcpc.
 * All divide-by-zero scenarios are safely handled.
 */
public final class MetricsUtil {

    private MetricsUtil() {}

    public static Double profit(Double revenue, Double spend) {
        if (revenue == null || spend == null) return 0.0;
        return revenue - spend;
    }

    public static Double roas(Double revenue, Double spend) {
        if (revenue == null || spend == null || spend == 0) return 0.0;
        return revenue / spend;
    }

    public static Double roi(Double revenue, Double spend) {
        if (revenue == null || spend == null || spend == 0) return 0.0;
        return (revenue - spend) / spend;
    }

    public static Double lpctr(Long lpClicks, Long lpViews) {
        if (lpClicks == null || lpViews == null || lpViews == 0) return 0.0;
        return lpClicks.doubleValue() / lpViews.doubleValue();
    }

    public static Double epc(Double revenue, Long lpClicks) {
        if (revenue == null || lpClicks == null || lpClicks == 0) return 0.0;
        return revenue / lpClicks.doubleValue();
    }

    public static Double lpcpc(Double spend, Long lpClicks) {
        if (spend == null || lpClicks == null || lpClicks == 0) return 0.0;
        return spend / lpClicks.doubleValue();
    }
}
