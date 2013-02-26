package org.kimios.kernel.converter;

import org.kimios.kernel.converter.exception.ConverterNotFound;
import org.kimios.kernel.converter.impl.DocToHTML;
import org.kimios.kernel.converter.impl.FileToZip;
import org.kimios.kernel.converter.impl.PDFMerger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConverterFactory {

    private static Logger log = LoggerFactory.getLogger(ConverterFactory.class);

    public static Converter getConverter(String className) throws ConverterNotFound {
        try {

            log.debug("Calling ConverterFactory...");
            return (Converter) Class.forName(className).newInstance();

        } catch (ClassNotFoundException e) {

            /*
            Check if given converter corresponds to file extension
             */
            log.warn("Converter implementation not found, trying to get from extension...");

            if (className.equals("zip")) {
                return new FileToZip();
            }

            if (className.equals("pdf")) {
                return new PDFMerger();
            }

            if (className.equals("html")) {
                return new DocToHTML();
            }

            throw new ConverterNotFound(e);

        } catch (InstantiationException e) {
            throw new ConverterNotFound(e);

        } catch (IllegalAccessException e) {
            throw new ConverterNotFound(e);
        }
    }
}
