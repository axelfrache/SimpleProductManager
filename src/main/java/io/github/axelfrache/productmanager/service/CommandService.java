package io.github.axelfrache.productmanager.service;

import io.github.axelfrache.productmanager.model.Command;
import io.github.axelfrache.productmanager.model.CommandProduct;
import io.github.axelfrache.productmanager.repository.CommandRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class CommandService {

    private final CommandRepository commandRepository;

    @Autowired
    public CommandService(CommandRepository commandRepository) {
        this.commandRepository = commandRepository;
    }

    public void deleteById(Long id) {
        this.commandRepository.deleteById(id);
    }

    public Iterable<Command> findAll() {
        return this.commandRepository.findAll();
    }

    public Command findById(Long id) {
        return this.commandRepository.findById(id).orElse(null);
    }

    @Transactional
    public void save(Command command) {
        this.commandRepository.save(command);
    }

    public void update(Command command) {
        Command existingCommand = this.commandRepository.findById(command.getId())
                .orElseThrow(() -> new IllegalArgumentException("Command not found with id: " + command.getId()));

        existingCommand.setClient(command.getClient());
        existingCommand.setTotalPrice(command.getTotalPrice());

        updateCommandProducts(existingCommand, command.getCommandProducts());

        this.commandRepository.save(existingCommand);
    }

    public void delete(Command command) {
        this.commandRepository.delete(command);
    }

    public void deleteAll() {
        this.commandRepository.deleteAll();
    }

    public boolean existsById(Long id) {
        return this.commandRepository.existsById(id);
    }

    public long count() {
        return this.commandRepository.count();
    }

    public void deleteAll(Iterable<Command> orders) {
        this.commandRepository.deleteAll(orders);
    }

    @Transactional
    public void updateCommandProducts(Command commandToUpdate, List<CommandProduct> newCommandProducts) {
        commandToUpdate.getCommandProducts().removeIf(existingCp ->
                newCommandProducts.stream().noneMatch(newCp ->
                        newCp.getProduct().getId().equals(existingCp.getProduct().getId())));

        for (CommandProduct newCp : newCommandProducts) {
            CommandProduct existingCp = commandToUpdate.getCommandProducts().stream()
                    .filter(cp -> cp.getProduct().getId().equals(newCp.getProduct().getId()))
                    .findFirst()
                    .orElse(null);

            if (existingCp == null) {
                newCp.setCommand(commandToUpdate);
                commandToUpdate.getCommandProducts().add(newCp);
            } else {
                existingCp.setQuantity(newCp.getQuantity());
            }
        }

        commandRepository.save(commandToUpdate);
    }

}
