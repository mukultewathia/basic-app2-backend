package com.example.counter.notes;

import com.example.counter.user.User;
import com.example.counter.user.UserRepository;
import com.example.counter.challenge.Challenge;
import com.example.counter.challenge.ChallengeRepository;
import com.example.counter.auth.security.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.counter.notes.NoteDto.NoteResponse;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class NoteService {
    
    @Autowired
    private NoteRepository noteRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ChallengeRepository challengeRepository;
    
    public NoteResponse upsertNote(LocalDate noteDate, String noteText) {
        Long userId = CurrentUser.getCurrentUserId();
        if (userId == null) {
            throw new RuntimeException("User not authenticated");
        }
        
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        Note existingNote = noteRepository.findByUserAndNoteDate(user, noteDate).orElse(null);
        
        if (existingNote != null) {
            existingNote.setNoteText(noteText);
            Note savedNote = noteRepository.save(existingNote);
            return new NoteResponse(savedNote);
        } else {
            Note newNote = new Note(user, noteText, noteDate);
            Note savedNote = noteRepository.save(newNote);
            return new NoteResponse(savedNote);
        }
    }
    
    public List<NoteResponse> getAllNotes() {
        Long userId = CurrentUser.getCurrentUserId();
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        
        List<Note> notes = noteRepository.findAllByUserOrderByNoteDateDesc(user);
        return notes.stream()
                .map(NoteResponse::new)
                .toList();
    }
    
    public NoteResponse getNoteByDate(LocalDate noteDate) {
        Long userId = CurrentUser.getCurrentUserId();
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        
        Note note = noteRepository.findByUserAndNoteDate(user, noteDate)
                .orElse(null);
        
        return note != null ? new NoteResponse(note) : null;
    }
    
    public List<NoteResponse> getNotesByChallenge(String username, Long challengeId) {
        // Validate challenge and user
        Challenge challenge = challengeRepository.findByUsernameAndChallengeIdAndNotDeleted(username, challengeId)
            .orElseThrow(() -> new RuntimeException("Challenge not found with id: " + challengeId));
        
        List<Note> notes = noteRepository.findAllByChallengeOrderByNoteDateDesc(challenge);
        return notes.stream()
                .map(NoteResponse::new)
                .toList();
    }
}
