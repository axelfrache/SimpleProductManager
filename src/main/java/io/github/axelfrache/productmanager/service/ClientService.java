package io.github.axelfrache.productmanager.service;

import io.github.axelfrache.productmanager.model.Client;
import io.github.axelfrache.productmanager.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public void deleteById(Long id) {
        this.clientRepository.deleteById(id);
    }

    public Iterable<Client> findAll() {
        return this.clientRepository.findAll();
    }

    public Client findById(Long id) {
        return this.clientRepository.findById(id).orElse(null);
    }

    public void save(Client client) {
        this.clientRepository.save(client);
    }
}
