package net.javaguides.springboot.service.impl;

import net.javaguides.springboot.dto.NoteDto;
import net.javaguides.springboot.entity.Note;
import net.javaguides.springboot.entity.User;
import net.javaguides.springboot.repository.NoteRepository;
import net.javaguides.springboot.service.NoteService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;

    public NoteServiceImpl(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @Override
    public void addNote(NoteDto noteDto) {
        Note note = new Note();
        note.setTitle(noteDto.getTitle());
        note.setContent(noteDto.getContent());
        note.setUser(getCurrentUser()); // Реализуйте метод получения текущего пользователя
        noteRepository.save(note);
    }

    @Override
    public NoteDto getNoteById(Long id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));
        return convertToDto(note);
    }

    @Override
    public List<NoteDto> getAllNotes() {
        return noteRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<NoteDto> getAllNotesByUser() {
        Long userId = getCurrentUserId(); // Реализуйте метод получения текущего userId
        return noteRepository.findNoteByUserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public NoteDto updateNote(NoteDto noteDto, Long id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        note.setTitle(noteDto.getTitle());
        note.setContent(noteDto.getContent());
        noteRepository.save(note);
        return convertToDto(note);
    }

    @Override
    public void deleteNoteById(Long id) {
        noteRepository.deleteById(id);
    }

    private NoteDto convertToDto(Note note) {
        return new NoteDto(note.getId(), note.getTitle(), note.getContent());
    }

    private Long getCurrentUserId() {
        // TODO
        return 1L;
    }

    private User getCurrentUser() {
        // TODO
        return new User();
    }
}
