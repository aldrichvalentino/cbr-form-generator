package model.templates;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import model.OMembers;
import model.VLMembers;
import model.XLabel;

public class FieldGroupTemplate {
  private ArrayList<FormFieldTemplate> formFieldTemplates;

  public FieldGroupTemplate(ArrayList<FormFieldTemplate> formFieldTemplates, String layout) {
    setFormFieldTemplates(formFieldTemplates);
  }

  public FieldGroupTemplate(ArrayList<VLMembers> formLayouts, List<OMembers> orderMembers,
      Map<String, XLabel> labels, String owlPath, String owlUrl) {

    ArrayList<FormFieldTemplate> formFieldTemplates = new ArrayList<>();
    for (int currentElement = 0; currentElement < orderMembers.size(); currentElement++) {
      formFieldTemplates.add(new FormFieldTemplate(formLayouts.get(currentElement).getName(),
          labels.get(orderMembers.get(currentElement).getMemberName()), owlPath, owlUrl));
    }

    setFormFieldTemplates(formFieldTemplates);
  }

  /**
   * @return the formFieldTemplates
   */
  public ArrayList<FormFieldTemplate> getFormFieldTemplates() {
    return formFieldTemplates;
  }

  /**
   * @param formFieldTemplates the formFieldTemplates to set
   */
  public void setFormFieldTemplates(ArrayList<FormFieldTemplate> formFieldTemplates) {
    this.formFieldTemplates = formFieldTemplates;
  }

}
