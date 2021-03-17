/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2015  DevLib'
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
package org.kimios.kernel.controller.impl;

import org.kimios.exceptions.*;
import org.kimios.kernel.configuration.Config;
import org.kimios.kernel.controller.AKimiosController;
import org.kimios.kernel.controller.IExtensionController;
import org.kimios.kernel.dms.*;
import org.kimios.kernel.dms.model.DMEntityAttribute;
import org.kimios.kernel.dms.model.*;
import org.kimios.kernel.events.model.EventContext;
import org.kimios.api.events.annotations.DmsEvent;
import org.kimios.api.events.annotations.DmsEventName;
import org.kimios.kernel.mail.MailTemplate;
import org.kimios.kernel.mail.Mailer;
import org.kimios.kernel.security.model.Role;
import org.kimios.kernel.security.SecurityAgent;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.share.model.Share;
import org.kimios.kernel.share.model.ShareStatus;
import org.kimios.kernel.user.model.AuthenticationSource;
import org.kimios.kernel.user.model.User;
import org.kimios.kernel.user.impl.HAuthenticationSource;
import org.kimios.kernel.utils.PasswordGenerator;
import org.kimios.kernel.utils.TemplateUtil;
import org.kimios.utils.configuration.ConfigurationManager;
import org.kimios.utils.extension.IExtensionRegistryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

;import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Transactional
public class ExtensionController extends AKimiosController implements IExtensionController {

    private static Logger logger = LoggerFactory.getLogger(ExtensionController.class);

    private IExtensionRegistryManager extensionRegistryManager;

    public IExtensionRegistryManager getExtensionRegistryManager() {
        return extensionRegistryManager;
    }

    public void setExtensionRegistryManager(IExtensionRegistryManager extensionRegistryManager) {
        this.extensionRegistryManager = extensionRegistryManager;
    }

    /* (non-Javadoc)
        * @see org.kimios.kernel.controller.impl.IExtensionController#setAttribute(org.kimios.kernel.security.Session, long, java.lang.String, java.lang.String, boolean)
        */
    @DmsEvent(eventName = {DmsEventName.EXTENSION_ENTITY_ATTRIBUTE_SET})
    public void setAttribute(Session session, long dmEntityId, String attributeName, String attributeValue,
                             boolean indexed) throws Exception {
        DMEntity entity = dmsFactoryInstantiator.getDmEntityFactory().getEntity(dmEntityId);
        if (!SecurityAgent.getInstance()
                .isWritable(entity, session.getUserName(), session.getUserSource(), session.getGroups())) {
            throw new AccessDeniedException();
        }
        DMEntityAttribute attribute = new DMEntityAttribute();
        attribute.setName(attributeName);
        attribute.setValue(attributeValue);
        attribute.setIndexed(indexed);
        ((DMEntityImpl) entity).getAttributes().put(attributeName, attribute);
        dmsFactoryInstantiator.getDmEntityFactory().updateEntity((DMEntityImpl) entity);
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IExtensionController#getAttribute(org.kimios.kernel.security.Session, long, java.lang.String)
    */
    public DMEntityAttribute getAttribute(Session session, long dmEntityId, String attributeName) throws Exception {
        DMEntity entity = dmsFactoryInstantiator.getDmEntityFactory().getEntity(dmEntityId);
        if (!SecurityAgent.getInstance()
                .isReadable(entity, session.getUserName(), session.getUserSource(), session.getGroups())) {
            throw new AccessDeniedException();
        }
        return ((DMEntityImpl) entity).getAttributes().get(attributeName);
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IExtensionController#getAttributeValue(org.kimios.kernel.security.Session, long, java.lang.String)
    */
    public String getAttributeValue(Session session, long dmEntityId, String attributeName) throws Exception {
        DMEntity entity = dmsFactoryInstantiator.getDmEntityFactory().getEntity(dmEntityId);
        if (!SecurityAgent.getInstance()
                .isReadable(entity, session.getUserName(), session.getUserSource(), session.getGroups())) {
            throw new AccessDeniedException();
        }
        return ((DMEntityImpl) entity).getAttributes().get(attributeName).getValue();
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IExtensionController#getAttributes(org.kimios.kernel.security.Session, long)
    */
    public List<DMEntityAttribute> getAttributes(Session session, long dmEntityId) throws Exception {
        DMEntity entity = dmsFactoryInstantiator.getDmEntityFactory().getEntity(dmEntityId);
        if (!SecurityAgent.getInstance()
                .isReadable(entity, session.getUserName(), session.getUserSource(), session.getGroups())) {
            throw new AccessDeniedException();
        }
        return new ArrayList<DMEntityAttribute>(((DMEntityImpl) entity).getAttributes().values());
    }

    public String generatePasswordForUser(Session session, String userId, String userSource, boolean sendMail)
            throws ConfigException,
            DataSourceException, AccessDeniedException {
        if (securityFactoryInstantiator.getRoleFactory()
                .getRole(Role.ADMIN, session.getUserName(), session.getUserSource()) == null) {
            throw new AccessDeniedException();
        }
        AuthenticationSource authSource =
                authFactoryInstantiator.getAuthenticationSourceFactory().getAuthenticationSource(userSource);
        User u = authSource.getUserFactory().getUser(userId);
        if (u == null || !(authSource instanceof HAuthenticationSource)) {
            throw new AccessDeniedException();
        }
        String pwd = PasswordGenerator.generatePassword();
        authSource.getUserFactory().updateUser(u, pwd);
        //send by email
        if (sendMail) {
            try {
                HashMap<String, Object> datas = new HashMap<String, Object>();
                datas.put("user", u);
                datas.put("newPassword", pwd);
                String body = TemplateUtil.generateContent(datas, "/" + ConfigurationManager
                        .getValue(Config.TEMPLATE_NEW_PASSWORD), "UTF-8");
                MailTemplate mt = new MailTemplate(ConfigurationManager.getValue(Config.MAIL_SENDER_ADDRESS),
                        ConfigurationManager.getValue(Config.MAIL_SENDER_NAME),
                        u.getMail(),
                        "Un nouveau mot de passe a été définie pour votre compte.",
                        body,
                        "text/html"
                );
                Mailer ml = new Mailer(mt);
                ml.start();
            } catch (Exception e) {
                throw new ConfigException(e, e.getMessage());
            }
        }
        return pwd;
    }

    @Override
    @DmsEvent(eventName = {DmsEventName.DOCUMENT_TRASH})
    public void trashEntity(Session session, long dmEntityId, boolean force)
            throws ConfigException, DataSourceException, AccessDeniedException, DeleteDocumentWithActiveShareException {


        DMEntity d = dmsFactoryInstantiator.getDmEntityFactory().getEntity(dmEntityId);

        if(d instanceof Document){
            //check for lock and shares
            Lock lock = ((Document) d).getCheckoutLock();
            if (lock != null) {
                if (!session.getUserName().equals(lock.getUser())) {
                    throw new CheckoutViolationException();
                }
            }
            EventContext.addParameter("document", d);
        }
        if(d instanceof Folder || d instanceof Workspace){
            if (getSecurityAgent().hasAnyChildCheckedOut(d, session.getUserName(), session.getUserSource())) {
                throw new AccessDeniedException();
            }
            if (getSecurityAgent()
                    .hasAnyChildNotWritable(d, session.getUserName(), session.getUserSource(), session.getGroups())) {
                throw new AccessDeniedException();
            }
            if(d instanceof Folder)
                EventContext.addParameter("folder", d);
            if(d instanceof Workspace)
                EventContext.addParameter("workspace", d);
        }

        if (getSecurityAgent().isWritable(d, session.getUserName(), session.getUserSource(), session.getGroups())){
        {
            if (d instanceof Document) {
                Set<Share> shareSet = new HashSet<>();
                dmsFactoryInstantiator.getDocumentFactory().getDocument(d.getUid()).getShareSet().forEach(share -> {
                    if (share.getExpirationDate().after(new Date())
                            && share.getShareStatus().equals(ShareStatus.ACTIVE)) {
                        shareSet.add(share);
                    }
                });
                if (!force && !shareSet.isEmpty()) {
                    throw new DeleteDocumentWithActiveShareException();
                }
            }
            dmsFactoryInstantiator.getDmEntityFactory().trash((DMEntityImpl)d);
        }
        } else {
            throw new AccessDeniedException();
        }
    }


    @Override
    public List<DMEntity> viewTrash(Session session, Integer start, Integer count)
            throws ConfigException, DataSourceException, AccessDeniedException {
        if (getSecurityAgent().isAdmin(session.getUserName(), session.getUserSource())) {
            return dmsFactoryInstantiator.getDmEntityFactory().listTrashedEntities(start, count);
        } else {
            throw new AccessDeniedException();
        }

    }


    @Override
    @DmsEvent(eventName = {DmsEventName.DOCUMENT_UNTRASH})
    public DMEntity restoreEntity(Session session, long dmEntityId)
            throws ConfigException, DataSourceException, AccessDeniedException {
        DMEntityImpl d = (DMEntityImpl)dmsFactoryInstantiator.getDmEntityFactory().getEntity(dmEntityId);
        if (getSecurityAgent().isAdmin(session.getUserName(), session.getUserSource())) {
            dmsFactoryInstantiator.getDmEntityFactory().untrash(d);

            //reload entity
            d = (DMEntityImpl)dmsFactoryInstantiator.getDmEntityFactory().getEntity(dmEntityId);

            if(d instanceof Document)
            EventContext.addParameter("document", d);
            if(d instanceof Folder)
                EventContext.addParameter("folder", d);
            if(d instanceof Workspace)
                EventContext.addParameter("workspace", d);
            return d;
        } else {
            throw new AccessDeniedException();
        }
    }

    @Override
    public boolean canHandleAutomaticDocumentDeposit(Session session)
        throws ConfigException, DataSourceException, AccessDeniedException {
        try {
            PathTemplate template =
                    FactoryInstantiator.getInstance().getPathTemplateFactory().getDefaultPathTemplate();
            return  template != null;
        }catch (Exception ex){
            return false;
        }
    }


    public List<String> listExtensions(Session session, String extensionType)
        throws ConfigException, AccessDeniedException {
        try {
            return new ArrayList<String>(extensionRegistryManager.itemsAsString(Class.forName(extensionType)));
        }catch (ClassNotFoundException ex){
            throw new DmsKernelException(ex);
        }
    }


}

