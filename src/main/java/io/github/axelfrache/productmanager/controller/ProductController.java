package io.github.axelfrache.productmanager.controller;

import io.github.axelfrache.productmanager.model.Product;
import io.github.axelfrache.productmanager.service.CategoryService;
import io.github.axelfrache.productmanager.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    @Autowired
    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
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
        model.addAttribute("categories", categoryService.findAll());
        return "products/new";
    }

    @PostMapping
    public String createProduct(@Valid @ModelAttribute Product product, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("warning", "Please correct the fields indicated.");
            model.addAttribute("categories", categoryService.findAll());
            return "products/new";
        }
        if (product.getCategory() != null && product.getCategory().getId() == null) {
            product.setCategory(null);
        }
        productService.save(product);
        return "redirect:/products";
    }

    @GetMapping("/{id}/edit")
    public String editProduct(@PathVariable Long id, Model model) {
        Optional<Product> product = Optional.ofNullable(productService.findById(id));
        product.ifPresent(p -> model.addAttribute("product", p));
        model.addAttribute("categories", categoryService.findAll()); // For the category dropdown in edit form
        return product.map(p -> "products/edit").orElse("redirect:/products");
    }

    @PostMapping("/{id}")
    public String updateOrDeleteProduct(@PathVariable Long id, @Valid @ModelAttribute Product product, BindingResult result, Model model, @RequestParam(name = "_method", required = false) String method, RedirectAttributes redirectAttributes) {
        if ("put".equals(method)) {
            if (result.hasErrors()) {
                model.addAttribute("categories", categoryService.findAll());
                model.addAttribute("warning", "Please correct the fields indicated.");
                return "products/edit";
            }
            if (product.getCategory() != null && product.getCategory().getId() == null) {
                product.setCategory(null);
            }
            productService.update(product);
            redirectAttributes.addFlashAttribute("success", "Product updated successfully");
        } else if ("delete".equals(method)) {
            try {
                productService.deleteProductAndCleanUpCommands(id);
                redirectAttributes.addFlashAttribute("success", "Product deleted successfully");
            } catch (IllegalArgumentException e) {
                redirectAttributes.addFlashAttribute("error", "Error deleting product: " + e.getMessage());
            }
        }
        return "redirect:/products";
    }
}