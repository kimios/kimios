/*
 * Kimios - Document Management System Software
 * Copyright (C) 2012-2013  DevLib'
 *
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kimios.kernel.dms.metafeeds.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.kimios.kernel.dms.MetaFeedImpl;
import org.kimios.kernel.exception.MetaFeedSearchException;

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
        /*try {
            EnumerationValueFactory ef = FactoryInstantiator.getInstance().getEnumerationValueFactory();
            return ef.getValues(this.uid);
        } catch (ConfigException ce) {
            ce.printStackTrace();
            return new Vector<String>();
        } catch (DataSourceException dbe) {
            dbe.printStackTrace();
            return new Vector<String>();
        }*/
        return values;
    }

    @Override
    public String[] search(String criteria) throws MetaFeedSearchException
    {
        try {
            //Vector<String> values = FactoryInstantiator.getInstance().getEnumerationValueFactory().getValues(this.uid);
            Vector<String> v = new Vector<String>();

            Pattern pattern = Pattern.compile("^" + criteria + "$");
            for (String res : values) {
                Matcher mtch = pattern.matcher(res);
                if (mtch.groupCount() > 0) {
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

