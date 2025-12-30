package com.ecommerce.orderservice.validator;

import com.ecommerce.orderservice.model.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderValidatorImpl implements OrderValidator {
    @Override
    public boolean validate(Object order) {
        if (!(order instanceof Order)) {
            return false;
        }
        Order o = (Order) order;
        return o.getId() != null && o.getCustomerId() != null &&
                o.getItems() != null && !o.getItems().isEmpty();
    }
}
