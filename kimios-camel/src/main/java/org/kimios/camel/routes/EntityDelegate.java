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

    @DataField(columnName = "Version", pos = 2)
    private String customVersion;

    @DataField(columnName = "Chemin", pos = 3)
    protected String path;

    @DataField(columnName = "Auteur de la derniere modification", pos = 4)
    private String lastUpdateAuthor;

    @DataField(columnName = "Date de modification", pos = 5, pattern = "dd-MM-yyyy HH:mm")
    private Date versionUpdateDate;

    @DataField(columnName = "Validateur", pos = 6)
    private String validatorUserName;

    @DataField(columnName = "Type", pos = 7)
    private String documentTypeName;

    @DataField(columnName = "Statut Chaine de validation", pos = 8)
    private String workflowStatusName;

    @DataField(columnName = "Taille", pos = 9)
    private long length;

    @DataField(columnName = "Workflow name", pos = 10)
    private String workflowName;

    @DataField(columnName = "Auteur du document", pos = 11)
    protected String owner;

    @DataField(columnName = "Date de creation", pos = 12, pattern = "dd-MM-yyyy HH:mm")
    protected Date creationDate;


    protected Calendar updateDate;
    protected String ownerSource;
    private Long lastVersionId;
    private Calendar versionCreationDate;
    private long folderUid;
    private String mimeType;
    private String extension;
    private Boolean checkedOut = false;
    private String checkoutUser;
    private String checkoutUserSource;
    private Calendar checkoutDate;
    private String customVersionPending;
    private String lastUpdateAuthorSource;
    private Long workflowStatusUid = 0L;
    private String validatorUserSource;

    private Boolean isOutOfWorkflow = true;



    private Long documentTypeUid = 0L;

    private Float indexScore;


    public EntityDelegate(Document document){
        this.name = document.getName();
        this.path = document.getPath();
        this.documentTypeName = document.getDocumentTypeName();
        if(document.getValidatorUserName() != null && document.getValidatorUserName().length() > 0)
            this.validatorUserName = document.getValidatorUserName() + "@" + document.getValidatorUserSource();
        this.workflowStatusName = document.getWorkflowStatusName();
        this.versionUpdateDate = document.getVersionUpdateDate().getTime();
        this.length = document.getLength();
        this.customVersion = document.getCustomVersion();
        this.lastUpdateAuthor = document.getLastUpdateAuthor() + "@" + document.getLastUpdateAuthorSource();
        this.workflowName = document.getWorkflowName();
        this.owner = document.getOwner() + "@" + document.getOwnerSource();
        this.creationDate = document.getCreationDate().getTime();
    }


}
