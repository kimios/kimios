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
package org.kimios.kernel.controller.utils;

import org.kimios.kernel.filetransfer.DataTransfer;

import java.io.FileOutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Farf
 */
public class UploadStreamOptimizer
{
    private Map<Long, StreamHolder> streamsTx;

    private static UploadStreamOptimizer instance;

    private UploadStreamOptimizer()
    {
        streamsTx = new ConcurrentHashMap<Long, StreamHolder>();
    }

    synchronized public static UploadStreamOptimizer getInstance()
    {
        if (instance == null) {
            instance = new UploadStreamOptimizer();
        }
        return instance;
    }

    public void pushStream(DataTransfer dt, FileOutputStream stream)
    {
        if (dt.getTransferMode() == DataTransfer.UPLOAD) {
            if (dt.getDataSize() == 0) {
                StreamHolder stHolder = new StreamHolder();
                stHolder.fileOutputStream = stream;
                stHolder.timeStamp = System.currentTimeMillis();
                streamsTx.put(dt.getUid(), stHolder);
            }
        }
    }

    public FileOutputStream popStream(DataTransfer dt)
    {
        StreamHolder stHolder = streamsTx.remove(dt.getUid());
        return stHolder.fileOutputStream;
    }

    public class StreamHolder
    {
        protected long timeStamp;

        protected FileOutputStream fileOutputStream;
    }
}
