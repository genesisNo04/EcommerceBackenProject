package com.example.EcommerceBackendProject.Utilities;

import com.example.EcommerceBackendProject.Exception.BadRequestException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class PageableSortValidator {

    public Pageable validate(Pageable pageable, Set<String> allowedFields) {
        if (pageable.getSort().isUnsorted()) {
            return pageable;
        }

        if (pageable.getPageSize() > 100) {
            throw new BadRequestException("Page size exceeds maximum allowed (100)");
        }

        for (Sort.Order order : pageable.getSort()) {
            if (!allowedFields.contains(order.getProperty())) {
                throw new BadRequestException(
                        "Invalid sort field: " + order.getProperty()
                );
            }
        }
        return pageable;
    }
}
