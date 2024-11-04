package com.application.salesmanagementsystem.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Orders")
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer order_id; // Đổi từ orderID thành order_id

    @ManyToOne
    @JoinColumn(name = "Customer_ID", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "Employee_ID", nullable = false)
    private Employee employee;

    @Temporal(TemporalType.DATE)
    @Column(name = "Order_Date")
    private Date order_date; // Đổi từ orderDate thành order_date

    @Column(name = "Total_Amount")
    private Double total_amount; // Đổi từ totalAmount thành total_amount

    @Enumerated(EnumType.STRING)
    @Column(name = "Order_Status")
    private OrderStatus order_status; // Đổi từ orderStatus thành order_status

    // Getters và Setters

    public Integer getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Integer order_id) {
        this.order_id = order_id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Date getOrder_date() {
        return order_date;
    }

    public void setOrder_date(Date order_date) {
        this.order_date = order_date;
    }

    public Double getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(Double total_amount) {
        this.total_amount = total_amount;
    }

    public OrderStatus getOrder_status() {
        return order_status;
    }

    public void setOrder_status(OrderStatus order_status) {
        this.order_status = order_status;
    }

    // Định nghĩa OrderStatus enum bên trong lớp Orders
    public enum OrderStatus {
        Processing, Completed, Cancelled
    }
}
