package org.example.service;


import org.example.exception.NotFoundException;
import org.example.model.Post;
import org.example.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final AtomicLong counter = new AtomicLong(1);
    private final PostRepository repository;

    public PostService(PostRepository repository) {
        this.repository = repository;
    }

    public List<Post> all() {
        return repository.all()
                .stream()
                .filter(post -> !post.isFlaggedAsRemoved())
                .collect(Collectors.toList());
    }

    public Post getById(long id) {
        return repository.getById(id)
                .filter(post -> !post.isFlaggedAsRemoved())
                .orElseThrow(NotFoundException::new);
    }

    public Post save(Post post) {
        final var id = post.getId();
        if (id == 0) {
            post.setId(counter.getAndIncrement());
            return repository.save(post);
        }
        final var optionalPost = repository.getById(id);
        if (optionalPost.isEmpty()) {
            post.setId(counter.getAndIncrement());
            return repository.save(post);
        }
        final var oldPost = optionalPost.get();
        if (!oldPost.isFlaggedAsRemoved()) {
            oldPost.setContent(post.getContent());
            return repository.save(oldPost);
        } else {
            throw new NotFoundException();
        }
    }

    public void removeById(long id) {
        var post = getById(id);
        post.setFlaggedAsRemoved(true);
    }
}

