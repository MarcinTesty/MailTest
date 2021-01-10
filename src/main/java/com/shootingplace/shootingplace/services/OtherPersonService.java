package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.ClubEntity;
import com.shootingplace.shootingplace.domain.entities.MemberPermissionsEntity;
import com.shootingplace.shootingplace.domain.entities.OtherPersonEntity;
import com.shootingplace.shootingplace.domain.models.MemberPermissions;
import com.shootingplace.shootingplace.domain.models.OtherPerson;
import com.shootingplace.shootingplace.repositories.ClubRepository;
import com.shootingplace.shootingplace.repositories.MemberPermissionsRepository;
import com.shootingplace.shootingplace.repositories.OtherPersonRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class OtherPersonService {

    private final ClubRepository clubRepository;
    private final OtherPersonRepository otherPersonRepository;
    private final MemberPermissionsRepository memberPermissionsRepository;

    public OtherPersonService(ClubRepository clubRepository, OtherPersonRepository otherPersonRepository, MemberPermissionsRepository memberPermissionsRepository) {
        this.clubRepository = clubRepository;
        this.otherPersonRepository = otherPersonRepository;
        this.memberPermissionsRepository = memberPermissionsRepository;
    }

    public boolean addPerson(String club, OtherPerson person, MemberPermissions permissions) {

        MemberPermissionsEntity permissionsEntity = Mapping.map(permissions);

        boolean match = clubRepository.findAll().stream().anyMatch(a -> a.getName().equals(club));

        ClubEntity clubEntity;
        if (match) {
            clubEntity = clubRepository
                    .findAll()
                    .stream()
                    .filter(f -> f.getName()
                            .equals(club))
                    .findFirst()
                    .orElseThrow(EntityNotFoundException::new);
        } else {
            List<ClubEntity> all = clubRepository.findAll();
            all.sort(Comparator.comparing(ClubEntity::getId));
            Integer id = (all.get(0).getId()) + 1;
            clubEntity = ClubEntity.builder()
                    .id(id)
                    .name(club.toUpperCase()).build();
            clubRepository.saveAndFlush(clubEntity);
        }
        List<OtherPersonEntity> all = otherPersonRepository.findAll();
        int id;
        if (all.isEmpty()) {
            id = 1;
        } else {
            all.sort(Comparator.comparing(OtherPersonEntity::getId).reversed());
            id = (all.get(0).getId()) + 1;
        }
        memberPermissionsRepository.saveAndFlush(permissionsEntity);
        OtherPersonEntity otherPersonEntity = OtherPersonEntity.builder()
                .firstName(person.getFirstName())
                .secondName(person.getSecondName())
                .id(id)
                .permissionsEntity(permissionsEntity)
                .club(clubEntity).build();
        otherPersonRepository.saveAndFlush(otherPersonEntity);
        return true;

    }

    public List<String> getAllOthers() {

        List<String> list = new ArrayList<>();
        otherPersonRepository.findAll()
                .forEach(e -> list.add(e.getSecondName().concat(" " + e.getFirstName() + " Klub: " + e.getClub().getName() + " id: " + e.getId())));
        list.sort(Comparator.comparing(String::new));
        return list;
    }

    public List<String> getAllOthersArbiters() {

        List<String> list = new ArrayList<>();
        otherPersonRepository.findAll().stream().filter(f->f.getPermissionsEntity().getArbiterNumber()!=null)
                .forEach(e->list.add(e.getSecondName().concat(" " + e.getFirstName() + " Klub " + e.getClub().getName() + " Klasa " + e.getPermissionsEntity().getArbiterClass() + " id: " + e.getId())));
        list.sort(Comparator.comparing(String::new));
        return list;
    }

    public List<OtherPersonEntity> getAll() {
        return otherPersonRepository.findAll();
    }
}