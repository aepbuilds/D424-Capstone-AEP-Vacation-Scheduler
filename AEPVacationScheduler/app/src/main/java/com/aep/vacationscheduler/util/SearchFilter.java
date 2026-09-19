package com.aep.vacationscheduler.util;

import com.aep.vacationscheduler.data.Excursion;
import com.aep.vacationscheduler.data.Vacation;
import java.util.ArrayList;
import java.util.List;

public class SearchFilter {

    public static List<Vacation> filterVacationsByTitle(List<Vacation> vacations, String query) {
        List<Vacation> filtered = new ArrayList<>();
        String lower = query.toLowerCase().trim();
        for (Vacation v : vacations) {
            if (v.title.toLowerCase().contains(lower)) {
                filtered.add(v);
            }
        }
        return filtered;
    }

    public static List<Excursion> filterExcursionsByTitle(List<Excursion> excursions, String query) {
        List<Excursion> filtered = new ArrayList<>();
        String lower = query.toLowerCase().trim();
        for (Excursion e : excursions) {
            if (e.title.toLowerCase().contains(lower)) {
                filtered.add(e);
            }
        }
        return filtered;
    }
}