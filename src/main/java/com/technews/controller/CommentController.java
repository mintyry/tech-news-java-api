package com.technews.controller;

import com.technews.model.Comment;
import com.technews.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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
}
