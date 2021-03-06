package com.gcplot.analytics;

import java.util.EnumSet;

/**
 * @author <a href="mailto:art.dm.ser@gmail.com">Artem Dmitriev</a>
 *         2/24/17
 */
public enum GCEventFeature {
    SAMPLE_YOUNG, SAMPLE_TENURED, SAMPLE_CONCURRENT, SAMPLE_FULL, CALC_RATES, CALC_STATISTIC;

    private static final EnumSet<GCEventFeature> samplers;
    private static final EnumSet<GCEventFeature> all;
    private static final EnumSet<GCEventFeature> noStats;

    static {
        samplers = EnumSet.of(SAMPLE_YOUNG, SAMPLE_TENURED, SAMPLE_CONCURRENT, SAMPLE_FULL);
        all = EnumSet.allOf(GCEventFeature.class);
        noStats = EnumSet.of(SAMPLE_YOUNG, SAMPLE_TENURED, SAMPLE_CONCURRENT, SAMPLE_FULL, CALC_RATES);
    }

    public static EnumSet<GCEventFeature> getSamplers() {
        return samplers;
    }

    public static EnumSet<GCEventFeature> getAll() {
        return all;
    }

    public static EnumSet<GCEventFeature> getNoStats() {
        return noStats;
    }
}
