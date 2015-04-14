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

package org.kimios.kernel.dms;

import org.kimios.kernel.dms.utils.PathElement;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by farf on 10/04/15.
 */
@Entity
@Table(name = "path_templates")
@SequenceGenerator(name = "seq", allocationSize = 1, sequenceName = "path_template_seq")
public class PathTemplate {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    @Column(name = "path_template_id", nullable = false)
    private Long id;

    @Column(name = "path_template_name")
    private String templateName;

    @Column(name = "path_template_content", nullable = false, columnDefinition = "text")
    private String pathTemplateContent;

    @Column(name = "path_template_default", nullable = false, unique = true)
    private boolean defaultPathTemplate = false;

    @Transient
    private List<PathElement> pathElements = new ArrayList<PathElement>();


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getPathTemplateContent() {
        return pathTemplateContent;
    }

    public void setPathTemplateContent(String pathTemplateContent) {
        this.pathTemplateContent = pathTemplateContent;
    }

    public List<PathElement> getPathElements() {
        return pathElements;
    }

    public void setPathElements(List<PathElement> pathElements) {
        this.pathElements = pathElements;
    }

    public boolean isDefaultPathTemplate() {
        return defaultPathTemplate;
    }

    public void setDefaultPathTemplate(boolean defaultPathTemplate) {
        this.defaultPathTemplate = defaultPathTemplate;
    }
}
