package com.hotel.sistema_hotelero.config;

import com.hotel.sistema_hotelero.model.*;
import com.hotel.sistema_hotelero.repository.RoomCategoryRepository;
import com.hotel.sistema_hotelero.repository.RoomRepository;
import com.hotel.sistema_hotelero.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final RoomCategoryRepository roomCategoryRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            UserRepository userRepository,
            RoomRepository roomRepository,
            RoomCategoryRepository roomCategoryRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.roomCategoryRepository = roomCategoryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Solo ejecutar si no hay datos para evitar duplicados
        if (userRepository.count() == 0 && roomCategoryRepository.count() == 0) {
            System.out.println("No initial data found. Populating database with test data...");

            // 1. Crear Categorías de Habitación
            RoomCategory simple = new RoomCategory();
            simple.setName("Simple");
            simple.setBasePrice(50.00);
            simple.setDescription("Una habitación acogedora con una cama individual, ideal para viajeros solos.");

            RoomCategory doble = new RoomCategory();
            doble.setName("Doble");
            doble.setBasePrice(85.00);
            doble.setDescription("Una habitación espaciosa con una cama doble o dos camas individuales.");

            RoomCategory suite = new RoomCategory();
            suite.setName("Suite");
            suite.setBasePrice(150.00);
            suite.setDescription("Nuestra mejor habitación, con una cama king-size, sala de estar y vistas panorámicas.");

            roomCategoryRepository.saveAll(Arrays.asList(simple, doble, suite));

            // 2. Crear Habitaciones
            createRoom("101", simple);
            createRoom("102", simple);
            createRoom("201", doble);
            createRoom("202", doble);
            createRoom("301", suite);

            // 3. Crear Usuarios
            User admin = new User();
            admin.setUsername("admin");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);

            User receptionist = new User();
            receptionist.setUsername("recepcion");
            receptionist.setPasswordHash(passwordEncoder.encode("recep123"));
            receptionist.setRole(Role.RECEPTIONIST);
            userRepository.save(receptionist);

            System.out.println("Database has been populated with test data.");
        } else {
            System.out.println("Database already contains data. Skipping data initialization.");
        }
    }

    private void createRoom(String number, RoomCategory category) {
        Room room = new Room();
        room.setRoomNumber(number);
        room.setCategory(category);
        room.setStatus(RoomStatus.AVAILABLE);
        roomRepository.save(room);
    }
}
