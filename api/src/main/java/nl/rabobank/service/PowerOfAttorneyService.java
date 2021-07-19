package nl.rabobank.service;

import lombok.RequiredArgsConstructor;
import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.mongo.model.AccountDocument;
import nl.rabobank.mongo.model.AccountDocument.AccountType;
import nl.rabobank.mongo.model.PowerOfAttorneyDocument;
import nl.rabobank.mongo.model.PowerOfAttorneyDocument.AuthorizationType;
import nl.rabobank.mongo.repository.AccountRepository;
import nl.rabobank.mongo.repository.PowerOfAttorneyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class PowerOfAttorneyService {
    private final AccountRepository accountRepository;
    private final PowerOfAttorneyRepository powerOfAttorneyRepository;
    private final PowerOfAttorneyDocumentConverter powerOfAttorneyDocumentConverter;

    /**
     * Creates and saves a new {@link PowerOfAttorney}, or updates an existing one.
     *
     * @param grantor       id of the person that grants some control over their account, to another person
     * @param grantee       id the person that receives some control over another person's account
     * @param accountNumber account-number of the account over which some control is granted
     * @param isPayment     <code>true</code> if the account corresponding to accountNumber is a payment account;
     *                      <code>false</code> if the account is a savings account
     * @param isWrite       <code>true</code> if the type of control that is granted is WRITE;
     *                      <code>false</code> if the type of control that is granted is READ.
     * @return  the created or updated {@link PowerOfAttorney}
     * @throws InvalidAccountException if the combination of grantor, accountNumber and isPayment together do not
     *                                  correspond to an existing account
     */
    public PowerOfAttorney create(String grantor, String grantee, String accountNumber, boolean isPayment, boolean isWrite) {
        return findAccountDocument(accountNumber, grantor, AccountType.of(isPayment))
                .map(accountDocument -> findOrCreatePowerOfAttorneyDocument(grantee, accountDocument))
                .map(powerOfAttorneyDocument -> powerOfAttorneyDocument.withAuthorization(AuthorizationType.of(isWrite)))
                .map(powerOfAttorneyRepository::save)
                .map(powerOfAttorneyDocumentConverter::convert)
                .orElseThrow(() -> new InvalidAccountException(accountNumber, grantor, isPayment));
    }

    private Optional<AccountDocument> findAccountDocument(String accountNumber, String grantor, AccountType accountType) {
        return accountRepository.findByNumberAndHolderAndAccountType(accountNumber, grantor, accountType);
    }

    private PowerOfAttorneyDocument findOrCreatePowerOfAttorneyDocument(String grantee, AccountDocument accountDocument) {
        return powerOfAttorneyRepository.findByGranteeAndAccount(grantee, accountDocument)
                .orElseGet(() -> createPowerOfAttorneyDocument(grantee, accountDocument));
    }

    private PowerOfAttorneyDocument createPowerOfAttorneyDocument(String grantee, AccountDocument accountDocument) {
        return PowerOfAttorneyDocument.builder().account(accountDocument).grantee(grantee).build();
    }

    /**
     * Given a grantee, returns all of the {@link PowerOfAttorney}s that have been granted to that person.
     * @param grantee   the id of a person to return the PowerOfAttorneys for
     * @return          a List of PowerOfAttorneys, or an empty List if none exist
     */
    public List<PowerOfAttorney> list(String grantee) {
        return powerOfAttorneyRepository.findByGrantee(grantee)
                .stream()
                .map(powerOfAttorneyDocumentConverter::convert)
                .collect(toList());
    }
}
