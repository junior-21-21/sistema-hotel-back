package com.hotel.sistema_hotelero.controller;

import com.hotel.sistema_hotelero.dto.EmployeeCreationRequest;
import com.hotel.sistema_hotelero.model.User;
import com.hotel.sistema_hotelero.service.AuthService;
import com.hotel.sistema_hotelero.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private AuthService authService;
    @Autowired
    private UserService userService;
    /**
     * Endpoint de prueba para verificar que solo los usuarios con el rol ADMIN pueden acceder.
     * * @PreAuthorize("hasAuthority('ADMIN')"): Esta anotación es la clave. Spring Security
     * verifica que el Rol (Authority) del usuario logueado coincida con 'ADMIN'.
     */
    @GetMapping("/test-acceso")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> testAdminAccess() {
        return ResponseEntity.ok("Acceso concedido. Eres un Administrador.");
    }

    // Aquí irían otros endpoints sensibles, como:
    // @PostMapping("/users") para crear nuevos empleados
    // @DeleteMapping("/reports") para borrar reportes financieros
    @PostMapping("/employees")
    @PreAuthorize("hasAuthority('ADMIN')") // ¡Doble seguridad! Spring Security verifica el rol
    public ResponseEntity<?> createEmployee(@RequestBody EmployeeCreationRequest request) {
        try {
            authService.createEmployee(request);
            return ResponseEntity.ok("Empleado " + request.getUsername() + " creado exitosamente con rol: " + request.getRole());
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Maneja errores como "Username ya existe" o "Intento de crear un ADMIN"
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno al crear el empleado.");
        }
    }

    // Listar empleados
    @GetMapping("/employees")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<User>> getAllEmployees() {
        return ResponseEntity.ok(userService.findAllEmployees());
    }

    @PutMapping("/employees/{id}/password")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> changePassword(@PathVariable Long id, @RequestBody String newPassword) {
        userService.changePassword(id, newPassword);
        return ResponseEntity.ok("Contraseña actualizada correctamente");
    }

    // Eliminar empleado
    @DeleteMapping("/employees/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        userService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

}