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
    final String OWL_PATH = getClass().getResource("/owl/FormOnto2.owl").toExternalForm();
    final String OWL_URL = "http://www.semanticweb.org/hp/ontologies/2015/2/FormOnto2.owl";
    public static CBRCase adaptedCase = null;

    @PostMapping("/adapt")
    public String handlePost(@ModelAttribute SubmitCaseId submitCaseId, Model model) {
        int id = submitCaseId.getCaseId();
        Collection<CBRCase> cases = RetrieveController.retrievedCases;
        CBRQuery query = QueryController.globalQuery;
        CBRCase selectedCase = null;
        WordNetDatabase database =
                WordNetConnector.getInstance(env.getProperty("WORDNET_DIR")).getDatabase();
        OntoBridge ontoBridge = OntologyConnector.getInstance(OWL_URL, OWL_PATH).getOntoBridge();

        // TODO: change this searching algo
        for (CBRCase c : cases) {
            if (id == (int) c.getID()) {
                selectedCase = c;
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
