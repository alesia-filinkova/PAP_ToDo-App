package net.javaguides.springboot.service.impl;

import lombok.AllArgsConstructor;
import net.javaguides.springboot.CurrentUser;
import net.javaguides.springboot.dto.NoteDto;
import net.javaguides.springboot.entity.Note;
import net.javaguides.springboot.repository.NoteRepository;
import net.javaguides.springboot.service.NoteService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class NoteServiceImpl implements NoteService {

    private NoteRepository noteRepository;

    @Override
    public void addNote(NoteDto noteDto) {
        Note note = mapToNote(noteDto);
        note.setUser(CurrentUser.user);
        noteRepository.save(note);
    }

    @Override
    public NoteDto getNoteById(Long id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found with id: " + id));
        return mapToNoteDto(note);
    }

    @Override
    public List<NoteDto> getAllNotes() {
        List<Note> notes = noteRepository.findAll();
        return notes.stream().map(this::mapToNoteDto).collect(Collectors.toList());
    }

    @Override
    public List<NoteDto> getAllNotesByUser() {
        List<Note> notes = noteRepository.findNoteByUserId(CurrentUser.user.getId());
        return notes.stream().map(this::mapToNoteDto).collect(Collectors.toList());
    }

    @Override
    public NoteDto updateNote(NoteDto noteDto, Long id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found with id: " + id));
        note.setTitle(noteDto.getTitle());
        note.setContent(noteDto.getContent());

        Note updatedNote = noteRepository.save(note);
        return mapToNoteDto(updatedNote);
    }

    @Override
    public void deleteNoteById(Long id) {
        noteRepository.deleteById(id);
    }

    private NoteDto mapToNoteDto(Note note) {
        return new NoteDto(note.getId(), note.getTitle(), note.getContent());
    }

    private Note mapToNote(NoteDto noteDto) {
        return new Note(noteDto.getId(), noteDto.getTitle(), noteDto.getContent(), CurrentUser.user);
    }
}
