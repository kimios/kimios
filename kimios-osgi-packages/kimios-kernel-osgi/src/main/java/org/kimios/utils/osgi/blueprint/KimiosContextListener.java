package org.kimios.utils.osgi.blueprint;

import org.osgi.service.blueprint.container.BlueprintEvent;
import org.osgi.service.blueprint.container.BlueprintListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by farf on 1/3/15.
 */
public class KimiosContextListener implements BlueprintListener {



    private static Logger logger = LoggerFactory.getLogger(KimiosContextListener.class);

    public void blueprintEvent(BlueprintEvent event) {
        logger.info("event " + event.getType() + " ");

        if(event.getCause() != null){
            logger.error("event cause: ", event.getCause());
        }
    }
}
