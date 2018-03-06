package com.opinta.util;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;

import static com.opinta.util.DateValidationUtil.*;

@RunWith(MockitoJUnitRunner.class)
public class DateValidationUtilTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void validateFromToDates_FromFutureToPast_ShouldThrowDateValidationException() {
        LocalDate pastDate = LocalDate.now().minusDays(1);
        LocalDate futureDate = LocalDate.now().plusDays(1);

        exception.expect(DateValidationException.class);
        validateFromToDates(canBePast(futureDate), inclusive(canBePast(pastDate)));
    }

    @Test
    public void validateFromToDates_InclusiveSameDates() {
        LocalDate date = LocalDate.now();

        validateFromToDates(canBePast(date), inclusive(canBePast(date)));
    }

    @Test
    public void validateFromToDates_ExclusiveSameDates_ShouldThrowDateValidationException() {
        LocalDate date = LocalDate.now();

        exception.expect(DateValidationException.class);
        validateFromToDates(canBePast(date), exclusive(canBePast(date)));
    }

    @Test
    public void validateFromToDates_ExclusiveDifferentDates() {
        LocalDate pastDate = LocalDate.now().minusDays(1);
        LocalDate futureDate = LocalDate.now().plusDays(1);

        validateFromToDates(canBePast(pastDate), exclusive(canBePast(futureDate)));
    }

    @Test
    public void validateFromToDates_DatesCanBePast() {
        LocalDate pastDate = LocalDate.now().minusDays(1);
        LocalDate futureDate = LocalDate.now().plusDays(1);

        validateFromToDates(canBePast(pastDate), inclusive(canBePast(futureDate)));
    }

    @Test
    public void validateFromToDates_PastDatesMustBeFuture_ShouldThrowDateValidationException() {
        LocalDate pastDate = LocalDate.now().minusDays(1);
        LocalDate futureDate = LocalDate.now().plusDays(1);

        exception.expect(DateValidationException.class);
        validateFromToDates(mustBeFuture(pastDate), inclusive(mustBeFuture(futureDate)));
    }

    @Test
    public void validateFromToDates_FutureDatesMustBeFuture() {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        LocalDate evenMoreFutureDate = futureDate.plusDays(1);

        validateFromToDates(mustBeFuture(futureDate), inclusive(mustBeFuture(evenMoreFutureDate)));
    }
}
