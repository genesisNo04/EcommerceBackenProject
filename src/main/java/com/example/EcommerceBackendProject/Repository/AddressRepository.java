package com.example.EcommerceBackendProject.Repository;

import com.example.EcommerceBackendProject.Entity.Address;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findByUserUsername(String username);

    List<Address> findByUserId(Long userId);

    boolean existsByUserIdAndIsDefaultTrue(Long userId);

    Optional<Address> findByUserIdAndIsDefaultTrue(Long userId);

    //@Modifying: tell JPA that this query modifies data
    //TODO: remember to set transaction in service layer
    @Modifying
    @Query("UPDATE Address a SET a.isDefault = false WHERE a.user.id = :id")
    int resetDefaultForUser(@Param("id") Long id);
}
