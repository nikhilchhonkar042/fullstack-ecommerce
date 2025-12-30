package com.ecommerce.orderservice.result;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ValidationResult {
    private boolean valid;
    private List<String> errors = new ArrayList<>();

    public static ValidationResult success() {
        ValidationResult r = new ValidationResult();
        r.valid = true;
        return r;
    }

    public static ValidationResult failure(List<String> errors) {
        ValidationResult r = new ValidationResult();
        r.valid = false;
        r.errors = errors;
        return r;
    }
}
