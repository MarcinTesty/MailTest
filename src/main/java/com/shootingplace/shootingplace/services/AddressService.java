package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.AddressEntity;
import com.shootingplace.shootingplace.domain.entities.MemberEntity;
import com.shootingplace.shootingplace.domain.models.Address;
import com.shootingplace.shootingplace.repositories.AddressRepository;
import com.shootingplace.shootingplace.repositories.MemberRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.UUID;

@Service
public class AddressService {
    private final AddressRepository addressRepository;
    private final MemberRepository memberRepository;

    public AddressService(AddressRepository addressRepository, MemberRepository memberRepository) {
        this.addressRepository = addressRepository;
        this.memberRepository = memberRepository;
    }

    public boolean addAddress(UUID memberuuid, @Valid Address address) {
        MemberEntity memberEntity = memberRepository.findById(memberuuid).orElseThrow(EntityNotFoundException::new);
        AddressEntity addressEntity = map(address);
        addressRepository.saveAndFlush(addressEntity);
        memberEntity.setAddress(map(address));
        memberRepository.saveAndFlush(memberEntity);

        return false;
    }

    public boolean updateAddress(UUID uuid, Address address) {
        try{

        }catch (Exception ex){return false;}
        return true;
    }

//    public boolean updateCarMileage(UUID carUUID, Car car) {
//        try {
//            CarEntity updateCarEntity = carRepository
//                    .findById(carUUID)
//                    .orElseThrow(EntityNotFoundException::new);
//            if (car.getMileage() != null) {
//                car.setMileage(car.getMileage());
//            }
//            carRepository.save(updateCarEntity);
//            return true;
//        } catch (EntityNotFoundException ex) {
//            return false;
//        }
//    }

    //--------------------------------------------------------------------------
    //Mapping
    private Address map(AddressEntity a) {
        return Address.builder()
                .zipCode(a.getZipCode())
                .postOfficeCity(a.getPostOfficeCity())
                .street(a.getStreet())
                .streetNumber(a.getStreetNumber())
                .flatNumber(a.getFlatNumber())
                .build();
    }

    private AddressEntity map(Address a) {
        return AddressEntity.builder()
                .zipCode(a.getZipCode())
                .postOfficeCity(a.getPostOfficeCity())
                .street(a.getStreet())
                .streetNumber(a.getStreetNumber())
                .flatNumber(a.getFlatNumber())
                .build();
    }

}
