package nl.rabobank.api;

import com.google.common.annotations.VisibleForTesting;
import nl.rabobank.account.Account;
import nl.rabobank.account.PaymentAccount;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.generated.model.AccountType;
import nl.rabobank.generated.model.AuthorizationType;
import nl.rabobank.generated.model.PowerOfAttorneyType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Converts from the domain model {@link PowerOfAttorney} to the generated API model {@link PowerOfAttorneyType}.
 */
@Component
public class PowerOfAttorneyConverter implements Converter<PowerOfAttorney, PowerOfAttorneyType> {

    @Override
    public PowerOfAttorneyType convert(PowerOfAttorney powerOfAttorney) {
        return new PowerOfAttorneyType()
                .grantee(powerOfAttorney.getGranteeName())
                .accountType(getAccountType(powerOfAttorney.getAccount()))
                .accountHolder(powerOfAttorney.getAccount().getAccountHolderName())
                .accountNumber(powerOfAttorney.getAccount().getAccountNumber())
                .authorizationType(getAuthorizationType(powerOfAttorney.getAuthorization()));
    }

    @VisibleForTesting AccountType getAccountType(Account account) {
        return account instanceof PaymentAccount ? AccountType.PAYMENT : AccountType.SAVINGS;
    }

    @VisibleForTesting AuthorizationType getAuthorizationType(Authorization authorization) {
        return authorization == Authorization.READ ? AuthorizationType.READ : AuthorizationType.WRITE;
    }
}
