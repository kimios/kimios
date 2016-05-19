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

package org.kimios.kernel.templates.model;

import org.kimios.api.templates.ITemplate;
import org.kimios.api.templates.TemplateType;

import javax.persistence.*;

/**
 * Created by farf on 21/03/16.
 */

@Entity
@Table(name = "template")
@SequenceGenerator(sequenceName = "template_id_seq", allocationSize = 1, name = "seq")
public class Template implements ITemplate {


    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(generator = "seq", strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "template_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TemplateType type;

    @Column(name = "template_content", nullable = false, columnDefinition = "text")
    private String content;

    @Column(name = "template_name", nullable = false)
    private String name;

    @Column(name = "is_default", nullable = false)
    private boolean defaultTemplate = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setType(TemplateType type) {
        this.type = type;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDefaultTemplate() {
        return defaultTemplate;
    }

    public void setDefaultTemplate(boolean defaultTemplate) {
        this.defaultTemplate = defaultTemplate;
    }

    @Override
    public TemplateType getType() {
        return type;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Template{" +
                "type=" + type +
                ", id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
