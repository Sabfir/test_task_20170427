package com.opinta.util;

import java.time.LocalDate;

public interface TenseValidatedLocalDate {
    LocalDate getLocalDate();
    boolean isMustBeFuture();
}
