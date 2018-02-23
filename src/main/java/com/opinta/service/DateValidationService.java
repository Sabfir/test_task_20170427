package com.opinta.service;

import java.time.LocalDate;

public class DateValidationService {
    private static boolean exclusive;

    public static void validateFromToDates(LocalDate fromDate, LocalDate toDate) throws DateValidationException {
        if (fromDate.isAfter(toDate)) {
            throw new DateValidationException("fromDate cannot be later than toDate");
        }
        if (exclusive && fromDate.isEqual(toDate)) {
            throw new DateValidationException("Dates should not be the same");
        }
    }

    public static LocalDate inclusive(LocalDate date) {
        exclusive = false;
        return date;
    }

    public static LocalDate exclusive(LocalDate date) {
        exclusive = true;
        return date;
    }

    public static LocalDate canBePast(LocalDate date) {
        return date;
    }

    public static LocalDate mustBeFuture(LocalDate date) throws DateValidationException {
        if (!date.isAfter(LocalDate.now())) {
            throw new DateValidationException("Provided date must be in future");
        }
        return date;
    }
}
