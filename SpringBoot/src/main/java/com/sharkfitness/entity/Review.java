package com.sharkfitness.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name="review")
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="application_id", nullable=false)
    private Application application;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="from_user_id", nullable=false)
    private User fromUser;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="to_user_id", nullable=false)
    private User toUser;

    @Column(nullable=false)
    private Integer rating; // 1~5

    @Column(length=500)
    private String content;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Application getApplication() { return application; }
    public void setApplication(Application application) { this.application = application; }

    public User getFromUser() { return fromUser; }
    public void setFromUser(User fromUser) { this.fromUser = fromUser; }

    public User getToUser() { return toUser; }
    public void setToUser(User toUser) { this.toUser = toUser; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
