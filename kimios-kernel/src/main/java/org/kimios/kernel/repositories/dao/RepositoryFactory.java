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
package org.kimios.kernel.repositories.dao;

import org.kimios.kernel.hibernate.HFactory;
import org.kimios.kernel.repositories.Repository;

import java.util.List;

/**
 * @author Fabien Alin (Farf) <fabien.alin@gmail.com>
 */
public class RepositoryFactory extends HFactory
{
    public Repository loadById(Long id)
    {
        return null;
    }

    public Repository findDefaultRepository()
    {
        String query = "from RepositoryImpl where defaultRepository is true";
        List<Repository> items = getSession().createQuery(query)
                .list();

        if (items.size() == 0) {
            return null;
        } else {
            return items.get(0);
        }
    }
}
