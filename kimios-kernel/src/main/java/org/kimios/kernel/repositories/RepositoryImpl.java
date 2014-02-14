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
package org.kimios.kernel.repositories;

import javax.persistence.*;

/**
 * @author Fabien Alin (Farf) <fabien.alin@gmail.com>
 */
@Entity
@Table(name = "repositories")
@SequenceGenerator(allocationSize = 1, sequenceName = "repository_id_seq", name = "seq")
public class RepositoryImpl implements Repository
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    @Column(name = "repository_id")
    private Long id;

    @Column(name = "repository_name", nullable = false)
    private String name;

    @Column(name = "repository_path", nullable = false)
    private String path;

    @Column(name = "repository_default", nullable = true)
    private Boolean defaultRepository;

    public Boolean getDefaultRepository()
    {
        return defaultRepository;
    }

    public void setDefaultRepository(Boolean defaultRepository)
    {
        this.defaultRepository = defaultRepository;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }
}
