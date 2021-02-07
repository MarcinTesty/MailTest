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
    private final FilesService filesService;
    private final Logger LOG = LogManager.getLogger(getClass());

    public AddressService(AddressRepository addressRepository, MemberRepository memberRepository, FilesService filesService) {
        this.addressRepository = addressRepository;
        this.memberRepository = memberRepository;
        this.filesService = filesService;
    }


    void addAddress(String memberUUID, Address address) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        if (memberEntity.getAddress() != null) {
            LOG.error("nie można już dodać adresu");
        }
        AddressEntity addressEntity = Mapping.map(address);
        addressRepository.saveAndFlush(addressEntity);
        memberEntity.setAddress(addressEntity);
        memberRepository.saveAndFlush(memberEntity);
        LOG.info("Adres został zapisany");
    }

    //--------------------------------------------------------------------------

    @SneakyThrows
    public boolean updateAddress(String memberUUID, Address address) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        AddressEntity addressEntity = addressRepository.findById(memberEntity
                .getAddress()
                .getUuid())
                .orElseThrow(EntityNotFoundException::new);
        if (address.getZipCode() != null && !address.getZipCode().isEmpty()) {
            addressEntity.setZipCode(address.getZipCode());
            LOG.info("Dodano Kod pocztowy");
        }
        if (address.getPostOfficeCity() != null && !address.getPostOfficeCity().isEmpty()) {
            addressEntity.setPostOfficeCity(address.getPostOfficeCity().substring(0, 1).toUpperCase() + address.getPostOfficeCity().substring(1).toLowerCase());
            LOG.info("Dodano Miasto");
        }
        if (address.getStreet() != null && !address.getStreet().isEmpty()) {
            addressEntity.setStreet(address.getStreet().substring(0, 1).toUpperCase() + address.getStreet().substring(1).toLowerCase());
            LOG.info("Dodano Ulica");
        }
        if (address.getStreetNumber() != null && !address.getStreetNumber().isEmpty()) {
            addressEntity.setStreetNumber(address.getStreetNumber());
            LOG.info("Dodano Numer ulicy");
        }
        if (address.getFlatNumber() != null && !address.getFlatNumber().isEmpty()) {
            addressEntity.setFlatNumber(address.getFlatNumber());
            LOG.info("Dodano Numer mieszkania");
        }
        addressRepository.saveAndFlush(addressEntity);
        memberEntity.setAddress(addressEntity);
        memberRepository.saveAndFlush(memberEntity);
        LOG.info("Zaktualizowano adres");
        filesService.personalCardFile(memberEntity.getUuid());
        return true;
    }
}
