package org.kimios.kernel.index.utils.osgi;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by farf on 6/16/14.
 */
public class ObjectMapperBuilder {

        public ObjectMapper createInstance() {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            return mapper;
        }
}
