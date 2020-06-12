package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.AddressEntity;
import com.shootingplace.shootingplace.domain.entities.MemberEntity;
import com.shootingplace.shootingplace.domain.models.Address;
import com.shootingplace.shootingplace.repositories.AddressRepository;
import com.shootingplace.shootingplace.repositories.MemberRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.UUID;

@Service
public class AddressService {
    private final AddressRepository addressRepository;
    private final MemberRepository memberRepository;

    public AddressService(AddressRepository addressRepository, MemberRepository memberRepository) {
        this.addressRepository = addressRepository;
        this.memberRepository = memberRepository;
    }


    public void addAddress(UUID memberUUID, Address address) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);

        AddressEntity addressEntity = Mapping.map(address);
        addressRepository.saveAndFlush(addressEntity);
        memberEntity.setAddress(addressEntity);
        memberRepository.saveAndFlush(memberEntity);
        System.out.println("Address został zapisany");
    }

    //--------------------------------------------------------------------------

    public void updateAddress(UUID memberUUID, Address address) {
        try {
            MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
            AddressEntity addressEntity = addressRepository.findById(memberEntity.getAddress().getUuid()).orElseThrow(EntityNotFoundException::new);
            if (addressEntity == Mapping.map(address)) {
                addressRepository.saveAndFlush(addressEntity);
                memberEntity.setAddress(addressEntity);
                memberRepository.saveAndFlush(memberEntity);
            } else {
                if (address.getZipCode() != null) {
                    addressEntity.setZipCode(address.getZipCode());
                    System.out.println("Kod pocztowy");
                }
                if (address.getPostOfficeCity() != null) {
                    addressEntity.setPostOfficeCity(address.getPostOfficeCity());
                    System.out.println("Miasto");
                }
                if (address.getStreet() != null) {
                    addressEntity.setStreet(address.getStreet());
                    System.out.println("Ulica");
                }
                if (address.getStreetNumber() != null) {
                    addressEntity.setStreetNumber(address.getStreetNumber());
                    System.out.println("Numer ulicy");
                }
                if (address.getFlatNumber() != null) {
                    addressEntity.setFlatNumber(address.getFlatNumber());
                    System.out.println("Numer mieszkania");
                }
                addressRepository.saveAndFlush(addressEntity);
                memberEntity.setAddress(addressEntity);
                memberRepository.saveAndFlush(memberEntity);
                System.out.println("Zaktualizowano adres");
            }
        } catch (Exception ex) {
            ex.getMessage();
        }

    }

}
