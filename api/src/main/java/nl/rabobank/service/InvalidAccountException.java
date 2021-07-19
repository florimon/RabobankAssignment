package nl.rabobank.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static java.lang.String.format;

@Getter
@RequiredArgsConstructor
public class InvalidAccountException extends RuntimeException {
    private final String accountNumber;
    private final String accountHolder;
    private final boolean isPayment;

    @Override
    public String getMessage() {
        return format("No %s account with number '%s' for holder '%s'",
                        isPayment ? "Payment" : "Savings", accountNumber, accountHolder);
    }
}
