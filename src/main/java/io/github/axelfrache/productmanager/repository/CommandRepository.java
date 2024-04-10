package io.github.axelfrache.productmanager.repository;

import io.github.axelfrache.productmanager.model.Command;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommandRepository extends JpaRepository<Command, Long>{

    @Query("SELECT c FROM Command c JOIN c.commandProducts cp WHERE cp.product.id = :productId")
    List<Command> findAllWithProduct(@Param("productId") Long productId);

}
