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

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.kimios.kernel.dms.MetaFeedImpl;
import org.kimios.kernel.exception.MetaFeedSearchException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

@Entity
@DiscriminatorValue(value = "org.kimios.kernel.dms.metafeeds.impl.Enumeration")
public class Enumeration extends MetaFeedImpl
{
    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @CollectionTable(name = "enumeration_value",
                     joinColumns = @JoinColumn(name = "enumeration_id"), uniqueConstraints =
    @UniqueConstraint(columnNames = { "enumeration_id", "enumeration_value" }))
    @Column(name = "enumeration_value")
    private List<String> values = new ArrayList<String>();

    public Enumeration()
    {
    }

    public List<String> getValues()
    {
        return values;
    }

    @Override
    public String[] search(String criteria) throws MetaFeedSearchException
    {
        try {
            Vector<String> v = new Vector<String>();
            for (String res : values) {
                if (res.toLowerCase().contains( criteria.toLowerCase() )
                    || criteria.trim().length() == 0) {
                    v.add(res);
                }
            }

            String[] results = new String[v.size()];
            for (String s : v) {
                results[v.indexOf(s)] = s;
            }
            return results;
        } catch (Exception e) {
            throw new MetaFeedSearchException("Retrieving metafeed value error : " + e.getMessage());
        }
    }
}

