package nl.rabobank.api;

import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.generated.model.*;
import nl.rabobank.service.PowerOfAttorneyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PowerOfAttorneyControllerTest {

    @Mock
    private PowerOfAttorneyService powerOfAttorneyService;
    @Mock
    private PowerOfAttorneyConverter powerOfAttorneyConverter;

    @InjectMocks
    private PowerOfAttorneyController sut;

    @Test
    void testCreateByGrantor() {
        String accountNumber = "account#";
        String grantee = "grantee";
        String grantor = "grantor";
        boolean IS_PAYMENT = true;
        boolean IS_WRITE = true;
        CreationRequest creationRequest = new CreationRequest()
                .accountNumber(accountNumber)
                .grantee(grantee)
                .accountType(AccountType.PAYMENT)
                .authorizationType(AuthorizationType.WRITE);

        PowerOfAttorney powerOfAttorney = PowerOfAttorney.builder().build();
        when(powerOfAttorneyService.create(grantor, grantee, accountNumber, IS_PAYMENT, IS_WRITE)).thenReturn(powerOfAttorney);
        PowerOfAttorneyType powerOfAttorneyType = mock(PowerOfAttorneyType.class);
        when(powerOfAttorneyConverter.convert(powerOfAttorney)).thenReturn(powerOfAttorneyType);

        ResponseEntity<PowerOfAttorneyType> responseEntity = sut.createByGrantor(grantor, creationRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(powerOfAttorneyType, responseEntity.getBody());
    }

    @Test
    void testListForGrantee() {
        PowerOfAttorney powerOfAttorney1 = PowerOfAttorney.builder().build();
        PowerOfAttorney powerOfAttorney2 = PowerOfAttorney.builder().build();
        when(powerOfAttorneyService.list("grantee")).thenReturn(asList(powerOfAttorney1, powerOfAttorney2));
        PowerOfAttorneyType powerOfAttorneyType1 = mock(PowerOfAttorneyType.class);
        PowerOfAttorneyType powerOfAttorneyType2 = mock(PowerOfAttorneyType.class);
        when(powerOfAttorneyConverter.convert(isA(PowerOfAttorney.class))).thenReturn(powerOfAttorneyType1, powerOfAttorneyType2);

        ResponseEntity<ListResponse> responseEntity = sut.listForGrantee("grantee");

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(asList(powerOfAttorneyType1, powerOfAttorneyType2), responseEntity.getBody().getPowerOfAttorneys());
    }
}
