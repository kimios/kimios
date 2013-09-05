package org.kimios.webservices.impl.factory;

import org.bonitasoft.engine.identity.User;
import org.kimios.webservices.pojo.UserWrapper;

public class UserWrapperFactory {
    public static UserWrapper createUserWrapper(User user) {
        UserWrapper wrapper = new UserWrapper();
        wrapper.setId(user.getId());
        wrapper.setUserName(user.getUserName());
        wrapper.setCreatedBy(user.getCreatedBy());
        wrapper.setCreationDate(user.getCreationDate());
        wrapper.setFirstName(user.getFirstName());
        wrapper.setLastName(user.getLastName());
        wrapper.setIconName(user.getIconName());
        wrapper.setIconPath(user.getIconPath());
        wrapper.setJobTitle(user.getJobTitle());
        wrapper.setLastConnection(user.getLastConnection());
        wrapper.setLastUpdate(user.getLastUpdate());
        wrapper.setManagerUserId(user.getManagerUserId());
        wrapper.setTitle(user.getTitle());
        return wrapper;
    }
}
