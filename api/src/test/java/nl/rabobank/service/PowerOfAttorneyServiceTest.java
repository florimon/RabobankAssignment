package nl.rabobank.service;

import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.mongo.model.AccountDocument;
import nl.rabobank.mongo.model.AccountDocument.AccountType;
import nl.rabobank.mongo.model.PowerOfAttorneyDocument;
import nl.rabobank.mongo.model.PowerOfAttorneyDocument.AuthorizationType;
import nl.rabobank.mongo.repository.AccountRepository;
import nl.rabobank.mongo.repository.PowerOfAttorneyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PowerOfAttorneyServiceTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private PowerOfAttorneyRepository powerOfAttorneyRepository;
    @Mock
    private PowerOfAttorneyDocumentConverter powerOfAttorneyDocumentConverter;

    @InjectMocks
    private PowerOfAttorneyService sut;

    private static final boolean IS_PAYMENT = true;
    private static final boolean IS_WRITE = true;
    private final String accountNumber = "accountNumber";
    private final String accountHolder = "testuser";
    private final String grantor = accountHolder;
    private final String grantee = "grantee";

    // The actual fields of the finally returned PowerOfAttorney don't matter. The first two unittests are constructed
    // in such a way that all the mocked dependencies have to be called with the right arguments in order for
    // PowerOfAttorneyService#create(...) to produce this instance (no any()'s are used).
    private PowerOfAttorney powerOfAttorney = PowerOfAttorney.builder().build();

    @Test
    void createShouldReturnNewPowerOfAttorneyIfAccountExistsAndPowerOfAttorneyNotAlreadyExists() {
        AccountDocument accountDocument = mock(AccountDocument.class);
        when(accountRepository.findByNumberAndHolderAndAccountType(accountNumber, grantor, AccountType.PAYMENT))
                .thenReturn(Optional.of(accountDocument));
        when(powerOfAttorneyRepository.findByGranteeAndAccount(grantee, accountDocument))
                .thenReturn(Optional.empty());
        ArgumentMatcher<PowerOfAttorneyDocument> matchesPowerOfAttorneyDocument = getPowerOfAttorneyDocumentMatcher(
                accountDocument, grantee, AuthorizationType.WRITE);
        when(powerOfAttorneyRepository.save(argThat(matchesPowerOfAttorneyDocument))).thenAnswer(invocation ->
                invocation.getArgument(0));
        when(powerOfAttorneyDocumentConverter.convert(argThat(matchesPowerOfAttorneyDocument))).thenReturn(powerOfAttorney);

        PowerOfAttorney result = sut.create(grantor, grantee, accountNumber, IS_PAYMENT, IS_WRITE);

        assertEquals(powerOfAttorney, result);
    }

    private ArgumentMatcher<PowerOfAttorneyDocument> getPowerOfAttorneyDocumentMatcher(AccountDocument accountDocument,
                                                                                       String grantee,
                                                                                       AuthorizationType authorizationType) {
        return document ->  document.getAccount().equals(accountDocument) &&
                            document.getGrantee().equals(grantee) &&
                            document.getAuthorizationType() == authorizationType;
    }

    @Test
    void createShouldReturnUpdatedPowerOfAttorneyIfAccountExistsAndPowerOfAttorneyAlreadyExists() {
        AccountDocument accountDocument = mock(AccountDocument.class);
        PowerOfAttorneyDocument powerOfAttorneyDocument = mock(PowerOfAttorneyDocument.class);
        when(accountRepository.findByNumberAndHolderAndAccountType(accountNumber, grantor, AccountType.PAYMENT))
                .thenReturn(Optional.of(accountDocument));
        when(powerOfAttorneyRepository.findByGranteeAndAccount(grantee, accountDocument))
                .thenReturn(Optional.of(powerOfAttorneyDocument));
        when(powerOfAttorneyDocument.withAuthorization(AuthorizationType.WRITE)).thenReturn(powerOfAttorneyDocument);
        when(powerOfAttorneyRepository.save(powerOfAttorneyDocument)).thenReturn(powerOfAttorneyDocument);
        when(powerOfAttorneyDocumentConverter.convert(powerOfAttorneyDocument)).thenReturn(powerOfAttorney);

        PowerOfAttorney result = sut.create(grantor, grantee, accountNumber, IS_PAYMENT, IS_WRITE);

        assertEquals(powerOfAttorney, result);
    }

    @Test
    void createShouldThrowExceptionIfAccountNotExists() {
        when(accountRepository.findByNumberAndHolderAndAccountType(accountNumber, grantor, AccountType.PAYMENT))
                .thenReturn(Optional.empty());

        InvalidAccountException exception = assertThrows(InvalidAccountException.class, () ->
            sut.create(grantor, grantee, accountNumber, IS_PAYMENT, IS_WRITE)
        );
        assertEquals(accountNumber, exception.getAccountNumber());
        assertEquals(accountHolder, exception.getAccountHolder());
        assertEquals(IS_PAYMENT, exception.isPayment());
        assertEquals("No Payment account with number 'accountNumber' for holder 'testuser'", exception.getMessage());
    }

    @Test
    void listShouldReturnExistingPowerOfAttorneys() {
        PowerOfAttorneyDocument powerOfAttorneyDocument1 = mock(PowerOfAttorneyDocument.class);
        PowerOfAttorneyDocument powerOfAttorneyDocument2 = mock(PowerOfAttorneyDocument.class);
        when(powerOfAttorneyRepository.findByGrantee(grantee)).thenReturn(asList(powerOfAttorneyDocument1, powerOfAttorneyDocument2));
        PowerOfAttorney powerOfAttorney1 = PowerOfAttorney.builder().build();
        PowerOfAttorney powerOfAttorney2 = PowerOfAttorney.builder().build();
        when(powerOfAttorneyDocumentConverter.convert(isA(PowerOfAttorneyDocument.class))).thenReturn(powerOfAttorney1, powerOfAttorney2);

        List<PowerOfAttorney> result = sut.list(grantee);

        assertEquals(asList(powerOfAttorney1, powerOfAttorney2), result);
    }
}
