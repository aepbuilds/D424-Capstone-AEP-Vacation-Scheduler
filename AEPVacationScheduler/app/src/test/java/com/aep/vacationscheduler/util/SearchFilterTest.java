package com.aep.vacationscheduler.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.aep.vacationscheduler.data.Vacation;
import com.aep.vacationscheduler.data.Excursion;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

public class SearchFilterTest {

    private List<Vacation> sampleVacations() {
        List<Vacation> vacations = new ArrayList<>();
        vacations.add(new Vacation("Bermuda Trip", "Fairmont Southampton", "09/02/26", "09/09/26"));
        vacations.add(new Vacation("Spring Break", "Beachside Resort", "03/10/26", "03/17/26"));
        vacations.add(new Vacation("London Trip", "The Savoy", "06/01/26", "06/08/26"));
        return vacations;
    }

    @Test
    public void filterVacationsByTitle_matchingSubstring_returnsMatches() {
        List<Vacation> result = SearchFilter.filterVacationsByTitle(sampleVacations(), "trip");
        assertEquals(2, result.size());
    }

    @Test
    public void filterVacationsByTitle_caseInsensitive_returnsMatches() {
        List<Vacation> result = SearchFilter.filterVacationsByTitle(sampleVacations(), "BERMUDA");
        assertEquals(1, result.size());
        assertEquals("Bermuda Trip", result.get(0).title);
    }

    @Test
    public void filterVacationsByTitle_noMatch_returnsEmptyList() {
        List<Vacation> result = SearchFilter.filterVacationsByTitle(sampleVacations(), "Paris");
        assertTrue(result.isEmpty());
    }

    @Test
    public void filterVacationsByTitle_emptyQuery_returnsAllVacations() {
        List<Vacation> result = SearchFilter.filterVacationsByTitle(sampleVacations(), "");
        assertEquals(3, result.size());
    }

    @Test
    public void filterExcursionsByTitle_matchingSubstring_returnsMatches() {
        List<Excursion> excursions = new ArrayList<>();
        excursions.add(new Excursion(1, "Snorkeling", "09/03/26"));
        excursions.add(new Excursion(1, "Hiking", "09/04/26"));

        List<Excursion> result = SearchFilter.filterExcursionsByTitle(excursions, "hik");
        assertEquals(1, result.size());
        assertEquals("Hiking", result.get(0).title);
    }
}