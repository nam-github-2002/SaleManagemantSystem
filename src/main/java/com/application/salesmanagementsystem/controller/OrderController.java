package com.application.salesmanagementsystem.controller;

import com.application.salesmanagementsystem.model.*;
import com.application.salesmanagementsystem.model.Order;
import com.application.salesmanagementsystem.repository.*;
import com.application.salesmanagementsystem.service.CustomerService;
import com.application.salesmanagementsystem.service.EmployeeService;
import com.application.salesmanagementsystem.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService ordersService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    // Hiển thị danh sách đơn hàng
    @GetMapping
    public String showListOrder(Model model, @RequestParam(defaultValue = "0") int page,
                               @RequestParam(required = false) String keyword,
                               HttpSession session, HttpServletRequest request)
    {
        if (!LoginController.isAuthenticated(session, model)) {
            return "login";
        }

        int pageSize = 6;
        page = Math.max(page, 0);

        Page<Order> orders;
        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");
        boolean validKeword = keyword != null && !keyword.isEmpty() && !keyword.equalsIgnoreCase("null");

        if (validKeword) {

            orders = ordersService.findAllByKeyword(keyword, PageRequest.of(page, pageSize));
        } else {

            orders = ordersService.getAllOrders(PageRequest.of(page, pageSize));
        }

        model.addAttribute("orders", orders.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orders.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("error", model.getAttribute("error"));
        model.addAttribute("currentUser", loggedInUser);

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "order/order :: orderPage";
        }

        return "order/order";
    }


    // Hiển thị chi tiết hoá đơn
    @GetMapping("/detail/{id}")
    public String showDetailForm(@PathVariable int id,
                                 Model model, HttpSession session, HttpServletRequest request)
    {
        if (!LoginController.isAuthenticated(session, model)) {
            return "login";
        }

        Optional<Order> order = ordersService.getOrderById(id);
        Employee employee = (Employee) session.getAttribute("loggedInUser");
        if (order.isPresent()) {
            model.addAttribute("viewMode", true);
            model.addAttribute("editMode", false);
            model.addAttribute("exist", true);
            model.addAttribute("newOrder", order.get());
            model.addAttribute("currentUser", employee);
        } else {
            model.addAttribute("error", "Không tìm thấy hoá đơn.");
        }

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "order/order-form :: orderDetailPage";
        }


        return "order/order-form";
    }

    // Thống kê đơn hàng
    @GetMapping("/statistics")
    public String orderStatistics(Model model, HttpSession session)
    {
        Order loggedInUser = (Order) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "login";
        }

        double totalOrders = ordersService.countTotalOrders();
        double completedOrders = ordersService.countCompletedOrders();
        double processingOrders = ordersService.countProcessingOrders();
        double cancelledOrders = ordersService.countCancelledOrders();
        double totalRevenue = ordersService.calculateTotalRevenue();

        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("completedOrders", completedOrders);
        model.addAttribute("processingOrders", processingOrders);
        model.addAttribute("cancelledOrders", cancelledOrders);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("currentUser", loggedInUser);

        return "order/statistics";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model,
                                 HttpSession session, HttpServletRequest request)
    {
        if (!LoginController.isAuthenticated(session, model)) {
            return "login";
        }

        Employee employee = (Employee) session.getAttribute("loggedInUser");
        Customer customer = new Customer();
        customer.setCustomerID(customerService.generateCustomerID());

        Order newOrder = new Order();
        newOrder.setOrderId(ordersService.generateOrderId());
        newOrder.setCustomer(customer);
        newOrder.setEmployee(employee);

        OrderDetail orderDetail = new OrderDetail(new Product());
        orderDetail.setOrder(newOrder);

        newOrder.setOrderDetails(new ArrayList<OrderDetail>());


        model.addAttribute("viewMode", false);
        model.addAttribute("editMode", true);
        model.addAttribute("exist", false);
        model.addAttribute("newOrder", newOrder);
        model.addAttribute("employee", employee);
        model.addAttribute("currentUser", employee);
        model.addAttribute("error", model.getAttribute("error"));

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "order/order-form :: orderDetailPage";
        }

        return "order/order-form";
    }





    @PostMapping("/new")
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
            System.out.println("========================== productid: " + detail.getProduct().getProductID());
            Product product = productRepository.findByProductID(detail.getProduct().getProductID());
            if (product == null) {
                redirectAttributes.addFlashAttribute("error", "Sản phẩm không tồn tại: " + detail.getProduct().getProductName());
                return "redirect:/orders/new";
            }

            // Kiểm tra tồn kho
            if (product.getQuantity() < detail.getQuantity()) {
                redirectAttributes.addFlashAttribute("error",
                        "Không đủ hàng cho sản phẩm: " + product.getProductName() + ", còn lại: " + product.getQuantity());
                return "redirect:/orders/new";
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

        return "redirect:/orders";
    }


    @PostMapping("/edit/{id}")
    public ResponseEntity<Map<String, Object>> updateOrderStatus(
            @PathVariable Integer id, @RequestBody Order orderData)
    {
        // Gọi service để cập nhật trạng thái đơn hàng
        OrderStatus orderStatus = orderData.getOrderStatus();
        int updatedOrder = ordersService.updateOrderStatus(id, orderStatus);

        // Trả về phản hồi thành công
        return ResponseEntity.ok(Map.of("success", true, "updatedOrder", updatedOrder));
    }

    // Xóa đơn hàng
    @PostMapping("/delete/{id}")
    public String deleteOrder(@PathVariable int id)
    {
        ordersService.deleteOrder(id);
        return "redirect:/orders";
    }



}
