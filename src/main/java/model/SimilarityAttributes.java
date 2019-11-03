package model;

public class SimilarityAttributes {
  private double formNameWeight;
  private double inputFieldsWeight;
  private double outputFieldsWeight;
  private double controlButtonsWeight;
  private int kNumber;

  public SimilarityAttributes() {
  }

  /**
   * @return the kNumber
   */
  public int getkNumber() {
    return kNumber;
  }

  /**
   * @param kNumber the kNumber to set
   */
  public void setkNumber(int kNumber) {
    this.kNumber = kNumber;
  }

  /**
   * @return the controlButtonsWeight
   */
  public double getControlButtonsWeight() {
    return controlButtonsWeight;
  }

  /**
   * @param controlButtonsWeight the controlButtonsWeight to set
   */
  public void setControlButtonsWeight(double controlButtonsWeight) {
    this.controlButtonsWeight = controlButtonsWeight;
  }

  /**
   * @return the outputFieldsWeight
   */
  public double getOutputFieldsWeight() {
    return outputFieldsWeight;
  }

  /**
   * @param outputFieldsWeight the outputFieldsWeight to set
   */
  public void setOutputFieldsWeight(double outputFieldsWeight) {
    this.outputFieldsWeight = outputFieldsWeight;
  }

  /**
   * @return the inputFieldsWeight
   */
  public double getInputFieldsWeight() {
    return inputFieldsWeight;
  }

  /**
   * @param inputFieldsWeight the inputFieldsWeight to set
   */
  public void setInputFieldsWeight(double inputFieldsWeight) {
    this.inputFieldsWeight = inputFieldsWeight;
  }

  /**
   * @return the formNameWeight
   */
  public double getFormNameWeight() {
    return formNameWeight;
  }

  /**
   * @param formNameWeight the formNameWeight to set
   */
  public void setFormNameWeight(double formNameWeight) {
    this.formNameWeight = formNameWeight;
  }
}
