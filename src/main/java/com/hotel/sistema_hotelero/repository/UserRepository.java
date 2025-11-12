package com.hotel.sistema_hotelero.repository;

import com.hotel.sistema_hotelero.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {


    User findByUsername(String username);
}