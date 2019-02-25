package core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import es.ucm.fdi.gaia.jcolibri.cbraplications.StandardCBRApplication;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCaseBase;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRQuery;
import es.ucm.fdi.gaia.jcolibri.exception.ExecutionException;

@SpringBootApplication
public class Application implements StandardCBRApplication {

    Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void configure() throws ExecutionException {
        // not used
    }

    @Override
    public CBRCaseBase preCycle() throws ExecutionException {
        // not used
        return null;
    }

    @Override
    public void cycle(CBRQuery query) throws ExecutionException {
        // not used
    }

    @Override
    public void postCycle() throws ExecutionException {
        // not used
    }

}
