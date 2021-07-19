package nl.rabobank.service;

import nl.rabobank.account.Account;
import nl.rabobank.account.PaymentAccount;
import nl.rabobank.account.SavingsAccount;
import nl.rabobank.mongo.model.AccountDocument;
import nl.rabobank.mongo.model.AccountDocument.AccountType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccountDocumentConverterTest {

    private AccountDocumentConverter sut = new AccountDocumentConverter();

    @Test
    void testConvertToPaymentAccount() {
        AccountDocument accountDocument = getAccountDocument(AccountType.PAYMENT);

        Account account = sut.convert(accountDocument);

        assertTrue(account instanceof PaymentAccount);
        assertEquals(123.45, account.getBalance());
        assertEquals("holder", account.getAccountHolderName());
        assertEquals("number", account.getAccountNumber());
    }

    @Test
    void testConvertToSavingsAccount() {
        AccountDocument accountDocument = getAccountDocument(AccountType.SAVINGS);

        Account account = sut.convert(accountDocument);

        assertTrue(account instanceof SavingsAccount);
        assertEquals(123.45, account.getBalance());
        assertEquals("holder", account.getAccountHolderName());
        assertEquals("number", account.getAccountNumber());
    }

    private AccountDocument getAccountDocument(AccountType accountType) {
        return AccountDocument.builder()
                .accountType(accountType)
                .balance(123.45)
                .holder("holder")
                .number("number")
                .build();
    }
}
