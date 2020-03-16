package io.sbs.model;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.LoggerNameAwareMessage;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Logs4jBankSystem {
    private static Logger logger = LogManager.getLogger(Logs4jBankSystem.class);

    public String Loggs(){
        logger.info("this is infro");
        logger.fatal("this is fatal");
        logger.error("this is erroe");
        logger.warn("this is warn");
        return "Done";
    }

}
