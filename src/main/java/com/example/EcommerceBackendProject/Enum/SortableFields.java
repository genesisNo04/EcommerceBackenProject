package com.example.EcommerceBackendProject.Enum;

import lombok.Getter;

import java.util.Set;

@Getter
public enum SortableFields {
    PRODUCT(Set.of("productName", "price", "stockQuantity", "createdAt")),
    REVIEW(Set.of("rating", "createdAt")),
    ORDER(Set.of("totalAmount", "createdAt")),
    ORDERITEM(Set.of("createdAt")),
    PAYMENT(Set.of("paymentType", "status", "amount", "createdAt")),
    ADDRESS(Set.of("street", "city", "state", "country", "zipCode", "createdAt"));

    private final Set<String> fields;

    SortableFields(Set<String> fields) {
        this.fields = fields;
    }
}
