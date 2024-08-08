package hiber.dao;

import hiber.model.Car;
import hiber.model.User;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class UserDaoImp implements UserDao {

   private final String GETUSERSBYCARQUERY = "SELECT u FROM User u JOIN u.car c WHERE c.model = :model AND c.series = :series";
   private final String GETUSERSLISTQUERY = "from User";

   private SessionFactory sessionFactory;

   @Autowired
   public UserDaoImp(SessionFactory sessionFactory) {
      this.sessionFactory = sessionFactory;
   }

   @Override
   public void addUser(User user) {
      sessionFactory.getCurrentSession().save(user);
   }

   @Override
   @SuppressWarnings("unchecked")
   public List<User> getUsersList() {
      TypedQuery<User> query=sessionFactory.getCurrentSession().createQuery(GETUSERSLISTQUERY);
      return query.getResultList();
   }

   @Override
   public User getUserByCar(String model, int series) {
      TypedQuery<User> query = sessionFactory.getCurrentSession().createQuery(GETUSERSBYCARQUERY, User.class);
      query.setParameter("model", model);
      query.setParameter("series", series);
      return query.getSingleResult();
   }
}
