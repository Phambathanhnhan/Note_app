package model;
import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    @Column(name = "username", unique = true, nullable = false)
    public String username;

    @Column(name = "password", nullable = false)
    public String password;

    public User() {}
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}