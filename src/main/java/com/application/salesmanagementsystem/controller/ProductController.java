package com.application.salesmanagementsystem.controller;

import com.application.salesmanagementsystem.model.Product;
import com.application.salesmanagementsystem.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public String showProduct(Model model) {
        if (model.containsAttribute("products")) {
            List<Product> Products = (List<Product>) model.getAttribute("Products");
            model.addAttribute("products", Products);
        } else {
            List<Product> Products = productService.getAllProducts();
            model.addAttribute("Products", Products);
        }

        if (!model.containsAttribute("error")) {
            model.addAttribute("error", false);
        }

        if (!model.containsAttribute("newProduct")) {
            Product newProduct = new Product();
            model.addAttribute("newProduct", newProduct);
        }

        if (!model.containsAttribute("keyword")) {
            model.addAttribute("keyword", null);
        }

        if (!model.containsAttribute("modal")) {
            model.addAttribute("modal", false);
        }

        return "product";
    }

    //Mở form thêm khách hàng
    @GetMapping("/products/new")
    public String createProduct(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("modal", true);
        return "redirect:/products";
    }

    @PostMapping("/products/edit/{id}")
    public String editProduct(@PathVariable int id, RedirectAttributes redirectAttributes) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isPresent()) {
            redirectAttributes.addFlashAttribute("modal", true);
            redirectAttributes.addFlashAttribute("newProduct", product.get());
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm.");
        }
        return "redirect:/products";
    }


    @PostMapping("/products")
    public String saveProduct(@ModelAttribute("newProduct") Product Product) {
        productService.saveProduct(Product);
        return "redirect:/products";
    }

    @PostMapping("/products/{id}")
    public String deleteProduct(@PathVariable int id) {
        productService.deleteProduct(id);
        return "redirect:/products";
    }

    @PostMapping("/products/search")
    public String searchProducts(@RequestParam("keyword") String keyword, RedirectAttributes redirectAttributes) {
        List<Product> searchResults = productService.searchProducts(keyword);

        if (searchResults.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm nào với từ khóa '" + keyword + "'.");
        } else {
            redirectAttributes.addFlashAttribute("products", searchResults); // Ghi đè thuộc tính Products
        }

        redirectAttributes.addAttribute("keyword", keyword);

        return "redirect:/products";
    }

}
