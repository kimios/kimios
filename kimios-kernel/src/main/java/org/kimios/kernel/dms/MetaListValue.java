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

package org.kimios.kernel.dms;

import org.kimios.kernel.exception.MetaValueTypeException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by farf on 3/31/14.
 */
@Entity
@Table(name = "meta_list")
public class MetaListValue extends MetaValueBean<List<String>> {

    @ElementCollection(fetch = FetchType.LAZY, targetClass = String.class)
    @CollectionTable(
            name="meta_list_values",
            joinColumns={@JoinColumn(name="meta_id"), @JoinColumn(name = "document_version_id")}
    )
    private List<String> value = new ArrayList<String>();

    public MetaListValue()
    {
    }

    public MetaListValue(DocumentVersion version, long metaUid, List<String> value)
    {
        super(version, metaUid);
        this.value = value;
    }

    public MetaListValue(DocumentVersion version, Meta meta, List value)
    {
        super(version, meta);
        this.value = value;
    }

    @Override
    public List<String> getValue() {
        return value;
    }

    @Override
    public void setValue(List<String> value) throws MetaValueTypeException {
        this.value = value;
    }
}
