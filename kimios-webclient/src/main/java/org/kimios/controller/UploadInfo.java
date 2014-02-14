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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kimios.controller;

/**
 *
 * @author Fabien Alin
 */
public class UploadInfo{
        private String startTime;
        private long bytesRead;
        private long totalLength;
        
        public UploadInfo(String st, long bR, long tL){
            this.startTime = st;
            this.bytesRead = bR;
            this.totalLength = tL;
        }
        
        public long getBytesRead() {
            return bytesRead;
        }

        public void setBytesRead(long bytesRead) {
            this.bytesRead = bytesRead;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public long getTotalLength() {
            return totalLength;
        }

        public void setTotalLength(long totalLength) {
            this.totalLength = totalLength;
        }
    }

