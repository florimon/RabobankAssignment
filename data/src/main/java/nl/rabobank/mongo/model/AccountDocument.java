package nl.rabobank.mongo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class AccountDocument {

    @Id
    private String id;
    private String number;
    private String holder;
    private Double balance;
    private AccountType accountType;

    public boolean isPaymentAccount() {
        return accountType == AccountType.PAYMENT;
    }

    public enum AccountType {
        PAYMENT, SAVINGS;

        public static AccountType of(boolean isPayment) {
            return isPayment ? PAYMENT : SAVINGS;
        }
    }
}
