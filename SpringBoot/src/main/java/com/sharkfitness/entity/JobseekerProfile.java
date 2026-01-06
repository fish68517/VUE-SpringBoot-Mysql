package com.sharkfitness.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sharkfitness.entity.enums.Gender;
import com.sharkfitness.entity.enums.VerifyStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "jobseeker_profile")
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class JobseekerProfile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 多个档案也可以（为了演示每表6条数据），真实可改成@OneToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @Column(name="real_name", nullable=false)
    private String realName;

    @Enumerated(EnumType.STRING)
    private Gender gender = Gender.U;

    private Integer age;
    private String city;
    private String skills;

    @Column(name="available_time_desc")
    private String availableTimeDesc;

    @Column(name="resume_file")
    private String resumeFile;       // file/xxx.pdf

    @Column(name="id_card_front")
    private String idCardFront;      // image/xxx.jpg

    @Column(name="id_card_back")
    private String idCardBack;       // image/xxx.jpg

    @Enumerated(EnumType.STRING)
    @Column(name="verify_status")
    private VerifyStatus verifyStatus = VerifyStatus.PENDING;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public String getAvailableTimeDesc() { return availableTimeDesc; }
    public void setAvailableTimeDesc(String availableTimeDesc) { this.availableTimeDesc = availableTimeDesc; }

    public String getResumeFile() { return resumeFile; }
    public void setResumeFile(String resumeFile) { this.resumeFile = resumeFile; }

    public String getIdCardFront() { return idCardFront; }
    public void setIdCardFront(String idCardFront) { this.idCardFront = idCardFront; }

    public String getIdCardBack() { return idCardBack; }
    public void setIdCardBack(String idCardBack) { this.idCardBack = idCardBack; }

    public VerifyStatus getVerifyStatus() { return verifyStatus; }
    public void setVerifyStatus(VerifyStatus verifyStatus) { this.verifyStatus = verifyStatus; }
}
