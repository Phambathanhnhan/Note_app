package dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Note;

public class NoteDAO {
    public List<Note> getnotes(int userId, String keyword) {
        List<Note> list = new ArrayList<>();
        String sql = "SELECT * FROM notes WHERE user_id = ? AND (title LIKE ? OR content LIKE ?)";
        try (Connection conn = DBConnection.getconnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, "%" + keyword + "%");
            stmt.setString(3, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new Note(rs.getInt("id"), rs.getString("title"), rs.getString("content")));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public boolean addnote(int userId, String title, String content) {
        String sql = "INSERT INTO notes (user_id, title, content) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getconnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, title);
            stmt.setString(3, content);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) { return false; }
    }

    public boolean updatenote(int id, String title, String content) {
        String sql = "UPDATE notes SET title = ?, content = ? WHERE id = ?";
        try (Connection conn = DBConnection.getconnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.setString(2, content);
            stmt.setInt(3, id);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) { return false; }
    }

    public boolean deletenote(int id) {
        String sql = "DELETE FROM notes WHERE id = ?";
        try (Connection conn = DBConnection.getconnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) { return false; }
    }
}