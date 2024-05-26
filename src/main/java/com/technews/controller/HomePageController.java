package com.technews.controller;

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
}
