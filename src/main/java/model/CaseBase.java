package model;

import java.util.Collection;

import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCase;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCaseBase;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CaseBaseFilter;
import es.ucm.fdi.gaia.jcolibri.cbrcore.Connector;
import es.ucm.fdi.gaia.jcolibri.exception.InitializingException;

public class CaseBase implements CBRCaseBase {
    private Connector connector;

    @Override
    public void init(Connector connector) throws InitializingException {
        this.connector = connector;
    }

    @Override
    public void close() {
        this.connector.close();
    }

    @Override
    public Collection<CBRCase> getCases() {
        return this.connector.retrieveAllCases();
    }

    @Override
    public Collection<CBRCase> getCases(CaseBaseFilter filter) {
        return null;
    }

    @Override
    public void learnCases(Collection<CBRCase> cases) {
        connector.storeCases(cases);
    }

    @Override
    public void forgetCases(Collection<CBRCase> cases) {

    }

}