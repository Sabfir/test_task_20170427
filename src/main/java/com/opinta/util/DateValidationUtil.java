package com.opinta.util;

import java.time.LocalDate;

public class DateValidationUtil {

    public static void validateFromToDates(TenseValidatedLocalDate tenseValidatedLocalDate,
                                           IntersectionValidatedLocalDate intersectionValidatedLocalDate)
            throws DateValidationException {
        LocalDate actualFromDate = tenseValidatedLocalDate.getLocalDate();
        TenseValidatedLocalDate tenseValidatedToDate = intersectionValidatedLocalDate.getTenseValidatedLocalDate();
        LocalDate actualToDate = tenseValidatedToDate.getLocalDate();

        if (tenseValidatedLocalDate.isMustBeFuture() && !actualFromDate.isAfter(LocalDate.now())) {
            throw new DateValidationException("fromDate must be in future");
        }
        if (tenseValidatedToDate.isMustBeFuture() && !actualToDate.isAfter(LocalDate.now())) {
            throw new DateValidationException("toDate must be in future");
        }
        if (actualFromDate.isAfter(actualToDate)) {
            throw new DateValidationException("fromDate cannot be later than toDate");
        }
        if (intersectionValidatedLocalDate.isExclusive() && actualFromDate.isEqual(actualToDate)) {
            throw new DateValidationException("Dates should not be the same");
        }
    }

    public static IntersectionValidatedLocalDate inclusive(TenseValidatedLocalDate date) {
        return new IntersectionValidatedLocalDateImpl(date, false);
    }

    public static IntersectionValidatedLocalDate exclusive(TenseValidatedLocalDate date) {
        return new IntersectionValidatedLocalDateImpl(date, true);
    }

    public static TenseValidatedLocalDateImpl canBePast(LocalDate date) {
        return new TenseValidatedLocalDateImpl(date, false);
    }

    public static TenseValidatedLocalDateImpl mustBeFuture(LocalDate date) {
        return new TenseValidatedLocalDateImpl(date, true);
    }
}
