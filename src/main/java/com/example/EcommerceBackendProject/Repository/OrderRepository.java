package com.example.EcommerceBackendProject.Repository;

import com.example.EcommerceBackendProject.Entity.Order;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Enum.Status;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser(User user, Pageable pageable);

    List<Order> findByUserId(Long userId , Sort sort);

    //Dont need query because JPA can derived from the name
    //Put the query here just for learning purpose
    //@Query("SELECT o FROM Order o JOIN User u WHERE createdAt BETWEEN :start AND :end AND u.id == :userId")
    List<Order> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Order> findByStatus(Long userId, Status status, Pageable pageable);
}
