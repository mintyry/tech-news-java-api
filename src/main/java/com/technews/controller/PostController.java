package com.technews.controller;

import com.technews.model.Post;
import com.technews.repository.PostRepository;
import com.technews.repository.UserRepository;
import com.technews.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PostController {
    @Autowired
    PostRepository repository;

    @Autowired
    VoteRepository voteRepository;

    @Autowired
    UserRepository userRepository;

//    get posts
//    defines method that handles GET requests; when GET request is made, getAllPosts(); is called
    @GetMapping("/api/posts")
//    method definition for handling GET req. Returns list of Post objects.
//    public means the method can be access from anywhere in app
//    List<Post> is a return type of the method; indicates method will return a list of objects of type Post. List is an interface in Java (represents an ordered collection of elements)
//    summary: the method is public and returns a list of Post objects
    public List<Post> getAllPosts(){
//        gets all the posts from db with findAll() provided by PostRepository (extends JpaRepo); postList will contain all posts retrieved from db.
        List<Post> postList = repository.findAll();
//        loop iterates over each post in postList
        for (Post p : postList) {
//            for each post (p), this line sets voteCount by counting the number of votes for that post using countVotesByPostId provided by voteRepo.
//            setVoteCount is a method from post model
            p.setVoteCount(voteRepository.countVotesByPostId(p.getId()));
        }
        return postList;
    }

//    get single post
    @GetMapping("/api/posts/{id}")
    public Post getPost(@PathVariable Integer id) {
        Post returnPost = repository.getById(id);
        returnPost.setVoteCount(voteRepository.countVotesByPostId(returnPost.getId()));

        return returnPost;
    }

//    Create post
    @PostMapping("/api/posts")
    @ResponseStatus(HttpStatus.CREATED)
    public Post addPost(@RequestBody Post post) {
        repository.save(post);
        return post;
    }
}
