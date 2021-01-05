package com.shootingplace.shootingplace.domain.entities;

import com.shootingplace.shootingplace.validators.ValidPESEL;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.UUID;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID uuid;

    private LocalDate joinDate;
    private Integer legitimationNumber;
    @NotBlank
    private String firstName;
    @NotBlank
    private String secondName;
    @OneToOne(orphanRemoval = true)
    private LicenseEntity license;
    @OneToOne(orphanRemoval = true)
    private ShootingPatentEntity shootingPatent;
    @Email
    private String email = "";
    @NotBlank
    @ValidPESEL
    @Pattern(regexp = "[0-9]*")
    private String pesel;
    @NotBlank
    private String IDCard;
    @OneToOne
    private ClubEntity club;

    @OneToOne
    private AddressEntity address;
    @NotBlank
    @Pattern(regexp = "^\\+[0-9]{11}$")
    private String phoneNumber;
    @OneToOne(orphanRemoval = true)
    private WeaponPermissionEntity weaponPermission;

    private Boolean active = true;
    private Boolean adult = true;
    private Boolean erased = false;
    private String erasedReason;

    @OneToOne(orphanRemoval = true)
    private HistoryEntity history;

    @OneToOne(orphanRemoval = true)
    private MemberPermissionsEntity memberPermissions;

    @OneToOne(orphanRemoval = true)
    private PersonalEvidenceEntity personalEvidence;

    public UUID getUuid() {
        return uuid;
    }

    void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }

    public Integer getLegitimationNumber() {
        return legitimationNumber;
    }

    public void setLegitimationNumber(Integer legitimationNumber) {
        this.legitimationNumber = legitimationNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public LicenseEntity getLicense() {
        return license;
    }

    public void setLicense(LicenseEntity license) {
        this.license = license;
    }

    public ShootingPatentEntity getShootingPatent() {
        return shootingPatent;
    }

    public void setShootingPatent(ShootingPatentEntity shootingPatent) {
        this.shootingPatent = shootingPatent;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPesel() {
        return pesel;
    }

    public void setPesel(String pesel) {
        this.pesel = pesel;
    }

    public String getIDCard() {
        return IDCard;
    }

    public void setIDCard(String IDCard) {
        this.IDCard = IDCard;
    }

    public AddressEntity getAddress() {
        return address;
    }

    public void setAddress(AddressEntity address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public WeaponPermissionEntity getWeaponPermission() {
        return weaponPermission;
    }

    public void setWeaponPermission(WeaponPermissionEntity weaponPermission) {
        this.weaponPermission = weaponPermission;
    }

    public String getErasedReason() {
        return erasedReason;
    }

    public void setErasedReason(String erasedReason) {
        this.erasedReason = erasedReason;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void toggleActive() {
        this.active = !this.active;
    }

    public Boolean getAdult() {
        return adult;
    }

    public void toggleAdult() {
        this.adult = true;
    }

    public Boolean getErased() {
        return erased;
    }

    public void toggleErase() {
        this.erased = !this.erased;
    }

    public HistoryEntity getHistory() {
        return history;
    }

    public void setHistory(HistoryEntity history) {
        this.history = history;
    }

    public MemberPermissionsEntity getMemberPermissions() {
        return memberPermissions;
    }

    public void setMemberPermissions(MemberPermissionsEntity memberPermissions) {
        this.memberPermissions = memberPermissions;
    }

    public PersonalEvidenceEntity getPersonalEvidence() {
        return personalEvidence;
    }

    public void setPersonalEvidence(PersonalEvidenceEntity personalEvidence) {
        this.personalEvidence = personalEvidence;
    }

    public ClubEntity getClub() {
        return club;
    }

    public void setClub(ClubEntity club) {
        this.club = club;
    }
}
