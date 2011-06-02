/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.brixcms.rmiserver;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserService {
    private SessionFactory sf;
    private PasswordEncoder encoder;

    /**
     * Proxy constructor for spring injection
     */
    public UserService() {

    }

    @Transactional
    public User create(UserDto dto) {
        User user = new User(dto.login, encoder.encode(dto.password), dto.roles);
        user.setLocked(dto.locked);
        sf.getCurrentSession().persist(user);
        return user;
    }

    public UserDto dto(User user) {
        UserDto dto = new UserDto();
        dto.login = user.getLogin();
        dto.roles.addAll(user.getRoles());
        dto.locked = user.isLocked();
        return dto;
    }

    public User load(Long id) {
        User user = (User) sf.getCurrentSession().get(User.class, id);
        if (user == null) {
            throw new RuntimeException("Cannot load user with id: " + id);
        }
        return user;
    }

    @SuppressWarnings("unchecked")
    public List<User> query(int first, int count) {
        return sf.getCurrentSession().getNamedQuery("user.list").setFirstResult(first)
                .setMaxResults(count).list();
    }

    @Transactional
    public User query(String login, String password) {
        User result = null;

        Session session = sf.getCurrentSession();
        Query query = session.getNamedQuery("user.login");
        query.setParameter("login", login);
        result = (User) query.uniqueResult();

        if (result != null) {
            if (!encoder.check(password.trim(), result.getPasswordHash())) {
                result = null;
            }
        }

        return result;
    }

    public int queryCount() {
        return ((Number) sf.getCurrentSession().getNamedQuery("user.count").uniqueResult())
                .intValue();
    }

    public void setPasswordEncoder(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    public void setSessionFactory(SessionFactory sf) {
        this.sf = sf;
    }

    @Transactional
    public void update(User user, UserDto dto) {
        user.setLogin(dto.login);
        user.setLocked(dto.locked);
        user.getRoles().clear();
        user.getRoles().addAll(dto.roles);
    }

    @Transactional
    public void updatePassword(User user, String password) {
        user.setPasswordHash(encoder.encode(password));
    }

    public static class UserDto implements Serializable {
        private static final long serialVersionUID = 1L;
        public String login;
        public String password;
        public List<Role> roles = new ArrayList<Role>();
        public boolean locked = false;

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public List<Role> getRoles() {
            return roles;
        }

        public void setRoles(List<Role> roles) {
            this.roles = roles;
        }

        public boolean isLocked() {
            return locked;
        }

        public void setLocked(boolean locked) {
            this.locked = locked;
        }
    }
}
