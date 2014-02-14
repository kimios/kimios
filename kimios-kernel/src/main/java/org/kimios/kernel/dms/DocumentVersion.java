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
package org.kimios.kernel.dms;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.exception.RepositoryException;
import org.kimios.kernel.repositories.RepositoryManager;
import org.kimios.kernel.utils.HashCalculator;

import javax.persistence.*;
import java.io.*;
import java.util.Date;

@Entity
@Table(name = "document_version")
@SequenceGenerator(name = "seq", sequenceName = "doc_version_id_seq", allocationSize = 1)
public class DocumentVersion implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    private long uid;

    @Column(name = "author", nullable = false)
    private String author;

    @Column(name = "author_source", nullable = false)
    private String authorSource;

    @Column(name = "creation_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @Column(name = "modification_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modificationDate;

    @Column(name = "document_id", nullable = true)
    private Long documentUid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = true, insertable = false, updatable = false)
    private Document document;

    @Column(name = "storage_path", nullable = false, unique = true)
    private String storagePath;

    @Column(name = "version_length")
    private long length = 0;

    @ManyToOne(targetEntity = DocumentType.class)
    @JoinColumn(name = "document_type_id", nullable = true)
    private DocumentType documentType;

    @Column(name = "hash_md5")
    private String hashMD5;

    @Column(name = "hash_sha1")
    private String hashSHA1;

    public DocumentVersion() {
    }

    public DocumentVersion(long uid, String author, String authorSource, Date creationDate, Date modificationDate,
                           Long documentUid, long length, DocumentType documentType) {
        this.uid = uid;
        this.author = author;
        this.authorSource = authorSource;
        this.creationDate = creationDate;
        this.modificationDate = modificationDate;
        this.documentUid = documentUid;
        this.length = length;
        this.documentType = documentType;
    }

    public String getAuthorSource() {
        return authorSource;
    }

    public void setAuthorSource(String authorSource) {
        this.authorSource = authorSource;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public Date getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(Date modificationDate) {
        this.modificationDate = modificationDate;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public long getDocumentUid() {
        return documentUid;
    }

    public void setDocumentUid(Long documentUid) {
        this.documentUid = documentUid;
    }

    public Document getDocument() {
        return this.document;
    }

    public void setDocument(Document document) {
        if (document != null) {
            this.documentUid = document.getUid();
        } else {
            documentUid = null;
        }
        this.document = document;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getHashMD5() {
        return hashMD5;
    }

    public void setHashMD5(String hashMD5) {
        this.hashMD5 = hashMD5;
    }

    public String getHashSHA1() {
        return hashSHA1;
    }

    public void setHashSHA1(String hashSHA1) {
        this.hashSHA1 = hashSHA1;
    }

    @Transient
    public InputStream getInputStream() throws ConfigException, RepositoryException, IOException {
        return RepositoryManager.accessVersionStream(this);
    }

    public RandomAccessFile getRandomAccessFile(String mode) throws Exception {
        return RepositoryManager.randomAccessFile(this, mode);
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public void initRepositoryStorage() throws Exception {
        RepositoryManager.initRepositoryStorage(this);
    }

    public void writeData(InputStream in) throws DataSourceException, ConfigException, RepositoryException {
        RepositoryManager.writeVersion(this, in);
        FactoryInstantiator.getInstance().getDocumentVersionFactory().updateDocumentVersion(this);
    }

    public void updateVersionInformation() throws DataSourceException, ConfigException, RepositoryException {
        try {
            InputStream fis =
                    RepositoryManager.accessVersionStream(this);
            this.setLength(fis.available());
            try {
                HashCalculator hc = new HashCalculator("MD5");
                this.setHashMD5(hc.hashToString(fis).replaceAll(" ", ""));
                hc.setAlgorithm("SHA-1");
                fis = RepositoryManager.accessVersionStream(this);
                this.setHashSHA1(hc.hashToString(fis).replaceAll(" ", ""));
            } catch (Exception ex) {
            }
            fis.close();
            FactoryInstantiator.getInstance().getDocumentVersionFactory().updateDocumentVersion(this);
        } catch (IOException io) {
            throw new RepositoryException(io.getMessage());
        }
    }

    public OutputStream getOutputStream() throws Exception {
        return RepositoryManager.accessOutputStreamVersion(this);
    }

    public org.kimios.kernel.ws.pojo.DocumentVersion toPojo() throws Exception {
        long docTypeUid = -1;
        String docTypeName = "";
        if (this.getDocumentType() != null) {
            docTypeUid = this.getDocumentType().getUid();
            docTypeName = this.getDocumentType().getName();
        }
        return new org.kimios.kernel.ws.pojo.DocumentVersion(this.uid, this.author, this.authorSource,
                this.creationDate,
                this.modificationDate, this.documentUid, this.length, docTypeUid, docTypeName, this.hashMD5,
                this.hashSHA1);
    }

    public boolean equals(Object o) {
        try {
            if (o instanceof DocumentVersion) {
                DocumentVersion new_name = (DocumentVersion) o;
                return (new_name.getUid() == this.getUid());
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return "DocumentVersion{" +
                "uid=" + uid +
                ", author='" + author + '\'' +
                ", authorSource='" + authorSource + '\'' +
                ", creationDate=" + creationDate +
                ", modificationDate=" + modificationDate +
                ", documentUid=" + documentUid +
                ", document=" + document +
                ", storagePath='" + storagePath + '\'' +
                ", length=" + length +
                ", documentType=" + documentType +
                ", hashMD5='" + hashMD5 + '\'' +
                ", hashSHA1='" + hashSHA1 + '\'' +
                '}';
    }
}

