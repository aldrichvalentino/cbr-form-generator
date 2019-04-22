package utils.builder;

import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCase;
import es.ucm.fdi.gaia.ontobridge.OntoBridge;
import j2html.tags.ContainerTag;
import model.ControlButtons;
import model.FormDescription;
import model.FormSolution;
import model.GMembers;
import model.Groups;
import model.HLMembers;
import model.InputFields;
import model.OMembers;
import model.Orders;
import model.OutputFields;
import model.VLMembers;
import model.XLabel;
import utils.OntologyConnector;
import static j2html.TagCreator.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// How to run with gradle
// sh: ./gradlew -PmainClass=utils.builder.Builder execute

public class HTMLBuilder {
    // private FormDescription formDescription;
    // private FormSolution formSolution;
    private OntoBridge ontoBridge;
    final String OWL_PATH = getClass().getResource("/owl/FormOnto2.owl").toExternalForm();
    final String OWL_URL = "http://www.semanticweb.org/hp/ontologies/2015/2/FormOnto2.owl";
    Logger logger = LoggerFactory.getLogger(HTMLBuilder.class);

    public HTMLBuilder() {
        ontoBridge = OntologyConnector.getInstance(OWL_URL, OWL_PATH).getOntoBridge();
    }

    public String buildHTML(CBRCase _case) {
        FormDescription fDescription = (FormDescription) _case.getDescription();
        FormSolution fSolution = (FormSolution) _case.getSolution();

        return html(buildHead(fDescription.getFormName()), buildBody(fDescription, fSolution))
                .render();
    }

    private ContainerTag buildHead(String formName) {
        // return head with default css from Bootstrap 4
        return head(title(formName), meta().withCharset("utf-8"),
                meta().withName("viewport")
                        .withContent("width=device-width, initial-scale=1, shrink-to-fit=no"),
                link().withRel("stylesheet").withHref(
                        "https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css"),
                link().withRel("stylesheet").withHref("/css/custom.css"));
    }

    private ContainerTag buildBody(FormDescription formDescription, FormSolution formSolution) {
        ArrayList<ContainerTag> elements = new ArrayList<ContainerTag>();
        for (Orders order : formSolution.getOrder()) {
            elements.add(buildGroup(order.getoMembers(), formSolution.getlabel()));
        }

        // TODO: call build HTML with layouting (genCoba)
        return body(div(genCoba(formSolution.getvlMember(), formSolution.getlabel())));

        // return body(div(h1(formDescription.getFormName()),
        // form().withMethod("post").with(elements).withClass("col-xs-12"))
        // .withClass("container"));
    }

    private ContainerTag buildGroup(List<OMembers> orders, Map<String, XLabel> labels) {
        ArrayList<ContainerTag> fields = new ArrayList<ContainerTag>();
        for (OMembers element : orders) {
            fields.add(buildFormElement(element.getMemberName(),
                    labels.get(element.getMemberName()).getLabel()));
        }
        return div().with(fields).with(hr());
    }

    private ContainerTag buildFormElement(String elementName, String labelName) {
        String type = getType(elementName);
        return div(label(labelName),
                input().withType(type).withName(elementName).withClass("form-control"))
                        .withClass("form-group");
    }

    private String getType(String elm) {
        String type = "";
        Iterator<String> it = ontoBridge.listPropertyValue(elm, "isATypeOf");
        while (it.hasNext()) {
            type = ontoBridge.getShortName(it.next());
            // Out.println(elm+" is a type of "+" "+type);
        }

        switch (type) {
            case "ttext":
                type = "text";
                break;
            case "tcheckbox":
                type = "checkbox";
                break;
            case "tlabel":
                type = "Label";
                break;
            case "tradio":
                type = "radio";
                break;
            // case "toption":
            // type = buildDropDown(xLabels.get(elm).getLabel();
            // break;
            // case "tdropdown":
            // type = buildDropDown(xLabels.get(elm).getLabel();
            // break;
            case "tpassword":
                type = "password";
                break;
            case "tsubmit":
                type = "submit";
                break;
            case "tbutton":
                type = "button";
                break;
            case "timage":
                type = "image";
                break;
            case "treset":
                type = "reset";
                break;
            case "tnumber":
                type = "number";
                break;
            case "tdate":
                type = "date";
                break;
            case "ttime":
                type = "time";
                break;
            case "temail":
                type = "email";
                break;
            case "tsearch":
                type = "search";
                break;
            case "ttel":
                type = "tel";
                break;
            default:
                type = "text";
        }
        return type;
    }

    public String genCoba(List<VLMembers> group, Map<String, XLabel> labels) {
        String res = "";
        for (VLMembers element : group) {
            String elementName = element.getName();
            for (int i = 0; i < elementName.length(); i++) {
                switch (elementName.charAt(i)) {
                    case '[':
                        res += "<div class='custom-group'>";
                        break;
                    case '(':
                        res += "<div class='custom-row'>";
                        break;
                    case '{':
                        res += "<div class='custom-col'>";
                        break;
                    case ']':
                    case ')':
                    case '}':
                        res += "</div>";
                        break;
                    case ' ':
                        break;
                    default:
                        int nextClosingGroup = elementName.indexOf(']', i + 1);
                        int nextClosingRow = elementName.indexOf(')', i + 1);
                        int nextClosingCol = elementName.indexOf('}', i + 1);
                        int nextChar = elementName.length();
                        if (nextClosingGroup == -1 && nextClosingCol == -1
                                && nextClosingRow == -1) {
                            nextChar = elementName.length();
                        } else {
                            nextClosingGroup = nextClosingGroup == -1 ? 9999 : nextClosingGroup;
                            nextClosingCol = nextClosingCol == -1 ? 9999 : nextClosingCol;
                            nextClosingRow = nextClosingRow == -1 ? 9999 : nextClosingRow;
                            nextChar = Math.min(nextClosingGroup,
                                    Math.min(nextClosingCol, nextClosingRow));
                        }
                        String word = elementName.substring(i, nextChar);
                        String HTMLelement =
                                buildFormElement(word, labels.get(word).getLabel()).render();
                        res += "<div class='custom-content'>" + HTMLelement + "</div>";
                        i = nextChar - 1;
                }
            }
        }
        return res;
    }

    public String genCoba(String group, Map<String, XLabel> labels) {
        String res = "";
        for (int i = 0; i < group.length(); i++) {
            switch (group.charAt(i)) {
                case '[':
                    res += "<div class='custom-group'>";
                    break;
                case '(':
                    res += "<div class='custom-row'>";
                    break;
                case '{':
                    res += "<div class='custom-col'>";
                    break;
                case ']':
                case ')':
                case '}':
                    res += "</div>";
                    break;
                case ' ':
                    break;
                default:
                    int nextSpace = group.indexOf(' ', i + 1);
                    int nextClosingGroup = group.indexOf(']', i + 1);
                    int nextClosingRow = group.indexOf(')', i + 1);
                    int nextClosingCol = group.indexOf('}', i + 1);
                    int nextChar = -1;
                    if (nextSpace == -1) { // last element
                        nextChar = Math.min(nextClosingGroup,
                                Math.min(nextClosingRow, nextClosingCol));
                    } else {
                        nextChar = Math.min(Math.min(nextSpace, nextClosingGroup),
                                Math.min(nextClosingCol, nextClosingRow));
                    }
                    String word = group.substring(i, nextChar);
                    String element = buildFormElement(word, labels.get(word).getLabel()).render();
                    res += "<div class='custom-content'>" + element + "</div>";
                    i = nextChar - 1;
            }
        }
        return "<h1>" + group + "</h1>" + res;
    }

    public static void main(String args[]) {
        String group =
                "{[{(first_name last_name email) country coba (lagi oke)}] [({first_name last_name (email country)} mantap submit {cancel reset finish})]}";
        // String group = "{[{first_name last_name}] [(submit cancel)]}";
        HTMLBuilder htmlBuilder = new HTMLBuilder();
        // System.out.println(htmlBuilder.genCoba(group));

        System.exit(0);
    }
}
