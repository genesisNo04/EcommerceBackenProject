package com.example.EcommerceBackendProject.Repository;

import com.example.EcommerceBackendProject.Entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
}
