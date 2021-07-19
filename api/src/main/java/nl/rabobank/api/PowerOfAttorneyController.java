package nl.rabobank.api;

import lombok.RequiredArgsConstructor;
import nl.rabobank.generated.api.PowerOfAttorneyApi;
import nl.rabobank.generated.model.*;
import nl.rabobank.service.PowerOfAttorneyService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import static java.util.stream.Collectors.toList;

@Controller
@RequiredArgsConstructor
public class PowerOfAttorneyController implements PowerOfAttorneyApi {
    private final PowerOfAttorneyService powerOfAttorneyService;
    private final PowerOfAttorneyConverter powerOfAttorneyConverter;

    @Override
    public ResponseEntity<PowerOfAttorneyType> createByGrantor(String xUserId, CreationRequest request) {
        return ResponseEntity.ok(
            powerOfAttorneyConverter.convert(
                powerOfAttorneyService.create(xUserId, request.getGrantee(), request.getAccountNumber(),
                                                request.getAccountType() == AccountType.PAYMENT,
                                                request.getAuthorizationType() == AuthorizationType.WRITE)));
    }

    @Override
    public ResponseEntity<ListResponse> listForGrantee(String xUserId) {
        return ResponseEntity.ok(
            new ListResponse().powerOfAttorneys(
                powerOfAttorneyService.list(xUserId).stream().map(powerOfAttorneyConverter::convert).collect(toList())));
    }
}

