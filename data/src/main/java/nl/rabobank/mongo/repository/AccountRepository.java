package nl.rabobank.mongo.repository;

import nl.rabobank.mongo.model.AccountDocument;
import nl.rabobank.mongo.model.AccountDocument.AccountType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends MongoRepository<AccountDocument, String> {

    Optional<AccountDocument> findByNumberAndHolderAndAccountType(String number, String holder, AccountType accountType);

}
