package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.CaliberEntity;
import com.shootingplace.shootingplace.domain.entities.MemberEntity;
import com.shootingplace.shootingplace.domain.models.Caliber;
import com.shootingplace.shootingplace.repositories.CaliberRepository;
import com.shootingplace.shootingplace.repositories.MemberRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.*;

@Service
public class CaliberService {

    private final CaliberRepository caliberRepository;
    private final MemberRepository memberRepository;
    private final Logger LOG = LogManager.getLogger();


    public CaliberService(CaliberRepository caliberRepository, MemberRepository memberRepository) {
        this.caliberRepository = caliberRepository;
        this.memberRepository = memberRepository;
    }

    List<CaliberEntity> getCalibersList() {
        if (caliberRepository.findAll().isEmpty()) {
            createAllCalibersEntities();
        }

        LOG.info("Wyświetlono listę kalibrów");
        return caliberRepository.findAll();
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

    void addAmmoUsedByMemberToCaliber(UUID memberUUID, UUID caliberUUID, Integer quantity) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        CaliberEntity caliberEntity = caliberRepository.findById(caliberUUID).orElseThrow(EntityNotFoundException::new);
        caliberEntity.getMembers().add(memberEntity);
        if (caliberEntity.getAmmoUsed() == null) {
            Integer[] integers = new Integer[1];
            integers[0] = quantity;
            caliberEntity.setAmmoUsed(integers);
            caliberRepository.saveAndFlush(caliberEntity);
        } else {
            Integer[] ammoUsed = new Integer[caliberEntity.getAmmoUsed().length + 1];
            for (int i = 0; i < ammoUsed.length - 1; i++) {
                ammoUsed[i] = caliberEntity.getAmmoUsed()[i];
                ammoUsed[i + 1] = quantity;
            }
            caliberEntity.setAmmoUsed(ammoUsed);
            caliberRepository.saveAndFlush(caliberEntity);
            LOG.info("Dodano Amunicje do listy");
        }
    }

    private void addNewCaliber(Caliber caliber) {
        CaliberEntity caliberEntity = Mapping.map(caliber);
        caliberEntity.setMembers(null);
        caliberRepository.saveAndFlush(caliberEntity);
    }

    Map<String, Integer> returnMap(UUID memberUUID, UUID caliberUUID) {
        CaliberEntity caliberEntity = caliberRepository.findById(caliberUUID).orElseThrow(EntityNotFoundException::new);
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        List<MemberEntity> members = caliberEntity.getMembers();
        Integer[] integers = caliberEntity.getAmmoUsed();
        Integer integer = 0;
        for (int i = 0; i < members.size(); i++) {
            if (members.get(i).equals(memberEntity)) {
                integer=integer+integers[i];
                integers[i]=integer;
            }
        }
        Map<String, Integer> map1 = new HashMap<>();
        for (int i = 0; i < members.size(); i++) {

            map1.put(memberEntity.getSecondName().concat(" " + memberEntity.getFirstName()), integer);

        }
        return map1;
    }


}
