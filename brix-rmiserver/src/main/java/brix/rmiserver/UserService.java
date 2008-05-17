package brix.rmiserver;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

public class UserService
{
    public static class UserDto implements Serializable
    {
        private static final long serialVersionUID = 1L;
        public String login;
        public String password;
        public List<Role> roles = new ArrayList<Role>();
        public boolean locked = false;
    }

    private SessionFactory sf;
    private PasswordEncoder encoder;

    /** Proxy constructor for spring injection */
    public UserService()
    {

    }


    public void setSessionFactory(SessionFactory sf)
    {
        this.sf = sf;
    }


    public void setPasswordEncoder(PasswordEncoder encoder)
    {
        this.encoder = encoder;
    }


    @Transactional
    public User create(UserDto dto)
    {
        User user = new User(dto.login, encoder.encode(dto.password), dto.roles);
        user.setLocked(dto.locked);
        sf.getCurrentSession().persist(user);
        return user;
    }

    @Transactional
    public void update(User user, UserDto dto)
    {
        user.setLogin(dto.login);
        user.setLocked(dto.locked);
        user.getRoles().clear();
        user.getRoles().addAll(dto.roles);
    }

    @Transactional
    public void updatePassword(User user, String password)
    {
        user.setPasswordHash(encoder.encode(password));
    }

    public UserDto dto(User user)
    {
        UserDto dto = new UserDto();
        dto.login = user.getLogin();
        dto.roles.addAll(user.getRoles());
        dto.locked = user.isLocked();
        return dto;
    }

    @SuppressWarnings("unchecked")
    public List<User> query(int first, int count)
    {
        return sf.getCurrentSession().getNamedQuery("user.list").setFirstResult(first)
            .setMaxResults(count).list();
    }

    public int queryCount()
    {
        return ((Number)sf.getCurrentSession().getNamedQuery("user.count").uniqueResult())
            .intValue();
    }

    @Transactional
    public User query(String login, String password)
    {
        User result = null;

        Session session = sf.getCurrentSession();
        Query query = session.getNamedQuery("user.login");
        query.setParameter("login", login);
        result = (User)query.uniqueResult();

        if (result != null)
        {
            if (!encoder.check(password, result.getPasswordHash()))
            {
                result = null;
            }
        }

        return result;

    }

    public User load(Long id)
    {
        User user = (User)sf.getCurrentSession().get(User.class, id);
        if (user == null)
        {
            throw new RuntimeException("Cannot load user with id: " + id);
        }
        return user;
    }


}
