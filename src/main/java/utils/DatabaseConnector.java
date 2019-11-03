package utils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import model.ControlButtons;
import model.FormDescription;
import model.FormSolution;
import model.Forms;
import model.GMembers;
import model.Groups;
import model.HLMembers;
import model.InputFields;
import model.OMembers;
import model.Orders;
import model.OutputFields;
import model.VLMembers;
import model.XLabel;

public class DatabaseConnector implements Connector {
  private static DatabaseConnector connector = null;

  Logger logger = LoggerFactory.getLogger(DatabaseConnector.class);

  Configuration cfg;
  SessionFactory sessionFactory;

  final String FORMS_PATH = getClass().getResource("/database/Form.hbm.xml").toExternalForm();
  final String FORM_DESCRIPTION_PATH =
      getClass().getResource("/database/FormDescription.hbm.xml").toExternalForm();
  final String FORM_SOLUTION_PATH =
      getClass().getResource("/database/FormSolution.hbm.xml").toExternalForm();

  private DatabaseConnector(String driver, String connection, String dialect, String username,
      String password) {
    try {
      cfg = new Configuration().setProperty("hibernate.connection.driver_class", driver)
          .setProperty("hibernate.connection.url", connection)
          .setProperty("hibernate.dialect", dialect)
          .setProperty("hibernate.connection.username", username)
          .setProperty("hibernate.connection.password", password);
      cfg.addResource(FORM_DESCRIPTION_PATH);
      cfg.addResource(FORM_SOLUTION_PATH);
      cfg.addResource(FORMS_PATH);
      sessionFactory = cfg.buildSessionFactory();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static DatabaseConnector getInstance(String driver, String conn, String dialect,
      String username, String password) {
    if (connector == null) {
      connector = new DatabaseConnector(driver, conn, dialect, username, password);
    }
    return connector;
  }

  public SessionFactory getSession() {
    return sessionFactory;
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

    Session session = sessionFactory.openSession();
    Transaction transaction = session.beginTransaction();
    try {
      for (CBRCase frmi : cases) {
        Forms forms = setToZero((int) frmi.getID(), (FormDescription) frmi.getDescription(),
            (FormSolution) frmi.getSolution());
        session.save(forms);
      }
      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      e.printStackTrace();
    }
    session.close();
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
      session = sessionFactory.openSession();
      transaction = session.beginTransaction();
      solList = new HashMap<Object, CaseComponent>();
      List list =
          session.createQuery(String.format("from %s", FormSolution.class.getName())).list();

      transaction.commit();
      session.close();

      for (Iterator iter = list.iterator(); iter.hasNext();) {
        CaseComponent cc = (CaseComponent) iter.next();
        solList.put(cc.getIdAttribute().getValue(cc), cc);
      }

      session = sessionFactory.openSession();
      transaction = session.beginTransaction();
      descList =
          session.createQuery(String.format("from %s", FormDescription.class.getName())).list();
      transaction.commit();
      session.close();

      for (Iterator iter = descList.iterator(); iter.hasNext();) {
        CBRCase _case = new CBRCase();
        CaseComponent desc = (CaseComponent) iter.next();
        _case.setDescription(desc);

        CaseComponent cc = solList.get(desc.getIdAttribute().getValue(desc));
        if (cc != null)
          _case.setSolution(cc);
        res.add(_case);
      }
    } catch (Exception e) {
      logger.error("Error in making query to the database");
      e.printStackTrace();
    }

    logger.info(res.size() + " cases read from the database.");
    return res;
  }

  @Override
  public Collection<CBRCase> retrieveSomeCases(CaseBaseFilter filter) {
    return null;
  }

  private Forms setToZero(int id, FormDescription formDescription, FormSolution formSolution) {
    Forms newForm = new Forms();
    FormDescription newFormDescription = new FormDescription();
    FormSolution newFormSolution = new FormSolution();

    Set<InputFields> inputFields = new HashSet<InputFields>();
    for (InputFields inputField : formDescription.getInputFields()) {
      inputFields.add(new InputFields(inputField.getName()));
    }

    Set<OutputFields> outputFields = new HashSet<OutputFields>();
    for (OutputFields outputField : formDescription.getOutputFields()) {
      outputFields.add(new OutputFields(outputField.getName()));
    }

    Set<ControlButtons> controlButtons = new HashSet<ControlButtons>();
    for (ControlButtons controlButton : formDescription.getControlButtons()) {
      controlButtons.add(new ControlButtons(controlButton.getName()));
    }

    newFormDescription.setFormName(formDescription.getFormName());
    newFormDescription.setInputFields(inputFields);
    newFormDescription.setOutputFields(outputFields);
    newFormDescription.setControlButtons(controlButtons);
    Set<FormDescription> setFormDescription = new HashSet<FormDescription>();
    setFormDescription.add(newFormDescription);
    newForm.setFormDes(setFormDescription);

    Map<String, XLabel> labels = formSolution.getlabel();
    for (Map.Entry<String, XLabel> label : labels.entrySet()) {
      label.setValue(new XLabel(label.getValue().getLabel()));
    }

    List<Groups> groups = new ArrayList<Groups>();
    for (Groups group : formSolution.getGroup()) {
      Groups g = new Groups();
      Set<GMembers> gMembers = new HashSet<GMembers>();
      for (GMembers gMember : group.getgMembers()) {
        gMembers.add(new GMembers(gMember.getName()));
      }
      g.setgMembers(gMembers);
      groups.add(g);
    }

    List<Orders> orders = new ArrayList<Orders>();
    for (Orders group : formSolution.getOrder()) {
      Orders o = new Orders();
      List<OMembers> oMembers = new ArrayList<OMembers>();
      for (OMembers oMember : group.getoMembers()) {
        oMembers.add(new OMembers(oMember.getMemberName()));
      }
      o.setoMembers(oMembers);
      orders.add(o);
    }

    List<HLMembers> hLMembers = new ArrayList<HLMembers>();
    for (HLMembers hLMember : formSolution.gethlMember()) {
      hLMembers.add(new HLMembers(hLMember.getMemberName()));
    }

    List<VLMembers> vLMembers = new ArrayList<VLMembers>();
    for (VLMembers vLMember : formSolution.getvlMember()) {
      vLMembers.add(new VLMembers(vLMember.getName()));
    }

    // newFormSolution.setId(id);
    newFormSolution.setlabel(labels);
    newFormSolution.setGroup(groups);
    newFormSolution.setOrder(orders);
    newFormSolution.sethlMember(hLMembers);
    newFormSolution.setvlMember(vLMembers);
    Set<FormSolution> setFormSolution = new HashSet<FormSolution>();
    setFormSolution.add(newFormSolution);
    newForm.setFormSol(setFormSolution);

    System.out.println(newFormDescription);
    System.out.println(newFormSolution);

    return newForm;
  }
}
