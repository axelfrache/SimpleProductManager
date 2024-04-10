package io.github.axelfrache.productmanager.controller;

import io.github.axelfrache.productmanager.model.Command;
import io.github.axelfrache.productmanager.service.ClientService;
import io.github.axelfrache.productmanager.service.CommandService;
import io.github.axelfrache.productmanager.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/orders")
public class CommandController {

    private final CommandService orderService;
    private final ClientService clientService;
    private final ProductService productService;

    @Autowired
    public CommandController(CommandService orderService, ClientService clientService, ProductService productService) {
        this.orderService = orderService;
        this.clientService = clientService;
        this.productService = productService;
    }

    @GetMapping
    public String listOrders(Model model) {
        model.addAttribute("orders", orderService.findAll());
        return "orders/list";
    }

    @GetMapping("/new")
    public String showCommandForm(Model model) {
        model.addAttribute("command", new Command());
        model.addAttribute("clients", clientService.findAll());
        model.addAttribute("allProducts", productService.findAll());
        return "orders/form";
    }

    @PostMapping
    public String saveOrder(@ModelAttribute Command command) {
        orderService.save(command);
        return "redirect:/orders";
    }

    @GetMapping("/{id}/edit")
    public String showEditOrderForm(@PathVariable Long id, Model model) {
        Command command = orderService.findById(id);
        model.addAttribute("order", command);
        model.addAttribute("clients", clientService.findAll());
        model.addAttribute("products", productService.findAll());
        return "orders/form";
    }

    @PostMapping("/{id}")
    public String updateOrder(@PathVariable Long id, @ModelAttribute Command command) {
        command.setId(id);
        orderService.update(command);
        return "redirect:/orders";
    }

    @PostMapping("/{id}/delete")
    public String deleteOrder(@PathVariable Long id) {
        orderService.deleteById(id);
        return "redirect:/orders";
    }
}