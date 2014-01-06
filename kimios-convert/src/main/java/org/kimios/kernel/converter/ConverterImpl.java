package org.kimios.kernel.converter;

import org.kimios.kernel.configuration.Config;
import org.kimios.kernel.converter.exception.ConverterException;
import org.kimios.kernel.converter.source.InputSource;
import org.kimios.kernel.converter.exception.MethodNotImplemented;
import org.kimios.utils.configuration.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public abstract class ConverterImpl implements Converter {

    protected static Logger log = LoggerFactory.getLogger(ConverterImpl.class);
    protected final String temporaryRepository;

    protected ConverterImpl() {
        temporaryRepository = ConfigurationManager.getValue(Config.DEFAULT_TEMPORARY_PATH);
        try{
            File file = new File(temporaryRepository);
            if(!file.exists())
                file.mkdirs();
        } catch (Exception e){
            log.error("Error while creating temp repository converter", e);
        }
        if(log.isDebugEnabled()){
            log.debug("Calling " + this.getClass().getName() + " converter implementation...");
        }
    }

    public InputSource convertInputSource(InputSource source) throws ConverterException {
        throw new MethodNotImplemented("convertInputSource");
    }

    public InputSource convertInputSources(List<InputSource> sources) throws ConverterException {
        throw new MethodNotImplemented("convertInputSources");
    }
}
