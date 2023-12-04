//package ssbd01.mok.managers;
//
//import jakarta.ejb.Local;
//import ssbd01.common.CommonManagerLocalInterface;
//import ssbd01.entities.Account;
//import ssbd01.entities.Token;
//import ssbd01.entities.TokenType;
//
//import java.util.Date;
//import java.util.List;
//
//@Local
//public interface TokenManagerLocal extends CommonManagerLocalInterface {
//
//  void sendVerificationToken(Account account, String code);
//
//  void sendResetPasswordToken(Account account);
//
//  void verifyAccount(String token);
//
//  void sendEmailChangeEmail(Account account, String new_email);
//
//  void confirmEmailChange(String token);
//
//  void setNewPassword(String token, String newPassword);
//
//  List<Token> findTokensByTokenTypeAndExpirationDateBefore(
//          TokenType verification, Date halfExpirationDate);
//}
