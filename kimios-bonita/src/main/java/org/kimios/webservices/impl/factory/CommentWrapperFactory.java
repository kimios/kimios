package org.kimios.webservices.impl.factory;

import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.bpm.comment.Comment;
import org.bonitasoft.engine.identity.UserNotFoundException;
import org.kimios.webservices.pojo.CommentWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommentWrapperFactory {
    private static Logger log = LoggerFactory.getLogger(CommentWrapperFactory.class);

    public static CommentWrapper createCommentWrapper(Comment comment, IdentityAPI identityAPI) throws UserNotFoundException {
        CommentWrapper wrapper = new CommentWrapper();
        wrapper.setId(comment.getId());
        wrapper.setContent(comment.getContent());
        wrapper.setPostDate(comment.getPostDate());
        wrapper.setProcessInstanceId(comment.getProcessInstanceId());
        wrapper.setTenantId(comment.getTenantId());

        try {
            wrapper.setUserWrapper(UserWrapperFactory.createUserWrapper(identityAPI.getUser(comment.getUserId())));
        } catch (Exception e) {
            log.error("No user for comment " + e.getMessage(), e);
        }

        return wrapper;
    }
}
