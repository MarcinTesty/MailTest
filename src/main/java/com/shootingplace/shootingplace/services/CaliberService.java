package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.CaliberEntity;
import com.shootingplace.shootingplace.repositories.CaliberRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class CaliberService {

    private final CaliberRepository caliberRepository;
    private final Logger LOG = LogManager.getLogger();


    public CaliberService(CaliberRepository caliberRepository) {
        this.caliberRepository = caliberRepository;
    }

    List<CaliberEntity> getCalibersList() {
        List<CaliberEntity> caliberEntityList = caliberRepository.findAll();
        if (caliberEntityList.isEmpty()) {
            createAllCalibersEntities();
        }
        caliberEntityList.sort(Comparator.comparing(CaliberEntity::getName));
        LOG.info("Wyświetlono listę kalibrów");
        return caliberEntityList;
    }

    private void createAllCalibersEntities() {

        CaliberEntity caliberEntity = CaliberEntity.builder()
                .name("5,6mm")
                .build();
        caliberRepository.saveAndFlush(caliberEntity);
        CaliberEntity caliberEntity1 = CaliberEntity.builder()
                .name("9x19mm")
                .build();
        caliberRepository.saveAndFlush(caliberEntity1);
        CaliberEntity caliberEntity2 = CaliberEntity.builder()
                .name("7,62x39mm")
                .build();
        caliberRepository.saveAndFlush(caliberEntity2);
        CaliberEntity caliberEntity3 = CaliberEntity.builder()
                .name(".38")
                .build();
        caliberRepository.saveAndFlush(caliberEntity3);
        CaliberEntity caliberEntity4 = CaliberEntity.builder()
                .name(".357")
                .build();
        caliberRepository.saveAndFlush(caliberEntity4);
        CaliberEntity caliberEntity5 = CaliberEntity.builder()
                .name("12/70")
                .build();
        caliberRepository.saveAndFlush(caliberEntity5);
    }

    public boolean createNewCaliber(String caliber) {

        boolean match = caliberRepository.findAll().stream().anyMatch(a -> a.getName().equals(caliber.trim().toLowerCase()));
        if (!match) {
            CaliberEntity caliberEntity = CaliberEntity.builder()
                    .name(caliber.trim().toLowerCase())
                    .build();
            caliberRepository.saveAndFlush(caliberEntity);
            return true;
        } else {
            return false;
        }
    }
}
