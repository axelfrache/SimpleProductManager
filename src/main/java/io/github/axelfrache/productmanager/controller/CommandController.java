package io.github.axelfrache.productmanager.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;
import com.fasterxml.jackson.databind.ObjectMapper;


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
        return "orders/new";
    }

    @GetMapping("/{id}/edit")
    public String showEditOrderForm(@PathVariable Long id, Model model) {
        Command command = orderService.findById(id);
        if (command != null) {
            model.addAttribute("command", command);
            model.addAttribute("clients", clientService.findAll());
            prepareProductOptions(model);

            List<Map<String, Object>> productInfos = command.getCommandProducts().stream()
                    .map(cp -> {
                        Map<String, Object> info = new HashMap<>();
                        info.put("productId", cp.getProduct().getId());
                        info.put("quantity", cp.getQuantity());
                        return info;
                    })
                    .collect(Collectors.toList());

            ObjectMapper objectMapper = new ObjectMapper();
            String existingProductsJson = "[]";
            try {
                existingProductsJson = objectMapper.writeValueAsString(productInfos);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            model.addAttribute("existingProducts", existingProductsJson);

            return "orders/edit";
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
            redirectAttributes.addFlashAttribute("error", "The number of products does not match the number of quantities.");
            return "redirect:/orders/new";
        }

        Client client = clientService.findById(clientId);
        if (client == null) {
            redirectAttributes.addFlashAttribute("error", "The client ID is not valid.");
            return "redirect:/orders/new";
        }

        command.setClient(client);
        List<CommandProduct> commandProducts = new ArrayList<>();

        for (int i = 0; i < productIds.size(); i++) {
            Long productId = productIds.get(i);
            Product product = productService.findById(productId);
            if (product == null) {
                redirectAttributes.addFlashAttribute("error", "One of the product IDs is not valid.");
                return "redirect:/orders/new";
            }
            Integer quantity = quantities.get(i);
            if (quantity <= 0) {
                redirectAttributes.addFlashAttribute("error", "Quantity must be positive.");
                return "redirect:/orders/new";
            }

            CommandProduct commandProduct = new CommandProduct();
            commandProduct.setCommand(command);
            commandProduct.setProduct(product);
            commandProduct.setQuantity(quantity);

            commandProducts.add(commandProduct);
        }

        command.setCommandProducts(commandProducts);
        orderService.save(command);

        redirectAttributes.addFlashAttribute("success", "Order created successfully.");
        return "redirect:/orders";
    }

    @PostMapping("/{id}")
    public String updateOrder(@PathVariable Long id,
                              @RequestParam("clientId") Long clientId,
                              @RequestParam("products[]") List<Long> productIds,
                              @RequestParam("quantities[]") List<Integer> quantities,
                              RedirectAttributes redirectAttributes) {
        Command commandToUpdate = orderService.findById(id);
        if (commandToUpdate == null) {
            redirectAttributes.addFlashAttribute("error", "Command not found.");
            return "redirect:/orders";
        }

        Client client = clientService.findById(clientId);
        if (client == null) {
            redirectAttributes.addFlashAttribute("error", "Client not found.");
            return "redirect:/orders";
        }
        commandToUpdate.setClient(client);

        List<CommandProduct> newCommandProducts = new ArrayList<>();
        for (int i = 0; i < productIds.size(); i++) {
            Long productId = productIds.get(i);
            Integer quantity = quantities.get(i);

            Product product = productService.findById(productId);
            if (product != null && quantity > 0) {
                CommandProduct commandProduct = new CommandProduct();
                commandProduct.setProduct(product);
                commandProduct.setQuantity(quantity);
                newCommandProducts.add(commandProduct);
            }
        }
        orderService.updateCommandProducts(commandToUpdate, newCommandProducts);

        redirectAttributes.addFlashAttribute("success", "Order updated successfully.");
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
