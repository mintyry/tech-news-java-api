package com.technews.controller;

import com.technews.model.Post;
import com.technews.model.User;
import com.technews.repository.CommentRepository;
import com.technews.repository.PostRepository;
import com.technews.repository.UserRepository;
import com.technews.repository.VoteRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

//import Model class from Spring framework
import org.springframework.ui.Model;

@Controller
public class TechNewsController {

    @Autowired
    PostRepository postRepository;

    @Autowired
    VoteRepository voteRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CommentRepository commentRepository;

// log in
    @PostMapping("/users/login")
    public String login(@ModelAttribute User user, Model model, HttpServletRequest request) throws Exception {
// checks all fields were filled in correctly
//        strings cannot be compared with == operator; use .equals()
        if ((user.getPassword().equals(null) || user.getPassword().isEmpty()) || (user.getEmail().equals(null) || user.getPassword().isEmpty())) {
            model.addAttribute("notice", "Email address and password must be populated in order to log in!");
            return "login";
        }

//        if user is found by email, make new session variable & eventually set loggedIn to true and return dashboard template, if not found, redirect to login
        User sessionUser = userRepository.findUserByEmail(user.getEmail());

        try {
//            if sessionUser is invalid, running .equals() will throw an error
            if (sessionUser.equals(null)) {

            }
//            We catch error and notify client that email isnt recognized
        } catch (NullPointerException e) {
            model.addAttribute("notice", "Email is not recognized!");
            return "login";
        }

//        Validate pw
        String sessionUserPassword = sessionUser.getPassword();
        boolean isPasswordValid = BCrypt.checkpw(user.getPassword(), sessionUserPassword);
        if(isPasswordValid == false) {
            model.addAttribute("notice", "Password is not valid!");
            return "login";
        }

        sessionUser.setLoggedIn(true);
        request.getSession().setAttribute("SESSION_USER", sessionUser);

//        return the dashboard template
        return "redirect:/dashboard";
    }

//    add user
//    make sure all fields are filled
    @PostMapping("/users")
        public String signup(@ModelAttribute User user, Model model, HttpServletRequest request) throws Exception {
        if ((user.getUsername().equals(null) || user.getUsername().isEmpty()) || (user.getPassword().equals(null) || user.getPassword().isEmpty()) || (user.getEmail().equals(null) || user.getPassword().isEmpty())) {
            model.addAttribute("notice", "Fill out all fields to sign up!");
            return "login";
        }

        try {
//            encrypt pw then save user to db; if successful, redirects to login page where they can enter new credentials
            user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("notice", "Email address is not available -- choose a different one");
            return "login";
        }

        User sessionUser = userRepository.findUserByEmail(user.getEmail());
//            if fail to add user to db, show error message and have them try to login again
        try {
            if (sessionUser.equals(null)) {

            }
        } catch (NullPointerException e) {
            model.addAttribute("notice", "User is not recognized.");
            return "login";
        }

        sessionUser.setLoggedIn(true);
        request.getSession().setAttribute("SESSION_USER", sessionUser);

       return "redirect:/dashboard";
    }

//    post endpoint
    @PostMapping("/posts")
    public String addPostDashboardPage(@ModelAttribute Post post, Model model, HttpServletRequest request) {

        if ((post.getTitle().equals(null) || post.getTitle().isEmpty()) || (post.getPostUrl().equals(null) || post.getPostUrl().isEmpty())) {
            return "redirect:/dashboardEmptyTitleAndLink";
        }

        if(request.getSession(false) == null){
            return "redirect:/login";
        } else {
            User sessionUser = (User) request.getSession().getAttribute("SESSION_USER");
            post.setUserId(sessionUser.getId());
            postRepository.save(post);

            return "redirect:/dashboard";
        }
    }

}
