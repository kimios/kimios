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

package org.kimios.kernel.jobs;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * Created by farf on 09/02/16.
 */
public class CustomFutureTask<V> extends FutureTask<V> {

    CustomFutureTask(Callable<V> callable){
       super(callable);
        this.runnable = callable;
    }

    private Callable<V> runnable;

    public Callable<V> getTask(){
        return runnable;
    }

    @Override
    public String toString() {
        if(runnable instanceof Job){
            return "Custom task " + runnable.getClass().getName() + " ===> "+ ((Job)runnable).getTaskId();
        } else {
            return super.toString() + ". Backed Runnable: " + (runnable != null ? runnable.getClass().getName() : " Null runnable");
        }
    }


    //Directly return underlying Job
    public Callable<V> unwrap(){
        if(runnable instanceof WrapCallable){
            return ((WrapCallable) runnable).unwrap();
        }
        return null;
    }
}
