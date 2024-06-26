package com.technews.controller;

import com.technews.model.Comment;
import com.technews.model.Post;
import com.technews.model.User;
import com.technews.model.Vote;
import com.technews.repository.CommentRepository;
import com.technews.repository.PostRepository;
import com.technews.repository.UserRepository;
import com.technews.repository.VoteRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

//    post endpoint -- add a post from dboard
    @PostMapping("/posts")
    public String addPostDashboardPage(@ModelAttribute Post post, Model model, HttpServletRequest request) {
//if title and link are empty... redirect to notice
        if ((post.getTitle().equals(null) || post.getTitle().isEmpty()) || (post.getPostUrl().equals(null) || post.getPostUrl().isEmpty())) {
            return "redirect:/dashboardEmptyTitleAndLink";
        }
//if sesh aint valid, bring to login
        if(request.getSession(false) == null){
            return "redirect:/login";
        } else {
//            if sesh is valid, set user id for that post to be id of sessionUser
            User sessionUser = (User) request.getSession().getAttribute("SESSION_USER");
            post.setUserId(sessionUser.getId());
            postRepository.save(post);

            return "redirect:/dashboard";
        }
    }

//    update endpoint
    @PostMapping("/posts/{id}")
    public String updatePostDashboardPage(@PathVariable int id, @ModelAttribute Post post, Model model, HttpServletRequest request) {
//        invalid sesh redirects to login
        if (request.getSession(false) == null) {
            model.addAttribute("user", new User());
            return "redirect/dashboard";
        } else {
//            if it is valid, get specific post by id; take updated title data and save it as new title via setTitle
            Post tempPost = postRepository.getById(id);
            tempPost.setTitle(post.getTitle());
            postRepository.save(tempPost);

            return "redirect:/dashboard";
        }

    } //ends update block

//    comment endpoint - user can comment on own or others' posts
    @PostMapping("/comments")
    public String createCommentCommentsPage(@ModelAttribute Comment comment, Model model, HttpServletRequest request) {
//if comment is blank, redirect to notice and return them to the edit-post template, done by getting post id of the comment
        if (comment.getCommentText().isEmpty() || comment.getCommentText().equals(null)) {
            return "redirect:/singlePostEmptyComment/" + comment.getPostId();
        } else {
//            if session is invalid, return to login page
//            if session IS valid, set comment userId to equal to current sessionUser, bc current session's user is the one who made comment
            if (request.getSession(false) != null) {
                User sessionUser = (User) request.getSession().getAttribute("SESSION_USER");
                comment.setUserId(sessionUser.getId());
//                save comment and return to edit post template with new comment created
                commentRepository.save(comment);
                return "redirect:/post/" + comment.getPostId();
            } else {
                return "login";
            }
        }
    }

//    edit comment endpoint
    @PostMapping("/comments/edit")
    public String createCommentEditPage(@ModelAttribute Comment comment, HttpServletRequest request) {
//if text is empty, notice to user to fill text and return to edit post template
        if (comment.getCommentText().equals("") || comment.getCommentText().equals(null)) {
            return "redirect:/editPostEmptyComment/" + comment.getPostId();
        } else {
//            if sesh valid, set comment userId equal to sessionUser's id, then save updated comment and redirect to edit page
            if (request.getSession(false) != null) {
                User sessionUser = (User) request.getSession().getAttribute("SESSION_USER");
                comment.setUserId(sessionUser.getId());
                commentRepository.save(comment);

                return "redirect:/dashboard/edit/" + comment.getPostId();
            } else {
//                if sesh invalid, login page
                return "redirect:/login";
            }
        }
    }

//    upvote endpoint
    @PutMapping("/posts/upvote")
    public void addVoteCommentsPage(@RequestBody Vote vote, HttpServletRequest request, HttpServletResponse response) {
//        if session is valid, set vote userId = to sessionUser id
        if(request.getSession(false) != null){
            Post returnPost = null;
            User sessionUser = (User) request.getSession().getAttribute("SESSION_USER");
            vote.setUserId(sessionUser.getId());
//            save vote and then increment by 1
            voteRepository.save(vote);

            returnPost = postRepository.getById(vote.getPostId());
            returnPost.setVoteCount(voteRepository.countVotesByPostId(vote.getPostId()));
        }
    }

}
