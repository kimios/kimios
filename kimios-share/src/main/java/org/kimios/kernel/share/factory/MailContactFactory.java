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

package org.kimios.kernel.share.factory;

import org.kimios.kernel.hibernate.HFactory;
import org.kimios.kernel.share.model.MailContact;

import java.util.List;

/**
 * Created by farf on 20/07/15.
 */
public class MailContactFactory extends HFactory {


    public void addContact(String emailAddress, String fullName){

        MailContact mc  = new MailContact();
        mc.setEmailAddress(emailAddress);
        mc.setFullName(fullName);
        getSession().saveOrUpdate(mc);
    }


    public List<MailContact> searchContact(String search) {
        String query = "from MailContact mc where lower(mc.emailAddress) like :search or lower(mc.fullName) like :search" +
                " order by mc.emailAddress";

        return getSession().createQuery(query)
                .setString("search", "%" + search.toLowerCase() + "%")
                .list();
    }


}
