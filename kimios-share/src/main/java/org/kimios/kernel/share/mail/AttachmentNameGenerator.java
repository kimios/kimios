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

package org.kimios.kernel.share.mail;

import org.kimios.kernel.dms.model.DocumentVersion;
import org.kimios.kernel.dms.FactoryInstantiator;
import org.kimios.kernel.dms.model.MetaValue;

import java.util.List;

/**
 * Created by farf on 10/09/15.
 */
public class AttachmentNameGenerator {


    private String metaName;

    public AttachmentNameGenerator(String metaName){
        this.metaName = metaName;
    }

    public String generate(DocumentVersion documentVersion){

        List<MetaValue> values = FactoryInstantiator.getInstance()
                .getMetaValueFactory()
                .getMetaValues(documentVersion);

        if(values != null && values.size() > 0){
            for(MetaValue mv: values){
                if(mv.getMeta().getName().equals(metaName)){
                    return mv.getValue().toString();
                }
            }
        }
        return null;
    }


}
