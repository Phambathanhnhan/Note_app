package model;
import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "notes")
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    @Column(name = "user_id", nullable = false)
    public int userId;

    @Column(name = "title", columnDefinition = "TEXT", nullable = false)
    public String title;

    @Column(name = "content", columnDefinition = "TEXT")
    public String content;

    @Column(name = "label")
    public String label;

    @Column(name = "reminder")
    public String reminder;

    @Column(name = "is_archived")
    public boolean isArchived;

    @Column(name = "is_trashed")
    public boolean isTrashed;

    // CỘT MỚI: Theo dõi thời gian nằm trong thùng rác
    @Column(name = "trashed_at")
    public Timestamp trashedAt;

    public Note() {}

    public Note(int userId, String title, String content, String label, String reminder) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.label = label;
        this.reminder = reminder;
        this.isArchived = false;
        this.isTrashed = false;
        this.trashedAt = null;
    }
}