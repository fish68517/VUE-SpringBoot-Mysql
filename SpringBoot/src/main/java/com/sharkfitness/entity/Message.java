package com.sharkfitness.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name="message")
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class Message extends BaseEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="to_user_id", nullable=false)
    private User toUser;

    @Column(nullable=false, length=120)
    private String title;

    @Column(nullable=false, length=1000)
    private String content;

    @Column(name="read_flag", nullable=false)
    private Integer readFlag = 0; // 0未读 1已读

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getToUser() { return toUser; }
    public void setToUser(User toUser) { this.toUser = toUser; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Integer getReadFlag() { return readFlag; }
    public void setReadFlag(Integer readFlag) { this.readFlag = readFlag; }
}
