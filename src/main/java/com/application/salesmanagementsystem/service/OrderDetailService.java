package com.application.salesmanagementsystem.service;

import com.application.salesmanagementsystem.model.OrderDetail;

import java.util.List;
import java.util.Optional;

public interface OrderDetailService {
    List<OrderDetail> getOrderDetailsByOrderId(Integer orderId);
}
