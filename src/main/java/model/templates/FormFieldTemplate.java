package model.templates;

import java.util.Iterator;
import es.ucm.fdi.gaia.ontobridge.OntoBridge;
import model.InputFields;
import model.XLabel;
import utils.OntologyConnector;

public class FormFieldTemplate {
    private String name;
    private String type;
    private String label;
    private String horizontalLayout;

    final String owlPath = getClass().getResource("/owl/FormOnto2.owl").toExternalForm();
    final String owlUrl = "http://www.semanticweb.org/hp/ontologies/2015/2/FormOnto2.owl";
    // TODO: change owlPath and owlUrl to env or put in a conf file

    public FormFieldTemplate(String name, String type, String label) {
        this.setName(name);
        this.setType(type);
        this.setLabel(label);
        this.setHorizontalLayout("none");
    }

    public FormFieldTemplate(InputFields inputFields) {
        name = inputFields.getName();
        type = getType(name);
        label = name;
        this.setHorizontalLayout("none");
    }

    public FormFieldTemplate(String elementName, XLabel elementLabel) {
        // Find the horizontal layout based on '(' or ')'
        if (elementName.charAt(0) == '(') {
            this.setHorizontalLayout("start");
        } else if (elementName.charAt(elementName.length() - 1) == ')') {
            this.setHorizontalLayout("end");
        } else {
            this.setHorizontalLayout("none");
        }
        // Clean the name from unwanted tokens
        name = elementName.replace("(", "").replace(")", "");
        type = getType(name);
        String originalLabel = elementLabel.getLabel();
        if (originalLabel.startsWith("xn")) {
            // Label has options
            String[] parts = originalLabel.split(",");
            label = parts[0].replace("xn", "");
        } else {
            label = originalLabel;
        }
    }

    private String getType(String elm) {
        String type = "text"; // default value
        OntoBridge ontoBridge = OntologyConnector.getInstance(owlUrl, owlPath).getOntoBridge();
        try {
            Iterator<String> it = ontoBridge.listPropertyValue(elm, "isATypeOf");
            while (it.hasNext()) {
                type = ontoBridge.getShortName(it.next());
            }

            switch (type) {
                case "tcheckbox":
                    type = "checkbox";
                    break;
                case "tradio":
                    type = "radio";
                    break;
                case "toption":
                    type = "select";
                    break;
                case "tdropdown":
                    type = "select";
                    break;
                case "tpassword":
                    type = "password";
                    break;
                case "tsubmit":
                    type = "button-submit";
                    break;
                case "treset":
                    type = "button-reset";
                    break;
                case "tbutton":
                    type = "button";
                    break;
                case "timage":
                    type = "file";
                    break;
                case "tnumber":
                    type = "number";
                    break;
                case "tdate":
                    type = "date";
                    break;
                case "temail":
                    type = "email";
                    break;
                default:
                    type = "text";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return type;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return the horizontalLayout
     */
    public String getHorizontalLayout() {
        return horizontalLayout;
    }

    /**
     * @param horizontalLayout the horizontalLayout to set
     */
    public void setHorizontalLayout(String horizontalLayout) {
        this.horizontalLayout = horizontalLayout;
    }

}
