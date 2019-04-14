package model.templates;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import model.OMembers;
import model.XLabel;

public class FieldGroupTemplate {
    private ArrayList<FormFieldTemplate> formFieldTemplates;
    private String layout;

    public FieldGroupTemplate(ArrayList<FormFieldTemplate> formFieldTemplates, String layout) {
        setFormFieldTemplates(formFieldTemplates);
        setLayout(layout);
    }

    public FieldGroupTemplate(List<OMembers> orderMembers, String orientation,
            Map<String, XLabel> labels) {

        ArrayList<FormFieldTemplate> formFieldTemplates = new ArrayList<>();
        for (OMembers orders : orderMembers) {
            formFieldTemplates.add(new FormFieldTemplate(orders.getMemberName(),
                    labels.get(orders.getMemberName())));
        }

        setFormFieldTemplates(formFieldTemplates);
        setLayout(orientation);
    }

    /**
     * @return the layout
     */
    public String getLayout() {
        return layout;
    }

    /**
     * @param layout the layout to set
     */
    public void setLayout(String layout) {
        this.layout = layout;
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
