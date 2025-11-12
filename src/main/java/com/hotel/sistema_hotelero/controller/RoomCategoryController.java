package com.hotel.sistema_hotelero.controller;

import com.hotel.sistema_hotelero.model.RoomCategory;
import com.hotel.sistema_hotelero.service.RoomCategoryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class RoomCategoryController {

    @Autowired
    private RoomCategoryService categoryService;

    // 1. Obtener todas: Accesible para todos los empleados que trabajen con inventario
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'RECEPTIONIST')")
    public ResponseEntity<List<RoomCategory>> getAllCategories() {
        return ResponseEntity.ok(categoryService.findAllCategories());
    }

    // 2. Obtener por ID: Accesible para todos los empleados
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'RECEPTIONIST')")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(categoryService.findCategoryById(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 3. Crear: Solo ADMIN y MANAGER pueden modificar el inventario y precios
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public ResponseEntity<RoomCategory> createCategory(@RequestBody RoomCategory category) {
        RoomCategory newCategory = categoryService.createCategory(category);
        return ResponseEntity.status(201).body(newCategory);
    }

    // 4. Actualizar: Solo ADMIN y MANAGER
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody RoomCategory categoryDetails) {
        try {
            RoomCategory updatedCategory = categoryService.updateCategory(id, categoryDetails);
            return ResponseEntity.ok(updatedCategory);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 5. Eliminar: Solo ADMIN (acción más destructiva)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}