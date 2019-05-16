package utils.builder;

import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCase;
import es.ucm.fdi.gaia.ontobridge.OntoBridge;
import j2html.tags.ContainerTag;
import model.FormDescription;
import model.FormSolution;
import model.OMembers;
import model.Orders;
import model.VLMembers;
import model.XLabel;
import utils.OntologyConnector;
import static j2html.TagCreator.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// How to run with gradle
// sh: ./gradlew -PmainClass=utils.builder.Builder execute

public class HTMLBuilder {
    // private FormDescription formDescription;
    // private FormSolution formSolution;
    private OntoBridge ontoBridge;
    Logger logger = LoggerFactory.getLogger(HTMLBuilder.class);

    public HTMLBuilder(String owlPath, String owlUrl) {
        ontoBridge = OntologyConnector.getInstance(owlUrl, owlPath).getOntoBridge();
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

        return body(div(generateHTML(formSolution.getvlMember(), formSolution.getlabel())));

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
        switch (type) {
            case "select":
                return div(label(labelName),
                        select(option("default 1"), option("default 2"), option("default 3"))
                                .withClass("form-control").withName(elementName))
                                        .withClass("form-group");
            // Other types like Radio and Checkbox will be sent to custom case
            case "checkbox":
            case "radio":
                return div(label(elementName),
                        input().withType(type).withName(elementName).withClass("form-control"),
                        span("option 1"), br(),
                        input().withType(type).withName(elementName).withClass("form-control"),
                        span("option 2"), br(),
                        input().withType(type).withName(elementName).withClass("form-control"),
                        span("option 3"), br()).withClass("form-group");
            case "submit":
                return div(button(labelName).withClass("btn btn-primary"));
            case "reset":
                return div(button(labelName).withClass("btn btn-danger"));
            default:
                return div(label(labelName),
                        input().withType(type).withName(elementName).withClass("form-control"))
                                .withClass("form-group");
        }
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

    public String generateHTML(List<VLMembers> group, Map<String, XLabel> labels) {
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
}
