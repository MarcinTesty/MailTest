package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.AddressEntity;
import com.shootingplace.shootingplace.domain.entities.MemberEntity;
import com.shootingplace.shootingplace.domain.models.Address;
import com.shootingplace.shootingplace.repositories.AddressRepository;
import com.shootingplace.shootingplace.repositories.MemberRepository;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityNotFoundException;
import java.util.UUID;

@Service
public class AddressService {
    private final AddressRepository addressRepository;
    private final MemberRepository memberRepository;
    private final Logger LOG = LogManager.getLogger(getClass());

    public AddressService(AddressRepository addressRepository, MemberRepository memberRepository) {
        this.addressRepository = addressRepository;
        this.memberRepository = memberRepository;
    }


    public void addAddress(UUID memberUUID, Address address) {
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

    public void updateAddress(UUID memberUUID, Address address) {
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
                addressEntity.setPostOfficeCity(address.getPostOfficeCity());
                LOG.info("Dodano Miasto");
            }
            if (address.getStreet() != null && !address.getStreet().isEmpty()) {
                addressEntity.setStreet(address.getStreet());
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

    }
}
