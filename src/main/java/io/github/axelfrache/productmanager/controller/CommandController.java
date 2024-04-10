package io.github.axelfrache.productmanager.controller;

import io.github.axelfrache.productmanager.model.Client;
import io.github.axelfrache.productmanager.model.Command;
import io.github.axelfrache.productmanager.model.CommandProduct;
import io.github.axelfrache.productmanager.model.Product;
import io.github.axelfrache.productmanager.service.ClientService;
import io.github.axelfrache.productmanager.service.CommandService;
import io.github.axelfrache.productmanager.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        prepareProductOptions(model);
        return "orders/form";
    }

    @GetMapping("/{id}/edit")
    public String showEditOrderForm(@PathVariable Long id, Model model) {
        Command command = orderService.findById(id);
        if (command != null) {
            model.addAttribute("command", command);
            model.addAttribute("clients", clientService.findAll());
            prepareProductOptions(model);
            return "orders/form";
        } else {
            return "redirect:/orders";
        }
    }

    @PostMapping
    public String saveOrder(@ModelAttribute Command command,
                            @RequestParam("clientId") Long clientId,
                            @RequestParam("products[]") List<Long> productIds,
                            @RequestParam("quantities[]") List<Integer> quantities,
                            RedirectAttributes redirectAttributes) {

        if (productIds.size() != quantities.size()) {
            // Error: The number of products does not match the number of quantities
            redirectAttributes.addFlashAttribute("error", "The number of products does not match the number of quantities.");
            return "redirect:/orders/new";
        }

        Client client = clientService.findById(clientId);
        if (client == null) {
            // Error: The client ID is not valid
            redirectAttributes.addFlashAttribute("error", "The client ID is not valid.");
            return "redirect:/orders/new";
        }

        command.setClient(client);
        List<CommandProduct> commandProducts = new ArrayList<>();

        for (int i = 0; i < productIds.size(); i++) {
            Long productId = productIds.get(i);
            Product product = productService.findById(productId);
            if (product == null) {
                // Error: One of the product IDs is not valid
                redirectAttributes.addFlashAttribute("error", "One of the product IDs is not valid.");
                return "redirect:/orders/new";
            }
            Integer quantity = quantities.get(i);

            CommandProduct commandProduct = new CommandProduct();
            commandProduct.setCommand(command);
            commandProduct.setProduct(product);
            commandProduct.setQuantity(quantity);

            commandProducts.add(commandProduct);
        }

        command.setCommandProducts(commandProducts);
        orderService.save(command);
        return "redirect:/orders";
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

    private void prepareProductOptions(Model model) {
        Iterable<Product> iterableProducts = productService.findAll();
        List<Product> allProducts = new ArrayList<>();
        iterableProducts.forEach(allProducts::add);
        String productOptions = allProducts.stream()
                .map(product -> "\"" + product.getId() + "\":\"" + product.getName() + "\"")
                .collect(Collectors.joining(", ", "{", "}"));
        model.addAttribute("productOptions", productOptions);
    }

}
