package com.sharkfitness.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sharkfitness.entity.enums.JobPostStatus;
import com.sharkfitness.entity.enums.SalaryUnit;
import com.sharkfitness.entity.enums.SettlementType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="job_post")
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class JobPost extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="company_id", nullable=false)
    private Company company;

    @Column(nullable=false, length=120)
    private String title;

    @Column(nullable=false, length=50)
    private String category;

    @Column(name="salary_amount", nullable=false)
    private BigDecimal salaryAmount;

    @Enumerated(EnumType.STRING)
    @Column(name="salary_unit", nullable=false)
    private SalaryUnit salaryUnit;

    @Enumerated(EnumType.STRING)
    @Column(name="settlement_type", nullable=false)
    private SettlementType settlementType;

    @Column(name="work_city", nullable=false, length=50)
    private String workCity;

    @Column(name="work_address", nullable=false, length=255)
    private String workAddress;

    private Double longitude;
    private Double latitude;

    @Column(name="start_time", nullable=false)
    private LocalDateTime startTime;

    @Column(name="end_time", nullable=false)
    private LocalDateTime endTime;

    @Column(nullable=false)
    private Integer headcount;

    @Column(name="applied_count", nullable=false)
    private Integer appliedCount = 0;

    @Column(name="contact_phone")
    private String contactPhone;

    @Column(name="cover_img")
    private String coverImg; // image/job_xxx.jpg

    @Column(name="attachment_file")
    private String attachmentFile; // file/xxx.pdf

    @Lob
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private JobPostStatus status = JobPostStatus.PUBLISHED;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public BigDecimal getSalaryAmount() { return salaryAmount; }
    public void setSalaryAmount(BigDecimal salaryAmount) { this.salaryAmount = salaryAmount; }

    public SalaryUnit getSalaryUnit() { return salaryUnit; }
    public void setSalaryUnit(SalaryUnit salaryUnit) { this.salaryUnit = salaryUnit; }

    public SettlementType getSettlementType() { return settlementType; }
    public void setSettlementType(SettlementType settlementType) { this.settlementType = settlementType; }

    public String getWorkCity() { return workCity; }
    public void setWorkCity(String workCity) { this.workCity = workCity; }

    public String getWorkAddress() { return workAddress; }
    public void setWorkAddress(String workAddress) { this.workAddress = workAddress; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public Integer getHeadcount() { return headcount; }
    public void setHeadcount(Integer headcount) { this.headcount = headcount; }

    public Integer getAppliedCount() { return appliedCount; }
    public void setAppliedCount(Integer appliedCount) { this.appliedCount = appliedCount; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getCoverImg() { return coverImg; }
    public void setCoverImg(String coverImg) { this.coverImg = coverImg; }

    public String getAttachmentFile() { return attachmentFile; }
    public void setAttachmentFile(String attachmentFile) { this.attachmentFile = attachmentFile; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public JobPostStatus getStatus() { return status; }
    public void setStatus(JobPostStatus status) { this.status = status; }
}
