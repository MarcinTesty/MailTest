package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.AddressEntity;
import com.shootingplace.shootingplace.domain.entities.MemberEntity;
import com.shootingplace.shootingplace.domain.models.Address;
import com.shootingplace.shootingplace.repositories.AddressRepository;
import com.shootingplace.shootingplace.repositories.MemberRepository;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
public class AddressService {
    private final AddressRepository addressRepository;
    private final MemberRepository memberRepository;
    private final Logger LOG = LogManager.getLogger(getClass());

    public AddressService(AddressRepository addressRepository, MemberRepository memberRepository) {
        this.addressRepository = addressRepository;
        this.memberRepository = memberRepository;
    }

    @SneakyThrows
    public boolean updateAddress(String memberUUID, Address address) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        AddressEntity addressEntity = memberEntity.getAddress();
        if (address.getZipCode() != null && !address.getZipCode().isEmpty()) {
            addressEntity.setZipCode(address.getZipCode());
            LOG.info("Dodano Kod pocztowy : {[]}",address);
        }
        if (address.getPostOfficeCity() != null && !address.getPostOfficeCity().isEmpty()) {
            String[] s1 = address.getPostOfficeCity().split(" ");
            StringBuilder postOfficeCity = new StringBuilder();
            for (String value : s1) {
                String splinted = value.substring(0, 1).toUpperCase() + value.substring(1).toLowerCase() + " ";
                postOfficeCity.append(splinted);
            }
            addressEntity.setPostOfficeCity(postOfficeCity.toString());
            LOG.info("Dodano Miasto");
        }
        if (address.getStreet() != null && !address.getStreet().isEmpty()) {
            String[] s1 = address.getStreet().split(" ");
            StringBuilder street = new StringBuilder();
            for (String value : s1) {
                String splinted = value.substring(0, 1).toUpperCase() + value.substring(1).toLowerCase() + " ";
                street.append(splinted);
            }
            addressEntity.setStreet(street.toString());
            LOG.info("Dodano Ulica");
        }
        if (address.getStreetNumber() != null && !address.getStreetNumber().isEmpty()) {
            addressEntity.setStreetNumber(address.getStreetNumber().toUpperCase());
            LOG.info("Dodano Numer ulicy");
        }
        if (address.getFlatNumber() != null && !address.getFlatNumber().isEmpty()) {
            addressEntity.setFlatNumber(address.getFlatNumber().toUpperCase());
            LOG.info("Dodano Numer mieszkania");
        }
        addressRepository.saveAndFlush(addressEntity);
        LOG.info("Zaktualizowano adres");
        return true;
    }
}
