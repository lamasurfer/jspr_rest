package org.example.service;

import org.example.exception.NotFoundException;
import org.example.model.Post;
import org.example.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    private final long id = 1;
    private final String content = "test";

    @Mock
    PostRepository postRepository;

    @InjectMocks
    PostService postService;

    @Test
    void test_all_excludesRemovedPosts() {
        final Post removedPost = new Post(id, content);
        removedPost.setFlaggedAsRemoved(true);

        final List<Post> posts = List.of(removedPost);

        when(postRepository.all()).thenReturn(posts);

        final int expectedSize = 0;
        assertEquals(expectedSize, postService.all().size());
    }

    @Test
    void test_all_getsStoredPosts() {
        final Post storedPost = new Post(id, content);
        storedPost.setFlaggedAsRemoved(false);

        final List<Post> posts = List.of(storedPost);

        when(postRepository.all()).thenReturn(posts);

        final int expectedSize = 1;
        assertEquals(expectedSize, postService.all().size());
    }

    @Test
    void test_getById_ifNoPost_throwsNotFoundException() {
        when(postRepository.getById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> postService.getById(id));
    }

    @Test
    void test_getById_ifPostFlaggedAsRemoved_throwsNotFoundException() {
        final Post removedPost = new Post(id, content);
        removedPost.setFlaggedAsRemoved(true);

        when(postRepository.getById(id)).thenReturn(Optional.of(removedPost));

        assertThrows(NotFoundException.class, () -> postService.getById(id));
    }

    @Test
    void test_save_savesNewPostIfIdIsZero() {
        final int zeroId = 0;
        final Post newPost = new Post(zeroId, content);

        postService.save(newPost);

        verify(postRepository).save(newPost);
    }

    @Test
    void test_save_savesPostIfIdIsNotZeroButPostIsNotPresent() {
        final int mistakenId = 100;
        final Post newPost = new Post(mistakenId, content);

        postService.save(newPost);

        verify(postRepository).save(newPost);
    }

    @Test
    void test_save_updatesOldPost() {
        final long updateId = 10;
        final String oldContent = "oldContent";
        final String newContent = "newContent";
        final Post oldPost = new Post(updateId, oldContent);
        final Post newPost = new Post(updateId, newContent);

        when(postRepository.getById(updateId)).thenReturn(Optional.of(oldPost));

        postService.save(newPost);

        assertEquals(newContent, oldPost.getContent());
        verify(postRepository).save(oldPost);
    }

    @Test
    void test_save_ifPostFlaggedAsRemoved_throwsNotFoundException() {
        final Post removedPost = new Post(id, content);
        removedPost.setFlaggedAsRemoved(true);

        when(postRepository.getById(id)).thenReturn(Optional.of(removedPost));

        assertThrows(NotFoundException.class, () -> postService.save(removedPost));
    }

    @Test
    void test_removeById_ifNoPost_throwsNotFoundException() {

        assertThrows(NotFoundException.class, () -> postService.removeById(id));
    }

    @Test
    void test_removeById_ifPostFlaggedAsRemoved_throwsNotFoundException() {
        final Post removedPost = new Post(id, content);
        removedPost.setFlaggedAsRemoved(true);

        when(postRepository.getById(id)).thenReturn(Optional.of(removedPost));

        assertThrows(NotFoundException.class, () -> postService.removeById(id));
    }

    @Test
    void test_removeById_flagsAsRemovedAndRepositoryDoesNotRemove() {
        final Post postToRemove = new Post(id, content);

        when(postRepository.getById(id)).thenReturn(Optional.of(postToRemove));

        postService.removeById(id);

        verify(postRepository, never()).removeById(id);
        assertTrue(postToRemove.isFlaggedAsRemoved());
    }
}