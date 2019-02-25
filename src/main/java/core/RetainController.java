package core;

import java.util.Collection;
import java.util.HashSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCase;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCaseBase;
import es.ucm.fdi.gaia.jcolibri.cbrcore.Connector;
import model.CaseBase;
import utils.DatabaseConnector;

@Controller
public class RetainController {

    @Autowired
    private Environment env;

    @GetMapping("/retain")
    public String handleGet(Model model) {
        try {
            CBRCaseBase caseBase = new CaseBase();
            Connector connector = DatabaseConnector.getInstance(env.getProperty("HIBERNATE_DRIVER"),
                    env.getProperty("HIBERNATE_CONNECTION"), env.getProperty("HIBERNATE_DIALECT"),
                    env.getProperty("DB_USERNAME"), env.getProperty("DB_PASSWORD"));

            caseBase.init(connector);

            // retain case
            System.out.println("mo retain");
            System.out.println(AdaptationController.adaptedCase);
            Collection<CBRCase> cases = new HashSet<CBRCase>();
            cases.add(AdaptationController.adaptedCase);
            caseBase.learnCases(cases); // FIXME: this still cause error
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "retainResult";
    }
}
