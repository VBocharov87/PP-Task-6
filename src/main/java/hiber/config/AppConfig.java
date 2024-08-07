package hiber.config;

import hiber.model.Car;
import hiber.model.User;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;


@Configuration
@PropertySource("classpath:db.properties")      // ДЛЯ УКАЗАНИЯ ФАЙЛА СВОЙСТВ
@EnableTransactionManagement        // заменяет <tx:annotation-driven transaction-manager="transactionManager" />
// для того, что бы не открывать / закрывать транзакции самостоятельно мы используем transactionManager и над методом должны писать @Transactional а для активации такой аннотации нужно вот это самое выражение

@ComponentScan(value = "hiber")

public class AppConfig {

   @Autowired
   private Environment env;   //  позволяет извлекать значения из различных источников конфигурации, таких как свойства, переменные окружения и системные свойства
                              // позволяет получать значения свойств, определенных в конфигурационных файлах (например, application.properties



//   Почему использовать ComboPooledDataSource?
//   Пул соединений: ComboPooledDataSource предоставляет возможность создания пула соединений, что позволяет уменьшить
//   время, затрачиваемое на создание новых соединений с базой данных. Это особенно полезно в высоконагруженных
//   приложениях.
//
//   Дополнительные настройки: Вы можете настраивать различные параметры пула соединений, такие как минимальное и
//   максимальное количество соединений, время жизни соединения, прирост соединений и т.д.
//   Производительность: Использование пула соединений может значительно улучшить производительность вашего приложения
//   за счет повторного использования соединений и уменьшения накладных расходов на создание и закрытие соединений.
//
//   Использование ComboPooledDataSource или любого другого источника данных с поддержкой пула соединений рекомендуется
//   для продакшн-среды, где важна производительность и стабильность.
//   DriverManagerDataSource может быть использован в простых или тестовых сценариях, где требования к производительности
//   не столь критичны.

//    Просто фишка в том, что Заур исользовал ComboPooledDataSource для получения DataSource

//    В дальнейшем DataSource в приложении не используется, нужен именно в конфиге Спринга для подключения к БД.

   @Bean
   public DataSource getDataSource() {
      DriverManagerDataSource dataSource = new DriverManagerDataSource();
      dataSource.setDriverClassName(env.getProperty("db.driver"));
      dataSource.setUrl(env.getProperty("db.url"));
      dataSource.setUsername(env.getProperty("db.username"));
      dataSource.setPassword(env.getProperty("db.password"));
      return dataSource;
   }

//Раньше (при работе только с Hibernate) мы создавали фабрику сессий именно для Хибера:
//
//   public SessionFactory getSessionFactory() {
//      if (sessionFactory == null || sessionFactory.isClosed()) {
//         try {
//            org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration();
//            configuration.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/users?useSSL=false&serverTimezone=UTC");
//            configuration.setProperty("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
//            configuration.setProperty("hibernate.connection.username", "root");
//            configuration.setProperty("hibernate.connection.password", "root");
//            configuration.setProperty("hibernate.current_session_context_class", "thread");
//            configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
//            configuration.setProperty("hibernate.show_sql", "true");
//            configuration.addAnnotatedClass(jm.task.core.jdbc.model.User.class);
//            return configuration.buildSessionFactory();
//         } catch (Throwable ex) {
//            System.err.println("Initial SessionFactory creation failed." + ex);
//         }
//      }
//      return sessionFactory;
//   }

//   Теперть же мы просто скармливаем SessionFactory наш DataSource и устанавливаем доп. свойства:

   @Bean
   public LocalSessionFactoryBean getSessionFactory() {
      LocalSessionFactoryBean factoryBean = new LocalSessionFactoryBean();
      factoryBean.setDataSource(getDataSource());
      
      Properties props=new Properties();
      props.put("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
      props.put("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));

//      hbm2ddl.auto может изменять столбцы базы данных, создавать базу данных, устанавливать тип содержащихся в ней
//      значений (добавляет новые столбцы, если они отсутствуют, но не удаляет существующие столбцы или таблицы)
//      конкретную таблицу мы не указываем здесь, а указываем в сущности @Table(name = "users")

//      но фишка тут в том, что если БД была создана ранее и не установлены значения полей по-умолч,
//      то, он создаст доп. столбцы, но не сможет заполнить прдд. значения (т.к. нет по-умолч,
//      null например)

      factoryBean.setHibernateProperties(props);
      factoryBean.setAnnotatedClasses(User.class, Car.class);
      return factoryBean;
   }

//   используем HibernateTransactionManager для того, что бы не открывать / закрывать транзакции
//   самостоятельно

   @Bean
   public HibernateTransactionManager getTransactionManager() {
      HibernateTransactionManager transactionManager = new HibernateTransactionManager();
      transactionManager.setSessionFactory(getSessionFactory().getObject());
      return transactionManager;
   }
}
