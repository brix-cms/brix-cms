package brix.rmiserver.web.admin;

import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import brix.rmiserver.User;
import brix.rmiserver.UserService;

public class UserModel extends LoadableDetachableModel<User>
{
    private static final long serialVersionUID = 1L;

    @SpringBean
    private UserService users;

    private final Long id;

    public UserModel(User user)
    {
        super(user);
        InjectorHolder.getInjector().inject(this);
        this.id = user.getId();
    }

    @Override
    protected User load()
    {
        return users.load(id);
    }


}
