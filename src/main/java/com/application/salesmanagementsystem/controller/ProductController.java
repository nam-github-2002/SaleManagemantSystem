package com.application.salesmanagementsystem.controller;

import com.application.salesmanagementsystem.model.Employee;
import com.application.salesmanagementsystem.model.Image;
import com.application.salesmanagementsystem.model.Supplier;
import com.application.salesmanagementsystem.service.ImageService;
import com.application.salesmanagementsystem.service.SupplierService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;


import com.application.salesmanagementsystem.model.Product;
import com.application.salesmanagementsystem.service.ProductService;

import javax.sql.rowset.serial.SerialException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.*;


@Controller
@RequestMapping("/products")
@SuppressWarnings("unchecked")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private ImageService imageService;

    // Hiển thị danh sách sản phẩm
    @GetMapping
    public String showProductList(Model model, @RequestParam(defaultValue = "0") int page,
                                  HttpSession session, HttpServletRequest request)
    {
        if (!LoginController.isAuthenticated(session, model)) {
            return "redirect:/login";
        }
        int pageSize = 8;
        Page<Product> products;
        if (model.containsAttribute("products")) {

            products = (Page<Product>) model.getAttribute("products");
        } else {

            products = productService.getAllProducts(PageRequest.of(page, pageSize));
        }
        model.addAttribute("products", products.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", products.getTotalPages());

        if (model.containsAttribute("error")) {
            model.addAttribute("error", model.getAttribute("error"));
        } else {
            model.addAttribute("error", false);
        }
        if (!model.containsAttribute("keyword")) {
            model.addAttribute("keyword", null);
        }

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "product/product :: productPage";
        }

        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");
        model.addAttribute("currentUser", loggedInUser);

        return "product/product";
    }

    @GetMapping("/detail/{id}")
    public String showProductDetail(@PathVariable int id, Model model,
                                    HttpSession session, HttpServletRequest request) {
        if (!LoginController.isAuthenticated(session, model)) {
            return "redirect:/login";
        }

        Optional<Product> product = productService.getProductById(id);
        List<Image> images = productService.getProductImages(id);

        if (product.isPresent()) {
            model.addAttribute("viewMode", true);
            model.addAttribute("editMode", false);
            model.addAttribute("exist", true);
            model.addAttribute("newProduct", product.get());
            model.addAttribute("images", images);
        } else {
            model.addAttribute("error", "Không tìm thấy sản phẩm.");
        }

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "product/product-form :: productDetailPage";
        }

        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");
        model.addAttribute("currentUser", loggedInUser);
        return "product/product-form";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model,
                                 HttpSession session, HttpServletRequest request) {
        if (!LoginController.isAuthenticated(session, model)) {
            return "redirect:/login";
        }

        model.addAttribute("viewMode", false);
        model.addAttribute("editMode", true);
        model.addAttribute("exist", false);

        List<Supplier> suppliers = supplierService.getAllSuppliers();
        model.addAttribute("suppliers", suppliers);

        Product newProduct = new Product();
        newProduct.setProductID(productService.generateNewProductId());
        model.addAttribute("newProduct", newProduct);

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "product/product-form :: productDetailPage";
        }

        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");
        model.addAttribute("currentUser", loggedInUser);
        return "product/product-form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable int id, Model model,
                               HttpSession session, HttpServletRequest request) {
        if (!LoginController.isAuthenticated(session, model)) {
            return "redirect:/login";
        }

        Optional<Product> opProduct = productService.getProductById(id);
        if (opProduct.isPresent()) {
            Product product = opProduct.get();
            model.addAttribute("viewMode", false);
            model.addAttribute("editMode", true);
            model.addAttribute("exist", true);
            model.addAttribute("newProduct", product);
        } else {
            model.addAttribute("error", "Không tìm thấy sản phẩm.");
        }

        List<Supplier> suppliers = supplierService.getAllSuppliers();
        model.addAttribute("suppliers", suppliers);

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "product/product-form :: productDetailPage";
        }

        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");
        model.addAttribute("currentUser", loggedInUser);
        return "product/product-form";
    }

    @GetMapping("/display")
    public ResponseEntity<byte[]> displayImage(@RequestParam("id") int id) throws IOException, SQLException
    {
        Image image = imageService.viewById(id);
        byte [] imageBytes = null;
        imageBytes = image.getImageContent().getBytes(1,(int) image.getImageContent().length());
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
    }




    @PostMapping({ "/new"})
    public String saveProduct(@ModelAttribute("newProduct") Product newProduct,
                                @RequestParam("image") MultipartFile image,
                                BindingResult result,
                                RedirectAttributes redirectAttributes) throws IOException, SQLException {
        if (result.hasErrors()) {
            return "redirect:/products";
        }

        productService.saveProduct(newProduct);

        if(productService.findById(newProduct.getProductID()).isPresent()) {
            byte[] bytes = image.getBytes();
            Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);

            Image newImage = new Image();
            newImage.setProduct(newProduct);
            newImage.setImageContent(blob);
            imageService.create(newImage);
            newProduct.addImages(newImage);
        }

        productService.saveProduct(newProduct);
        redirectAttributes.addFlashAttribute("viewMode", true);
        redirectAttributes.addFlashAttribute("editMode", false);
        return "redirect:/products/detail/" + newProduct.getProductID();
    }

    @PostMapping({"/edit/{id}"})
    public String updateProduct(@ModelAttribute("newProduct") Product newProduct,
                                @RequestParam("image") MultipartFile image,
                                BindingResult result,
                                RedirectAttributes redirectAttributes,
                                @PathVariable(value = "id", required = false) Integer id) throws IOException, SQLException {
        if (result.hasErrors()) {
            return "redirect:/products";
        }

        byte[] bytes = image.getBytes();
        Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);

        Image newImage = new Image();
        newImage.setProduct(newProduct);
        newImage.setImageContent(blob);
        imageService.create(newImage);
        newProduct.addImages(newImage);

        productService.saveProduct(newProduct);
        redirectAttributes.addFlashAttribute("viewMode", true);
        redirectAttributes.addFlashAttribute("editMode", false);
        return "redirect:/products/detail/" + id;
    }

    @PostMapping("/{id}")
    public String deleteProduct(@PathVariable int id) {
        productService.deleteProduct(id);
        return "redirect:/products";
    }

    @PostMapping("/search")
    public String searchProducts(@RequestParam("keyword") String keyword, @RequestParam(defaultValue = "0") int page,
                                 RedirectAttributes redirectAttributes) {
        int pageSize = 8;
        Page<Product> searchResults = productService.searchProducts(keyword, PageRequest.of(page, pageSize));

        if (searchResults.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm nào với từ khóa '" + keyword + "'.");
        } else {
            redirectAttributes.addFlashAttribute("products", searchResults);
        }

        redirectAttributes.addFlashAttribute("keyword", keyword);
        return "redirect:/products";
    }
}
