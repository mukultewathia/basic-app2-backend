package com.example.counter.notes;

import com.example.counter.user.User;
import com.example.counter.challenge.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    
    @Query("SELECT n FROM Note n WHERE n.user = :user AND n.noteDate = :noteDate")
    Optional<Note> findByUserAndNoteDate(@Param("user") User user, @Param("noteDate") LocalDate noteDate);
    
    @Query("SELECT n FROM Note n WHERE n.user = :user ORDER BY n.noteDate DESC")
    List<Note> findAllByUserOrderByNoteDateDesc(@Param("user") User user);
    
    @Query("SELECT n FROM Note n WHERE n.user = :user AND n.noteDate = :noteDate AND n.challenge IS NULL")
    Optional<Note> findByUserAndNoteDateWithoutChallenge(@Param("user") User user, @Param("noteDate") LocalDate noteDate);
    
    @Query("SELECT n FROM Note n WHERE n.user = :user AND n.noteDate = :noteDate AND n.challenge = :challenge")
    Optional<Note> findByUserAndNoteDateAndChallenge(@Param("user") User user, @Param("noteDate") LocalDate noteDate, @Param("challenge") Challenge challenge);
    
    @Query("SELECT n FROM Note n WHERE n.challenge = :challenge ORDER BY n.noteDate DESC")
    List<Note> findAllByChallengeOrderByNoteDateDesc(@Param("challenge") Challenge challenge);
}
