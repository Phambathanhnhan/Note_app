package dao;
import model.User;
import security.PasswordHasher;
import utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public class UserDAO {
    public int login(String username, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM User WHERE username = :user AND password = :pass";
            Query<User> query = session.createQuery(hql, User.class);
            query.setParameter("user", username);
            query.setParameter("pass", PasswordHasher.hashpassword(password));

            User user = query.uniqueResult();
            return user != null ? user.id : -1;
        } catch (Exception e) { return -1; }
    }

    public boolean register(String username, String password) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            User user = new User(username, PasswordHasher.hashpassword(password));
            session.save(user);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            return false;
        }
    }
}