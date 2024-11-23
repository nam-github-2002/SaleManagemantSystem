package com.application.salesmanagementsystem.controller;

import com.application.salesmanagementsystem.model.*;
import com.application.salesmanagementsystem.repository.CustomerRepository;
import com.application.salesmanagementsystem.repository.ProductRepository;
import com.application.salesmanagementsystem.service.CustomerService;
import com.application.salesmanagementsystem.service.OrderService;
import com.application.salesmanagementsystem.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.TemplateEngine;

import java.time.LocalDate;
import java.util.*;

@Controller
public class CartController {
    @Autowired
    private ProductService productService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderService ordersService;

    @PostMapping("/cart/add")
    @ResponseBody
    public String addToCart(@RequestParam int productId, @RequestParam(defaultValue = "1") int quantity, HttpSession session) {
        // Lấy giỏ hàng từ session
        Map<Product, Integer> cart = (Map<Product, Integer>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>(); // Nếu giỏ hàng chưa có, khởi tạo Map trống
        }

        // Tìm sản phẩm từ database
        Optional<Product> product = productService.getProductById(productId);
        if (product.isPresent()) {
            Product p = product.get();
            if (cart.containsKey(p)) {
                cart.put(p, cart.get(p) + quantity);  // Cập nhật số lượng nếu sản phẩm đã tồn tại trong giỏ hàng
            } else {
                cart.put(p, quantity);  // Thêm mới sản phẩm vào giỏ hàng
            }

            // Cập nhật giỏ hàng trong session
            session.setAttribute("cart", cart);

            return "Sản phẩm đã được thêm vào giỏ hàng!";
        }

        return "Sản phẩm không tồn tại!";
    }


    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        // Lấy giỏ hàng từ session
        @SuppressWarnings("unchecked")
        Map<Product, Integer> cart = (Map<Product, Integer>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
        }

        // Lấy thông tin khách hàng từ session
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        // Tạo đơn hàng mới và gắn khách hàng
        Order newOrder = new Order();
        newOrder.setCustomer(customer);
        newOrder.setOrderDetails(new ArrayList<>()); // Khởi tạo danh sách chi tiết đơn hàng

        Double totalAmount = 0.0;
        // Duyệt qua giỏ hàng để tạo danh sách chi tiết đơn hàng
        for (Map.Entry<Product, Integer> entry : cart.entrySet()) {
            Product product = entry.getKey();  // Lấy sản phẩm
            Integer quantity = entry.getValue();  // Lấy số lượng

            // Tạo chi tiết đơn hàng
            OrderDetail detail = new OrderDetail();
            detail.setOrder(newOrder);
            detail.setProduct(product);
            detail.setQuantity(quantity);
            detail.setUnitPrice(product.getPrice());

            // Thêm chi tiết vào đơn hàng
            detail.setTotalPrice(detail.getQuantity() * detail.getUnitPrice());
            totalAmount += detail.getTotalPrice();
            newOrder.getOrderDetails().add(detail);
        }

        newOrder.setTotalAmount(totalAmount);
        // Gắn đơn hàng mới và giỏ hàng vào model
        model.addAttribute("paymentMethods", Arrays.asList(PaymentMethod.values()));
        model.addAttribute("orderStatus", OrderStatus.Processing);
        model.addAttribute("newOrder", newOrder);
        model.addAttribute("cart", cart);
        model.addAttribute("loggedInCustomer", customer);


        return "shop/cart";
    }


    @PostMapping("/cart/remove")
    @ResponseBody
    public ResponseEntity<?> removeFromCart(@RequestParam int productId, HttpSession session) {
        // Giỏ hàng lưu trữ sản phẩm theo Product ID
        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute("cart");

        if (cart != null) {
            // Kiểm tra nếu sản phẩm có trong giỏ hàng
            if (cart.containsKey(productId)) {
                cart.remove(productId);  // Xóa sản phẩm theo Product ID
                session.setAttribute("cart", cart);  // Cập nhật lại giỏ hàng trong session
                return ResponseEntity.ok(Map.of("message", "Sản phẩm đã được xóa!"));
            }
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Sản phẩm không tồn tại trong giỏ hàng!"));
    }

    @PostMapping("/cart/process")
    public String saveOrder(@ModelAttribute("newOrder") Order newOrder, Model model,
                            RedirectAttributes redirectAttributes)
    {
        String customerID = newOrder.getCustomer().getCustomerID();
        String customerName = newOrder.getCustomer().getName();
        String address = newOrder.getCustomer().getAddress();
        String phone = newOrder.getCustomer().getPhone();

        // Kiểm tra và xử lý khách hàng
        Customer customer = customerRepository.findByNameAndPhone(customerName, phone);
        if (customer == null) {
            customer = new Customer();
            customer.setCustomerID(customerID);
            customer.setName(customerName);
            customer.setAddress(address);
            customer.setPhone(phone);
            customerService.saveCustomer(customer);
        }
        newOrder.setCustomer(customer);


        for (OrderDetail detail : newOrder.getOrderDetails()) {
            Product product = productRepository.findByProductID(detail.getProduct().getProductID());
            if (product == null) {
                redirectAttributes.addFlashAttribute("error", "Sản phẩm không tồn tại: " + detail.getProduct().getProductName());
                return "redirect:/cart";
            }

            // Kiểm tra tồn kho
            if (product.getQuantity() < detail.getQuantity()) {
                redirectAttributes.addFlashAttribute("error",
                        "Không đủ hàng cho sản phẩm: " + product.getProductName() + ", còn lại: " + product.getQuantity());
                return "redirect:/cart";
            }
            if (product.getQuantity() == detail.getQuantity()) {
                product.setStatus(false);
            }


            // Cập nhật tồn kho
            product.setQuantity(product.getQuantity() - detail.getQuantity());
            productRepository.save(product);

            // Gắn thông tin sản phẩm vào OrderDetail
            detail.setProduct(product);
            detail.setOrder(newOrder);
            detail.setTotalPrice(detail.getQuantity() * detail.getUnitPrice());
        }
        // Lưu hóa đơn
        newOrder.setOrderDate(LocalDate.now());
        ordersService.createOrder(newOrder);

        return "redirect:/";
    }

}
