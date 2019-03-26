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
    private FormDescription formDescription;
    private FormSolution formSolution;
    private OntoBridge ontoBridge;
    final String OWL_PATH = getClass().getResource("/owl/FormOnto2.owl").toExternalForm();
    final String OWL_URL = "http://www.semanticweb.org/hp/ontologies/2015/2/FormOnto2.owl";
    Logger logger = LoggerFactory.getLogger(HTMLBuilder.class);

    public HTMLBuilder() {
        // for DEV only
        formDescription = new FormDescription();
        formSolution = new FormSolution();
        ontoBridge = OntologyConnector.getInstance(OWL_URL, OWL_PATH).getOntoBridge();

        // for testing only
        formDescription.setFormName("testing");
        Set<InputFields> inputFields = new HashSet<InputFields>();
        inputFields.add(new InputFields("first_name"));
        inputFields.add(new InputFields("last_name"));
        inputFields.add(new InputFields("country"));
        inputFields.add(new InputFields("birth_date"));

        Set<OutputFields> outputFields = new HashSet<OutputFields>();
        outputFields.add(new OutputFields("greeting"));

        Set<ControlButtons> controlButtons = new HashSet<ControlButtons>();
        controlButtons.add(new ControlButtons("submit"));
        controlButtons.add(new ControlButtons("rcaptcha"));

        formDescription.setInputFields(inputFields);
        formDescription.setOutputFields(outputFields);
        formDescription.setControlButtons(controlButtons);

        // desc
        Map<String, XLabel> labels = new HashMap<String, XLabel>();
        labels.put("first_name", new XLabel("Nama Depan"));
        labels.put("last_name", new XLabel("Nama Belakang"));
        labels.put("country", new XLabel("Negara Asal"));
        labels.put("birth_date", new XLabel("Ulang tahun"));
        labels.put("greeting", new XLabel("Salam"));
        labels.put("submit", new XLabel("Kumpul"));
        labels.put("rcaptcha", new XLabel("Batal"));

        List<Groups> groups = new ArrayList<Groups>();
        Groups g = new Groups();
        Set<GMembers> gMembers = new HashSet<GMembers>();
        gMembers.add(new GMembers("last_name"));
        gMembers.add(new GMembers("first_name"));
        g.setgMembers(gMembers);
        groups.add(g);

        Groups g1 = new Groups();
        Set<GMembers> gMembers1 = new HashSet<GMembers>();
        gMembers1.add(new GMembers("country"));
        gMembers1.add(new GMembers("birth_date"));
        g1.setgMembers(gMembers1);
        groups.add(g1);

        Groups g2 = new Groups();
        Set<GMembers> gMembers2 = new HashSet<GMembers>();
        gMembers2.add(new GMembers("greeting"));
        g2.setgMembers(gMembers2);
        groups.add(g2);

        Groups g3 = new Groups();
        Set<GMembers> gMembers3 = new HashSet<GMembers>();
        gMembers3.add(new GMembers("submit"));
        gMembers3.add(new GMembers("rcaptcha"));
        g3.setgMembers(gMembers3);
        groups.add(g3);

        List<Orders> orders = new ArrayList<Orders>();
        Orders o = new Orders();
        List<OMembers> oMembers = new ArrayList<OMembers>();
        oMembers.add(new OMembers("first_name"));
        oMembers.add(new OMembers("last_name"));
        o.setoMembers(oMembers);
        orders.add(o);

        Orders o1 = new Orders();
        List<OMembers> oMembers1 = new ArrayList<OMembers>();
        oMembers1.add(new OMembers("birth_date"));
        oMembers1.add(new OMembers("country"));
        o1.setoMembers(oMembers1);
        orders.add(o1);

        Orders o2 = new Orders();
        List<OMembers> oMembers2 = new ArrayList<OMembers>();
        oMembers2.add(new OMembers("greeting"));
        o2.setoMembers(oMembers2);
        orders.add(o2);

        Orders o3 = new Orders();
        List<OMembers> oMembers3 = new ArrayList<OMembers>();
        oMembers3.add(new OMembers("rcaptcha"));
        oMembers3.add(new OMembers("submit"));
        o3.setoMembers(oMembers3);
        orders.add(o3);

        List<HLMembers> hLMembers = new ArrayList<HLMembers>();

        List<VLMembers> vLMembers = new ArrayList<VLMembers>();
        vLMembers.add(new VLMembers("birth_date"));
        vLMembers.add(new VLMembers("country"));
        vLMembers.add(new VLMembers("first_name"));
        vLMembers.add(new VLMembers("last_name"));
        vLMembers.add(new VLMembers("rcaptcha"));
        vLMembers.add(new VLMembers("submit"));
        vLMembers.add(new VLMembers("greeting"));

        formSolution.setlabel(labels);
        formSolution.setGroup(groups);
        formSolution.setOrder(orders);
        formSolution.sethlMember(hLMembers);
        formSolution.setvlMember(vLMembers);

        System.out.println(formDescription);
        System.out.println(formSolution);
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
                        "https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css"));
    }

    private ContainerTag buildBody(FormDescription formDescription, FormSolution formSolution) {
        ArrayList<ContainerTag> elements = new ArrayList<ContainerTag>();
        for (Orders order : formSolution.getOrder()) {
            elements.add(buildGroup(order.getoMembers(), formSolution.getlabel()));
        }

        return body(div(h1(formDescription.getFormName()),
                form().withMethod("post").with(elements).withClass("col-xs-12"))
                        .withClass("container"));
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

        logger.debug("ini loh tipenya : " + type);

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

    public static void main(String args[]) {
        HTMLBuilder builder = new HTMLBuilder();
        System.out.println(builder.buildHTML(null));

        System.out.println("mantap");

        System.exit(0);
    }
}
