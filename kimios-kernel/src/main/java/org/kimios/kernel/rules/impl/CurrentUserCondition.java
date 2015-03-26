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
package org.kimios.kernel.rules.impl;

import org.kimios.kernel.security.SecurityEntity;
import org.kimios.kernel.user.AuthenticationSource;
import org.kimios.kernel.user.FactoryInstantiator;
import org.kimios.kernel.user.Group;
import org.kimios.kernel.user.User;
import org.kimios.kernel.utils.ClassGeneric;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class CurrentUserCondition extends RuleImpl
{
    @ClassGeneric(classType = User.class)
    private List<User> actionUsers = new ArrayList<User>();

    @ClassGeneric(classType = SecurityEntity.class)
    private List<SecurityEntity> sendTo = new ArrayList<SecurityEntity>();

    private String mailBody;

    private String mailSubject;

    private String sender;

    @Override
    public boolean isTrue()
    {
        if (this.getContext().getSession() != null) {
            try {
                AuthenticationSource sc = FactoryInstantiator.getInstance().getAuthenticationSourceFactory()
                        .getAuthenticationSource(this.getContext().getSession().getUserSource());
                User u = sc.getUserFactory().getUser(this.getContext().getSession().getUserName());
                return actionUsers.contains(u);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public void execute()
    {
        try {
            if (sendTo != null) {
                for (SecurityEntity sec : sendTo) {
                    System.out.println(sec.getClass().getName());
                    if (sec instanceof Group) {
                        AuthenticationSource sc = FactoryInstantiator.getInstance().getAuthenticationSourceFactory()
                                .getAuthenticationSource(((Group) sec).getAuthenticationSourceName());
                        Vector<User> users = sc.getUserFactory().getUsers((Group) sec);
                        for (User u : users) {
                            sendMail(u, mailBody, mailSubject);
                        }
                    } else {
                        sendMail((User) sec, mailBody, mailSubject);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMail(User user, String body, String subject)
    {
        if (user.getMail() != null) {
//      MailTemplate mt = new MailTemplate();
//      mt.setMailBody(body);
//      mt.setMailSubject(subject);
//      mt.setMailFrom(sender);
//      mt.addMailTo(user.getMail());
//      new Mailer(mt).start();
        }
    }
}

