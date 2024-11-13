package com.application.salesmanagementsystem.controller;

import com.application.salesmanagementsystem.model.Employee;
import com.application.salesmanagementsystem.model.Image;
import com.application.salesmanagementsystem.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    private static final String TEMP_DIR = "src/main/resources/static/images/employees/";

    // Hiển thị danh sách khách hàng
    @GetMapping
    public String showEmployee(Model model, @RequestParam(defaultValue = "0") int page,
                               HttpSession session, HttpServletRequest request)
    {
        if (!LoginController.isAuthenticated(session, model)) {
            return "redirect:/login";
        }

        int pageSize = 8;
        Page<Employee> employees;

        if (model.containsAttribute("employees")) {

            employees = (Page<Employee>) model.getAttribute("employees");;
        } else {

            employees = employeeService.getAllEmployees(PageRequest.of(page, pageSize));
        }

        model.addAttribute("employees", employees.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", employees.getTotalPages());

        if (model.containsAttribute("error")) {
            model.addAttribute("error", model.getAttribute("error"));
        } else {
            model.addAttribute("error", false);
        }

        if (!model.containsAttribute("keyword")) {
            model.addAttribute("keyword", null);
        }

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "employee/employee :: employeePage";
        }

        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");
        model.addAttribute("currentUser", loggedInUser);

        return "employee/employee";
    }


    // Hiển thị chi tiết khách hàng
    @GetMapping("/detail/{id}")
    public String showDetailForm(@PathVariable int id, Model model,
                                 HttpSession session, HttpServletRequest request)
    {
        if (!LoginController.isAuthenticated(session, model)) {
            return "redirect:/login";
        }

        Employee employee = employeeService.getEmployeeById(id);
        if (employee != null) {
            model.addAttribute("viewMode", true);
            model.addAttribute("editMode", false);
            model.addAttribute("exist", true);
            model.addAttribute("newEmployee", employee);
        } else {
            model.addAttribute("error", "Không tìm thấy khách hàng.");
        }

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "employee/employee-form :: employeeDetailPage";
        }

        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");
        model.addAttribute("currentUser", loggedInUser);
        return "employee/employee-form";
    }

    // Hiển thị form tạo mới khách hàng
    @GetMapping("/new")
    public String showCreateForm(Model model,
                                 HttpSession session, HttpServletRequest request)
    {
        if (!LoginController.isAuthenticated(session, model)) {
            return "redirect:/login";
        }

        int newId = employeeService.generateEmployeeId();
        Employee newEmployee = new Employee();
        newEmployee.setEmployeeId(newId);

        model.addAttribute("viewMode", false);
        model.addAttribute("editMode", true);
        model.addAttribute("exist", false);
        model.addAttribute("newEmployee", newEmployee);

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "employee/employee-form :: employeeDetailPage";
        }

        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");
        model.addAttribute("currentUser", loggedInUser);
        return "employee/employee-form";
    }

    // Hiển thị form chỉnh sửa khách hàng
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable int id, Model model,
                               HttpSession session, HttpServletRequest request)
    {
        if (!LoginController.isAuthenticated(session, model)) {
            return "redirect:/login";
        }

        Employee employee = employeeService.getEmployeeById(id);
        if (employee != null) {
            model.addAttribute("viewMode", false);
            model.addAttribute("editMode", true);
            model.addAttribute("exist", true);
            model.addAttribute("newEmployee", employee);
        } else {
            model.addAttribute("error", "Không tìm thấy khách hàng.");
        }

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "employee/employee-form :: employeeDetailPage";
        }

        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");
        model.addAttribute("currentUser", loggedInUser);
        return "employee/employee-form";
    }

    @GetMapping("/display")
    public ResponseEntity<byte[]> displayImage(@RequestParam("id") int id) throws IOException, SQLException
    {
        Employee employee = employeeService.getEmployeeById(id);
        byte [] imageBytes = null;
        imageBytes = employee.getImage().getBytes(1,(int) employee.getImage().length());
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
    }




    // Tạo mới khách hàng
    @PostMapping("/new")
    public String createEmployee(@ModelAttribute("newEmployee") Employee newEmployee,
                                 BindingResult result,
                                 @RequestParam("image") MultipartFile image) throws SQLException, IOException
    {
        if (result.hasErrors()) {
            System.out.println("-----------------" + result.getAllErrors() + "-----------------------");
        }

         if(image != null) {
            newEmployee.setImage(null);
            byte[] bytes = image.getBytes();
            Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);
            newEmployee.setImage(blob);
        }

        newEmployee.setUsername(newEmployee.getName().replaceAll("\\s+", "").toLowerCase() + newEmployee.getEmployeeId());
        newEmployee.setPassword("123");
        employeeService.saveEmployee(newEmployee);
        return "redirect:/employees/detail/" + newEmployee.getEmployeeId();
    }


    // Cập nhật thông tin khách hàng
    @PostMapping("/edit/{id}")
    public String updateEmployee(@ModelAttribute("newEmployee") Employee newEmployee,
                                 BindingResult result,
                                 @RequestParam("image") MultipartFile image) throws SQLException, IOException
    {
        if (result.hasErrors()) {
            System.out.println("-----------------" + result.getAllErrors() + "-----------------------");
        }

        if(!image.isEmpty()) {
            newEmployee.setImage(null);
            byte[] bytes = image.getBytes();
            Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);
            newEmployee.setImage(blob);
        }

        employeeService.saveEmployee(newEmployee);
        return "redirect:/employees/detail/" + newEmployee.getEmployeeId();
    }

    // Xóa khách hàng
    @PostMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable int id) {
        employeeService.deleteEmployee(id);
        return "redirect:/employees";
    }

    // Tìm kiếm khách hàng
    @PostMapping("/search")
    public String searchEmployees(@RequestParam("keyword") String keyword, @RequestParam(defaultValue = "0") int page, RedirectAttributes redirectAttributes) {
        int pageSize = 8;
        Page<Employee> searchResults = employeeService.searchAllField(keyword, PageRequest.of(page, pageSize));

        if (searchResults.isEmpty()) {

            redirectAttributes.addFlashAttribute("error", "Không tìm thấy khách hàng nào với từ khóa '" + keyword + "'.");
        } else {

            redirectAttributes.addFlashAttribute("employees", searchResults);
        }

        redirectAttributes.addFlashAttribute("keyword", keyword);
        return "redirect:/employees";
    }
}