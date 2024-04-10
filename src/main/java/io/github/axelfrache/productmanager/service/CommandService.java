package io.github.axelfrache.productmanager.service;

import io.github.axelfrache.productmanager.model.Command;
import io.github.axelfrache.productmanager.repository.CommandRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        this.commandRepository.save(command);
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

}
