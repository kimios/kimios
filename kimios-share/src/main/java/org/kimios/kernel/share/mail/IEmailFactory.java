package org.kimios.kernel.share.mail;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.kimios.kernel.dms.model.Document;
import org.kimios.kernel.dms.model.DocumentVersion;

public interface IEmailFactory {

    MultiPartEmail getMultipartEmailObject() throws EmailException;

    void addDocumentVersionAttachment(MultiPartEmail email, Document document, DocumentVersion documentVersion) throws Exception;
}
