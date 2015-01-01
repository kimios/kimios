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
package org.kimios.kernel.filetransfer;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.kimios.kernel.dms.DocumentVersion;
import org.kimios.kernel.ws.pojo.DataTransaction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "data_transaction")
@SequenceGenerator(name = "seq", sequenceName = "dt_id_seq", allocationSize = 1)
public class DataTransfer implements Serializable
{
    public static final int UNKNOWN = 0;

    public static final int UPLOAD = 1;

    public static final int DOWNLOAD = 2;

    public static final int TOKEN = 3;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    private long uid;

    @Column(name = "username", nullable = false)
    private String userName;

    @Column(name = "user_source", nullable = false)
    private String userSource;

    @Column(name = "last_activity_date", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastActivityDate;

    @ManyToOne(targetEntity = DocumentVersion.class)
    @JoinColumn(name = "document_version_id", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private DocumentVersion version;

    @Column(name = "document_version_id", nullable = false)
    private long documentVersionUid;

    @Column(name = "is_compressed", nullable = false)
    private boolean isCompressed = false;

    @Column(name = "file_path", length = 2000)
    private String filePath;

    @Column(name = "data_size", nullable = true)
    private long dataSize;

    @Column(name = "hash_md5", nullable = true)
    private String hashMD5;

    @Column(name = "hash_sha", nullable = true)
    private String hashSHA;

    @Column(name = "transfer_mode", nullable = false)
    private int transferMode;

    @Column(name = "has_been_chk_out", nullable = false)
    private boolean hasBeenCheckedOutOnStart = false;

    @Column(name = "dl_token", nullable = true)
    private String downloadToken = null;

    public DataTransfer()
    {
    }

    /**
     * @param uid
     * @param userName
     * @param userSource
     * @param lastActivityDate
     * @param documentVersionUid
     * @param isCompressed
     * @param filePath
     * @param dataSize
     * @param hashMD5
     * @param hashSHA
     * @param transferMode
     */
    public DataTransfer(long uid, String userName, String userSource, Date lastActivityDate,
            long documentVersionUid, boolean isCompressed, String filePath, long dataSize,
            String hashMD5, String hashSHA, int transferMode)
    {
        this.uid = uid;
        this.userName = userName;
        this.userSource = userSource;
        this.documentVersionUid = documentVersionUid;
        this.lastActivityDate = lastActivityDate;
        this.isCompressed = isCompressed;
        this.filePath = filePath;
        this.dataSize = dataSize;
        this.hashMD5 = hashMD5;
        this.hashSHA = hashSHA;
        this.transferMode = transferMode;
    }

    public long getUid()
    {
        return uid;
    }

    public void setUid(long uid)
    {
        this.uid = uid;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getUserSource()
    {
        return userSource;
    }

    public void setUserSource(String userSource)
    {
        this.userSource = userSource;
    }

    public Date getLastActivityDate()
    {
        return lastActivityDate;
    }

    public void setLastActivityDate(Date lastActivityDate)
    {
        this.lastActivityDate = lastActivityDate;
    }

    public long getDocumentVersionUid()
    {
        return documentVersionUid;
    }

    public void setDocumentVersionUid(long documentVersionUid)
    {
        this.documentVersionUid = documentVersionUid;
    }

    public boolean getIsCompressed()
    {
        return isCompressed;
    }

    public void setIsCompressed(boolean isCompressed)
    {
        this.isCompressed = isCompressed;
    }

    public String getFilePath()
    {
        return filePath;
    }

    public void setFilePath(String filePath)
    {
        this.filePath = filePath;
    }

    public long getDataSize()
    {
        return dataSize;
    }

    public void setDataSize(long dataSize)
    {
        this.dataSize = dataSize;
    }

    public String getHashMD5()
    {
        return hashMD5;
    }

    public void setHashMD5(String hashMD5)
    {
        this.hashMD5 = hashMD5;
    }

    public String getHashSHA()
    {
        return hashSHA;
    }

    public void setHashSHA(String hashSHA)
    {
        this.hashSHA = hashSHA;
    }

    public int getTransferMode()
    {
        return transferMode;
    }

    public void setTransferMode(int transferMode)
    {
        this.transferMode = transferMode;
    }

    public boolean isHasBeenCheckedOutOnStart()
    {
        return hasBeenCheckedOutOnStart;
    }

    public void setHasBeenCheckedOutOnStart(boolean hasBeenCheckedOutOnStart)
    {
        this.hasBeenCheckedOutOnStart = hasBeenCheckedOutOnStart;
    }

    public String getDownloadToken() {
        return downloadToken;
    }

    public void setDownloadToken(String downloadToken) {
        this.downloadToken = downloadToken;
    }

    public DataTransaction toPojo()
    {
        return new DataTransaction(this.uid, this.dataSize, this.isCompressed, this.hashMD5, this.hashSHA, this.downloadToken);
    }

    public DocumentVersion getVersion()
    {
        return version;
    }

    public void setVersion(DocumentVersion version)
    {
        this.version = version;
        if (version != null) {
            this.documentVersionUid = version.getUid();
        }
    }
}

