package utils.builder;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import es.ucm.fdi.gaia.ontobridge.OntoBridge;
import model.InputFields;
import utils.OntologyConnector;

public class SQLBuilder {
    final String owlPath = getClass().getResource("/owl/FormOnto2.owl").toExternalForm();
    final String owlUrl = "http://www.semanticweb.org/hp/ontologies/2015/2/FormOnto2.owl";

    public SQLBuilder() {

    }

    // SQL in this project is using the MySQL dialect
    public String buildSQL(Set<InputFields> inputFields) {
        String ddlDropTable = "DROP TABLE IF EXISTS entity;";
        String ddlCreateTable =
                "CREATE TABLE entity (id INTEGER(3) AUTO_INCREMENT %s, PRIMARY KEY(id));";
        String fields = "";
        for (InputFields inputField : inputFields) {
            fields += ", " + inputField.getName() + " " + getType(inputField.getName());
        }
        return ddlDropTable + String.format(ddlCreateTable, fields);
    }

    private String getType(String elm) {
        String type = "";
        OntoBridge ontoBridge = OntologyConnector.getInstance(owlUrl, owlPath).getOntoBridge();
        Iterator<String> it = ontoBridge.listPropertyValue(elm, "isATypeOf");
        while (it.hasNext()) {
            type = ontoBridge.getShortName(it.next());
            // Out.println(elm+" is a type of "+" "+type);
        }

        switch (type) {
            case "tdate":
                type = "date";
                break;
            default:
                type = "varchar(255)";
        }
        return type;
    }

    public static void main(String args[]) {
        SQLBuilder sqlBuilder = new SQLBuilder();

        // for DEV only
        Set<InputFields> inputFields = new HashSet<InputFields>();
        inputFields.add(new InputFields("first_name"));
        inputFields.add(new InputFields("email"));
        inputFields.add(new InputFields("password"));
        inputFields.add(new InputFields("birth_date"));

        System.out.println(sqlBuilder.buildSQL(inputFields));
        System.exit(0);
    }
}
