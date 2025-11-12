package com.hotel.sistema_hotelero.service;

import com.hotel.sistema_hotelero.model.RoomCategory;
import com.hotel.sistema_hotelero.repository.RoomCategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomCategoryService {

    @Autowired
    private RoomCategoryRepository categoryRepository;

    // 1. Crear una nueva categoría
    public RoomCategory createCategory(RoomCategory category) {
        return categoryRepository.save(category);
    }

    // 2. Obtener todas las categorías
    public List<RoomCategory> findAllCategories() {
        return categoryRepository.findAll();
    }

    // 3. Obtener una categoría por ID
    public RoomCategory findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada con ID: " + id));
    }

    // 4. Actualizar una categoría
    public RoomCategory updateCategory(Long id, RoomCategory categoryDetails) {
        RoomCategory category = findCategoryById(id);

        category.setName(categoryDetails.getName());
        category.setBasePrice(categoryDetails.getBasePrice());
        category.setDescription(categoryDetails.getDescription());

        return categoryRepository.save(category);
    }

    // 5. Eliminar una categoría
    public void deleteCategory(Long id) {
        // Nota de Negocio: En un sistema real, antes de eliminar, deberías
        // verificar que NO haya habitaciones (`Room`) asociadas a esta categoría.
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Categoría no encontrada con ID: " + id);
        }
        categoryRepository.deleteById(id);
    }
}