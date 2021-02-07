package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.MemberEntity;
import com.shootingplace.shootingplace.domain.entities.WeaponPermissionEntity;
import com.shootingplace.shootingplace.domain.models.WeaponPermission;
import com.shootingplace.shootingplace.repositories.MemberRepository;
import com.shootingplace.shootingplace.repositories.WeaponPermissionRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
public class WeaponPermissionService {

    private final MemberRepository memberRepository;
    private final WeaponPermissionRepository weaponPermissionRepository;
    private final Logger LOG = LogManager.getLogger(getClass());


    public WeaponPermissionService(MemberRepository memberRepository, WeaponPermissionRepository weaponPermissionRepository) {
        this.memberRepository = memberRepository;
        this.weaponPermissionRepository = weaponPermissionRepository;
    }

    void addWeaponPermission(String memberUUID, WeaponPermission weaponPermission) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        if (memberEntity.getWeaponPermission() != null) {
            LOG.error("nie można już dodać pozwolenia");
        }
        WeaponPermissionEntity weaponPermissionEntity = Mapping.map(weaponPermission);
        weaponPermissionRepository.saveAndFlush(weaponPermissionEntity);
        memberEntity.setWeaponPermission(weaponPermissionEntity);
        memberRepository.saveAndFlush(memberEntity);
        LOG.info("Pozwolenie na broń zostało dodane");
    }

    boolean updateWeaponPermission(String memberUUID, WeaponPermission weaponPermission) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        WeaponPermissionEntity weaponPermissionEntity = memberEntity.getWeaponPermission();
        if (weaponPermission.getNumber() != null) {
            if (weaponPermissionRepository.findByNumber(weaponPermission.getNumber()).isPresent()
                    && !memberEntity.getWeaponPermission().getNumber().equals(weaponPermission.getNumber())) {
                LOG.error("ktoś już ma taki numer pozwolenia");
            } else {
                weaponPermissionEntity.setNumber(weaponPermission.getNumber());
                weaponPermissionEntity.setExist(true);
                LOG.info("Wprowadzono numer pozwolenia");
            }
        }
        if (weaponPermission.getAdmissionToPossessAWeapon() != null) {
            if (weaponPermissionRepository.findByAdmissionToPossessAWeapon(weaponPermission.getAdmissionToPossessAWeapon()).isPresent()
                    && !memberEntity.getWeaponPermission().getAdmissionToPossessAWeapon().equals(weaponPermission.getAdmissionToPossessAWeapon())) {
                LOG.error("ktoś już ma taki numer dopuszczenia");
            } else {
                weaponPermissionEntity.setAdmissionToPossessAWeapon(weaponPermission.getAdmissionToPossessAWeapon());
                weaponPermissionEntity.setAdmissionToPossessAWeaponIsExist(true);
                LOG.info("Wprowadzono numer dopuszczenia");
            }
        }
//        if (weaponPermission.getNumber().equals("0")) && !weaponPermission.getExist()) {
//            weaponPermissionEntity.setNumber(null);
//            weaponPermissionEntity.setExist(false);
//            LOG.info("Usunięto pozwolenie");
//        }
//        if (weaponPermission.getAdmissionToPossessAWeapon().equals("0")) && !weaponPermission.getAdmissionToPossessAWeaponIsExist()) {
//            weaponPermissionEntity.setAdmissionToPossessAWeapon(null);
//            weaponPermissionEntity.setAdmissionToPossessAWeaponIsExist(false);
//            LOG.info("Usunięto dopuszczenie");
//        }
        weaponPermissionRepository.saveAndFlush(weaponPermissionEntity);
        memberEntity.setWeaponPermission(weaponPermissionEntity);
        memberRepository.saveAndFlush(memberEntity);
        LOG.info("Zaktualizowano pozwolenie na broń");
        return true;
    }


}
