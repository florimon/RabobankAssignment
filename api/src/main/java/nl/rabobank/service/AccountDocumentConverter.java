package nl.rabobank.service;

import nl.rabobank.account.Account;
import nl.rabobank.account.PaymentAccount;
import nl.rabobank.account.SavingsAccount;
import nl.rabobank.mongo.model.AccountDocument;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Converts from data model {@link AccountDocument} to domain model {@link Account}.
 */
@Component
public class AccountDocumentConverter implements Converter<AccountDocument, Account> {

    @Override
    public Account convert(AccountDocument document) {
        if (document.isPaymentAccount()) {
            return new PaymentAccount(document.getNumber(), document.getHolder(), document.getBalance());
        } else {
            return new SavingsAccount(document.getNumber(), document.getHolder(), document.getBalance());
        }
    }
}
