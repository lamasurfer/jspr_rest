package org.example.service;


import org.example.exception.NotFoundException;
import org.example.model.Post;
import org.example.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class PostService {
    private final AtomicLong counter = new AtomicLong(1);
    private final PostRepository repository;

    public PostService(PostRepository repository) {
        this.repository = repository;
    }

    public List<Post> all() {
        return repository.all();
    }

    public Post getById(long id) {
        return repository.getById(id).orElseThrow(NotFoundException::new);
    }

    public Post save(Post post) {
        final var id = post.getId();
        final var optionalPost = repository.getById(id);
        if (id != 0 && optionalPost.isPresent()) {
            final var oldPost = optionalPost.get();
            oldPost.setContent(post.getContent());
            return repository.save(oldPost);
        } else {
            post.setId(counter.getAndIncrement());
            return repository.save(post);
        }
    }

    public void removeById(long id) {
        repository.removeById(id);
    }
}

