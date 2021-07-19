package nl.rabobank.mongo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class PowerOfAttorneyDocument {

    @Id
    private String id;

    @DBRef
    private AccountDocument account;
    private AuthorizationType authorizationType;
    private String grantee;
    // grantor is implied by account.holder


    public PowerOfAttorneyDocument withAuthorization(AuthorizationType type) {
        setAuthorizationType(type);
        return this;
    }

    public enum AuthorizationType {
        READ, WRITE;

        public static AuthorizationType of(boolean isWrite) {
            return isWrite ? WRITE : READ;
        }
    }
}
