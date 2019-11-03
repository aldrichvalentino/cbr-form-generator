package model;

import java.util.*;
import es.ucm.fdi.gaia.jcolibri.cbrcore.Attribute;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CaseComponent;;

public class FormDescription implements CaseComponent {

  int id;
  private String formName;
  private String inputFieldsText;
  private String outputFieldsText;
  private String controlButtonsText;

  Set<InputFields> inputFields;
  Set<OutputFields> outputFields;
  Set<ControlButtons> controlButtons;

  public String itoString() {
    String s = "";
    Iterator<InputFields> iitr = inputFields.iterator();
    while (iitr.hasNext())
      s = s.concat(iitr.next().getName()).concat(" ");
    return s;
  }

  public String otoString() {
    String s = "";
    Iterator<OutputFields> oitr = outputFields.iterator();
    while (oitr.hasNext())
      s = s.concat(oitr.next().getName()).concat(" ");
    return s;
  }

  public String ctoString() {
    String s = "";
    Iterator<ControlButtons> citr = controlButtons.iterator();
    while (citr.hasNext())
      s = s.concat(citr.next().getName()).concat(" ");
    return s;
  }

  public String toString() {
    String s = "[ Form Name= ";
    s = s.concat(formName);
    s = s.concat(" Input Fields= ");
    for (InputFields iitr : inputFields)
      s = s.concat(iitr.getName()).concat(" ");
    s = s.concat(" Output Fields= ");
    for (OutputFields oitr : outputFields)
      s = s.concat(oitr.getName()).concat(" ");
    s = s.concat(" Control Buttons= ");
    for (ControlButtons citr : controlButtons)
      s = s.concat(citr.getName()).concat(" ");
    s = s.concat("]");
    return s;
  }

  /**
   * @return the FormName
   */
  public String getFormName() {
    return formName;
  }

  /**
   * @param the form name to set
   */
  public void setFormName(String formname) {
    formName = formname;
  }

  /**
   * @return the caseId
   */
  public int getId() {
    return id;
  }

  /**
   * @param caseId the caseId to set
   */
  public void setId(int Id) {
    this.id = Id;
  }

  /**
   * @return the input fields
   */
  public Set<InputFields> getInputFields() {
    return inputFields;
  }

  /**
   * @param the input fields to set
   */
  public void setInputFields(Set<InputFields> inputfields) {
    inputFields = inputfields;
  }

  /**
   * @return the output fields
   */
  public Set<OutputFields> getOutputFields() {
    return outputFields;
  }

  /**
   * @param the output fields to set
   */
  public void setOutputFields(Set<OutputFields> outputfields) {
    outputFields = outputfields;
  }

  /**
   * @return the function buttons
   */
  public Set<ControlButtons> getControlButtons() {
    return controlButtons;
  }

  /**
   * @param the function buttons to set
   */
  public void setControlButtons(Set<ControlButtons> functions) {
    controlButtons = functions;
  }

  /**
   * @return the controlButtonsText
   */
  public String getControlButtonsText() {
    return controlButtonsText;
  }

  /**
   * @param controlButtonsText the controlButtonsText to set
   */
  public void setControlButtonsText(String controlButtonsText) {
    this.controlButtonsText = controlButtonsText;
  }

  /**
   * @return the outputFieldsText
   */
  public String getOutputFieldsText() {
    return outputFieldsText;
  }

  /**
   * @param outputFieldsText the outputFieldsText to set
   */
  public void setOutputFieldsText(String outputFieldsText) {
    this.outputFieldsText = outputFieldsText;
  }

  /**
   * @return the inputFieldsText
   */
  public String getInputFieldsText() {
    return inputFieldsText;
  }

  /**
   * @param inputFieldsText the inputFieldsText to set
   */
  public void setInputFieldsText(String inputFieldsText) {
    this.inputFieldsText = inputFieldsText;
  }

  public Attribute getIdAttribute() {
    return new Attribute("id", this.getClass());
  }

}
