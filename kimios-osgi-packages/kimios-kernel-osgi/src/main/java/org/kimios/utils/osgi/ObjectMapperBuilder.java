package org.kimios.utils.osgi;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Created by farf on 6/16/14.
 */
public class ObjectMapperBuilder {


        public ObjectMapper createInstance() {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
            return mapper;
        }

}
