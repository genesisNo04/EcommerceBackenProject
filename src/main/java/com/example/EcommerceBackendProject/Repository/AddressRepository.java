package com.example.EcommerceBackendProject.Repository;

import com.example.EcommerceBackendProject.Entity.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    Page<Address> findByUserId(Long userId, Pageable pageable);

    boolean existsByUserIdAndIsDefaultTrue(Long userId);

    Optional<Address> findByUserIdAndIsDefaultTrue(Long userId);

    Optional<Address> findByUserIdAndId(Long userId, Long addressId);

    @Modifying
    @Query("UPDATE Address a SET a.isDefault = false WHERE a.user.id = :userId")
    void resetDefaultForUser(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE Address a SET a.isDefault = true WHERE a.user.id = :userId and a.id = :addressId")
    void updateDefaultForUser(@Param("userId") Long userId, @Param("addressId") Long addressId);

    Optional<Address> findFirstByUserIdOrderByCreatedAtAsc(Long userId);

}
