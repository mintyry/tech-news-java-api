package com.technews.controller;

import com.technews.model.Comment;
import com.technews.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CommentController {

//    make comment methods and such available
    @Autowired
    CommentRepository repository;

//    get route for comments
    @GetMapping("/api/comments")
//    public method returns a list of comment objects
    public List<Comment> getAllComments() {
        return repository.findAll();
    }
//    get route for single comment
    @GetMapping("/api/comments/{id}")
//    public method returns single comment, take in id from url path
    public Comment getComment(@PathVariable int id) {
//        returns the comment by id
        return repository.getById(id);
    }

//    create route
    @PostMapping("/api/comments")
//    provides res status saying content was created
    @ResponseStatus(HttpStatus.CREATED)
//    public method return single comment, takes comment object from req body
    public Comment createComment(@RequestBody Comment comment){
//        saves comment to db
        return repository.save(comment);
    }

//    put route
    @PutMapping("/api/updateComment")
//    returns single comment via comment object in req body
//    perhaps comment id is already in comment object
    public Comment updateComment(@RequestBody Comment comment){
//        saves to db
        return repository.save(comment);
    }

    @DeleteMapping("/api/comments/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable int id) {
        repository.deleteById(id);
    }
}
