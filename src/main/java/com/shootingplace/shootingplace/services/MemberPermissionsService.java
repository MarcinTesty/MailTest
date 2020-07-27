package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.repositories.MemberPermissionsRepository;
import org.springframework.stereotype.Service;

@Service
public class MemberPermissionsService {

    private final MemberPermissionsRepository memberPermissionsRepository;

    public MemberPermissionsService(MemberPermissionsRepository memberPermissionsRepository) {
        this.memberPermissionsRepository = memberPermissionsRepository;
    }
}
