package com.example.EcommerceBackendProject.Repository;

import com.example.EcommerceBackendProject.Entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>, JpaSpecificationExecutor<Review> {

    boolean existsByUserIdAndProductId(Long userId, Long productId);
}
