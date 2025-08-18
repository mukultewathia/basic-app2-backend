package com.example.counter.habit;

import com.example.counter.user.User;
import com.example.counter.user.UserRepository;
import com.example.counter.auth.security.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.counter.habit.HabitDto.NoteResponse;
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
    
    public Note upsertNote(LocalDate noteDate, String noteText) {
        Long userId = CurrentUser.getCurrentUserId();
        if (userId == null) {
            throw new RuntimeException("User not authenticated");
        }
        
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        Note existingNote = noteRepository.findByUserAndNoteDate(user, noteDate).orElse(null);
        
        if (existingNote != null) {
            existingNote.setNoteText(noteText);
            return noteRepository.save(existingNote);
        } else {
            Note newNote = new Note(user, noteText, noteDate);
            return noteRepository.save(newNote);
        }
    }
    
    public List<NoteResponse> getAllNotes() {
        Long userId = CurrentUser.getCurrentUserId();
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        
        List<Note> notes = noteRepository.findAllByUserOrderByNoteDateDesc(user);
        return notes.stream()
                .map(HabitDto.NoteResponse::new)
                .toList();
    }
    
    public Note getNoteByDate(LocalDate noteDate) {
        Long userId = CurrentUser.getCurrentUserId();
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        
        return noteRepository.findByUserAndNoteDate(user, noteDate)
                .orElse(null);
    }
}
