package nl.rabobank.mongo.repository;

import nl.rabobank.mongo.model.AccountDocument;
import nl.rabobank.mongo.model.PowerOfAttorneyDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PowerOfAttorneyRepository extends MongoRepository<PowerOfAttorneyDocument, String> {

    List<PowerOfAttorneyDocument> findByGrantee(String grantee);

    Optional<PowerOfAttorneyDocument> findByGranteeAndAccount(String grantee, AccountDocument account);
}
