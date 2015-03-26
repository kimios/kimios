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
package org.kimios.kernel.rules.impl;

import org.kimios.kernel.events.EventContext;
import org.kimios.kernel.rules.Condition;
import org.kimios.kernel.rules.RuleApplication;

public abstract class RuleImpl implements Condition, RuleApplication
{
    protected EventContext ctx;

    public abstract boolean isTrue();

    final public EventContext getContext()
    {
        return this.ctx;
    }

    final public void setConditionContext(EventContext ctx)
    {
        this.ctx = ctx;
    }

    public abstract void execute() throws Exception;
}

