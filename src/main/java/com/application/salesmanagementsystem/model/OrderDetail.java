package com.application.salesmanagementsystem.model;

import jakarta.persistence.*;

@Entity
@Table(name = "OrderDetail")
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderDetailId;

    @ManyToOne
    @JoinColumn(name = "OrderID", nullable = false)
    private Orders orderId;

    @ManyToOne
    @JoinColumn(name = "ProductID", nullable = false)
    private Product product;

    private Short quantity;

    private Double unitPrice;

    // Getters và Setters

    public Orders getOrder() {
        return orderId;
    }

    public void setOrder(Orders order) {
        this.orderId = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Short getQuantity() {
        return quantity;
    }

    public void setQuantity(Short quantity) {
        this.quantity = quantity;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getOrderdetailid() {
        return orderDetailId;
    }

    public void setOrderdetailid(int orderdetailid) {
        this.orderDetailId = orderdetailid;
    }
}