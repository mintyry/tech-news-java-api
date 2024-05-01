package com.technews.controller;

import com.technews.model.Post;
import com.technews.model.User;
import com.technews.repository.UserRepository;
import com.technews.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {
    @Autowired
    UserRepository repository;

    @Autowired
    VoteRepository voteRepository;

    @GetMapping("/api/users")
    public List<User> getAllUsers() {
//        set return type to List<User>
        List<User> userList = repository.findAll();
        for (User u : userList) {
            List<Post> postList = u.getPosts();
            for (Post p : postList) {
                p.setVoteCount(voteRepository.countVotesByPostId(p.getId()));
            }
        }
        return userList;
    }

    @GetMapping("/api/users/{id}")
    public User getUserById(@PathVariable Integer id) {
        User returnUser = repository.getById(id);
        List<Post> postList = returnUser.getPosts();
        for (Post p : postList) {
            p.setVoteCount(voteRepository.countVotesByPostId(p.getId()));
        }
        return returnUser;
    }

    @PostMapping("/api/users")
    public User addUser(@RequestBody User user) {
//        Encrypt password
        user.setPassword(Bcrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        repository.save(user);
        return user;
    }

//    update user by id
    @PutMapping("/api/users/{id}")
//    id is extracted from url path; user is second arg, which is deserialized from req body JSON into a User obj
    public User updateUser(@PathVariable int id, @RequestBody User user) {
//        retrieves user by id from repository.
        User tempUser = repository.getById(id);
//            if user exists, sets id of user object to ID of tempUser object; ensures updated user object has correct ID before saving to repository.
        if(!tempUser.equals(null)) {
            user.setId(tempUser.getId());
            repository.save(user);
        }
        return user;
    }

    @DeleteMapping("/api/users/{id}")
//    Annotation sets http res status code to 204 - no content if user is successfully deleted
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable int id) {
        repository.deleteById(id);
    }
}



