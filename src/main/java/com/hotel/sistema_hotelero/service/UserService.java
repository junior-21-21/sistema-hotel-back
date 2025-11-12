package com.hotel.sistema_hotelero.service;

import com.hotel.sistema_hotelero.model.User;
import com.hotel.sistema_hotelero.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Busca un usuario por su nombre de usuario.
     * Es crucial para obtener la entidad completa del usuario logueado.
     */
    public Optional<User> findByUsername(String username) {
        // Asumiendo que UserRepository ya tiene el método findByUsername
        return Optional.ofNullable(userRepository.findByUsername(username));
    }
    public List<User> findAllEmployees() {
        return userRepository.findAll();
    }

    public void changePassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    public void deleteEmployee(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Empleado no encontrado");
        }
        userRepository.deleteById(userId);
    }


}