package com.application.salesmanagementsystem.controller;

import com.application.salesmanagementsystem.model.Employee;
import com.application.salesmanagementsystem.service.EmployeeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping
public class LoginController {
    @Autowired
    private EmployeeService employeeService;

    // Hiển thị form đăng nhập
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";  // Trả về trang login.html
    }

    // Xử lý đăng nhập
    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, RedirectAttributes redirectAttributes, HttpSession session, Model model) {
        Employee employee = employeeService.findByUsername(username);

        if (employee != null && employeeService.checkPassword(employee, password)) {
            // Lưu thông tin người dùng vào session
            session.setAttribute("loggedInUser", employee);

            // Truyền thông tin người dùng vào model
            model.addAttribute("currentUser", employee);

            // Thêm thông báo thành công vào flash attribute
            redirectAttributes.addFlashAttribute("success", true);
            return "redirect:/";  // Chuyển hướng đến trang chủ
        } else {
            redirectAttributes.addFlashAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng!");
            return "redirect:/login";  // Nếu thất bại, quay lại trang login
        }
    }


    // Xử lý đăng xuất
    @PostMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();  // Xóa toàn bộ thông tin trong session
        redirectAttributes.addFlashAttribute("success", false);
        redirectAttributes.addFlashAttribute("message", "Bạn đã đăng xuất thành công!");
        return "redirect:/login";  // Chuyển hướng đến trang login sau khi đăng xuất
    }

    // Kiểm tra người dùng đã đăng nhập chưa
    public static boolean isAuthenticated(HttpSession session, Model model) {
        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return false;
        }

        Boolean success = (Boolean) model.asMap().get("success");
        if (success != null && !success) {
            // Nếu success == false, đăng xuất và xóa thông tin đăng nhập
            session.invalidate();
            return false;  // Trả về false để chuyển hướng về trang đăng nhập
        }

        return true;
    }
}
