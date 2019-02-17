package core;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.WordNetDatabase;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRQuery;
import es.ucm.fdi.gaia.jcolibri.cbrcore.Connector;
import es.ucm.fdi.gaia.ontobridge.OntoBridge;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.FormDescription;
import model.SimilarityAttributes;
import utils.DatabaseConnector;
import utils.OntologyConnector;
import utils.WordNetConnector;
import model.InputFields;
import model.OutputFields;
import model.ControlButtons;

@Controller
public class QueryController {

    public static FormDescription formDescription = new FormDescription();

    @Autowired
    private Environment env;
    final String OWL_PATH = getClass().getResource("/owl/FormOnto2.owl").toExternalForm();
    final String OWL_URL = "http://www.semanticweb.org/hp/ontologies/2015/2/FormOnto2.owl";
    Logger logger = LoggerFactory.getLogger(QueryController.class);

    @GetMapping("/query")
    public String handleGet(Model model) {
        logger.info("Initiating environment: WordNet, OntoBridge, and Database connection.");
        try {
            WordNetDatabase database = WordNetConnector.getInstance(env.getProperty("WORDNET_DIR")).getDatabase();
            OntoBridge ontoBridge = OntologyConnector.getInstance(OWL_URL, OWL_PATH).getOntoBridge();
            Connector connector = DatabaseConnector.getInstance(env.getProperty("HIBERNATE_DRIVER"),
                    env.getProperty("HIBERNATE_CONNECTION"), env.getProperty("HIBERNATE_DIALECT"),
                    env.getProperty("DB_USERNAME"), env.getProperty("DB_PASSWORD"));
            if (database == null || ontoBridge == null || connector == null) {
                throw new InstantiationException();
            }
        } catch (Exception e) {
            logger.error("Error in initiating environment.");
            e.printStackTrace();
        }
        logger.info("Query page loaded: Waiting for query...");
        model.addAttribute("query", new FormDescription());
        return "query";
    }

    @PostMapping("/query")
    public String handlePost(@ModelAttribute FormDescription queryModel, Model model) {
        logger.info("Query Page: Normalizing Query");
        CBRQuery query = new CBRQuery();
        query.setDescription(queryModel);
        try {
            query = normalize(query);
            queryModel = (FormDescription) query.getDescription();
        } catch (Exception e) {
            logger.error("Error in normalizing query.");
            e.printStackTrace();
        }
        // return similarity form
        model.addAttribute("similarityAttributes", new SimilarityAttributes());
        model.addAttribute("query", queryModel);
        // put state in a global variable
        formDescription = queryModel;
        logger.info("Normalized Query Page: Waiting for similarity configuration...");
        return "normalizedQuery";
    }

    private CBRQuery normalize(CBRQuery query) throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        OntoBridge ontoBridge = OntologyConnector.getInstance(OWL_URL, OWL_PATH).getOntoBridge();
        WordNetDatabase database = WordNetConnector.getInstance(env.getProperty("WORDNET_DIR")).getDatabase();
        FormDescription fd = (FormDescription) query.getDescription();

        /* Step 1: Form Name normalization */
        logger.info("Normalization of Form Name");
        String fn = fd.getFormName();
        fn = checkOntology(fn, "FormName", ontoBridge, database);
        fd.setFormName(fn);
        logger.info("Normalization of Form Name finished: " + fn);

        /* Step 2: Input Field normalization */
        logger.info("Normalization of Input Fields");
        String[] inputFields = fd.getInputFieldsText().split(",");
        Set<InputFields> sInputFields = new HashSet<InputFields>();
        for (String fields : inputFields) {
            InputFields ifds = new InputFields(fields.trim().replaceAll("\\s+", "_").toLowerCase());
            sInputFields.add(ifds);
        }
        fd.setInputFields(sInputFields);

        for (InputFields if1 : sInputFields) {
            String ifn = if1.getName();
            String oif = checkOntology(ifn, "InputFields", ontoBridge, database);
            String[] oifa = oif.split("\\s+"); // jika mengandung spasi, berarti kompon
            if (oifa.length > 1) {
                // System.out.println("ada komponen bro ");
                sInputFields.remove(if1);
                for (int i = 0; i < oifa.length; i++) {
                    // Out.println("Komponen " + oifa[i]);
                    if (ontoBridge.isInstanceOf(oifa[i], "InputFields")) {
                        // Out.println(oifa[i] + " sbg IF");
                        fd.getInputFields().add(new InputFields(oifa[i]));
                    } else if (ontoBridge.isInstanceOf(oifa[i], "OutputFields")) {
                        // Out.println(oifa[i] + " sbg OF");
                        fd.getOutputFields().add(new OutputFields(oifa[i]));
                    } else if (ontoBridge.isInstanceOf(oifa[i], "ControlButtons")) {
                        // Out.println(oifa[i] + " sbg CB");
                        fd.getControlButtons().add(new ControlButtons(oifa[i]));
                    }
                }
            } else {
                if (!ifn.equals(oifa[0]))
                    if1.setName(oifa[0]);
            }
        }

        // Menghapus elemen dgn nama yang sama
        Set<String> namaflds = removeDuplicate(sInputFields);
        sInputFields.removeAll(sInputFields);
        for (String namafld : namaflds) {
            sInputFields.add(new InputFields(namafld));
        }
        fd.setInputFields(sInputFields);
        logger.info("Normalization of Input Fields finished");

        /* Step 3: Output Fields normalization */
        logger.info("Normalization of Output Fields");
        String[] outputFields = fd.getOutputFieldsText().split(",");
        Set<OutputFields> sOutpuFields = new HashSet<OutputFields>();
        for (String fields : outputFields) {
            OutputFields ofds = new OutputFields(fields.trim().replaceAll("\\s+", "_").toLowerCase());
            sOutpuFields.add(ofds);
        }

        for (OutputFields of1 : sOutpuFields) {
            String ofn = of1.getName();
            String oof = checkOntology(ofn, "OutputFields", ontoBridge, database);
            String[] oofa = oof.split("\\s+");
            if (oofa.length > 1) { // ada komponen
                of1.setName(oofa[0]);
                for (int i = 1; i < oofa.length; i++) {
                    OutputFields ofi = new OutputFields(oofa[i]);
                    sOutpuFields.add(ofi);
                }
            } else {
                if (!ofn.equals(oofa[0]))
                    of1.setName(oofa[0]);
            }
        }

        Set<String> nameOutputFields = removeDuplicate(sOutpuFields);
        sOutpuFields.removeAll(sOutpuFields);
        for (String namafld : nameOutputFields) {
            sOutpuFields.add(new OutputFields(namafld));
        }
        fd.setOutputFields(sOutpuFields);
        logger.info("Normalization of Output Fields finished");

        /* Step 4: Control Buttons normalization */
        logger.info("Normalization of Control Buttons");
        String[] controlButtons = fd.getControlButtonsText().split(",");
        Set<ControlButtons> sControlButSet = new HashSet<ControlButtons>();
        for (String fields : controlButtons) {
            ControlButtons cbtns = new ControlButtons(fields.trim().replaceAll("\\s+", "_").toLowerCase());
            sControlButSet.add(cbtns);
        }

        for (ControlButtons cb1 : sControlButSet) {
            String cbn = cb1.getName();
            String ocb = checkOntology(cbn, "ControlButtons", ontoBridge, database);
            String[] cfa = ocb.split("\\s+");
            if (cfa.length > 1) {
                cb1.setName(cfa[0]);
                for (int i = 1; i < cfa.length; i++) {
                    ControlButtons cfi = new ControlButtons(cfa[i]);
                    sControlButSet.add(cfi);
                }
            } else {
                if (!cbn.equals(cfa[0]))
                    cb1.setName(cfa[0]);
            }
        }

        Set<String> nameControlBut = removeDuplicate(sControlButSet);
        sControlButSet.removeAll(sControlButSet);
        for (String namafld : nameControlBut) {
            sControlButSet.add(new ControlButtons(namafld));
        }
        fd.setControlButtons(sControlButSet);
        logger.info("Normalization of Control Buttons finished");

        CBRQuery normalizedQuery = new CBRQuery();
        normalizedQuery.setDescription(fd);
        return normalizedQuery;
    }

    private String checkOntology(String ins, String cls, OntoBridge ob, WordNetDatabase database) {
        String ret = "";

        if (ob.existsInstance(ins, cls)) {
            // System.out.println(ins + " ada di onto");
            // it adalah himp komponen ins
            Iterator<String> it = ob.listPropertyValue(ins, "hasComponents");
            Iterator<String> ist = ob.listPropertyValue(ins, "hasNorName");

            if (it.hasNext() && !cls.equals("FormName")) { // jika ins punya komponen dan bukan nama form
                while (it.hasNext())
                    // gabungkan komponen2 itu
                    ret = ret.concat(" " + ob.getShortName(it.next())).trim();
            } else if (ist.hasNext()) {
                ret = ob.getShortName(ist.next());
            } else {
                ret = ins;
            }
        } else {// jk tdk ada di ontologi form, cek di wordnet.
            Synset[] synsets = database.getSynsets(ins);
            if (synsets.length > 0) { // jika memiliki padanan di WN
                // System.out.println(ins + " ada sinonim di wn");
                for (int i = 0; i < synsets.length; i++) {
                    String[] wordForms = synsets[i].getWordForms();
                    for (int j = 0; j < wordForms.length; j++) {
                        String rep = wordForms[j].replaceAll("\\s+", "_");
                        // System.out.println("padanan " + ins + " : " + rep);
                        // cek di ontologi form
                        if (ob.existsInstance(rep, cls)) {
                            // System.out.println("padanan ada di onto: " + rep);
                            return rep; // jika wordform ada di ontologi
                        } else {
                            ret = ins;
                            // logger.error("Synonym " + ins + " is not found in ontology.");
                        }
                    }
                }
            } else { // jika tidak ada di Ontologi form dan tdk ada di WN
                logger.error(ins + " is not found in ontology and wordnet.");
                ret = ins;
            }
        }
        return ret;
    }

    // private <T> boolean berisi(Set<T> setfld, String elm) throws
    // NoSuchMethodException, SecurityException,
    // IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    // for (T elmc : setfld) {
    // Method m = elmc.getClass().getDeclaredMethod("getName");
    // String fln = (String) m.invoke(elmc); // mengambil nama elemen
    // Out.println("Nama Elemen di set " + fln);
    // if (fln.equals(elm)) {
    // Out.println("Berisi ..");
    // return true;
    // }
    // }
    // return false;
    // }

    private <T> Set<String> removeDuplicate(Set<T> setfld) throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Set<String> setNama = new HashSet<String>();
        for (T elmc : setfld) {
            Method m = elmc.getClass().getDeclaredMethod("getName");
            String fln = (String) m.invoke(elmc); // mengambil nama elemen
            setNama.add(fln);
        }
        if (setNama.iterator().hasNext()) {
            // Out.println("Print set ");
            setNama.iterator().next().toString();
        }
        return setNama;
    }

}