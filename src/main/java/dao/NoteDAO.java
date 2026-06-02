package dao;
import model.Note;
import security.NoteEncryptor;
import utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.ArrayList;
import java.util.List;

public class NoteDAO {

    public List<String> getUniqueLabels(int userId) {
        List<String> labels = new ArrayList<>();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT DISTINCT label FROM Note WHERE userId = :uid AND label IS NOT NULL AND trim(label) != '' AND isTrashed = false";
            Query<String> query = session.createQuery(hql, String.class);
            query.setParameter("uid", userId);
            labels = query.list();
        } catch (Exception e) { e.printStackTrace(); }
        return labels;
    }

    public List<Note> getnotes(int userId, String keyword, String viewType, String labelFilter) {
        List<Note> filteredList = new ArrayList<>();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Note WHERE userId = :uid";
            Query<Note> query = session.createQuery(hql, Note.class);
            query.setParameter("uid", userId);
            List<Note> allNotes = query.list();

            for (Note n : allNotes) {
                boolean matchView = false;
                if ("trash".equals(viewType)) { matchView = n.isTrashed; }
                else if ("archive".equals(viewType)) { matchView = n.isArchived && !n.isTrashed; }
                else if ("reminders".equals(viewType)) { matchView = (n.reminder != null && !n.reminder.isEmpty()) && !n.isTrashed; }
                else if ("label".equals(viewType)) { matchView = !n.isTrashed && n.label != null && n.label.equals(labelFilter); }
                else if ("all".equals(viewType)) { matchView = true; }
                else { matchView = !n.isArchived && !n.isTrashed; }

                if (!matchView) continue;

                String decContent = NoteEncryptor.decrypt(n.content);
                String decTitle = NoteEncryptor.decrypt(n.title);
                String lowerKeyword = keyword.toLowerCase();
                String labelStr = (n.label != null) ? n.label.toLowerCase() : "";

                if (decTitle.isEmpty() && decContent.isEmpty() && matchView && "label".equals(viewType)) {
                    continue;
                }

                if (decTitle.toLowerCase().contains(lowerKeyword) || decContent.toLowerCase().contains(lowerKeyword) || labelStr.contains(lowerKeyword)) {
                    n.content = decContent;
                    n.title = decTitle;
                    filteredList.add(n);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return filteredList;
    }

    public boolean addnote(int userId, String title, String content, String label, String reminder) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Note note = new Note(userId, NoteEncryptor.encrypt(title), NoteEncryptor.encrypt(content), label, reminder);
            session.save(note);
            tx.commit();
            return true;
        } catch (Exception e) { if (tx != null) tx.rollback(); return false; }
    }

    public boolean updatenote(int id, String title, String content, String label, String reminder) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Note note = session.get(Note.class, id);
            if (note != null) {
                note.title = NoteEncryptor.encrypt(title);
                note.content = NoteEncryptor.encrypt(content);
                note.label = label;
                note.reminder = reminder;
                session.update(note);
                tx.commit();
                return true;
            }
            return false;
        } catch (Exception e) { if (tx != null) tx.rollback(); return false; }
    }

    public boolean changeStatus(int id, boolean isArchived, boolean isTrashed) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Note note = session.get(Note.class, id);
            if (note != null) {
                note.isArchived = isArchived;

                if (isTrashed && !note.isTrashed) {
                    note.trashedAt = new java.sql.Timestamp(System.currentTimeMillis());
                } else if (!isTrashed) {
                    note.trashedAt = null;
                }
                note.isTrashed = isTrashed;

                session.update(note);
                tx.commit();
                return true;
            }
            return false;
        } catch (Exception e) { if (tx != null) tx.rollback(); return false; }
    }

    public boolean deletePermanently(int id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Note note = session.get(Note.class, id);
            if (note != null) { session.delete(note); tx.commit(); return true; }
            return false;
        } catch (Exception e) { if (tx != null) tx.rollback(); return false; }
    }

    public boolean createEmptyLabel(int userId, String label) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Note note = new Note(userId, NoteEncryptor.encrypt(""), NoteEncryptor.encrypt(""), label, "");
            note.isArchived = true;
            session.save(note);
            tx.commit();
            return true;
        } catch (Exception e) { if (tx != null) tx.rollback(); return false; }
    }

    public boolean renameLabel(int userId, String oldLabel, String newLabel) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            String hql = "UPDATE Note SET label = :newLabel WHERE userId = :uid AND label = :oldLabel";
            Query query = session.createQuery(hql);
            query.setParameter("newLabel", newLabel);
            query.setParameter("uid", userId);
            query.setParameter("oldLabel", oldLabel);
            query.executeUpdate();
            tx.commit();
            return true;
        } catch (Exception e) { if (tx != null) tx.rollback(); return false; }
    }

    public boolean deleteLabel(int userId, String label) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            String hql = "UPDATE Note SET label = '' WHERE userId = :uid AND label = :oldLabel";
            Query query = session.createQuery(hql);
            query.setParameter("uid", userId);
            query.setParameter("oldLabel", label);
            query.executeUpdate();

            String hqlDelete = "DELETE FROM Note WHERE userId = :uid AND label = '' AND title = :emptyTitle";
            Query queryDel = session.createQuery(hqlDelete);
            queryDel.setParameter("uid", userId);
            queryDel.setParameter("emptyTitle", NoteEncryptor.encrypt(""));
            queryDel.executeUpdate();

            tx.commit();
            return true;
        } catch (Exception e) { if (tx != null) tx.rollback(); return false; }
    }

    public Note getNoteByExactTitle(int userId, String title) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Note WHERE userId = :uid AND title = :encTitle AND isTrashed = false";
            Query<Note> query = session.createQuery(hql, Note.class);
            query.setParameter("uid", userId);
            query.setParameter("encTitle", NoteEncryptor.encrypt(title));
            List<Note> list = query.list();
            if (!list.isEmpty()) return list.get(0);
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public void cleanExpiredTrash() {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            long sevenDaysAgo = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000);
            java.sql.Timestamp threshold = new java.sql.Timestamp(sevenDaysAgo);

            String hql = "DELETE FROM Note WHERE isTrashed = true AND trashedAt <= :threshold";
            Query query = session.createQuery(hql);
            query.setParameter("threshold", threshold);
            int deletedCount = query.executeUpdate();
            tx.commit();

            if(deletedCount > 0) {
                System.out.println("[Trash Cleanup] Automatically deleted " + deletedCount + " trashed notes older than 7 days.");
            }
        } catch (Exception e) { if (tx != null) tx.rollback(); e.printStackTrace(); }
    }
}