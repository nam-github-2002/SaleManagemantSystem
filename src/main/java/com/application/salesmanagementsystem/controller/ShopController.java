package com.application.salesmanagementsystem.controller;

import com.application.salesmanagementsystem.model.Category;
import com.application.salesmanagementsystem.model.Customer;
import com.application.salesmanagementsystem.model.Employee;
import com.application.salesmanagementsystem.model.Product;
import com.application.salesmanagementsystem.repository.ImageRepository;
import com.application.salesmanagementsystem.repository.ProductRepository;
import com.application.salesmanagementsystem.service.CategoryServiceImpl;
import com.application.salesmanagementsystem.service.CustomerService;
import com.application.salesmanagementsystem.service.ImageService;
import com.application.salesmanagementsystem.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class ShopController {
    @Autowired
    private ProductService productService;

    @Autowired
    private CustomerService customerService;


    @GetMapping("/")
    public String showShopPage(
            @RequestParam(defaultValue = "0") int page,
            HttpSession session,
            Model model)
    {

        int pageSize = 12;
        List<Product> allProducts = productService.getAllProducts();
        int totalProducts = allProducts.size();
        int totalPages = (int) Math.ceil((double) totalProducts / pageSize);

        if (page < 0) page = 0;
        if (page >= totalPages) page = totalPages - 1;

        int start = page * pageSize;
        int end = Math.min(start + pageSize, totalProducts);

        List<Product> products = allProducts.subList(start, end);

        Map<Integer, Integer> productImages = new HashMap<>();
        for (Product product : products) {
            List<Integer> imageIds = productService.findImagesByProductID(product.getProductID());
            if (imageIds != null && !imageIds.isEmpty()) {
                productImages.put(product.getProductID(), imageIds.get(0));
            }
        }

        model.addAttribute("products", products);
        model.addAttribute("productImages", productImages);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        if (!LoginController.isAuthenticated(session, model)) {
            return "shop/shopping";
        }

        @SuppressWarnings("unchecked")
        Map<Product, Integer> cart = (Map<Product, Integer>) session.getAttribute("cart");
        if (cart == null) {
            model.addAttribute("cartCount", 0);
        } else {
            model.addAttribute("cartCount", cart.values().stream().mapToInt(Integer::intValue).sum());
        }

        Customer loggedInCustomer = (Customer) session.getAttribute("loggedInCustomer");
        model.addAttribute("loggedInCustomer", loggedInCustomer);

        return "shop/shopping";
    }

    @GetMapping("/productDetail/{id}")
    public String showProductDetail(@PathVariable("id") int id, Model model, HttpSession session)
    {
        System.out.println("================================id:" + id );
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
        Customer loggedInCustomer = (Customer) session.getAttribute("loggedInCustomer");

        // Nếu người dùng đang đăng nhập, thêm vào model
        if (loggedInCustomer != null) {
            model.addAttribute("loggedInCustomer", loggedInCustomer);
        } else {
            model.addAttribute("loggedInCustomer", null); // Trường hợp không có người dùng đăng nhập
        }

        // Trả về view chi tiết sản phẩm
        return "shop/shoppingDetail";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model)
    {
        Customer customer = new Customer();
        model.addAttribute("customer", customer);

        return "shop/register";
    }

    // Xử lý đăng ký
    @PostMapping("/register")
    public String registerCustomer(@ModelAttribute("customer") Customer customer,
                                   BindingResult bindingResult,
                                   Model model)
    {
        if (bindingResult.hasErrors()) {
            return "redirect:/register";
        }

        try {
            customer.setCustomerID(customerService.generateCustomerID());
            customerService.registerCustomer(customer);
        } catch (RuntimeException e) {

            model.addAttribute("customer", customer);
            model.addAttribute("error", e.getMessage());
            return "shop/register";
        }

        return "redirect:/login";
    }

}
