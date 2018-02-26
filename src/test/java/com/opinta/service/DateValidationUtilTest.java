package com.opinta.service;

import com.opinta.util.DateValidationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;

import static com.opinta.util.DateValidationUtil.*;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class DateValidationUtilTest {
    @Test
    public void validateFromToDates_DatesInclusiveAndCanBePast() {
        LocalDate pastDate = LocalDate.now().minusDays(1);
        LocalDate futureDate = LocalDate.now().plusDays(1);
        String fromFutureToPastMessage = null;
        String fromPastToFutureMessage = null;
        String sameDatesMessage = null;

        try {
            validateFromToDates(canBePast(futureDate), inclusive(canBePast(pastDate)));
        } catch (DateValidationException e) {
            fromFutureToPastMessage = e.getMessage();
        }
        try {
            validateFromToDates(canBePast(pastDate), inclusive(canBePast(futureDate)));
        } catch (DateValidationException e) {
            fromPastToFutureMessage = e.getMessage();
        }
        try {
            validateFromToDates(canBePast(pastDate), inclusive(canBePast(pastDate)));
        } catch (DateValidationException e) {
            sameDatesMessage = e.getMessage();
        }

        assertEquals("fromDate cannot be later than toDate", fromFutureToPastMessage);
        assertEquals(null, fromPastToFutureMessage);
        assertEquals(null, sameDatesMessage);
    }

    @Test
    public void validateFromToDates_DatesExclusiveAndMustBeFuture() {
        LocalDate pastDate = LocalDate.now().minusDays(1);
        LocalDate futureDate = LocalDate.now().plusDays(1);
        LocalDate evenMoreFutureDate = futureDate.plusDays(1);
        String fromFutureToPastMessage = null;
        String fromPastToFutureMessage = null;
        String futureDatesMessage = null;
        String sameFutureDatesMessage = null;

        try {
            validateFromToDates(mustBeFuture(futureDate), exclusive(mustBeFuture(pastDate)));
        } catch (DateValidationException e) {
            fromFutureToPastMessage = e.getMessage();
        }
        try {
            validateFromToDates(mustBeFuture(pastDate), exclusive(mustBeFuture(futureDate)));
        } catch (DateValidationException e) {
            fromPastToFutureMessage = e.getMessage();
        }
        try {
            validateFromToDates(mustBeFuture(futureDate), exclusive(mustBeFuture(futureDate)));
        } catch (DateValidationException e) {
            sameFutureDatesMessage = e.getMessage();
        }
        try {
            validateFromToDates(mustBeFuture(futureDate), exclusive(mustBeFuture(evenMoreFutureDate)));
        } catch (DateValidationException e) {
            futureDatesMessage = e.getMessage();
        }

        assertEquals("toDate must be in future", fromFutureToPastMessage);
        assertEquals("fromDate must be in future", fromPastToFutureMessage);
        assertEquals("Dates should not be the same", sameFutureDatesMessage);
        assertEquals(null, futureDatesMessage);
    }

    @Test
    public void validateFromToDates_DatesInclusiveAndToDateMustBeFuture() {
        LocalDate pastDate = LocalDate.now().minusDays(1);
        LocalDate futureDate = LocalDate.now().plusDays(1);
        String fromFutureToPastMessage = null;
        String fromPastToFutureMessage = null;
        String samePastDatesMessage = null;
        String sameFutureDatesMessage = null;

        try {
            validateFromToDates(canBePast(futureDate), inclusive(mustBeFuture(pastDate)));
        } catch (DateValidationException e) {
            fromFutureToPastMessage = e.getMessage();
        }
        try {
            validateFromToDates(canBePast(pastDate), inclusive(mustBeFuture(futureDate)));
        } catch (DateValidationException e) {
            fromPastToFutureMessage = e.getMessage();
        }
        try {
            validateFromToDates(canBePast(pastDate), inclusive(mustBeFuture(pastDate)));
        } catch (DateValidationException e) {
            samePastDatesMessage = e.getMessage();
        }
        try {
            validateFromToDates(canBePast(futureDate), inclusive(mustBeFuture(futureDate)));
        } catch (DateValidationException e) {
            sameFutureDatesMessage = e.getMessage();
        }

        assertEquals("toDate must be in future", fromFutureToPastMessage);
        assertEquals(null, fromPastToFutureMessage);
        assertEquals("toDate must be in future", samePastDatesMessage);
        assertEquals(null, sameFutureDatesMessage);
    }
}
