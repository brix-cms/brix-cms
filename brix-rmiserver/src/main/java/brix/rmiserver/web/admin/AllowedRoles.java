package brix.rmiserver.web.admin;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import brix.rmiserver.Role;

@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AllowedRoles {
    Role[] value();
}
