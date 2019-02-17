package utils;

import java.util.ArrayList;

import es.ucm.fdi.gaia.jcolibri.util.OntoBridgeSingleton;
import es.ucm.fdi.gaia.ontobridge.OntoBridge;
import es.ucm.fdi.gaia.ontobridge.OntologyDocument;

public class OntologyConnector {
    private static OntologyConnector connector = null;
    private OntoBridge ontoBridge = null;

    private OntologyConnector(String owlUrl, String owlPaString) {
        ontoBridge = OntoBridgeSingleton.getOntoBridge();
        ontoBridge.initWithPelletReasoner();
        OntologyDocument mainOnto = new OntologyDocument(owlUrl, owlPaString);
        // There are not subontologies
        ArrayList<OntologyDocument> subOntologies = new ArrayList<OntologyDocument>();
        ontoBridge.loadOntology(mainOnto, subOntologies, false);
    }

    public static OntologyConnector getInstance(String owlUrl, String owlPath) {
        if (connector == null) {
            connector = new OntologyConnector(owlUrl, owlPath);
        }
        return connector;
    }

    public OntoBridge getOntoBridge() {
        return ontoBridge;
    }
}