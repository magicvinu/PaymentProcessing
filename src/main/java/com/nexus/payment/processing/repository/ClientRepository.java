package com.nexus.payment.processing.repository;

import com.nexus.payment.processing.model.Client;
import java.util.Optional;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

  /**
   * PESSIMISTIC_WRITE – allows us to obtain an exclusive lock and prevent the data from being read,
   * updated or deleted. When processPayment endpoint is called from multiple server concurrently
   * for a given client, PESSIMISTIC_WRITE lock ensure only one server can get lock on the client
   * record. This will help us to avoid same unpaid order processed multiple times.
   *
   * @param clientId
   * @return
   */
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<Client> findById(Long clientId);
}
