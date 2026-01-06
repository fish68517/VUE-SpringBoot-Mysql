package com.sharkfitness.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sharkfitness.entity.enums.ComplaintStatus;
import com.sharkfitness.entity.enums.ComplaintTargetType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="complaint")
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class Complaint extends BaseEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="from_user_id", nullable=false)
    private User fromUser;

    @Enumerated(EnumType.STRING)
    @Column(name="target_type", nullable=false)
    private ComplaintTargetType targetType;

    @Column(name="target_id", nullable=false)
    private Long targetId;

    @Column(nullable=false, length=50)
    private String type;

    @Column(nullable=false, length=500)
    private String content;

    @Column(name="evidence_img")
    private String evidenceImg; // image/xxx.png

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private ComplaintStatus status = ComplaintStatus.PENDING;

    @Column(length=500)
    private String result;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="handler_admin_id")
    private User handlerAdmin;

    @Column(name="handle_time")
    private LocalDateTime handleTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getFromUser() { return fromUser; }
    public void setFromUser(User fromUser) { this.fromUser = fromUser; }

    public ComplaintTargetType getTargetType() { return targetType; }
    public void setTargetType(ComplaintTargetType targetType) { this.targetType = targetType; }

    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getEvidenceImg() { return evidenceImg; }
    public void setEvidenceImg(String evidenceImg) { this.evidenceImg = evidenceImg; }

    public ComplaintStatus getStatus() { return status; }
    public void setStatus(ComplaintStatus status) { this.status = status; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public User getHandlerAdmin() { return handlerAdmin; }
    public void setHandlerAdmin(User handlerAdmin) { this.handlerAdmin = handlerAdmin; }

    public LocalDateTime getHandleTime() { return handleTime; }
    public void setHandleTime(LocalDateTime handleTime) { this.handleTime = handleTime; }
}
