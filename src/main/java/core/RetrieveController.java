package core;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import es.ucm.fdi.gaia.jcolibri.cbrcore.Attribute;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCase;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCaseBase;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRQuery;
import es.ucm.fdi.gaia.jcolibri.cbrcore.Connector;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.RetrievalResult;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.NNretrieval.NNConfig;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.NNretrieval.NNScoringMethod;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.NNretrieval.similarity.local.Equal;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.selection.SelectCases;
import model.CaseBase;
import model.FormDescription;
import model.SimilarityAttributes;
import utils.DatabaseConnector;
import utils.Jaccard;
import utils.StandardGlobalSimilarityFunction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class RetrieveController {

    public static Collection<CBRCase> retrievedCases = null;

    @Autowired
    private Environment env;
    Logger logger = LoggerFactory.getLogger(QueryController.class);

    @PostMapping("/retrieval")
    public String handlePost(@ModelAttribute SimilarityAttributes similarityAttributes,
            Model model) {

        // Set NNConfig
        logger.info("Retrieval Page: Setting the similarity attributes");
        NNConfig nnConfig = new NNConfig();
        Attribute attribute;

        // form name weight
        attribute = new Attribute("formName", FormDescription.class);
        nnConfig.addMapping(attribute, new Equal()); // use equal similarity function
        nnConfig.setWeight(attribute, similarityAttributes.getFormNameWeight());

        // input fields weight
        attribute = new Attribute("inputFields", FormDescription.class);
        nnConfig.addMapping(attribute, new Jaccard());
        nnConfig.setWeight(attribute, similarityAttributes.getInputFieldsWeight());

        // output fields weight
        attribute = new Attribute("outputFields", FormDescription.class);
        nnConfig.addMapping(attribute, new Jaccard());
        nnConfig.setWeight(attribute, similarityAttributes.getOutputFieldsWeight());

        // control buttons weight
        attribute = new Attribute("controlButtons", FormDescription.class);
        nnConfig.addMapping(attribute, new Jaccard());
        nnConfig.setWeight(attribute, similarityAttributes.getControlButtonsWeight());

        // set similarity function as Average
        nnConfig.setDescriptionSimFunction(new StandardGlobalSimilarityFunction());

        // Execute NN
        logger.info("Retrieval Page: Executing NN retrieval");
        try {
            CBRCaseBase caseBase = new CaseBase();
            Connector connector = DatabaseConnector.getInstance(env.getProperty("HIBERNATE_DRIVER"),
                    env.getProperty("HIBERNATE_CONNECTION"), env.getProperty("HIBERNATE_DIALECT"),
                    env.getProperty("DB_USERNAME"), env.getProperty("DB_PASSWORD"));

            caseBase.init(connector);
            CBRQuery query = new CBRQuery();
            query.setDescription(QueryController.formDescription);

            Collection<RetrievalResult> eval =
                    NNScoringMethod.evaluateSimilarity(caseBase.getCases(), query, nnConfig);

            List<Double> evalScores = new ArrayList<Double>();
            for (Iterator<RetrievalResult> it = eval.iterator(); it.hasNext();) {
                evalScores.add(it.next().getEval());
            }
            evalScores.sort(Collections.reverseOrder());
            evalScores = evalScores.subList(0, similarityAttributes.getkNumber());

            // Select k cases
            Collection<CBRCase> selectedcases =
                    SelectCases.selectTopK(eval, similarityAttributes.getkNumber());

            // store cases in global variable
            retrievedCases = selectedcases;

            model.addAttribute("cases", selectedcases.toArray());
            model.addAttribute("evalScores", evalScores);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "retrievalResult";
    }
}
