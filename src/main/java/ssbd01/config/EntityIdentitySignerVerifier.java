package ssbd01.config;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import lombok.extern.java.Log;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import ssbd01.common.SignableEntity;
import ssbd01.exceptions.ApplicationException;

import java.text.ParseException;


@Log
@Dependent
public class EntityIdentitySignerVerifier {

  private String ETAG_SECRET = "7d03a8e6t2f59s4a833a1f4m9d0c592f3bfb";

  public void checkEtagIntegrity(SignableEntity entity, String etag) {
    if (!verifyEntityIntegrity(entity, etag)) {
      throw ApplicationException.createEtagNotValidException();
    }
  }

  public String calculateEntitySignature(SignableEntity entity) {
    try {
      JWSSigner signer = new MACSigner(ETAG_SECRET);

      JWSObject jwsObject =
          new JWSObject(
              new JWSHeader(JWSAlgorithm.HS256), new Payload(entity.getSignablePayload()));
      jwsObject.sign(signer);
      return jwsObject.serialize();
    } catch (JOSEException e) {
      throw ApplicationException.createEtagCreationException();
    }
  }

  public boolean validateEntitySignature(String tag) {
    try {
      JWSObject jwsObject = JWSObject.parse(tag);
      JWSVerifier verifier = new MACVerifier(ETAG_SECRET);
      return jwsObject.verify(verifier);
    } catch (ParseException | JOSEException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean verifyEntityIntegrity(SignableEntity entity, String tag) {
    try {
      final String header = JWSObject.parse(tag).getPayload().toString();
      final String signablePayload = entity.getSignablePayload();
      return validateEntitySignature(tag) && signablePayload.equals(header);
    } catch (ParseException e) {
      e.printStackTrace();
      return false;
    }
  }
}
