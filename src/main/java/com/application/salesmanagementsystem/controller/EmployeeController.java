package com.application.salesmanagementsystem.controller;

import com.application.salesmanagementsystem.model.Employee;
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

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    // Hiển thị danh sách khách hàng
    @GetMapping
    public String showEmployee(Model model, @RequestParam(defaultValue = "0") int page,
                               @RequestParam(required = false) String keyword,
                               HttpSession session, HttpServletRequest request)
    {
        if (!LoginController.isAuthenticated(session, model)) {
            return "login";
        }
        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");

        int pageSize = 8;
        Page<Employee> employees;
        boolean validKeword = keyword != null && !keyword.isEmpty() && !keyword.equalsIgnoreCase("keyword");

        if (validKeword) {

            employees = employeeService.searchAllField(keyword, PageRequest.of(page, pageSize));
        } else {

            employees = employeeService.getAllEmployees(PageRequest.of(page, pageSize));
        }

        model.addAttribute("employees", employees.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", employees.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("error", model.getAttribute("error"));
        model.addAttribute("currentUser", loggedInUser);


        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "employee/employee :: employeePage";
        }


        return "employee/employee";
    }


    // Hiển thị chi tiết khách hàng
    @GetMapping("/detail/{id}")
    public String showDetailForm(@PathVariable int id, Model model,
                                 HttpSession session, HttpServletRequest request)
    {
        if (!LoginController.isAuthenticated(session, model)) {
            return "login";
        }

        Employee employee = employeeService.getEmployeeById(id);
        if (employee != null) {
            model.addAttribute("viewMode", true);
            model.addAttribute("editMode", false);
            model.addAttribute("exist", true);
            model.addAttribute("newEmployee", employee);
        } else {
            model.addAttribute("error", "Không tìm thấy nhân viên.");
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
            return "login";
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
            return "login";
        }

        Employee employee = employeeService.getEmployeeById(id);
        if (employee != null) {
            model.addAttribute("viewMode", false);
            model.addAttribute("editMode", true);
            model.addAttribute("exist", true);
            model.addAttribute("newEmployee", employee);
        } else {
            model.addAttribute("error", "Không tìm thấy nhân viên.");
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

}