package com.technews.controller;

import com.technews.model.Post;
import com.technews.repository.CommentRepository;
import com.technews.repository.PostRepository;
import com.technews.repository.UserRepository;
import com.technews.repository.VoteRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

//import Model class from Spring framework
import org.springframework.ui.Model;
//import User model for use
import com.technews.model.User;

import java.util.List;


//Reminder: Autowired informs Spring to scan for objects that need to be instantiated for a class or method to run

//we included repos we created before and mapped them to objects that will be instantiated in this class when necessary.
@Controller
public class HomePageController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    VoteRepository voteRepository;

    @Autowired
    CommentRepository commentRepository;

//    login endpoint
    @GetMapping("/login")
//    Model is from Spring framework; remapped this to "model" variable
    public String login(Model model, HttpServletRequest request) {

        if (request.getSession(false) != null) {
            return "redirect:/";
        }
//addAttribute is a method to send info to template
//        in this case, we send newly created user to the template as string user so it can be displayed in the template. Once logged in, user is redirected to / route (homepage)
        model.addAttribute("user", new User());
        return "login";
    }

//    logout endpoint
    @GetMapping("/users/logout")
//    this route checks if session exists; if yes, invalidate session and log user out and redirect to login route
    public String logout(HttpServletRequest request) {
        if (request.getSession(false) != null) {
            request.getSession().invalidate();
        }
        return "redirect:/login";
    }

//    homepage endpoint
    @GetMapping("/")
    public String homepageSetup(Model model, HttpServletRequest request) {
        User sessionUser = new User();
// check if user is logged in, if so...
        if (request.getSession(false) != null) {
            sessionUser = (User) request.getSession().getAttribute("SESSION_USER");
            model.addAttribute("loggedIn", sessionUser.isLoggedIn());
        } else {
            model.addAttribute("loggedIn", false);
        }

//retrieve post data
//        make postList variable
        List<Post> postList = postRepository.findAll();
//        for loop gets all posts and populates into postList variable
        for (Post p : postList) {
            p.setVoteCount(voteRepository.countVotesByPostId(p.getId()));
            User user = userRepository.getById(p.getUserId());
            p.setUserName(user.getUsername());
        }
//add the postList and loggedIn details to the user model
        model.addAttribute("postList", postList);
        model.addAttribute("loggedIn", sessionUser.isLoggedIn());

        // "point" and "points" attributes refer to upvotes.
        model.addAttribute("point", "point");
        model.addAttribute("points", "points");

//        display this data in homepage
        return "homepage";
    }
}
