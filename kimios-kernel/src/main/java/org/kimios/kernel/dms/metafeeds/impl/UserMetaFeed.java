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
package org.kimios.kernel.dms.metafeeds.impl;

import org.kimios.kernel.dms.MetaFeedImpl;
import org.kimios.kernel.user.AuthenticationSource;
import org.kimios.kernel.user.FactoryInstantiator;
import org.kimios.kernel.user.User;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.Vector;

@Entity
@DiscriminatorValue(value = "org.kimios.kernel.dms.metafeeds.impl.UserMetaFeed")
public class UserMetaFeed extends MetaFeedImpl
{
    private String name = "User meta feed";

    public String getName()
    {
        return this.name;
    }

    public long getUid()
    {
        return this.uid;
    }

    public Vector<String> getValues()
    {
        Vector<String> v = new Vector<String>();
        try {
            Vector<AuthenticationSource> sources =
                    FactoryInstantiator.getInstance().getAuthenticationSourceFactory().getAuthenticationSources();
            for (AuthenticationSource s : sources) {
                try {
                    for (User u : s.getUserFactory().getUsers()) {
                        try {
                            v.add(u.getUid() + "@" + u.getAuthenticationSourceName());
                        } catch (Throwable ex) {
                            ex.printStackTrace();
                        }
                    }
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return v;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setUid(long uid)
    {
        this.uid = uid;
    }

    public String[] search(String criteria)
    {
        Vector<String> r = new Vector<String>();
        Vector<String> values = this.getValues();
        for (String s : values) {
            if (s.contains(criteria)) {
                r.add(s);
            }
        }
        String[] a = new String[r.size()];
        return r.toArray(a);
    }
}
