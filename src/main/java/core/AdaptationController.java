package core;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import edu.smu.tspell.wordnet.WordNetDatabase;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCase;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRQuery;
import es.ucm.fdi.gaia.ontobridge.OntoBridge;
import utils.Adaptation;
import utils.OntologyConnector;
import utils.WordNetConnector;

@Controller
public class AdaptationController {

    @Autowired
    private Environment env;
    private String owlPath;
    private String owlUrl;
    public static CBRCase adaptedCase = null;

    @PostMapping("/adapt")
    public String handlePost(@ModelAttribute SubmitCaseId submitCaseId, Model model) {
        int id = submitCaseId.getCaseId();
        Collection<CBRCase> cases = RetrieveController.retrievedCases;
        CBRQuery query = QueryController.globalQuery;
        CBRCase selectedCase = null;
        WordNetDatabase database =
                WordNetConnector.getInstance(env.getProperty("WORDNET_DIR")).getDatabase();
        owlPath =
                getClass().getResource("/owl/" + env.getProperty("OWL_FILENAME")).toExternalForm();
        owlUrl = env.getProperty("OWL_URL");
        OntoBridge ontoBridge = OntologyConnector.getInstance(owlUrl, owlPath).getOntoBridge();

        for (CBRCase c : cases) {
            if (id == (int) c.getID()) {
                selectedCase = c;
                break;
            }
        }

        // Adaptation
        try {
            Adaptation adaptation = new Adaptation(database, ontoBridge);
            adaptedCase = adaptation.adapt(query, selectedCase);
            // System.out.println(adaptedCase);
        } catch (Exception e) {
            e.printStackTrace();
        }

        model.addAttribute("result", adaptedCase);
        return "adaptationResult";
    }

    private class SubmitCaseId {
        private int caseId;

        public SubmitCaseId() {

        }

        public int getCaseId() {
            return caseId;
        }

        public void setCaseId(int caseId) {
            this.caseId = caseId;
        }
    }
}
