package com.hakimi.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.hakimi.model.HealthArchiveEntry;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class HealthArchiveStorageTest {

    @Test
    public void upsertEntry_replacesExistingSameDayRecord() {
        List<HealthArchiveEntry> entries = new ArrayList<>();
        entries.add(createEntry("2026-04-09", 4, 4, 4, 4, 4));

        List<HealthArchiveEntry> updated = HealthArchiveStorage.upsertEntry(
                entries,
                createEntry("2026-04-09", 2, 3, 4, 5, 1)
        );

        assertEquals(1, updated.size());
        assertEquals(2, updated.get(0).getMentalState());
        assertEquals(1, updated.get(0).getStressIndex());
    }

    @Test
    public void evaluateRisk_returnsTriggeredForThreeConsecutiveLowScores() {
        List<HealthArchiveEntry> entries = new ArrayList<>();
        entries.add(createEntry("2026-04-09", 2, 4, 4, 4, 4));
        entries.add(createEntry("2026-04-08", 1, 4, 4, 4, 4));
        entries.add(createEntry("2026-04-07", 2, 4, 4, 4, 4));

        HealthArchiveStorage.HealthRiskResult result = HealthArchiveStorage.evaluateRisk(entries);

        assertTrue(result.isTriggered());
        assertEquals("2026-04-09", result.getLatestDate());
        assertTrue(result.getDimensions().contains("精神状态"));
    }

    @Test
    public void evaluateRisk_doesNotTriggerForNonConsecutiveDates() {
        List<HealthArchiveEntry> entries = new ArrayList<>();
        entries.add(createEntry("2026-04-09", 2, 4, 4, 4, 4));
        entries.add(createEntry("2026-04-07", 1, 4, 4, 4, 4));
        entries.add(createEntry("2026-04-06", 2, 4, 4, 4, 4));

        HealthArchiveStorage.HealthRiskResult result = HealthArchiveStorage.evaluateRisk(entries);

        assertFalse(result.isTriggered());
    }

    private HealthArchiveEntry createEntry(String date, int mentalState, int bodyPain,
            int sleepQuality, int dietRegularity, int stressIndex) {
        HealthArchiveEntry entry = new HealthArchiveEntry();
        entry.setDate(date);
        entry.setMentalState(mentalState);
        entry.setBodyPain(bodyPain);
        entry.setSleepQuality(sleepQuality);
        entry.setDietRegularity(dietRegularity);
        entry.setStressIndex(stressIndex);
        return entry;
    }
}
