package brix.rmiserver;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.CollectionOfElements;

@NamedQueries( { @NamedQuery(name = "user.login", query = "FROM User WHERE login=:login"),
        @NamedQuery(name = "user.list", query = "FROM User ORDER BY login"),
        @NamedQuery(name = "user.count", query = "SELECT COUNT(*) FROM User") })
@Entity
@Table(name = "rmiserver_user")
public class User
{
    @Id
    @GeneratedValue
    @Column(name="u_id")
    private Long id;

    @Column(length = 32, nullable = false, unique = true, name = "u_login")
    private String login;

    @Column(length = 128, nullable = false, name = "u_pwhash")
    private String passwordHash;

    @CollectionOfElements(fetch = FetchType.EAGER)
    @Column(length = 32)
    @Enumerated(EnumType.STRING)
    @JoinTable(name = "rmiserver_user_role")
    @JoinColumn(name="fk_user_id")
    private Set<Role> roles;

    @Column(name = "u_locked")
    private boolean locked = false;

    /** Hibernate constructor */
    User()
    {

    }

    public User(String login, String passwordHash, Collection<Role> roles)
    {
        this.login = login;
        this.passwordHash = passwordHash;
        this.roles = new HashSet<Role>(roles);
    }


    public User(String login, String passwordHash, Role... roles)
    {
        this(login, passwordHash, Arrays.asList(roles));
    }


    public boolean isLocked()
    {
        return locked;
    }

    public void setLocked(boolean locked)
    {
        this.locked = locked;
    }

    public Long getId()
    {
        return id;
    }

    public String getLogin()
    {
        return login;
    }

    public void setLogin(String login)
    {
        this.login = login;
    }

    public String getPasswordHash()
    {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash)
    {
        this.passwordHash = passwordHash;
    }

    public Set<Role> getRoles()
    {
        return roles;
    }


}
