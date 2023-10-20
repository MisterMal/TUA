package ssbd01.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EtagVerification {
  private Map<String, EtagVersion> etagVersionList;
}
