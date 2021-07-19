package nl.rabobank.api;

import nl.rabobank.account.PaymentAccount;
import nl.rabobank.account.SavingsAccount;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.generated.model.AccountType;
import nl.rabobank.generated.model.AuthorizationType;
import nl.rabobank.generated.model.PowerOfAttorneyType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PowerOfAttorneyConverterTest {

    private final PowerOfAttorneyConverter sut = new PowerOfAttorneyConverter();

    @Test
    void testConvert() {
        PowerOfAttorney powerOfAttorney = PowerOfAttorney.builder()
                .grantorName("holder")
                .granteeName("grantee")
                .authorization(Authorization.READ)
                .account(new PaymentAccount("number", "holder", 123.45))
                .build();

        PowerOfAttorneyType result = sut.convert(powerOfAttorney);

        assertEquals("holder", result.getAccountHolder());
        assertEquals("grantee", result.getGrantee());
        assertEquals(AuthorizationType.READ, result.getAuthorizationType());
        assertEquals("number", result.getAccountNumber());
        assertEquals(AccountType.PAYMENT, result.getAccountType());
    }

    @Test
    void testGetAccountType() {
        assertEquals(AccountType.PAYMENT, sut.getAccountType(new PaymentAccount(null,null,null)));
        assertEquals(AccountType.SAVINGS, sut.getAccountType(new SavingsAccount(null,null,null)));
    }

    @Test
    void testAuthorizationType() {
        assertEquals(AuthorizationType.READ, sut.getAuthorizationType(Authorization.READ));
        assertEquals(AuthorizationType.WRITE, sut.getAuthorizationType(Authorization.WRITE));
    }
}
