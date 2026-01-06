package com.sharkfitness.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sharkfitness.entity.enums.ApplicationStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="application")
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class Application extends BaseEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="job_post_id", nullable=false)
    private JobPost jobPost;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="jobseeker_user_id", nullable=false)
    private User jobseeker;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private ApplicationStatus status = ApplicationStatus.APPLIED;

    @Column(name="apply_time", nullable=false)
    private LocalDateTime applyTime = LocalDateTime.now();

    @Column(name="merchant_note")
    private String merchantNote;

    @Column(name="cancel_reason")
    private String cancelReason;

    @Column(name="checkin_time")
    private LocalDateTime checkinTime;

    @Column(name="finish_time")
    private LocalDateTime finishTime;

    @Column(name="settle_time")
    private LocalDateTime settleTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public JobPost getJobPost() { return jobPost; }
    public void setJobPost(JobPost jobPost) { this.jobPost = jobPost; }

    public User getJobseeker() { return jobseeker; }
    public void setJobseeker(User jobseeker) { this.jobseeker = jobseeker; }

    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }

    public LocalDateTime getApplyTime() { return applyTime; }
    public void setApplyTime(LocalDateTime applyTime) { this.applyTime = applyTime; }

    public String getMerchantNote() { return merchantNote; }
    public void setMerchantNote(String merchantNote) { this.merchantNote = merchantNote; }

    public String getCancelReason() { return cancelReason; }
    public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }

    public LocalDateTime getCheckinTime() { return checkinTime; }
    public void setCheckinTime(LocalDateTime checkinTime) { this.checkinTime = checkinTime; }

    public LocalDateTime getFinishTime() { return finishTime; }
    public void setFinishTime(LocalDateTime finishTime) { this.finishTime = finishTime; }

    public LocalDateTime getSettleTime() { return settleTime; }
    public void setSettleTime(LocalDateTime settleTime) { this.settleTime = settleTime; }
}
