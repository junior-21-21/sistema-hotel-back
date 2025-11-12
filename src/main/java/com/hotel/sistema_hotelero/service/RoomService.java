package com.hotel.sistema_hotelero.service;

import com.hotel.sistema_hotelero.dto.RoomRequest;
import com.hotel.sistema_hotelero.model.Room;
import com.hotel.sistema_hotelero.model.RoomCategory;
import com.hotel.sistema_hotelero.model.RoomStatus;
import com.hotel.sistema_hotelero.repository.RoomCategoryRepository;
import com.hotel.sistema_hotelero.repository.RoomRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private RoomCategoryRepository categoryRepository;

    // Obtener todas las habitaciones (útil para el Front)
    public List<Room> findAllRooms() {
        return roomRepository.findAll();
    }

    public List<Room> getAvailableRooms() {
        return roomRepository.findByStatus(RoomStatus.AVAILABLE);
    }
    // Creación de una nueva habitación
    // Ejemplo de RoomService.java (BACKEND)
    public Room createRoom(RoomRequest request) {
        // 1. Verificar si la categoría existe
        RoomCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada con ID: " + request.getCategoryId()));

        // 2. Crear la nueva habitación con los datos del request
        Room newRoom = new Room();
        newRoom.setRoomNumber(request.getRoomNumber());
        newRoom.setCategory(category);
        // 3. Asignar el estado del request, si existe, de lo contrario usará el valor por defecto
        newRoom.setStatus(request.getStatus());

        // 4. Guardar la habitación y devolverla
        return roomRepository.save(newRoom);
    }
    public Room updateRoomStatus(Long roomId, RoomStatus newStatus) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Habitación no encontrada con ID: " + roomId));

        room.setStatus(newStatus);
        return roomRepository.save(room);
    }
    public Room updateRoom(Long id, RoomRequest request) {
        // 1. Verificar que la habitación exista
        Room existingRoom = roomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Habitación no encontrada con ID: " + id));

        // 2. Verificar que la categoría exista
        RoomCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + request.getCategoryId()));

        // 3. Actualizar los campos
        existingRoom.setRoomNumber(request.getRoomNumber());
        existingRoom.setStatus(request.getStatus());
        existingRoom.setCategory(category);

        // 4. Guardar y devolver la entidad actualizada
        return roomRepository.save(existingRoom);
    }
    // Eliminación de una habitación (implementar con manejo de excepciones si está ocupada/reservada)
    public void deleteRoom(Long id) {
        // Lógica de seguridad: quizás no permitir si el estado no es AVAILABLE o MAINTENANCE.
        roomRepository.deleteById(id);
    }
}