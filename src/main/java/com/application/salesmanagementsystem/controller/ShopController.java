package com.application.salesmanagementsystem.controller;

import com.application.salesmanagementsystem.model.Employee;
import com.application.salesmanagementsystem.model.Product;
import com.application.salesmanagementsystem.repository.ImageRepository;
import com.application.salesmanagementsystem.repository.ProductRepository;
import com.application.salesmanagementsystem.service.ImageService;
import com.application.salesmanagementsystem.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class ShopController {
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ImageRepository imageRepository;

    @GetMapping("/shop")
    public String showShopPage(
            @RequestParam(value = "page", defaultValue = "0") int page, // Trang hiện tại
            HttpSession session, // Để lấy thông tin người dùng từ session
            Model model) {

//        if (LoginController.isAuthenticated(session, model)) {
//            return "redirect:/login";
//        }

        int pageSize = 12; // Số sản phẩm trên mỗi trang
        List<Product> allProducts = productService.getAllProducts(); // Lấy tất cả sản phẩm từ DB
        int totalProducts = allProducts.size(); // Tổng số sản phẩm
        int totalPages = (int) Math.ceil((double) totalProducts / pageSize); // Tổng số trang

        // Kiểm tra giới hạn của `page`
        if (page < 0) page = 0;
        if (page >= totalPages) page = totalPages - 1;

        // Tính toán phạm vi sản phẩm của trang hiện tại
        int start = page * pageSize;
        int end = Math.min(start + pageSize, totalProducts);

        // Lấy danh sách sản phẩm cho trang hiện tại
        List<Product> products = allProducts.subList(start, end);

        // Tạo map chứa ID của hình ảnh đầu tiên liên quan đến sản phẩm
        Map<Integer, Integer> productImages = new HashMap<>();
        for (Product product : products) {
            List<Integer> imageIds = productService.findImagesByProductID(product.getProductID());
            if (imageIds != null && !imageIds.isEmpty()) {
                productImages.put(product.getProductID(), imageIds.get(0)); // Lấy ID ảnh đầu tiên
            }
        }

        // Lấy thông tin người dùng từ session
        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            model.addAttribute("currentUser", loggedInUser); // Thêm thông tin người dùng vào model
        } else {
            model.addAttribute("currentUser", null); // Nếu không có người dùng đăng nhập
        }

        // Truyền dữ liệu vào model
        model.addAttribute("products", products); // Sản phẩm của trang hiện tại
        model.addAttribute("productImages", productImages); // Hình ảnh sản phẩm
        model.addAttribute("currentPage", page); // Trang hiện tại
        model.addAttribute("totalPages", totalPages); // Tổng số trang

        return "shop/shopping"; // Trả về view đúng
    }

    @GetMapping("/shop/productDetail/{id}")
    public String showProductDetail(@PathVariable("id") int id, Model model, HttpSession session) {

        // Lấy thông tin sản phẩm và hình ảnh từ service
        Optional<Product> productOptional = productService.getProductById(id);
        List<Integer> images = productService.findImagesByProductID(id);

        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            model.addAttribute("product", product);
            model.addAttribute("images", images);
        } else {
            // Thêm thông báo lỗi nếu không tìm thấy sản phẩm
            model.addAttribute("error", "Không tìm thấy sản phẩm.");
        }

        // Lấy thông tin người dùng đang đăng nhập từ session
        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");

        // Nếu người dùng đang đăng nhập, thêm vào model
        if (loggedInUser != null) {
            model.addAttribute("currentUser", loggedInUser);
        } else {
            model.addAttribute("currentUser", null); // Trường hợp không có người dùng đăng nhập
        }

        // Trả về view chi tiết sản phẩm
        return "shop/shoppingDetail";
    }

}
