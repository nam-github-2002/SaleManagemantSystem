package com.application.salesmanagementsystem.model;
import jakarta.persistence.*;

@Entity
@Table(name = "Customer")
public class Customer {
    @Id
    @Column(name="customer_id",length = 10, unique = true, nullable = false)
    private String customerID;

    @Column(name= "name", nullable = false)
    private String name;

    @Column(name="phone", length = 10)
    private String phone;

    @Column(name="address")
    private String address;

    @Column(name="email",unique = true)
    private String email;

    @Column(name="type")
    private String type;

    @Column(name="total_spend")
    private Double totalSpend = 0.0;

    @Column(name="account", unique = true)
    private String account;

    @Column(name="password")
    private String password;

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getTotalSpend() {
        return totalSpend;
    }

    public void setTotalSpend(Double totalSpend) {
        this.totalSpend = totalSpend;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", name='" + name + '\'' +
                ", customerID='" + customerID + '\'' +
                '}';
    }
}