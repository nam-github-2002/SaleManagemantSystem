package com.application.salesmanagementsystem.controller;

import com.application.salesmanagementsystem.model.Employee;
import com.application.salesmanagementsystem.model.Image;
import com.application.salesmanagementsystem.model.Supplier;
import com.application.salesmanagementsystem.repository.ImageRepository;
import com.application.salesmanagementsystem.repository.ProductRepository;
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
    private ProductRepository productRepository;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ImageRepository imageRepository;


    // Hiển thị danh sách sản phẩm
    @GetMapping
    public String showProductList(Model model,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(required = false) String category,
                                  @RequestParam(required = false) String keyword,
                                  HttpSession session, HttpServletRequest request)
    {
        if (!LoginController.isAuthenticated(session, model)) {
            return "login";
        }

        int pageSize = 8;
        page = Math.max(page, 0);

        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");
        Page<Product> products;

        boolean validKeyword = keyword != null && !keyword.isEmpty() && !keyword.equalsIgnoreCase("null");
        boolean validCategory = category != null && !category.isEmpty() && !category.equalsIgnoreCase("null");

       if (validKeyword) {
            // Chỉ tìm kiếm theo từ khóa
            products = productService.searchProducts(keyword, PageRequest.of(page, pageSize));

        } else if (validCategory) {
            // Chỉ lọc theo danh mục
            products = productService.getProductsByCategory(category, PageRequest.of(page, pageSize));

        } else {
            // Lấy tất cả sản phẩm
            products = productService.getAllProducts(PageRequest.of(page, pageSize));
        }

        model.addAttribute("products", products.getContent());
        model.addAttribute("category", category);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", products.getTotalPages());
        model.addAttribute("currentUser", loggedInUser);
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);
        model.addAttribute("error", model.getAttribute("error"));

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "product/product :: productPage";
        }

        return "product/product";
    }


    @GetMapping("/detail/{id}")
    public String showProductDetail(@PathVariable int id, Model model,
                                    HttpSession session, HttpServletRequest request)
    {
        if (!LoginController.isAuthenticated(session, model)) {
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
        if (!LoginController.isAuthenticated(session, model)) {
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
        if (!LoginController.isAuthenticated(session, model)) {
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

//    @GetMapping("/display")
//    public ResponseEntity<byte[]> displayImage(@RequestParam("id") int id) throws IOException, SQLException
//    {
//        Image image = imageService.viewById(id);
//        byte [] imageBytes = null;
//        imageBytes = image.getImageContent().getBytes(1,(int) image.getImageContent().length());
//        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
//    }

//    @GetMapping("/display")
//    public ResponseEntity<byte[]> displayImage(@RequestParam("id") int productId) throws IOException, SQLException {
//        // Tìm Product dựa trên productId
//        Optional<Product> product = productService.getProductById(productId);
//        if (product.isEmpty()) {
//            // Trả về HTTP 404 nếu sản phẩm không tồn tại
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//        }
//        // Lấy hình ảnh đầu tiên liên kết với sản phẩm
//        List<Image> images = product.get().getImages();
//        if (images == null || images.isEmpty()) {
//            // Trả về HTTP 404 nếu không có hình ảnh nào liên kết
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//        }
//        // Lấy hình ảnh đầu tiên từ danh sách
//        Image image = images.get(0);
//        if (image.getImageContent() == null) {
//            // Trả về HTTP 404 nếu hình ảnh không có dữ liệu
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//        }
//        // Lấy dữ liệu nhị phân của hình ảnh
//        byte[] imageBytes = image.getImageContent().getBytes(1, (int) image.getImageContent().length());
//        // Trả về dữ liệu hình ảnh với loại MIME là JPEG
//        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
//    }

    @GetMapping("/display")
    public ResponseEntity<byte[]> displayImage(@RequestParam("id") int id) throws IOException, SQLException
    {
        Image image = imageService.viewById(id);
        byte [] imageBytes = null;
        imageBytes = image.getImageContent().getBytes(1,(int) image.getImageContent().length());
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
    }





    //Thêm mới
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

    //Edit
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

    //Xoá sản phẩm
    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable int id)
    {
        productService.deleteProduct(id);
        return "redirect:/products";
    }

    //Xoá ảnh
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

    //Tìm sản phẩm đã có
    @GetMapping("/searchProductName")
    @ResponseBody
    public List<Product> searchProduct(@RequestParam String query) {
        List<Product> products = productRepository.findByProductName(query);
        return products;
    }





}


