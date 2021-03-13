package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.ChangeHistoryEntity;
import com.shootingplace.shootingplace.domain.entities.UserEntity;
import com.shootingplace.shootingplace.repositories.ChangeHistoryRepository;
import com.shootingplace.shootingplace.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChangeHistoryService {

    private final ChangeHistoryRepository changeHistoryRepository;
    private final UserRepository userRepository;


    public ChangeHistoryService(ChangeHistoryRepository changeHistoryRepository, UserRepository userRepository) {
        this.changeHistoryRepository = changeHistoryRepository;
        this.userRepository = userRepository;
    }


    private ChangeHistoryEntity addRecord(UserEntity user, String change, String uuid) {
        return changeHistoryRepository.saveAndFlush(ChangeHistoryEntity.builder()
                .username(user)
                .classNamePlusMethod(change)
                .belongsTo(uuid)
                .dayNow(LocalDate.now())
                .timeNow(String.valueOf(LocalTime.now()))
                .build());
    }

    public boolean comparePinCode(String pinCode) {
        List<UserEntity> collect = userRepository.findAll().stream().filter(f -> f.getPinCode().equals(pinCode)).collect(Collectors.toList());
//        pinCode.equals("5062") || pinCode.equals("6420") || pinCode.equals("0127")
        return !collect.isEmpty();
    }

    void addRecordToChangeHistory(String pinCode, String classNamePlusMethod, String uuid) {

        UserEntity userEntity = userRepository.findAll().stream().filter(f -> f.getPinCode().equals(pinCode)).findFirst().orElse(null);


        if (userEntity != null) {

            userEntity.getList().add(addRecord(userEntity, classNamePlusMethod, uuid));
            userRepository.save(userEntity);
        }
//
//        if (pinCode.equals("5062")) {
//
//            UserEntity userEntity;
//
//            if (userRepository.findAll().stream().noneMatch(f -> f.getName().equals("Aleksandra"))) {
//
//                userEntity = userRepository.saveAndFlush(UserEntity.builder()
//                        .name("Aleksandra")
//                        .changeHistoryEntities(new ArrayList<>())
//                        .build());
//            } else {
//                userEntity = userRepository.findAll().stream().filter(f -> f.getName().equals("Aleksandra")).findFirst().orElse(null);
//
//            }
//            userEntity.getList().add(addRecord(userEntity, classNamePlusMethod, uuid));
//            userRepository.save(userEntity);
//        }
//        if (pinCode.equals("6420")) {
//
//            UserEntity userEntity;
//
//            if (userRepository.findAll().stream().noneMatch(f -> f.getName().equals("Marcin"))) {
//
//                userEntity = userRepository.saveAndFlush(UserEntity.builder()
//                        .name("Marcin")
//                        .changeHistoryEntities(new ArrayList<>())
//                        .build());
//            } else {
//                userEntity = userRepository.findAll().stream().filter(f -> f.getName().equals("Marcin")).findFirst().orElse(null);
//
//            }
//            userEntity.getList().add(addRecord(userEntity, classNamePlusMethod, uuid));
//            userRepository.save(userEntity);
//        }
//        if (pinCode.equals("0127")) {
//
//            UserEntity userEntity;
//
//            if (userRepository.findAll().stream().noneMatch(f -> f.getName().equals("Igor"))) {
//
//                userEntity = userRepository.saveAndFlush(UserEntity.builder()
//                        .name("Igor")
//                        .changeHistoryEntities(new ArrayList<>())
//                        .build());
//            } else {
//                userEntity = userRepository.findAll().stream().filter(f -> f.getName().equals("Igor")).findFirst().orElse(null);
//
//            }
//            userEntity.getList().add(addRecord(userEntity, classNamePlusMethod, uuid));
//            userRepository.save(userEntity);
//        }
    }
}

