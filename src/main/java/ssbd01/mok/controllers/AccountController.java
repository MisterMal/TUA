package ssbd01.mok.controllers;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.java.Log;
import ssbd01.common.AbstractController;
import ssbd01.config.ETagFilterBinding;
import ssbd01.config.EntityIdentitySignerVerifier;
import ssbd01.dto.AccessLevelDTO;
import ssbd01.dto.AccountAndAccessLevelsDTO;
import ssbd01.dto.AccountDTO;
import ssbd01.dto.SelfAccountWithAccessLevelDTO;
import ssbd01.dto.addAsAdmin.AddAdminAccountDto;
import ssbd01.dto.addAsAdmin.AddChemistAccountDto;
import ssbd01.dto.addAsAdmin.AddPatientAccountDto;
import ssbd01.dto.auth.NewPasswordDTO;
import ssbd01.dto.auth.ResetPasswordDTO;
import ssbd01.dto.auth.VerificationTokenDto;
import ssbd01.dto.editAccessLevel.EditAdminDataDTO;
import ssbd01.dto.editAccessLevel.EditChemistDataDTO;
import ssbd01.dto.editAccessLevel.EditPatientDataDTO;
import ssbd01.dto.editAccount.ChangePasswordDTO;
import ssbd01.dto.editAccount.EditAccountDTO;
import ssbd01.dto.editAccount.UpdateOtherUserPasswordDTO;
import ssbd01.dto.editAccount.grant.GrantAdminDataDTO;
import ssbd01.dto.editAccount.grant.GrantChemistDataDTO;
import ssbd01.dto.editAccount.grant.GrantPatientDataDTO;
import ssbd01.dto.editSelfAccessLevel.EditSelfAdminDataDTO;
import ssbd01.dto.editSelfAccessLevel.EditSelfChemistDataDTO;
import ssbd01.dto.editSelfAccessLevel.EditSelfPatientDataDTO;
import ssbd01.dto.register.RegisterPatientDTO;
import ssbd01.entities.*;
import ssbd01.mok.managers.AccountManager;
import ssbd01.util.converters.AccessLevelConverter;
import ssbd01.util.converters.AccountConverter;

import java.util.List;

@Path("account")
@RequestScoped
@DenyAll
@Log
public class AccountController extends AbstractController {

  @Inject public AccountManager accountManager;

  @Inject public EntityIdentitySignerVerifier entityIdentitySignerVerifier;

  @GET
  @Path("/")
  @RolesAllowed("getAllAccounts")
  @Produces(MediaType.APPLICATION_JSON)
  public List<AccountDTO> readAllClients() {
    List<Account> accounts =
        repeatTransaction(accountManager, () -> accountManager.getAllAccounts());
    return accounts.stream().map(AccountConverter::mapAccountToAccountDto).toList();
  }

  @POST
  @Path("/confirm/{token}")
  @PermitAll
  public Response confirmAccount(@PathParam("token") String token) {
    repeatTransactionVoid(
            accountManager, () -> accountManager.confirmAccountRegistration(token));
    return Response.ok().build();
  }

  @POST
  @Path("/confirm-email-change")
  @RolesAllowed("confirmEmailChange")
  public Response confirmEmailChange(@Valid VerificationTokenDto token) {
    repeatTransactionVoid(
        accountManager, () -> accountManager.confirmEmailChange(token.getToken()));
    return Response.ok().build();
  }

  @GET
  @Path("/{id}")
  @RolesAllowed("getAccount")
  @Produces(MediaType.APPLICATION_JSON)
  public Response readAccount(@PathParam("id") Long id) {
    Account account = repeatTransaction(accountManager, () -> accountManager.getAccount(id));
    AccountDTO accountDto = AccountConverter.mapAccountToAccountDto(account);
    String etag = entityIdentitySignerVerifier.calculateEntitySignature(accountDto);
    return Response.ok(accountDto).tag(etag).build();
  }

  @GET
  @Path("/details")
  @RolesAllowed("getCurrentUserWithAccessLevels")
  @Produces(MediaType.APPLICATION_JSON)
  public Response readOwnAccount() {
    Account account =
        repeatTransaction(accountManager, () -> accountManager.getCurrentUserWithAccessLevels());
    SelfAccountWithAccessLevelDTO accountDto =
        AccountConverter.mapAccountToSelfAccountWithAccessLevelsDto(account);
    String etag = entityIdentitySignerVerifier.calculateEntitySignature(accountDto);
    return Response.ok(accountDto).tag(etag).build();
  }

  @GET
  @Path("/{id}/details")
  @RolesAllowed("getAccountAndAccessLevels")
  @Produces(MediaType.APPLICATION_JSON)
  public Response readAccountAndAccessLevels(@PathParam("id") Long id) {
    Account account =
        repeatTransaction(accountManager, () -> accountManager.getAccountAndAccessLevels(id));
    AccountAndAccessLevelsDTO accountDto =
        AccountConverter.mapAccountToAccountAndAccessLevelsDto(account);
    String etag = entityIdentitySignerVerifier.calculateEntitySignature(accountDto);
    return Response.ok(accountDto).tag(etag).build();
  }

  @POST
  @Path("/register")
  @PermitAll
  @Consumes(MediaType.APPLICATION_JSON)
  public Response registerPatientAccount(@NotNull @Valid RegisterPatientDTO registerPatientDto) {
    Account account = AccountConverter.mapRegisterPatientDtoToAccount(registerPatientDto);
    repeatTransaction(accountManager, () -> accountManager.registerAccount(account));
    return Response.status(Response.Status.CREATED).build();
  }

  @PUT
  @Path("/{id}/block")
  @PermitAll
  public Response blockAccount(@PathParam("id") Long id) {
    repeatTransactionVoid(accountManager, () -> accountManager.blockAccount(id));
    return Response.status(Response.Status.OK).build();
  }

  @PUT
  @Path("/{id}/unblock")
  @PermitAll
  public Response unblockAccount(@PathParam("id") Long id) {
    repeatTransactionVoid(accountManager, () -> accountManager.unblockAccount(id));
    return Response.status(Response.Status.OK).build();
  }

  @PUT
  @Path("{id}/activate")
  @PermitAll
  public AccountDTO activateAccount(@PathParam("id") Long id) {
    Account account =
        repeatTransaction(accountManager, () -> accountManager.activateUserAccount(id));
    return AccountConverter.mapAccountToAccountDto(account);
  }

  @PUT
  @Path("/{id}/change-user-password")
  @PermitAll
  @ETagFilterBinding
  public AccountDTO changeUserPassword(
      @HeaderParam("If-Match") @NotEmpty String etag,
      @PathParam("id") Long id,
      @Valid UpdateOtherUserPasswordDTO passwordDTO) {
    entityIdentitySignerVerifier.checkEtagIntegrity(passwordDTO, etag);
    Account account =
        repeatTransaction(accountManager, () -> accountManager.updateUserPassword(
                id, passwordDTO.getPassword(), passwordDTO.getLogin(), passwordDTO.getVersion()));
    return AccountConverter.mapAccountToAccountDto(account);
  }

  @PUT
  @Path("/change-password")
  @PermitAll
  @ETagFilterBinding
  public Response changePassword(
      @HeaderParam("If-Match") @NotEmpty String etag,
      @Valid ChangePasswordDTO changePasswordDTO) {
    entityIdentitySignerVerifier.checkEtagIntegrity(changePasswordDTO, etag);
    repeatTransaction(accountManager,
        () -> accountManager.updateOwnPassword(
                changePasswordDTO.getOldPassword(), changePasswordDTO.getNewPassword(),
                changePasswordDTO.getLogin(), changePasswordDTO.getVersion()));
    return Response.status(Response.Status.OK).build();
  }

  @PUT
  @Path("/")
  @PermitAll
  @ETagFilterBinding
  public AccountDTO editOwnAccount(
      @HeaderParam("If-Match") @NotEmpty String etag,
      @Valid EditAccountDTO editAccountDTO) {
    entityIdentitySignerVerifier.checkEtagIntegrity(editAccountDTO, etag);
    Account account = repeatTransaction(accountManager,
            () -> accountManager.updateOwnEmail(editAccountDTO.getEmail(),
                    editAccountDTO.getVersion()));
    return AccountConverter.mapAccountToAccountDto(account);
  }

  @PUT
  @Path("/change-language")
  @PermitAll
  public Response changeLanguage(@QueryParam("language") String language) {
    repeatTransactionVoid(accountManager,
            () -> accountManager.changeAccountLanguage(language));
    return Response.status(Response.Status.NO_CONTENT).build();
  }

  @PUT
  @Path("/{id}")
  @PermitAll
  @ETagFilterBinding
  public AccountDTO editUserAccount(
      @HeaderParam("If-Match") @NotEmpty String etag,
      @PathParam("id") Long id,
      @Valid EditAccountDTO editAccountDTO) {
    entityIdentitySignerVerifier.checkEtagIntegrity(editAccountDTO, etag);
    Account account =
        repeatTransaction(accountManager, () -> accountManager.updateUserEmail(
                id, editAccountDTO.getEmail(), editAccountDTO.getLogin(), editAccountDTO.getVersion()));
    return AccountConverter.mapAccountToAccountDto(account);
  }

  @PUT
  @Path("/reset-password")
  @PermitAll
  public Response resetPassword(@Valid ResetPasswordDTO resetPasswordDTO) {
    repeatTransactionVoid(
        accountManager, () -> accountManager.sendResetPasswordToken(resetPasswordDTO.getEmail()));
    return Response.status(Response.Status.OK).build();
  }

  @PUT
  @Path("/new-password")
  @PermitAll
  public Response setNewPassword(@Valid NewPasswordDTO newPasswordDTO) {
    repeatTransactionVoid(
        accountManager,
        () ->
            accountManager.setNewPassword(newPasswordDTO.getToken(), newPasswordDTO.getPassword()));
    return Response.status(Response.Status.OK).build();
  }

  @POST
  @Path("/add-patient")
  @PermitAll
  @Consumes(MediaType.APPLICATION_JSON)
  public Response addPatientAccountAsAdmin(
      @NotNull @Valid AddPatientAccountDto addPatientAccountDto) {
    Account account = AccountConverter.mapAddPatientDtoToAccount(addPatientAccountDto);
    repeatTransaction(accountManager, () -> accountManager.createAccount(account));
    return Response.status(Response.Status.CREATED).build();
  }

  @POST
  @Path("/add-chemist")
  @PermitAll
  @Consumes(MediaType.APPLICATION_JSON)
  public Response addChemistAccountAsAdmin(
      @NotNull @Valid AddChemistAccountDto addChemistAccountDto) {
    Account account = AccountConverter.mapChemistDtoToAccount(addChemistAccountDto);
    repeatTransaction(accountManager, () -> accountManager.createAccount(account));
    return Response.status(Response.Status.CREATED).build();
  }

  @POST
  @Path("/add-admin")
  @PermitAll
  @Consumes(MediaType.APPLICATION_JSON)
  public Response addAdminAccountAsAdmin(@NotNull @Valid AddAdminAccountDto addAdminAccountDto) {
    Account account = AccountConverter.mapAdminDtoToAccount(addAdminAccountDto);
    repeatTransaction(accountManager, () -> accountManager.createAccount(account));
    return Response.status(Response.Status.CREATED).build();
  }

  // append access level
  @POST
  @Path("/{id}/patient")
  @PermitAll
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @ETagFilterBinding
  public AccountAndAccessLevelsDTO grantPatient(
      @HeaderParam("If-Match") @NotEmpty String etag,
      @PathParam("id") Long id,
      @Valid GrantPatientDataDTO patientDataDTO) {
    entityIdentitySignerVerifier.checkEtagIntegrity(patientDataDTO, etag);
    PatientData patientData =
        AccessLevelConverter.mapGrantPatientDataDTOtoPatientData(patientDataDTO);
    Account account =
        repeatTransaction(accountManager, () -> accountManager.grantAccessLevel(
                id, patientData, patientDataDTO.getLogin(), patientDataDTO.getVersion()));
    return AccountConverter.mapAccountToAccountAndAccessLevelsDto(account);
  }

  @POST
  @Path("/{id}/chemist")
  @PermitAll
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @ETagFilterBinding
  public AccountAndAccessLevelsDTO grantChemist(
      @HeaderParam("If-Match") @NotEmpty String etag,
      @PathParam("id") Long id,
      @Valid GrantChemistDataDTO chemistDataDTO) {
    entityIdentitySignerVerifier.checkEtagIntegrity(chemistDataDTO, etag);
    ChemistData chemistData =
        AccessLevelConverter.mapGrantChemistDataDtoToChemistData(chemistDataDTO);
    Account account =
        repeatTransaction(accountManager, () -> accountManager.grantAccessLevel(
                id, chemistData, chemistDataDTO.getLogin(), chemistDataDTO.getVersion()));
    return AccountConverter.mapAccountToAccountAndAccessLevelsDto(account);
  }

  @POST
  @Path("/{id}/admin")
  @PermitAll
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @ETagFilterBinding
  public AccountAndAccessLevelsDTO grantAdmin(
      @HeaderParam("If-Match") @NotEmpty String etag,
      @PathParam("id") Long id,
      @Valid GrantAdminDataDTO adminDataDTO) {
    entityIdentitySignerVerifier.checkEtagIntegrity(adminDataDTO, etag);
    AdminData adminData = AccessLevelConverter.mapGrantAdminDataDtoToAdminData(adminDataDTO);
    Account account =
        repeatTransaction(accountManager, () -> accountManager.grantAccessLevel(
                id, adminData, adminDataDTO.getLogin(), adminDataDTO.getVersion()));
    return AccountConverter.mapAccountToAccountAndAccessLevelsDto(account);
  }

  // read access level
  @GET
  @Path("/{id}/patient")
  @PermitAll
  @Produces(MediaType.APPLICATION_JSON)
  public Response getPatientData(@PathParam("id") Long id) {
    AccessLevel accessLevel = repeatTransaction(
            accountManager, () -> accountManager.getAccessLevel(id, Role.PATIENT));
    AccessLevelDTO accessLevelDTO = AccessLevelConverter.mapAccessLevelToAccessLevelDto(accessLevel);
    String etag = entityIdentitySignerVerifier.calculateEntitySignature(accessLevelDTO);
    return Response.status(Response.Status.OK).entity(accessLevelDTO).tag(etag).build();
  }

  @GET
  @Path("/{id}/chemist")
  @PermitAll
  @Produces(MediaType.APPLICATION_JSON)
  public Response getChemistData(@PathParam("id") Long id) {
    AccessLevel accessLevel = repeatTransaction(
            accountManager, () -> accountManager.getAccessLevel(id, Role.CHEMIST));
    AccessLevelDTO accessLevelDTO = AccessLevelConverter.mapAccessLevelToAccessLevelDto(accessLevel);
    String etag = entityIdentitySignerVerifier.calculateEntitySignature(accessLevelDTO);
    return Response.status(Response.Status.OK).entity(accessLevelDTO).tag(etag).build();
  }

  @GET
  @Path("/{id}/admin")
  @PermitAll
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAdminData(@PathParam("id") Long id) {
    AccessLevel accessLevel = repeatTransaction(
            accountManager, () -> accountManager.getAccessLevel(id, Role.ADMIN));
    AccessLevelDTO accessLevelDTO = AccessLevelConverter.mapAccessLevelToAccessLevelDto(accessLevel);
    String etag = entityIdentitySignerVerifier.calculateEntitySignature(accessLevelDTO);
    return Response.status(Response.Status.OK).entity(accessLevelDTO).tag(etag).build();
  }

  // edit access level
  @PUT
  @Path("/{id}/patient")
  @PermitAll
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @ETagFilterBinding
  public AccountAndAccessLevelsDTO editPatientData(
          @HeaderParam("If-Match") @NotEmpty String etag,
          @PathParam("id") Long id,
          @Valid EditPatientDataDTO patientDataDTO) {
    entityIdentitySignerVerifier.checkEtagIntegrity(patientDataDTO, etag);
    PatientData patientData =
            AccessLevelConverter.mapEditPatientDataDtoToPatientData(patientDataDTO);
    Account account =
            repeatTransaction(
                    accountManager,
                    () -> accountManager.editAccessLevel(id, patientData, patientDataDTO.getVersion()));
    return AccountConverter.mapAccountToAccountAndAccessLevelsDto(account);
  }

  @PUT
  @Path("/{id}/chemist")
  @PermitAll
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @ETagFilterBinding
  public AccountAndAccessLevelsDTO editChemistData(
          @HeaderParam("If-Match") @NotEmpty String etag,
          @PathParam("id") Long id,
          @Valid EditChemistDataDTO chemistDataDTO) {
    entityIdentitySignerVerifier.checkEtagIntegrity(chemistDataDTO, etag);
    ChemistData chemistData =
            AccessLevelConverter.mapEditChemistDataDtoToChemistData(chemistDataDTO);
    Account account =
            repeatTransaction(
                    accountManager,
                    () -> accountManager.editAccessLevel(id, chemistData, chemistDataDTO.getVersion()));
    return AccountConverter.mapAccountToAccountAndAccessLevelsDto(account);
  }

  @PUT
  @Path("/{id}/admin")
  @PermitAll
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @ETagFilterBinding
  public AccountAndAccessLevelsDTO editAdminData(
          @HeaderParam("If-Match") @NotEmpty String etag,
          @PathParam("id") Long id,
          @Valid EditAdminDataDTO adminDataDTO) {
    entityIdentitySignerVerifier.checkEtagIntegrity(adminDataDTO, etag);
    AdminData adminData = AccessLevelConverter.mapEditAdminDataDtoToAdminData(adminDataDTO);
    Account account =
            repeatTransaction(
                    accountManager,
                    () -> accountManager.editAccessLevel(id, adminData, adminDataDTO.getVersion()));
    return AccountConverter.mapAccountToAccountAndAccessLevelsDto(account);
  }

  @PUT
  @Path("/patient")
  @PermitAll
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @ETagFilterBinding
  public AccountAndAccessLevelsDTO editPatientDataSelf(
          @HeaderParam("If-Match") @NotEmpty String etag,
          @Valid EditSelfPatientDataDTO patientDataDTO) {
    entityIdentitySignerVerifier.checkEtagIntegrity(patientDataDTO, etag);
    PatientData patientData =
            AccessLevelConverter.mapEditSelfPatientDataDtoToPatientData(patientDataDTO);
    Account account =
            repeatTransaction(
                    accountManager,
                    () -> accountManager.editSelfAccessLevel(patientData, patientDataDTO.getVersion()));
    return AccountConverter.mapAccountToAccountAndAccessLevelsDto(account);
  }

  @PUT
  @Path("/chemist")
  @PermitAll
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @ETagFilterBinding
  public AccountAndAccessLevelsDTO editChemistDataSelf(
          @HeaderParam("If-Match") @NotEmpty String etag,
          @Valid EditSelfChemistDataDTO chemistDataDTO) {
    entityIdentitySignerVerifier.checkEtagIntegrity(chemistDataDTO, etag);
    ChemistData chemistData =
            AccessLevelConverter.mapEditSelfChemistDataDtoToChemistData(chemistDataDTO);
    Account account =
            repeatTransaction(
                    accountManager,
                    () -> accountManager.editSelfAccessLevel(chemistData, chemistDataDTO.getVersion()));
    return AccountConverter.mapAccountToAccountAndAccessLevelsDto(account);
  }

  @PUT
  @Path("/admin")
  @PermitAll
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @ETagFilterBinding
  public AccountAndAccessLevelsDTO editAdminDataSelf(
          @HeaderParam("If-Match") @NotEmpty String etag,
          @Valid EditSelfAdminDataDTO adminDataDTO) {
    entityIdentitySignerVerifier.checkEtagIntegrity(adminDataDTO, etag);
    AdminData adminData = AccessLevelConverter.mapEditSelfAdminDataDtoToAdminData(adminDataDTO);
    Account account =
            repeatTransaction(
                    accountManager,
                    () -> accountManager.editSelfAccessLevel(adminData, adminDataDTO.getVersion()));
    return AccountConverter.mapAccountToAccountAndAccessLevelsDto(account);
  }



  @PUT
  @Path("/{id}/admin/block")
  @PermitAll
  public Response blockRoleAdmin(@PathParam("id") Long id) {
    repeatTransactionVoid(
        accountManager, () -> accountManager.deactivateAccessLevel(id, Role.ADMIN));
    return Response.status(Response.Status.NO_CONTENT).build();
  }

  @PUT
  @Path("/{id}/chemist/block")
  @PermitAll
  public Response blockRoleChemist(@PathParam("id") Long id) {
    repeatTransactionVoid(
        accountManager, () -> accountManager.deactivateAccessLevel(id, Role.CHEMIST));
    return Response.status(Response.Status.NO_CONTENT).build();
  }

  @PUT
  @Path("/{id}/patient/block")
  @PermitAll
  public Response blockRolePatient(@PathParam("id") Long id) {
    repeatTransactionVoid(
        accountManager, () -> accountManager.deactivateAccessLevel(id, Role.PATIENT));
    return Response.status(Response.Status.NO_CONTENT).build();
  }

  @PUT
  @Path("/{id}/admin/unblock")
  @PermitAll
  public Response unblockRoleAdmin(@PathParam("id") Long id) {
    repeatTransactionVoid(accountManager, () -> accountManager.activateAccessLevel(id, Role.ADMIN));
    return Response.status(Response.Status.NO_CONTENT).build();
  }

  @PUT
  @Path("/{id}/chemist/unblock")
  @PermitAll
  public Response unblockRoleChemist(@PathParam("id") Long id) {
    repeatTransactionVoid(
        accountManager, () -> accountManager.activateAccessLevel(id, Role.CHEMIST));
    return Response.status(Response.Status.NO_CONTENT).build();
  }

  @PUT
  @Path("/{id}/patient/unblock")
  @PermitAll
  public Response unblockRolePatient(@PathParam("id") Long id) {
    repeatTransactionVoid(
        accountManager, () -> accountManager.activateAccessLevel(id, Role.PATIENT));
    return Response.status(Response.Status.NO_CONTENT).build();
  }
}
