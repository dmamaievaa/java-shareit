package item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.ShareItServer;
import ru.practicum.server.item.model.Comment;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.repository.CommentRepository;
import ru.practicum.server.item.repository.ItemRepository;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@Transactional
@SpringBootTest(classes = ShareItServer.class)
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private User owner;
    private Item item;
    private Comment comment;

    @BeforeEach
    void setUp() {
        owner = new User(1L, "Owner", "owner@example.com");
        owner = userRepository.save(owner);

        item = Item.builder()
                .name("Test Item")
                .description("Description")
                .available(true)
                .owner(owner)
                .build();
        item = itemRepository.save(item);

        comment = new Comment();
        comment.setText("Great Item!");
        comment.setItem(item);
        comment.setAuthor(owner);
        commentRepository.save(comment);
    }

    @Test
    void testFindByItem_Id() {
        List<Comment> comments = commentRepository.findByItem_Id(item.getId());

        assertNotNull(comments);
        assertEquals(1, comments.size());
        assertEquals(comment.getText(), comments.getFirst().getText());
    }

    @Test
    void testFindByItem_Id_NoComments() {
        List<Comment> comments = commentRepository.findByItem_Id(999L);

        assertNotNull(comments);
        assertTrue(comments.isEmpty());
    }
}
