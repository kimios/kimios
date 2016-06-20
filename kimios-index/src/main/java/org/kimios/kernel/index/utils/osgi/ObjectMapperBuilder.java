package org.kimios.kernel.index.utils.osgi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.annotation.JsonInclude;

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
