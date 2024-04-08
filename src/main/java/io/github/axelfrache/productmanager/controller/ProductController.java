package io.github.axelfrache.productmanager.controller;

import io.github.axelfrache.productmanager.model.Product;
import io.github.axelfrache.productmanager.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("products", productService.findAll());
        return "products/list";
    }

    @GetMapping("/{id}")
    public String showProduct(@PathVariable Long id, Model model) {
        Optional<Product> product = Optional.ofNullable(productService.findById(id));
        product.ifPresent(p -> model.addAttribute("product", p));
        return product.map(p -> "products/show").orElse("redirect:/products");
    }

    @GetMapping("/new")
    public String newProduct(Model model) {
        model.addAttribute("product", new Product());
        return "products/new";
    }

    @PostMapping
    public String createProduct(@Valid @ModelAttribute Product product, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("warning", "Please correct the fields indicated.");
            return "products/new";
        }
        productService.save(product);
        return "redirect:/products";
    }

    @GetMapping("/{id}/edit")
    public String editProduct(@PathVariable Long id, Model model) {
        Optional<Product> product = Optional.ofNullable(productService.findById(id));
        product.ifPresent(p -> model.addAttribute("product", p));
        return product.map(p -> "products/edit").orElse("redirect:/products");
    }

    @PostMapping("/{id}")
    public String updateOrDeleteProduct(@PathVariable Long id, @Valid @ModelAttribute Product product, BindingResult result, Model model, @RequestParam(name="_method", required=false) String method) {
        if ("put".equals(method)) {
            if (result.hasErrors()) {
                model.addAttribute("warning", "Please correct the fields indicated.");
                return "products/edit";
            }
            productService.update(id, product);
        } else if ("delete".equals(method)) {
            productService.deleteById(id);
        }
        return "redirect:/products";
    }
}