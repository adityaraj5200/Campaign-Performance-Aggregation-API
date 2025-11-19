package com.growthfusion.util;

/**
 * Utility for normalizing campaign names during joins.
 * Rules: trim -> lowercase
 */
public final class CampaignNameNormalizer {

    private CampaignNameNormalizer() {}

    public static String normalize(String name) {
        if (name == null) return null;
        return name.trim().toLowerCase();
    }
}
