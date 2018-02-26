package com.opinta.util;

public class IntersectionValidatedLocalDateImpl implements IntersectionValidatedLocalDate {
    private final TenseValidatedLocalDate localDate;
    private final boolean exclusive;

    IntersectionValidatedLocalDateImpl(TenseValidatedLocalDate localDate, boolean exclusive) {
        this.localDate = localDate;
        this.exclusive = exclusive;
    }

    @Override
    public TenseValidatedLocalDate getTenseValidatedLocalDate() {
        return localDate;
    }

    @Override
    public boolean isExclusive() {
        return exclusive;
    }
}
