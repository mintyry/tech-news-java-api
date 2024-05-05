package com.technews.controller;

import com.technews.model.Post;
import com.technews.model.Vote;
import com.technews.model.User;
import com.technews.repository.PostRepository;
import com.technews.repository.UserRepository;
import com.technews.repository.VoteRepository;
import jakarta.servlet.http.HttpServletRequest;
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
//    public method that returns a single object of type Post. It takes id parameter
    public Post getPost(@PathVariable Integer id) {
//        gets post via id in db and applies it to returnPost variable
        Post returnPost = repository.getById(id);
//        sets vote count by id extended by class/repo
        returnPost.setVoteCount(voteRepository.countVotesByPostId(returnPost.getId()));

        return returnPost;
    }

//    Create post
    @PostMapping("/api/posts")
//    annotation is used to specify the HTTP status code to be returned tot he client after req is made (basically saying it was successfully created)
    @ResponseStatus(HttpStatus.CREATED)
//    public method return single post; parameter takes in post variable (object) of type Post from the req body
    public Post addPost(@RequestBody Post post) {
//        saves post object to db
        repository.save(post);
        return post;
    }

//    update post
    @PutMapping("/api/posts/{id}")
//    public method returns single post; takes two parameters: id (which post to update) and post sent in the req body
    public Post updatePost(@PathVariable int id, @RequestBody Post post){
//        access post in db
        Post tempPost = repository.getById(id);
//        use class method to update post's title
        tempPost.setTitle(post.getTitle());
//        save updated post to db
        return repository.save(tempPost);
    }

//    update vote
    @PutMapping("/api/posts/upvote")
//    public method returns string, takes vote object in req body as parameter
//    HttpServletRequest is a a special object that represents request from browser to server with various details about the req (ie: what kind of req (get, post, etc), url, parameters like form data, headers sent, etc.)
//    we use httpservleterquest here bc we need to access session info
    public String addVote(@RequestBody Vote vote, HttpServletRequest request) {
//        making empty variable of string data type
        String returnValue = "";
//getting session object; we pass in false to tell servlet that if no session exists, do NOT make a new one. Return null. If it does not return null, then follow the logic. If session does exist, follow the logic.
        if(request.getSession(false) != null){
//            making variable for a post object; value is null. Gonna use this to store Post object later.
            Post returnPost = null;
//retrieves User object stored in the session under attribute name "SESSION_USER"
//            (User) is a type cast; tells Java to treat the result of the following line as a User object. It's necessary because getAttribute returns an Object and we want that Object to be treated as User.
//            This is an example cast method chaining (result of method gets cast to User object).
            User sessionUser = (User) request.getSession().getAttribute("SESSION_USER");
//            sets user id of the vote object to the ID of the user retrieved from session.
            vote.setUserId(sessionUser.getId());
//            save to db
            voteRepository.save(vote);
//retrieve post by id (which will be the vote object's post id)
            returnPost = repository.getById(vote.getPostId());
//            update vote count
            returnPost.setVoteCount(voteRepository.countVotesByPostId(vote.getPostId()));

            returnValue = "";
        } else {
//            if no session exists, run this.
            returnValue = "login";
        }

        return returnValue;
    }

//    delete post
    @DeleteMapping("/api/posts/{id}")
//    successful response status, but no content to return
    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public method  post (return value wont be used), takes id as parameter
    public void deletePost(@PathVariable int id){
//        deletes post by id in db
        repository.deleteById(id);
    }
}
