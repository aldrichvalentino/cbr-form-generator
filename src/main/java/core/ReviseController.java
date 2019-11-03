package core;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCase;
import model.ControlButtons;
import model.FormDescription;
import model.FormSolution;
import model.InputFields;
import model.OutputFields;
import model.RevisedModel;

@Controller
public class ReviseController {

  @GetMapping("/revise")
  public String handleGet(Model model) {
    model.addAttribute("revisedModel", new model.RevisedModel());
    model.addAttribute("adaptedCase", AdaptationController.adaptedCase);
    return "reviseForm";
  }

  @PostMapping("/revise")
  public String handlePost(@ModelAttribute RevisedModel revisedModel, Model model) {
    CBRCase revisedCase = AdaptationController.adaptedCase;
    FormDescription description = new FormDescription();
    FormSolution solution = new FormSolution();

    try {
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

      AdaptationController.adaptedCase = revisedCase;
      model.addAttribute("result", revisedCase);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return "adaptationResult";
  }
}
