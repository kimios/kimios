/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2014  DevLib'
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * aong with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
