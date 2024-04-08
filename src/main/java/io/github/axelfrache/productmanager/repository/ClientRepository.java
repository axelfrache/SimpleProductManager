package io.github.axelfrache.productmanager.repository;

import io.github.axelfrache.productmanager.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long>{
}