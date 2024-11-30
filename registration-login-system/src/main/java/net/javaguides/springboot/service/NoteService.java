package net.javaguides.springboot.service;

import net.javaguides.springboot.dto.NoteDto;

import java.util.List;

public interface NoteService {

    void addNote(NoteDto noteDto);

    NoteDto getNoteById(Long id);

    List<NoteDto> getAllNotes();

    List<NoteDto> getAllNotesByUser();

    NoteDto updateNote(NoteDto noteDto, Long id);

    void deleteNoteById(Long id);
}
