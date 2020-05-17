package com.shootingplace.shootingplace.controllers;

import com.shootingplace.shootingplace.Repositories.MemberRepository;
import com.shootingplace.shootingplace.domain.Entities.MemberEntity;
import com.shootingplace.shootingplace.domain.Models.Member;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MemberControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private MemberRepository memberRepository;

    private MemberEntity memberEntity;
    private UUID uuid;

    @Before
    public void setUp() {
        MemberEntity memberEntity = MemberEntity.builder()
                .firstName("Janusz")
                .secondName("Piekutowski")
                .email("cośta@mail.com")
                .pesel("22050612398")
                .build();
        memberEntity = memberRepository.saveAndFlush(memberEntity);
        uuid = memberEntity.getUuid();
    }

    @After
    public void tearDown() {
        memberRepository.deleteAll();
    }

    @Test
    public void addMemberTest() {
        Member member = Member.builder()
                .firstName("Janusz")
                .secondName("Piekutowski")
                .email("cośta@mail.com")
                .pesel("22050612398")
                .build();
        ResponseEntity<String> re = testRestTemplate.postForEntity("/member/", member, String.class);
        assertEquals(HttpStatus.OK, re.getStatusCode());
        assertTrue(memberRepository.findById(uuid).isPresent());
    }

    @Test
    public void getMemberListTest() {
        MemberEntity memberEntity = MemberEntity.builder()
                .firstName("Januszex")
                .secondName("Piekutowski")
                .email("cośtam@mail.com")
                .pesel("255555555")
                .build();
        memberRepository.saveAndFlush(memberEntity);
        ResponseEntity<Map<UUID, Member>> re = testRestTemplate
                .exchange("/member/list", HttpMethod.GET, null, new ParameterizedTypeReference<Map<UUID, Member>>() {
                });
        Map<UUID, Member> map = re.getBody();
        assertEquals(HttpStatus.OK, re.getStatusCode());
        assert map != null;
        assertEquals(2, map.size());
    }


    @Test
    public void updateMemberTest() {
        Member updatedMember = Member.builder()
                .firstName("Jasko")
                .build();
        testRestTemplate.put(String.format("/member/%s", uuid), updatedMember);
        System.out.println(uuid);
        memberEntity = memberRepository.findById(uuid).orElseGet(MemberEntity::new);
        assertEquals(updatedMember.getFirstName(), memberEntity.getFirstName());
        Member updatedMember2 = Member.builder()
                .secondName("Pie")
                .build();
        testRestTemplate.put(String.format("/member/%s", uuid), updatedMember2);
        System.out.println(uuid);
        memberEntity = memberRepository.findById(uuid).orElseGet(MemberEntity::new);
        assertEquals(updatedMember2.getSecondName(), memberEntity.getSecondName());

    }

    @Test
    public void addNewMemberAndUpdateHimTest(){
        MemberEntity memberEntity2 = MemberEntity.builder()
                .firstName("Janusz")
                .secondName("Piekutowski")
                .email("cośta@mail.com")
                .pesel("22050612398")
                .build();
        memberEntity2 = memberRepository.saveAndFlush(memberEntity2);
        UUID uuid2 = memberEntity2.getUuid();

        Member updatedMember = Member.builder()
                .firstName("Jasko")
                .build();
        Member updatedMember2 = Member.builder()
                .firstName("DRZWI KURWA")
                .build();
        testRestTemplate.put(String.format("/member/%s", uuid), updatedMember);
        System.out.println(uuid);
        testRestTemplate.put(String.format("/member/%s",uuid2),updatedMember2);
        System.out.println(uuid2);
        memberEntity = memberRepository.findById(uuid).orElseGet(MemberEntity::new);
        memberEntity2 = memberRepository.findById(uuid2).orElseGet(MemberEntity::new);
        assertEquals(updatedMember.getFirstName(),memberEntity.getFirstName());
        assertEquals(updatedMember2.getFirstName(),memberEntity2.getFirstName());



    }

    @Test
    public void deleteMemberTest() {
        testRestTemplate.delete(String.format("/member/%s", uuid));
        assertFalse(memberRepository.findById(uuid).isPresent());
    }


}
