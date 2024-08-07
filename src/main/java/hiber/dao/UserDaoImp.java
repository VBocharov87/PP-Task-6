package hiber.dao;

import hiber.model.Car;
import hiber.model.User;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.util.List;

//    @Repository - аннотация для DAO классов, содержит в себе @Component, класс будет добавлен в виде
//    бина в контейнер

@Repository
public class UserDaoImp implements UserDao {

   @Autowired
   private SessionFactory sessionFactory;

   @Override
   public void add(User user) {
      sessionFactory.getCurrentSession().save(user);
   }

   @Override
   @SuppressWarnings("unchecked")
   public List<User> listUsers() {

//      типизированный запрос. Он позволяет выполнять запросы, возвращающие результаты определенного
//      типа, что повышает безопасность типов и упрощает работу с результатами запросов

//      TypedQuery — это часть JPA API.
//      session.createQuery — это часть Hibernate API.

//      Основное отличие заключается в используемом API: JPA (TypedQuery и EntityManager) или
//      Hibernate (Session и createQuery с указанием класса результата).

//      В данном случае используется Hiberante весрия (не JPA), т.к. в JPA мы бы использовали
//      EntityManager (JPA роль Hibernate Session выполняет именно он):
//
//      @Repository
//      public class Emp_DAO_Impl implements EMP_DAO {
//
//         @Autowired
//         private EntityManager entityManager;
//
//         public Emp_DAO_Impl() {
//         }
//
//         @Override
//         public List<Employee> getAllEmps() {
//            Session session = entityManager.unwrap(Session.class);				// EntityManager является оберткой над Session и мы вытаскиваем
//            return session.createQuery("from Employee", Employee.class).getResultList();			сессию оттуда
//         }
//      }

//      Использование TypedQuery в Hibernate полезно, когда вам нужно управлять запросом более
//      детально, чем просто получить результаты. Например, если вы хотите установить параметры
//      запроса (для защиты от SQL инъекций, например установить восзраст > 18),
//      управлять пагинацией (первые 10 записей, с какой записи начинать работу и т.п.)


      TypedQuery<User> query=sessionFactory.getCurrentSession().createQuery("from User");
      return query.getResultList();
   }

   @Override
   public User getUserByCar(String model, int series) {
      String hql = "SELECT u FROM User u JOIN u.car c WHERE c.model = :model AND c.series = :series";
      TypedQuery<User> query = sessionFactory.getCurrentSession().createQuery(hql, User.class);
      query.setParameter("model", model);
      query.setParameter("series", series);

//      String hql = "SELECT c FROM Car c WHERE c.model = :name AND c.series = :series";
//      TypedQuery<Car> query = sessionFactory.getCurrentSession().createQuery(hql, Car.class);
//      query.setParameter("name", model);
//      query.setParameter("series", series);
//      Car car = query.getSingleResult();

      return query.getSingleResult();
   }
}
