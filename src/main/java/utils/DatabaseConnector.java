package utils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCase;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CaseBaseFilter;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CaseComponent;
import es.ucm.fdi.gaia.jcolibri.cbrcore.Connector;
import es.ucm.fdi.gaia.jcolibri.exception.InitializingException;
import model.FormDescription;
import model.FormSolution;

public class DatabaseConnector implements Connector {
    public static DatabaseConnector connector = null;

    Logger logger = LoggerFactory.getLogger(DatabaseConnector.class);

    Configuration cfg;
    SessionFactory sessionFactory;

    final String FORM_DESCRIPTION_PATH = getClass().getResource("/database/FormDescription.hbm.xml").toExternalForm();
    final String FORM_SOLUTION_PATH = getClass().getResource("/database/FormSolution.hbm.xml").toExternalForm();

    public DatabaseConnector(String driver, String connection, String dialect, String username, String password) {
        try {
            System.out.println(driver);
            cfg = new Configuration().setProperty("hibernate.connection.driver_class", driver)
                    .setProperty("hibernate.connection.url", connection).setProperty("hibernate.dialect", dialect)
                    .setProperty("hibernate.connection.username", username)
                    .setProperty("hibernate.connection.password", password);
            cfg.addResource(FORM_DESCRIPTION_PATH);
            cfg.addResource(FORM_SOLUTION_PATH);
            sessionFactory = cfg.buildSessionFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initFromXMLfile(URL file) throws InitializingException {
        // do nothing
    }

    @Override
    public void close() {
        sessionFactory.close();
    }

    @Override
    public void storeCases(Collection<CBRCase> cases) {

    }

    @Override
    public void deleteCases(Collection<CBRCase> cases) {

    }

    @Override
    public Collection<CBRCase> retrieveAllCases() {
        ArrayList<CBRCase> res = new ArrayList<CBRCase>();
        Session session;
        Transaction transaction;
        List descList = null;
        HashMap<Object, CaseComponent> solList = null;

        try {

            // if (solutionClassName != null) {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            solList = new HashMap<Object, CaseComponent>();
            List list = session.createQuery(String.format("from %s", FormSolution.class.getName())).list();

            transaction.commit();
            session.close();

            for (Iterator iter = list.iterator(); iter.hasNext();) {
                CaseComponent cc = (CaseComponent) iter.next();
                solList.put(cc.getIdAttribute().getValue(cc), cc);
            }

            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            descList = session.createQuery(String.format("from %s", FormDescription.class.getName())).list();
            transaction.commit();
            session.close();

            for (Iterator iter = descList.iterator(); iter.hasNext();) {
                CBRCase _case = new CBRCase();
                CaseComponent desc = (CaseComponent) iter.next();
                _case.setDescription(desc);

                // if (solutionClassName != null) {
                CaseComponent cc = solList.get(desc.getIdAttribute().getValue(desc));
                if (cc != null)
                    _case.setSolution(cc);
                // }
                // if (justOfSolutionClassName != null) {
                // CaseComponent cc = justSolList.get(desc.getIdAttribute().getValue(desc));
                // if (cc != null)
                // _case.setJustificationOfSolution(cc);
                // }
                // if (resultClassName != null) {
                // CaseComponent cc = resList.get(desc.getIdAttribute().getValue(desc));
                // if (cc != null)
                // _case.setResult(cc);
                // }

                res.add(_case);

            }

            // transaction.commit();
            // session.close();

        } catch (Exception e) {
            logger.error("Error in makin query to the database");
            e.printStackTrace();
        }
        logger.info(res.size() + " cases read from the database.");
        return res;
    }

    @Override
    public Collection<CBRCase> retrieveSomeCases(CaseBaseFilter filter) {
        return null;
    }

}