/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2016  DevLib'
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

package org.kimios.camel.routes;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import org.kimios.kernel.ws.pojo.Document;
import org.kimios.kernel.ws.pojo.MetaValue;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Created by farf on 27/10/16.
 */

@CsvRecord(separator = ",", generateHeaderColumns = true)
public class EntityDelegate {


    @DataField(columnName = "Nom", pos = 1)
    protected String name;

    protected Calendar creationDate;

    protected Calendar updateDate;

    protected String owner;

    protected String ownerSource;

    @DataField(columnName = "Chemin", pos = 3)
    protected String path;

    private Long lastVersionId;

    private Calendar versionCreationDate;

    @DataField(columnName = "Date de modification", pos = 5)
    private Date versionUpdateDate;

    private long folderUid;

    private String mimeType;

    private String extension;

    private Boolean checkedOut = false;

    private String checkoutUser;

    private String checkoutUserSource;

    private Calendar checkoutDate;

    @DataField(columnName = "Taille", pos = 9)
    private long length;


    @DataField(columnName = "Version", pos = 2)
    private String customVersion;

    private String customVersionPending;

    @DataField(columnName = "Auteur de la dernière modification", pos = 4)
    private String lastUpdateAuthor;

    private String lastUpdateAuthorSource;

    private Long workflowStatusUid = 0L;


    @DataField(columnName = "Statut Chaîne de validation", pos = 8)
    private String workflowStatusName;

    @DataField(columnName = "Validateur", pos = 6)
    private String validatorUserName;

    private String validatorUserSource;

    private Boolean isOutOfWorkflow = true;

    @DataField(columnName = "Type", pos = 7)
    private String documentTypeName;

    private Long documentTypeUid = 0L;

    private Float indexScore;


    public EntityDelegate(Document document){
        this.name = document.getName();
        this.path = document.getPath();
        this.documentTypeName = document.getDocumentTypeName();
        this.validatorUserName = document.getValidatorUserName();
        this.workflowStatusName = document.getWorkflowStatusName();
        this.versionUpdateDate = document.getVersionUpdateDate().getTime();
        this.length = document.getLength();
        this.customVersion = document.getCustomVersion();
        this.lastUpdateAuthor = document.getLastUpdateAuthor();
    }


}
