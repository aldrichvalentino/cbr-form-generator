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
import model.ControlButtons;
import model.FormDescription;
import model.FormSolution;
import model.InputFields;
import model.OutputFields;
import model.RevisedModel;
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

      // manualy convert from CBRCase to string, then to CBRCase again
      CBRCase revisedCase = adjustCase(AdaptationController.adaptedCase);

      // retain case
      Collection<CBRCase> cases = new HashSet<CBRCase>();
      cases.add(revisedCase);
      caseBase.learnCases(cases);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return "retainResult";
  }

  private CBRCase adjustCase(CBRCase _case) {
    CBRCase revisedCase = _case;
    FormDescription description = new FormDescription();
    FormSolution solution = new FormSolution();
    RevisedModel revisedModel = new RevisedModel();
    try {
      revisedModel.setFormName(((FormDescription) revisedCase.getDescription()).getFormName());
      revisedModel.setInputFields(((FormDescription) revisedCase.getDescription()).itoString());
      revisedModel.setOutputFields(((FormDescription) revisedCase.getDescription()).otoString());
      revisedModel.setControlButtons(((FormDescription) revisedCase.getDescription()).ctoString());
      revisedModel.setLabels(((FormSolution) revisedCase.getSolution()).ltoString());
      revisedModel.setGroups(((FormSolution) revisedCase.getSolution()).gtoString());
      revisedModel.setOrders(((FormSolution) revisedCase.getSolution()).otoString());
      revisedModel.setvLayouts(((FormSolution) revisedCase.getSolution()).vltoString());
      revisedModel.sethLayouts(((FormSolution) revisedCase.getSolution()).hltoString());

      description.setId((int) revisedCase.getID());
      description.setFormName(revisedModel.getFormName());
      description
          .setInputFields(revisedModel.addDesc(InputFields.class, revisedModel.getInputFields()));
      description.setOutputFields(
          revisedModel.addDesc(OutputFields.class, revisedModel.getOutputFields()));
      description.setControlButtons(
          revisedModel.addDesc(ControlButtons.class, revisedModel.getControlButtons()));

      solution.setId((int) revisedCase.getID());
      solution.setlabel(revisedModel.addLabel(revisedModel.getLabels()));
      solution.setGroup(revisedModel.addGroup(revisedModel.getGroups()));
      solution.setOrder(revisedModel.addOrder(revisedModel.getOrders()));
      solution.setvlMember(revisedModel.addVL(revisedModel.getvLayouts()));
      solution.sethlMember(revisedModel.addHL(revisedModel.gethLayouts()));

      revisedCase.setDescription(description);
      revisedCase.setSolution(solution);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return revisedCase;
  }
}
