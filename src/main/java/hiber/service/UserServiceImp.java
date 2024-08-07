package hiber.service;

import hiber.dao.UserDao;
import hiber.model.Car;
import hiber.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImp implements UserService {

//   Мы используем DAO класс, для того, что бы:
//
//   1. Что бы не было нагромождений в сервис-классе, не писать тут код реализации взаимодействия
//   с БД
//   2. Если у нас несколько сервис классов, то, что бы не писать в каждом реализицию работы с БД,
//   а просто обратитья в DAO, где это уже реализовано.

   @Autowired
   private UserDao userDao;

   @Transactional
   @Override
   public void add(User user) {
      userDao.add(user);
   }

   @Transactional
   @Override
   public List<User> listUsers() {
      return userDao.listUsers();
   }

   @Transactional
   @Override
   public User getUserByCar(String model, int series) {
      return userDao.getUserByCar(model, series);
   }

}
