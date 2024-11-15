package com.application.salesmanagementsystem.controller;

import com.application.salesmanagementsystem.model.Employee;
import com.application.salesmanagementsystem.model.Image;
import com.application.salesmanagementsystem.model.Supplier;
import com.application.salesmanagementsystem.repository.ImageRepository;
import com.application.salesmanagementsystem.service.ImageService;
import com.application.salesmanagementsystem.service.SupplierService;
import com.application.salesmanagementsystem.model.Product;
import com.application.salesmanagementsystem.service.ProductService;

import io.micrometer.common.util.StringUtils;
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

import java.io.IOException;
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

    private static final String TEMP_DIR = "src/main/resources/static/images/products";
    @Autowired
    private ImageRepository imageRepository;

    // Hiển thị danh sách sản phẩm
    @GetMapping
    public String showProductList(Model model,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(required = false) String category,
                                  HttpSession session, HttpServletRequest request)
    {
        if (LoginController.isAuthenticated(session, model)) {
            return "login";
        }

        int pageSize = 8;
        page = Math.max(page, 0);

        Page<Product> products;
        if (model.getAttribute("products") != null) {

            products = (Page<Product>) model.getAttribute("products");

        } else {

            if (category != null && StringUtils.isNotBlank(category) && !category.equals("null")) {
                products = productService.getProductsByCategory(category, PageRequest.of(page, pageSize));
            } else {
                products = productService.getAllProducts(PageRequest.of(page, pageSize));
            }
        }

        model.addAttribute("products", products.getContent());
        model.addAttribute("category", category);
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

        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");
        model.addAttribute("currentUser", loggedInUser);

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "product/product :: productPage";
        }

        return "product/product";
    }

    @GetMapping("/detail/{id}")
    public String showProductDetail(@PathVariable int id, Model model,
                                    HttpSession session, HttpServletRequest request)
    {
        if (LoginController.isAuthenticated(session, model)) {
            return "login";
        }

        Optional<Product> product = productService.getProductById(id);
        List<Integer> images = productService.findImagesByProductID(id);

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
                                 HttpSession session, HttpServletRequest request)
    {
        if (LoginController.isAuthenticated(session, model)) {
            return "login";
        }

        Product newProduct = new Product();
        newProduct.setProductID(productService.generateNewProductId());
        List<Supplier> suppliers = supplierService.getAllSuppliers();

        model.addAttribute("viewMode", false);
        model.addAttribute("editMode", true);
        model.addAttribute("exist", false);
        model.addAttribute("suppliers", suppliers);
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
                               HttpSession session, HttpServletRequest request)
    {
        if (LoginController.isAuthenticated(session, model)) {
            return "login";
        }

        Optional<Product> opProduct = productService.getProductById(id);
        List<Supplier> suppliers = supplierService.getAllSuppliers();
        List<Integer> images;

        if (model.containsAttribute("images")) {
            images = (List<Integer>) model.getAttribute("images");
        } else {
            images = productService.findImagesByProductID(id);
        }

        if (opProduct.isPresent()) {
            Product product = opProduct.get();

            model.addAttribute("viewMode", false);
            model.addAttribute("editMode", true);
            model.addAttribute("exist", true);
            model.addAttribute("newProduct", product);
            model.addAttribute("images", images);
            model.addAttribute("suppliers", suppliers);
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
                                @RequestParam("image") MultipartFile[] images,
                                BindingResult result,
                                RedirectAttributes redirectAttributes) throws IOException, SQLException
    {
        if (result.hasErrors()) {
            return "redirect:/products/new";
        }
        productService.saveProduct(newProduct);

        if(productService.findById(newProduct.getProductID()).isPresent() && images != null && images.length > 0) {
           for(MultipartFile image : images) {
               if(!image.isEmpty()) {
                   byte[] bytes = image.getBytes();
                   Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);

                   Image newImage = new Image();
                   newImage.setImageContent(blob);
                   newImage.setProduct(newProduct);
                   imageService.create(newImage);
               }
           }
        }

        return "redirect:/products/detail/" + newProduct.getProductID();
    }

    @PostMapping({"/edit/{id}"})
    public String updateProduct(@ModelAttribute("newProduct") Product newProduct,
                                @RequestParam("image") MultipartFile[] images,
                                BindingResult result,
                                RedirectAttributes redirectAttributes)
            throws IOException, SQLException
    {
        if (result.hasErrors()) {
            return "redirect:/products/edit/" + newProduct.getProductID();
        }

        if(images != null && images.length > 0 ) {
            for(MultipartFile image : images) {
                if(!image.isEmpty()) {
                    byte[] bytes = image.getBytes();
                    Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);

                    Image newImage = new Image();
                    newImage.setImageContent(blob);
                    newImage.setProduct(newProduct);
                    imageService.create(newImage);
                }
            }
        }

        productService.saveProduct(newProduct);
        return "redirect:/products/detail/" + newProduct.getProductID();
    }

    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable int id)
    {
        productService.deleteProduct(id);
        return "redirect:/products";
    }

    @PostMapping("/image/{id}")
    public String deleteImage(HttpServletRequest request,
                              @PathVariable int id,
                              RedirectAttributes redirectAttributes) {
        Image image = imageService.viewById(id);
        if (image == null) {
            redirectAttributes.addFlashAttribute("error", "Image not found");
            return "redirect:/products";
        }
        Integer productId = image.getProduct().getProductID();
        Product product = productService.getProductById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.getImages().remove(image);
        imageService.delete(id);
        productService.saveProduct(product);

        redirectAttributes.addFlashAttribute("success", "Image deleted successfully");

        return "redirect:/products/edit/" + productId;
    }


    @PostMapping("/search")
    public String searchProducts(@RequestParam("keyword") String keyword, @RequestParam(defaultValue = "0") int page,
                                 RedirectAttributes redirectAttributes, HttpSession session)
    {
        int pageSize = 8;
        Page<Product> searchResults = productService.searchProducts(keyword, PageRequest.of(page, pageSize));

        if (searchResults.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm nào với từ khóa '" + keyword + "'.");
        } else {
            redirectAttributes.addFlashAttribute("products", searchResults);
        }
        System.out.println("------------------Da chuyen huong------------");
        redirectAttributes.addAttribute("keyword", keyword);
        return "redirect:/products";
    }


}
