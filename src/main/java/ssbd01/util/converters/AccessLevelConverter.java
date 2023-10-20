package ssbd01.util.converters;

import ssbd01.dto.AccessLevelDTO;
import ssbd01.dto.AdminDataDTO;
import ssbd01.dto.ChemistDataDTO;
import ssbd01.dto.PatientDataDTO;
import ssbd01.dto.editAccessLevel.EditAdminDataDTO;
import ssbd01.dto.editAccessLevel.EditChemistDataDTO;
import ssbd01.dto.editAccessLevel.EditPatientDataDTO;
import ssbd01.dto.editAccount.grant.GrantAdminDataDTO;
import ssbd01.dto.editAccount.grant.GrantChemistDataDTO;
import ssbd01.dto.editAccount.grant.GrantPatientDataDTO;
import ssbd01.dto.editSelfAccessLevel.EditSelfAdminDataDTO;
import ssbd01.dto.editSelfAccessLevel.EditSelfChemistDataDTO;
import ssbd01.dto.editSelfAccessLevel.EditSelfPatientDataDTO;
import ssbd01.entities.AccessLevel;
import ssbd01.entities.AdminData;
import ssbd01.entities.ChemistData;
import ssbd01.entities.PatientData;
import ssbd01.exceptions.AccountApplicationException;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class AccessLevelConverter {

  private AccessLevelConverter() {}

  public static PatientData mapPatientDataDtoToPatientData(PatientDataDTO patientDataDTO) {
    return PatientData.builder()
        .id(patientDataDTO.getId())
        .firstName(patientDataDTO.getFirstName())
        .lastName(patientDataDTO.getLastName())
        .NIP(patientDataDTO.getNip())
        .phoneNumber(patientDataDTO.getPhoneNumber())
        .pesel(patientDataDTO.getPesel())
        .build();
  }

  public static PatientData mapEditPatientDataDtoToPatientData(EditPatientDataDTO patientDataDTO) {
    return PatientData.builder()
        .firstName(patientDataDTO.getFirstName())
        .lastName(patientDataDTO.getLastName())
        .NIP(patientDataDTO.getNip())
        .phoneNumber(patientDataDTO.getPhoneNumber())
        .pesel(patientDataDTO.getPesel())
        .build();
  }

  public static PatientData mapEditSelfPatientDataDtoToPatientData(EditSelfPatientDataDTO patientDataDTO) {
    return PatientData.builder()
            .firstName(patientDataDTO.getFirstName())
            .lastName(patientDataDTO.getLastName())
            .NIP(patientDataDTO.getNip())
            .phoneNumber(patientDataDTO.getPhoneNumber())
            .pesel(patientDataDTO.getPesel())
            .build();
  }

  public static Set<AccessLevelDTO> mapAccessLevelsToAccessLevelsDto(Set<AccessLevel> levels) {
    return null == levels
        ? null
        : levels.stream()
            .filter(Objects::nonNull)
            .map(AccessLevelConverter::mapAccessLevelToAccessLevelDto)
            .collect(Collectors.toSet());
  }

  public static AccessLevelDTO mapAccessLevelToAccessLevelDto(AccessLevel accessLevel) {
    if (accessLevel instanceof PatientData) {
      return mapPatientDataToPatientDataDto((PatientData) accessLevel);
    }
    if (accessLevel instanceof ChemistData) {
      return mapChemistDataToChemistDataDto((ChemistData) accessLevel);
    }
    if (accessLevel instanceof AdminData) {
      return mapAdminDataToAdminDataDto((AdminData) accessLevel);
    }
    throw AccountApplicationException.createUndefinedAccessLevelException();
  }

  // PATIENT
  public static PatientDataDTO mapPatientDataToPatientDataDto(PatientData patientData) {
    return PatientDataDTO.builder()
        .id(patientData.getId())
        .version(patientData.getVersion())
        .role(patientData.getRole())
        .active(patientData.getActive())
        .pesel(patientData.getPesel())
        .firstName(patientData.getFirstName())
        .lastName(patientData.getLastName())
        .phoneNumber(patientData.getPhoneNumber())
        .NIP(patientData.getNIP())
        .build();
  }

  // todo setting role
  // todo builder
  public static PatientData dtoToPatientData(PatientDataDTO patientDataDTO) {
    PatientData patientData = new PatientData();
    patientData.setPesel(patientDataDTO.getPesel());
    patientData.setFirstName(patientDataDTO.getFirstName());
    patientData.setLastName(patientDataDTO.getLastName());
    patientData.setPhoneNumber(patientDataDTO.getPhoneNumber());
    patientData.setNIP(patientDataDTO.getNip());
    return patientData;
  }

  public static PatientData mapGrantPatientDataDTOtoPatientData(GrantPatientDataDTO data) {
    return PatientData.builder()
        .pesel(data.getPesel())
        .firstName(data.getFirstName())
        .lastName(data.getLastName())
        .phoneNumber(data.getPhoneNumber())
        .NIP(data.getNip())
        .build();
  }

  // CHEMIST
  public static ChemistDataDTO mapChemistDataToChemistDataDto(ChemistData chemistData) {
    return ChemistDataDTO.builder()
        .id(chemistData.getId())
        .version(chemistData.getVersion())
        .role(chemistData.getRole())
        .active(chemistData.getActive())
        .licenseNumber(chemistData.getLicenseNumber())
        .build();
  }

  public static ChemistData mapEditChemistDataDtoToChemistData(EditChemistDataDTO chemistData) {
    return ChemistData.builder()
        .licenseNumber(chemistData.getLicenseNumber())
        .build();
  }

  public static ChemistData mapEditSelfChemistDataDtoToChemistData(EditSelfChemistDataDTO chemistData) {
    return ChemistData.builder()
            .licenseNumber(chemistData.getLicenseNumber())
            .build();
  }

  public static ChemistData mapChemistDataDtoToChemistData(ChemistDataDTO chemistDataDTO) {
    return ChemistData.builder()
        .id(chemistDataDTO.getId())
        .licenseNumber(chemistDataDTO.getLicenseNumber())
        .build();
  }

  public static ChemistData mapGrantChemistDataDtoToChemistData(GrantChemistDataDTO chemistData) {
    return ChemistData.builder().licenseNumber(chemistData.getLicenseNumber()).build();
  }

  // ADMIN
  public static AdminDataDTO mapAdminDataToAdminDataDto(AdminData adminData) {
    return AdminDataDTO.builder()
        .id(adminData.getId())
        .version(adminData.getVersion())
        .role(adminData.getRole())
        .active(adminData.getActive())
        .workPhoneNumber(adminData.getWorkPhoneNumber())
        .build();
  }

  public static AdminData mapAdminDataDtoToAdminData(AdminDataDTO adminDataDTO) {
    return AdminData.builder()
        .id(adminDataDTO.getId())
        .workPhoneNumber(adminDataDTO.getWorkPhoneNumber())
        .build();
  }

  public static AdminData mapEditAdminDataDtoToAdminData(EditAdminDataDTO adminDataDTO) {
    return AdminData.builder()
        .workPhoneNumber(adminDataDTO.getWorkPhoneNumber())
        .build();
  }

  public static AdminData mapEditSelfAdminDataDtoToAdminData(EditSelfAdminDataDTO adminDataDTO) {
    return AdminData.builder()
            .workPhoneNumber(adminDataDTO.getWorkPhoneNumber())
            .build();
  }

  public static AdminData mapGrantAdminDataDtoToAdminData(GrantAdminDataDTO addAdminAccountDto) {
    return AdminData.builder().workPhoneNumber(addAdminAccountDto.getWorkPhoneNumber()).build();
  }

  public static AdminData dtoToAdminData(AdminDataDTO adminDataDTO) {
    return AdminData.builder().workPhoneNumber(adminDataDTO.getWorkPhoneNumber()).build();
  }
}
