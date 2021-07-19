package nl.rabobank.service;

import nl.rabobank.account.Account;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.mongo.model.AccountDocument;
import nl.rabobank.mongo.model.PowerOfAttorneyDocument;
import nl.rabobank.mongo.model.PowerOfAttorneyDocument.AuthorizationType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PowerOfAttorneyDocumentConverterTest {

    @Mock
    private AccountDocumentConverter accountDocumentConverter;

    @InjectMocks
    private PowerOfAttorneyDocumentConverter sut;

    @Mock
    private PowerOfAttorneyDocument powerOfAttorneyDocument;
    @Mock
    private AccountDocument accountDocument;
    @Mock
    private Account account;

    private final String grantee = "grantee";
    private final String grantor = "grantor";

    @Test
    void testConvert() {
        when(powerOfAttorneyDocument.getGrantee()).thenReturn(grantee);
        when(powerOfAttorneyDocument.getAuthorizationType()).thenReturn(AuthorizationType.WRITE);
        when(powerOfAttorneyDocument.getAccount()).thenReturn(accountDocument);
        when(accountDocument.getHolder()).thenReturn(grantor);
        when(accountDocumentConverter.convert(accountDocument)).thenReturn(account);

        PowerOfAttorney powerOfAttorney = sut.convert(powerOfAttorneyDocument);

        assertEquals(grantee, powerOfAttorney.getGranteeName());
        assertEquals(grantor, powerOfAttorney.getGrantorName());
        assertEquals(account, powerOfAttorney.getAccount());
        assertEquals(Authorization.WRITE, powerOfAttorney.getAuthorization());
    }
}
