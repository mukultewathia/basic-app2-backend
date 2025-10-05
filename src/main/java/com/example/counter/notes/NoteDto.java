package com.example.counter.notes;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class NoteDto {
    
    public record NoteRequest(
            LocalDate noteDate,
            String noteText) {
    }

    public record NoteResponse(
            Long id,
            Long userId,
            String noteText,
            LocalDate noteDate,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt,
            Long challengeId,
            String message) {
        public NoteResponse(Note note) {
            this(note.getId(),
                 note.getUser().getUserId(),
                 note.getNoteText(),
                 note.getNoteDate(),
                 note.getCreatedAt(),
                 note.getUpdatedAt(),
                 note.getChallenge() != null ? note.getChallenge().getChallengeId() : null,
                 null);
        }
        
        public NoteResponse(Note note, String message) {
            this(note.getId(),
                 note.getUser().getUserId(),
                 note.getNoteText(),
                 note.getNoteDate(),
                 note.getCreatedAt(),
                 note.getUpdatedAt(),
                 note.getChallenge() != null ? note.getChallenge().getChallengeId() : null,
                 message);
        }
    }
}
