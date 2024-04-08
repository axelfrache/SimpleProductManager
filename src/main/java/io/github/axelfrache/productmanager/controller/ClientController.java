package io.github.axelfrache.productmanager.controller;

import io.github.axelfrache.productmanager.model.Client;
import io.github.axelfrache.productmanager.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public String listClients(Model model) {
        model.addAttribute("clients", clientService.findAll());
        return "clients/list";
    }

    @GetMapping("/new")
    public String showClientForm(Model model) {
        model.addAttribute("client", new Client());
        return "clients/form";
    }

    @PostMapping
    public String saveClient(@ModelAttribute Client client) {
        clientService.save(client);
        return "redirect:/clients";
    }

    @GetMapping("/{id}/edit")
    public String showEditClientForm(@PathVariable Long id, Model model) {
        Client client = clientService.findById(id);
        if (client != null) {
            model.addAttribute("client", client);
            return "clients/form";
        } else {
            return "redirect:/clients";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteClient(@PathVariable Long id) {
        clientService.deleteById(id);
        return "redirect:/clients";
    }
}
