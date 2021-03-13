package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.MemberEntity;
import com.shootingplace.shootingplace.domain.models.MemberDTO;
import com.shootingplace.shootingplace.repositories.MemberRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    private final MemberRepository memberRepository;

    public StatisticsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public String getMaxLegNumber() {
        MemberEntity memberEntity = memberRepository.findAll().stream().max(Comparator.comparing(MemberEntity::getLegitimationNumber)).orElseThrow();
        return "\"" + memberEntity.getLegitimationNumber() + "\"";
    }

    public String getActualYearMemberCounts(){

        int year = LocalDate.now().getYear();

        List<List<MemberDTO>> list = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            int finalI = i;
            List<MemberDTO> collect = memberRepository.findAll().stream()
                    .filter(f -> f.getJoinDate().getYear() == year)
                    .filter(f -> f.getJoinDate().getMonth().getValue() == finalI + 1)
                    .map(Mapping::map2)
                    .sorted(Comparator.comparing(MemberDTO::getJoinDate).thenComparing(MemberDTO::getSecondName).thenComparing(MemberDTO::getFirstName))
                    .collect(Collectors.toList());

            list.add(collect);
        }
        return String.valueOf(list.size());    }
}
