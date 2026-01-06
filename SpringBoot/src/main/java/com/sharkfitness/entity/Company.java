package com.sharkfitness.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sharkfitness.entity.enums.VerifyStatus;
import jakarta.persistence.*;

@Entity
@Table(name="company")
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class Company extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // merchant_user_id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="merchant_user_id", nullable=false)
    private User merchant;

    @Column(nullable=false)
    private String name;

    @Column(name="license_no", nullable=false)
    private String licenseNo;

    @Column(name="license_img")
    private String licenseImg; // image/license_xxx.jpg

    private String address;

    @Column(name="contact_name")
    private String contactName;

    @Column(name="contact_phone")
    private String contactPhone;

    @Enumerated(EnumType.STRING)
    @Column(name="verify_status", nullable=false)
    private VerifyStatus verifyStatus = VerifyStatus.PENDING;

    @Column(name="reject_reason")
    private String rejectReason;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getMerchant() { return merchant; }
    public void setMerchant(User merchant) { this.merchant = merchant; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLicenseNo() { return licenseNo; }
    public void setLicenseNo(String licenseNo) { this.licenseNo = licenseNo; }

    public String getLicenseImg() { return licenseImg; }
    public void setLicenseImg(String licenseImg) { this.licenseImg = licenseImg; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public VerifyStatus getVerifyStatus() { return verifyStatus; }
    public void setVerifyStatus(VerifyStatus verifyStatus) { this.verifyStatus = verifyStatus; }

    public String getRejectReason() { return rejectReason; }
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }
}
