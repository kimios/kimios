package org.kimios.kernel.converter.impl;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.StringBuilderWriter;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyXmlSerializer;
import org.htmlcleaner.TagNode;
import org.kimios.kernel.converter.ConverterImpl;
import org.kimios.kernel.converter.exception.ConverterException;
import org.kimios.kernel.converter.source.InputSource;
import org.kimios.kernel.converter.source.InputSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.*;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: farf
 * Date: 2/13/14
 * Time: 3:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class MailToHtml extends ConverterImpl {


    private static Logger logger = LoggerFactory.getLogger(MailToHtml.class);

    @Override
    public String converterTargetMimeType() {
        return "text/html";
    }


    @Override
    public InputSource convertInputSource(InputSource source) throws ConverterException {

        /*
            Try to parse email
         */
        try {
            MimeMessage message = new MimeMessage(Session.getDefaultInstance(new Properties()), source.getInputStream());
            if (message.getContent() instanceof Multipart) {
                // Convert file located to sourcePath into HTML web content
                String targetPath = temporaryRepository + "/" +
                        FileNameGenerator.generate() + ".html";


                StringBuilder buffer = new StringBuilder();

                Multipart multipart = (Multipart) message.getContent();




                for (int x = 0; x < multipart.getCount(); x++) {
                    BodyPart bodyPart = multipart.getBodyPart(x);
                    String disposition = bodyPart.getDisposition();
                    if (disposition != null && (disposition.equals(BodyPart.ATTACHMENT))) {
                        DataHandler handler = bodyPart.getDataHandler();
                        logger.debug(("mail have some attachment : " + handler.getName()));
                    } else {
                        if(bodyPart.getContentType().equals("text/html")){

                            IOUtils.copy(MimeUtility.decode(bodyPart.getInputStream(), "quoted-printable"), new StringBuilderWriter(buffer));
                            //(bodyPart.getContent());
                            break;
                        }
                    }

                }

                HtmlCleaner cleaner = new HtmlCleaner();
                CleanerProperties props = cleaner.getProperties();
                props.setCharset("UTF-8");

                // Clean HTML taken from simple string, file, URL, input stream,
                // input source or reader. Result is root node of created
                // tree-like structure. Single cleaner instance may be safely used
                // multiple times.
                TagNode node = cleaner.clean(buffer.toString());
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                new PrettyXmlSerializer(props).writeToStream(node, out);

                IOUtils.write(out.toString(), new FileOutputStream(targetPath), "UTF-8");
                InputSource result = InputSourceFactory.getInputSource(targetPath);
                result.setHumanName(source.getName() + "_" + source.getType() + ".html");
                result.setMimeType(this.converterTargetMimeType());
                return result;

            } else {

                String content = "<pre>" + message.getContent().toString() + "</pre>";
                // Convert file located to sourcePath into HTML web content
                String targetPath = temporaryRepository + "/" +
                        FileNameGenerator.generate() + ".html";


                IOUtils.write(content, new FileOutputStream(targetPath), "UTF-8");

                InputSource result = InputSourceFactory.getInputSource(targetPath);

                result.setHumanName(source.getName() + "_" + source.getType() + ".html");
                result.setMimeType(this.converterTargetMimeType());
                return result;

            }

        } catch (Exception exception) {
            throw new ConverterException(exception);
        }


    }
}
