package ssbd01.util.comparators;

import ssbd01.entities.Medication;

public class MedicationComparator {
    public static boolean equalsOmitStock(Medication a, Medication b) {
        return a.getCategory().getId().equals(b.getCategory().getId())
                && a.getName().equals(b.getName())
                && a.getCurrentPrice().compareTo(b.getCurrentPrice()) == 0;
    }
}
