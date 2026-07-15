package com.hilton.hotel.repository;

import com.hilton.hotel.domain.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GuestRepository extends JpaRepository<Guest, Long> {

    Optional<Guest> findByKeycloak(String keycloakId);

    Optional<Guest> findByEmail(String email);

    Optional<Guest> findByPhoneNo(String phoneNo);

    boolean existsByEmail(String email);

    boolean existsByPhoneNo(String phoneNo);

    boolean existsByKeycloak(String keycloak);
}