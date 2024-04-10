package io.github.axelfrache.productmanager.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Command {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Client client;

    private Double totalPrice;

    @OneToMany(mappedBy = "command", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommandProduct> commandProducts;

    public Double getTotalPrice() {
        if (commandProducts != null) {
            return commandProducts.stream()
                    .mapToDouble(cp -> cp.getProduct().getPrice() * cp.getQuantity())
                    .sum();
        }
        return 0.0;
    }
}