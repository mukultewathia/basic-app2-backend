package com.example.counter.habit;

import com.example.counter.user.User;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "notes")
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "note_text", nullable = false, columnDefinition = "TEXT")
    private String noteText;

    @Column(name = "note_date", nullable = false)
    private LocalDate noteDate;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    // Constructors
    public Note() {
        this.createdAt = getIndiaTimestamp();
        this.updatedAt = getIndiaTimestamp();
    }

    public Note(User user, String noteText, LocalDate noteDate) {
        this.user = user;
        this.noteText = noteText;
        this.noteDate = noteDate;
        this.createdAt = getIndiaTimestamp();
        this.updatedAt = getIndiaTimestamp();
    }
    
    // Helper method to get current timestamp in India timezone
    private OffsetDateTime getIndiaTimestamp() {
        ZoneId indiaZone = ZoneId.of("Asia/Kolkata");
        return OffsetDateTime.now(indiaZone);
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
        this.updatedAt = getIndiaTimestamp();
    }

    public LocalDate getNoteDate() {
        return noteDate;
    }

    public void setNoteDate(LocalDate noteDate) {
        this.noteDate = noteDate;
        this.updatedAt = getIndiaTimestamp();
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
