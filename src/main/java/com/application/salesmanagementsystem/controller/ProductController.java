package com.application.salesmanagementsystem.controller;

import com.application.salesmanagementsystem.model.Image;
import com.application.salesmanagementsystem.model.Supplier;
import com.application.salesmanagementsystem.service.ImageService;
import com.application.salesmanagementsystem.service.SupplierService;
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
    public static final String UPLOAD_DIR = "/image/";

    @Autowired
    private ProductService productService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private ImageService imageService;

    // Hiển thị danh sách sản phẩm
    @GetMapping
    public String showProductList(Model model, @RequestParam(defaultValue = "0") int page) {
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

        return "product/product :: productPage";
    }

    @GetMapping("/detail/{id}")
    public String showProductDetail(@PathVariable int id, Model model) {
        Optional<Product> product = productService.getProductById(id);
        List<Image> images = productService.getProductImages(id);
        for (Image image : images) {
            System.out.println("id: " +image.getId());
        }
        if (product.isPresent()) {
            model.addAttribute("viewMode", true);
            model.addAttribute("editMode", false);
            model.addAttribute("newProduct", product.get());
            model.addAttribute("images", images);
        } else {
            model.addAttribute("error", "Không tìm thấy sản phẩm.");
        }
        return "product/product-form :: productDetailPage";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("viewMode", false);
        model.addAttribute("editMode", true);
        model.addAttribute("exist", false);

        List<Supplier> suppliers = supplierService.getAllSuppliers();
        model.addAttribute("suppliers", suppliers);

        Product newProduct = new Product();
        newProduct.setProductID(productService.generateNewProductId());
        model.addAttribute("newProduct", newProduct);

        return "product/product-form :: productDetailPage";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable int id, Model model) {
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

        return "product/product-form :: productDetailPage";
    }

    @GetMapping("/display")
    public ResponseEntity<byte[]> displayImage(@RequestParam("id") int id) throws IOException, SQLException
    {
        Image image = imageService.viewById(id);
        byte [] imageBytes = null;
        imageBytes = image.getImageContent().getBytes(1,(int) image.getImageContent().length());
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
    }


    @PostMapping({"/edit/{id}", "/new"})
    public String updateProduct(@ModelAttribute("newProduct") Product newProduct,
                                @RequestParam("image") MultipartFile image,
                                BindingResult result,
                                RedirectAttributes redirectAttributes,
                                @PathVariable(value = "id", required = false) Integer id) throws IOException, SQLException {
        if (result.hasErrors()) {
            return "redirect:/products/edit/" + (id != null ? id : "new");
        }

        byte[] bytes = image.getBytes();
        Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);

        Image newImage = new Image();
        newImage.setProduct(newProduct);
        newImage.setImageContent(blob);
        imageService.create(newImage);

        productService.saveProduct(newProduct); // Lưu sản phẩm vào cơ sở dữ liệu
        redirectAttributes.addFlashAttribute("viewMode", true);
        redirectAttributes.addFlashAttribute("editMode", false);
        return "redirect:/products/detail/" + (id != null ? id : newProduct.getProductID());
    }

    @PostMapping("/{id}")
    public String deleteProduct(@PathVariable int id) {
        productService.deleteProduct(id);
        return "redirect:/products";
    }

    @PostMapping("/search")
    public String searchProducts(@RequestParam("keyword") String keyword, @RequestParam(defaultValue = "0") int page, RedirectAttributes redirectAttributes) {
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
