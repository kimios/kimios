package org.kimios.client.controller.helpers;

import org.apache.cxf.attachment.ByteDataSource;
import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;

import javax.activation.DataHandler;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;


@Provider
@Consumes({MediaType.APPLICATION_JSON, "multipart/form-data"})
@Produces({MediaType.APPLICATION_JSON, "multipart/form-data"})
public class UploadMessageBodyWriter implements MessageBodyWriter<Object> {
    @Context
    private MessageContext mc;

    public boolean isWriteable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return FileUploadBean.class.isAssignableFrom(aClass);
    }

    public long getSize(Object fileUploadBean, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return 0;
    }

    public void writeTo(Object fileUploadBean, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> stringObjectMultivaluedMap, OutputStream outputStream) throws IOException, WebApplicationException {

        MessageDigest md5 = null;
        MessageDigest sha1 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            sha1 = MessageDigest.getInstance("SHA-1");

        } catch (NoSuchAlgorithmException nsae) {
            nsae.printStackTrace();
            throw new IOException(nsae);
        }

        List<MessageDigest> digests = new ArrayList<MessageDigest>();
        digests.add(md5);
        digests.add(sha1);

        InputStream hashInputStream = new HashInputStream(digests, ((FileUploadBean) fileUploadBean).getStream());
//            InputStream in = fileUploadBean.getStream();

        Attachment documentAttachment = new Attachment("document", hashInputStream, null);

        documentAttachment.getDataHandler().writeTo(outputStream);

        String hashMD5 = HashCalculator.buildHexaString(md5.digest()).replaceAll(" ", "");
        String hashSHA = HashCalculator.buildHexaString(sha1.digest()).replaceAll(" ", "");

        DataHandler md5dh = new DataHandler(new ByteDataSource((byte[]) hashMD5.getBytes("UTF-8")));
        DataHandler sha1dh = new DataHandler(new ByteDataSource((byte[]) hashSHA.getBytes("UTF-8")));

        Attachment md5Attachment = new Attachment("md5", md5dh, null);
        Attachment sha1Attachment = new Attachment("sha1", sha1dh, null);

        List<Attachment> attachments = new ArrayList<Attachment>();
        attachments.add(md5Attachment);
        attachments.add(sha1Attachment);

        mc.put(MultipartBody.OUTBOUND_MESSAGE_ATTACHMENTS, attachments);

//        md5Attachment.getDataHandler().writeTo(outputStream);
//        sha1Attachment.getDataHandler().writeTo(outputStream);

//        int read = 0;
//        byte[] bytes = new byte[1024];
//        while ((read = hashInputStream.read(bytes)) != -1) {
//            outputStream.write(bytes, 0, read);
//        }

        System.out.println(">>> fileUploadBean >>> " + fileUploadBean);

//            outputStream.close();    DO NOT CLOSE NOW
    }

    public MessageContext getMc() {
        return mc;
    }

    public void setMc(MessageContext mc) {
        this.mc = mc;
    }
}
