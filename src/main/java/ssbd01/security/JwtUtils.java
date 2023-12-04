package ssbd01.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import lombok.extern.java.Log;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.crypto.SecretKey;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Log
@ApplicationScoped
public class JwtUtils {


  public int TIMEOUT = 15;

  public String SECRET = "6P2A9EsnwbdPyd8Ine7C5EcskiTIHOYtB30NnZyEKW2Y3kWearZv3SEs2N91z55q";

  public String create(String login, Set set) {
    String principal = login;
    Set<String> authorities = set;

    return Jwts.builder()
        .setSubject(principal)
        .claim("roles", String.join(",", authorities))
        .setExpiration(
            new Date(ZonedDateTime.now().plusMinutes(TIMEOUT).toInstant().toEpochMilli()))
        .signWith(getSecretKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public SecretKey getSecretKey() {
    byte[] encodeKey = Base64.getDecoder().decode(SECRET);
    return Keys.hmacShaKeyFor(encodeKey);
  }

  public Claims load(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSecretKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }
}
