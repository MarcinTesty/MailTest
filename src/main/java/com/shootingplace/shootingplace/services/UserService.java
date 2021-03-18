package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.UserEntity;
import com.shootingplace.shootingplace.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ResponseEntity<?> createSuperUser(String name, String pinCode) {
        if (name.trim().isEmpty() || pinCode.trim().isEmpty()) {
            ResponseEntity.badRequest().body("Musisz podać jakieś informacje.");
        }
        String trim = name.trim();

        String trim1 = pinCode.trim();
        if (trim1.toCharArray().length < 4) {
            ResponseEntity.status(409).body("Kod jest za krótki. Musi posiadać 4 cyfry.");
        }
        String[] failCode = {"0000", "1111", "2222", "3333", "4444", "5555", "6666", "7777", "8888", "9999"};
        for (int i = 0; i < trim1.toCharArray().length; i++) {
            if (trim1.equals(failCode[i])) {
                return ResponseEntity.status(409).body("Kod jest zbyt prosty - wymyśl coś trudniejszego.");
            }
        }
        boolean anyMatch1 = userRepository.findAll().stream().anyMatch(a -> a.getPinCode().equals(trim1));
        if (anyMatch1) {
            ResponseEntity.status(403).body("Ze względów bezpieczeństwa wymyśl inny kod");
        }

        boolean anyMatch = userRepository.findAll().stream().anyMatch(a -> a.getName().equals(trim));
        if (!anyMatch) {
            UserEntity userEntity = UserEntity.builder()
                    .superUser(true)
                    .name(name)
                    .pinCode(pinCode)
                    .build();
            userRepository.saveAndFlush(userEntity);
            ResponseEntity.ok("Utworzono użytkownika " + userEntity.getName() + ".");
        } else {
            ResponseEntity.status(406).body("Taki użytkownik już istnieje.");
        }
        return null;

    }

    public ResponseEntity<?> createUser(String name, String pinCode, String superPinCode) {

        if (userRepository.findAll().stream().filter(f -> f.getPinCode().equals(superPinCode)).anyMatch(UserEntity::isSuperUser)) {

            if (name.trim().isEmpty() || pinCode.trim().isEmpty()) {
                ResponseEntity.badRequest().body("Musisz podać jakieś informacje.");
            }
            String trim = name.trim();

            String trim1 = pinCode.trim();
            if (trim1.toCharArray().length < 4) {
                ResponseEntity.status(409).body("Kod jest za krótki. Musi posiadać 4 cyfry.");
            }
            String[] failCode = {"0000", "1111", "2222", "3333", "4444", "5555", "6666", "7777", "8888", "9999"};
            for (int i = 0; i < trim1.toCharArray().length; i++) {
                if (trim1.equals(failCode[i])) {
                    return ResponseEntity.status(409).body("Kod jest zbyt prosty - wymyśl coś trudniejszego.");
                }
            }
            boolean anyMatch = userRepository.findAll().stream().anyMatch(a -> a.getName().equals(trim));
            if (!anyMatch) {
                UserEntity userEntity = UserEntity.builder()
                        .superUser(false)
                        .name(name)
                        .pinCode(pinCode)
                        .build();
                userRepository.saveAndFlush(userEntity);
                ResponseEntity.ok("Utworzono użytkownika " + userEntity.getName() + ".");
            } else {
                ResponseEntity.status(406).body("Taki użytkownik już istnieje.");
            }

        }
            return null;
    }
}
