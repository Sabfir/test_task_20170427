package com.opinta.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;

import static com.opinta.service.DateValidationService.*;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class DateValidationServiceTest {
    @Test
    public void mustBeFuture_shouldCheckDate() {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        String futureDateMessage = null;
        LocalDate currentDate = LocalDate.now();
        String currentDateMessage = null;
        LocalDate pastDate = LocalDate.now().minusDays(1);
        String pastDateMessage = null;

        try {
            mustBeFuture(futureDate);
        } catch (DateValidationException e) {
            futureDateMessage = e.getMessage();
        }
        try {
            mustBeFuture(currentDate);
        } catch (DateValidationException e) {
            currentDateMessage = e.getMessage();
        }
        try {
            mustBeFuture(pastDate);
        } catch (DateValidationException e) {
            pastDateMessage = e.getMessage();
        }

        assertEquals(null, futureDateMessage);
        assertEquals("Provided date must be in future", currentDateMessage);
        assertEquals("Provided date must be in future", pastDateMessage);
    }

    @Test
    public void validateFromToDates_sameDates() {
        LocalDate date = LocalDate.now();
        String inclusiveMessage = null;
        String exclusiveMessage = null;
        String mustBeFutureMessage = null;

        try {
            validateFromToDates(date, inclusive(date));
        } catch (DateValidationException e) {
            inclusiveMessage = e.getMessage();
        }
        try {
            validateFromToDates(date, exclusive(date));
        } catch (DateValidationException e) {
            exclusiveMessage = e.getMessage();
        }
        try {
            validateFromToDates(canBePast(date), mustBeFuture(date));
        } catch (DateValidationException e) {
            mustBeFutureMessage = e.getMessage();
        }

        assertEquals(null, inclusiveMessage);
        assertEquals("Dates should not be the same", exclusiveMessage);
        assertEquals("Provided date must be in future", mustBeFutureMessage);
    }

    @Test
    public void validateFromToDates_differentDates() {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        LocalDate pastDate = LocalDate.now().minusDays(1);
        String fromFutureToPastMessage = null;
        String fromPastToFutureMessage = null;
        String fromDateCanBePast = null;
        String bothDatesCanBePast = null;
        String toDateMustBeFuture = null;
        String bothDatesMustBeFuture = null;

        try {
            validateFromToDates(futureDate, pastDate);
        } catch (DateValidationException e) {
            fromFutureToPastMessage = e.getMessage();
        }
        try {
            validateFromToDates(pastDate, futureDate);
        } catch (DateValidationException e) {
            fromPastToFutureMessage = e.getMessage();
        }
        try {
            validateFromToDates(canBePast(pastDate), futureDate);
        } catch (DateValidationException e) {
            fromDateCanBePast = e.getMessage();
        }
        try {
            validateFromToDates(canBePast(pastDate), canBePast(futureDate));
        } catch (DateValidationException e) {
            bothDatesCanBePast = e.getMessage();
        }
        try {
            validateFromToDates(pastDate, mustBeFuture(futureDate));
        } catch (DateValidationException e) {
            toDateMustBeFuture = e.getMessage();
        }
        try {
            validateFromToDates(mustBeFuture(pastDate), mustBeFuture(futureDate));
        } catch (DateValidationException e) {
            bothDatesMustBeFuture = e.getMessage();
        }

        assertEquals("fromDate cannot be later than toDate", fromFutureToPastMessage);
        assertEquals(null, fromPastToFutureMessage);
        assertEquals(null, fromDateCanBePast);
        assertEquals(null, bothDatesCanBePast);
        assertEquals(null, toDateMustBeFuture);
        assertEquals("Provided date must be in future", bothDatesMustBeFuture);
    }
}
