package com.technews.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

//persisting object so it can map to a table
@Entity
//ignore these properties when converted to JSON object
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
//Name the table
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String username;

    @Column(unique = true)
    private String email;

    private String password;
    
    @Transient
    boolean loggedIn;

    private List<Post> posts;
    private List<Vote> votes;
    private List<Comment> comments;
}
