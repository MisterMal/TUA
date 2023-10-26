//package ssbd01.moa.managers;
//
//import jakarta.annotation.security.PermitAll;
//import jakarta.ejb.Local;
//import jakarta.inject.Qualifier;
//import ssbd01.common.CommonManagerLocalInterface;
//import ssbd01.entities.Medication;
//
//import java.lang.annotation.Retention;
//import java.util.List;
//
//import static java.lang.annotation.RetentionPolicy.RUNTIME;
//
//@Local
//public interface MedicationManagerLocal extends CommonManagerLocalInterface {
//
//    @PermitAll
//    Medication findByName(String name);
//
//    Medication createMedication(Medication medication, String categoryName);
//
//    List<Medication> getAllMedications();
//
//    Medication getMedication(Long id);
//
//    Medication editMedication(Medication medication);
//
//    Medication getMedicationDetails(Long id);
//
//
//}
