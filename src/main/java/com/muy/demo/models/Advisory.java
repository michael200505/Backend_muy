package com.muy.demo.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "advisories")
public class Advisory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Programador que atiende
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "programmer_id", nullable = false)
    private User programmer;

    // Usuario externo que solicita
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "external_user_id", nullable = false)
    private User externalUser;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime startAt;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime endAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Modality modality;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdvisoryStatus status = AdvisoryStatus.PENDING;

    @Column(length = 2000)
    private String topic;

    @Column(length = 2000)
    private String rejectionReason;

    public Advisory() {}

    // getters/setters
    public Long getId() { return id; }
    public User getProgrammer() { return programmer; }
    public void setProgrammer(User programmer) { this.programmer = programmer; }
    public User getExternalUser() { return externalUser; }
    public void setExternalUser(User externalUser) { this.externalUser = externalUser; }
    public LocalDateTime getStartAt() { return startAt; }
    public void setStartAt(LocalDateTime startAt) { this.startAt = startAt; }
    public LocalDateTime getEndAt() { return endAt; }
    public void setEndAt(LocalDateTime endAt) { this.endAt = endAt; }
    public Modality getModality() { return modality; }
    public void setModality(Modality modality) { this.modality = modality; }
    public AdvisoryStatus getStatus() { return status; }
    public void setStatus(AdvisoryStatus status) { this.status = status; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
}
