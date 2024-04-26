//declares this class belongs to model package
package com.technews.model;

//imports include necessary classes from external libraries that we use (eg: List)
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

//persisting object so it can map to a table; indicates instances of this class will be entities that can be stored in a db.
@Entity
//ignore these properties when converted to JSON object; this instructs Jackson (a JSON library) to ignore certain properties when serializing object to JSON. The properties in this case are hibernateLazyInitializer and handler; theyre typically added to Hibernate-managed entities by the Hibernate framework and used for lazy loading and proxy managements. Not relevant in JSON serialization.
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
//Name the table
@Table(name = "user")
public class User implements Serializable {
//    indicates id field is primary key for the entity
    @Id
//    specifies strategy for generating values for the id field automatically
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String username;

    @Column(unique = true)
    private String email;

    private String password;

    @Transient
    boolean loggedIn;

//    mappedBy: specifies the field in the related entity that owns the relationship
//    operations like delete on this entity will cascade to the related entities
//    fetch strat; eager: asap; lazy: when needed
    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Post> posts;

//    Need to use FetchType.LAZY to resolve multiple bags exception
    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Vote> votes;

//    FetchType.LAZY to resolve multiple bags
    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments;

//    The empty constructor allows instances of the User class to be created without providing any initial values for the fields. This is useful in scenarios where you want to create a User object first and then set its properties later.
//Framework Compatibility:
//Some frameworks, such as Hibernate, require a default (no-argument) constructor to instantiate objects when retrieving them from a database. Having an empty constructor ensures compatibility with such frameworks.
//Java Bean Convention:
//Following the Java Bean convention, having a no-argument constructor allows the class to be easily used in JavaBeans contexts, where objects are created using default constructors and properties are set using setter methods.
    public User() {
    }

    public User(Integer id, String username, String email, String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
    }

//    getters and setters allow methods to access and modify fields of the User class
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public List<Vote> getVotes() {
        return votes;
    }

    public void setVotes(List<Vote> votes) {
        this.votes = votes;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

//    @Override overrides the default method in the Object class.
    @Override
//
    public boolean equals(Object o) {
//        compares this object to object o
        if (this == o) return true;
//        if null or not an instance of this class, returns false
        if (o == null || getClass() != o.getClass()) return false;
//        casting operation, which casts Object o to a  User object to make it possible to compare User objects and instances
        User user = (User) o;
//        checks every field against the isLoggedIn()
        return isLoggedIn() == user.isLoggedIn() && Objects.equals(getId(), user.getId()) && Objects.equals(getUsername(), user.getUsername()) && Objects.equals(getEmail(), user.getEmail()) && Objects.equals(getPassword(), user.getPassword()) && Objects.equals(getPosts(), user.getPosts()) && Objects.equals(getVotes(), user.getVotes()) && Objects.equals(getComments(), user.getComments());
    }

    @Override
//    generates hash code value for the object based on the User fields; combines each hash to generate a final hash for the object.
    public int hashCode() {
        return Objects.hash(getId(), getUsername(), getEmail(), getPassword(), isLoggedIn(), getPosts(), getVotes(), getComments());
    }

    @Override
//    returns a string representation of the object
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", loggedIn=" + loggedIn +
                ", posts=" + posts +
                ", votes=" + votes +
                ", comments=" + comments +
                '}';
    }
}
