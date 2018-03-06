package com.opinta.util;

import java.time.LocalDate;

public class TenseValidatedLocalDateImpl implements TenseValidatedLocalDate {
    private final LocalDate localDate;
    private final boolean mustBeFuture;

    TenseValidatedLocalDateImpl(LocalDate localDate, boolean mustBeFuture) {
        this.localDate = localDate;
        this.mustBeFuture = mustBeFuture;
    }

    @Override
    public LocalDate getLocalDate() {
        return localDate;
    }

    @Override
    public boolean isMustBeFuture() {
        return mustBeFuture;
    }
}
