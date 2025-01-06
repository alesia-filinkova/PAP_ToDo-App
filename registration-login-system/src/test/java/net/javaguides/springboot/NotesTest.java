package net.javaguides.springboot;

import net.javaguides.springboot.dto.NoteDto;
import net.javaguides.springboot.repository.UserRepository;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import net.javaguides.springboot.entity.Note;
import net.javaguides.springboot.repository.NoteRepository;
import net.javaguides.springboot.service.impl.NoteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;
import static org.mockito.Mockito.*;
import net.javaguides.springboot.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;
import net.javaguides.springboot.entity.User;


class NoteDtoTest {

    @Test
    void testNoteDtoSettersAndGetters() {
        NoteDto noteDto = new NoteDto();
        noteDto.setId(1L);
        noteDto.setTitle("Test Title");
        noteDto.setContent("Test Content");

        assertEquals(1L, noteDto.getId());
        assertEquals("Test Title", noteDto.getTitle());
        assertEquals("Test Content", noteDto.getContent());
    }
}

class NoteEnityTest {
    @Test
    void testNoteEntityMapping() {
        Note note = new Note();
        note.setTitle("Test Title");
        note.setContent("Test Content");

        assertEquals("Test Title", note.getTitle());
        assertEquals("Test Content", note.getContent());
    }

}
class NoteServiceImplTest {

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NoteServiceImpl noteService;

    private Note note;
    private NoteDto noteDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        note = new Note(1L, "Test Title", "Test Content", null);
        noteDto = new NoteDto(1L, "Test Title", "Test Content");
    }

    @Test
    void testAddNote() {
        when(noteRepository.save(any(Note.class))).thenReturn(note);
        noteService.addNote(noteDto);
        verify(noteRepository, times(1)).save(any(Note.class));
    }

    @Test
    void testGetNoteById() {
        when(noteRepository.findById(1L)).thenReturn(Optional.of(note));
        NoteDto result = noteService.getNoteById(1L);
        assertEquals("Test Title", result.getTitle());
        assertEquals("Test Content", result.getContent());
    }

    @Test
    void testUpdateNote() {
        when(noteRepository.findById(1L)).thenReturn(Optional.of(note));
        when(noteRepository.save(any(Note.class))).thenReturn(note);

        NoteDto updatedNoteDto = new NoteDto(1L, "Updated Title", "Updated Content");
        NoteDto result = noteService.updateNote(updatedNoteDto, 1L);

        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Content", result.getContent());
    }

    @Test
    void testDeleteNoteById() {
        doNothing().when(noteRepository).deleteById(1L);
        noteService.deleteNoteById(1L);
        verify(noteRepository, times(1)).deleteById(1L);
    }
}

@SpringBootTest
@Transactional
class NoteRepositoryTest {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindNoteByUserId() {
        User testUser = new User();
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser = userRepository.save(testUser);

        Note note = new Note();
        note.setTitle("Test Title");
        note.setContent("Test Content");
        note.setUser(testUser);
        noteRepository.save(note);

        List<Note> notes = noteRepository.findNoteByUserId(testUser.getId());
        assertEquals(1, notes.size());
        assertEquals("Test Title", notes.get(0).getTitle());
    }
}


@SpringBootTest
class NoteIntegrationTest {

    @Autowired
    private NoteService noteService;

    @Test
    void testAddAndRetrieveNotes() {
        NoteDto noteDto = new NoteDto(null, "Integration Title", "Integration Content");
        noteService.addNote(noteDto);

        List<NoteDto> notes = noteService.getAllNotes();
        assertTrue(notes.stream().anyMatch(note -> note.getTitle().equals("Integration Title")));
    }
}

class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NoteServiceImpl noteService;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
    }

    @Test
    void testAddNote() {
        NoteDto noteDto = new NoteDto(null, "Test Title", "Test Content");
        Note savedNote = new Note(1L, "Test Title", "Test Content", testUser);

        when(noteRepository.save(any(Note.class))).thenReturn(savedNote);

        noteService.addNote(noteDto);

        verify(noteRepository, times(1)).save(any(Note.class));
    }

    @Test
    void testGetNoteById() {
        Note note = new Note(1L, "Test Title", "Test Content", testUser);

        when(noteRepository.findById(1L)).thenReturn(Optional.of(note));

        NoteDto result = noteService.getNoteById(1L);

        assertEquals("Test Title", result.getTitle());
        assertEquals("Test Content", result.getContent());
    }
}

