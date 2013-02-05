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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kimios.core;

import flexjson.transformer.AbstractTransformer;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @author Farf
 */
public class MetaDateTransform extends AbstractTransformer {

  public void transform(Object arg0) {
    if (arg0.getClass().equals(long.class)) {
      getContext().write(
          new SimpleDateFormat("MM/dd/yyyy").format(new Date(
              (Long) arg0)));
    } else
      getContext().write("");
  }
}
