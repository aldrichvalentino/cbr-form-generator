package model.templates;

import java.util.ArrayList;
import java.util.Iterator;
import es.ucm.fdi.gaia.ontobridge.OntoBridge;
import model.InputFields;
import model.XLabel;
import utils.OntologyConnector;

public class ServerTemplate {
    private String name;
    private boolean hasOptions;
    private int isCheckbox;
    private ArrayList<String> values;
    private String type;

    private OntoBridge ontoBridge;

    public ServerTemplate(String name, boolean hasOptions, int isCheckbox, ArrayList<String> values,
            String type, String owlPath, String owlUrl) {
        this.ontoBridge = OntologyConnector.getInstance(owlUrl, owlPath).getOntoBridge();
        this.setName(name);
        this.setHasOptions(hasOptions);
        this.setValues(values);
        this.setIsCheckbox(isCheckbox);
        this.setType(type);
    }

    public ServerTemplate(InputFields singleField, XLabel labelForOptions, String owlPath,
            String owlUrl) {
        this.ontoBridge = OntologyConnector.getInstance(owlUrl, owlPath).getOntoBridge();
        this.setName(singleField.getName());
        this.buildOptionValues(labelForOptions);
        String type = getType(singleField.getName());
        this.setType(type);
        this.setIsCheckbox(type == "checkbox" ? 1 : 0);
    }

    private void buildOptionValues(XLabel label) {
        String[] labels = label.getLabel().split(",");
        if (labels[0].startsWith("xn")) {
            labels[0] = labels[0].replace("xn", "");
            // System.out.println("Ada Nama");
            this.setHasOptions(true);
            ArrayList<String> optionValues = new ArrayList<String>();
            for (int i = 1; i < labels.length; i++) {
                optionValues.add(labels[i]);
            }
            this.setValues(optionValues);
        } else {
            this.setHasOptions(false);
        }
    }

    private String getType(String elm) {
        String type = "text"; // default value
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
                case "toutput":
                    type = "output";
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
     * @return the hasOptions
     */
    public boolean isHasOptions() {
        return hasOptions;
    }

    /**
     * @param hasOptions the hasOptions to set
     */
    public void setHasOptions(boolean hasOptions) {
        this.hasOptions = hasOptions;
    }

    /**
     * @return the values
     */
    public ArrayList<String> getValues() {
        return values;
    }

    /**
     * @param values the values to set
     */
    public void setValues(ArrayList<String> values) {
        this.values = values;
    }

    /**
     * @return the isCheckbox
     */
    public int getIsCheckbox() {
        return isCheckbox;
    }

    /**
     * @param isCheckbox the isCheckbox to set
     */
    public void setIsCheckbox(int isCheckbox) {
        this.isCheckbox = isCheckbox;
    }
}
