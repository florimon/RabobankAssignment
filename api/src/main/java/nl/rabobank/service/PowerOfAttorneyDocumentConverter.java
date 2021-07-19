package nl.rabobank.service;

import lombok.RequiredArgsConstructor;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.mongo.model.PowerOfAttorneyDocument;
import nl.rabobank.mongo.model.PowerOfAttorneyDocument.AuthorizationType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Converts from data model {@link PowerOfAttorneyDocument} to domain model {@link PowerOfAttorney}.
 */
@Component
@RequiredArgsConstructor
public class PowerOfAttorneyDocumentConverter implements Converter<PowerOfAttorneyDocument, PowerOfAttorney> {
    private final AccountDocumentConverter accountDocumentConverter;

    @Override
    public PowerOfAttorney convert(PowerOfAttorneyDocument document) {
        return PowerOfAttorney.builder()
                .granteeName(document.getGrantee())
                .grantorName(document.getAccount().getHolder())
                .account(accountDocumentConverter.convert(document.getAccount()))
                .authorization(convert(document.getAuthorizationType()))
                .build();
    }

    private Authorization convert(AuthorizationType authorizationType) {
        return authorizationType == AuthorizationType.READ ? Authorization.READ : Authorization.WRITE;
    }
}
