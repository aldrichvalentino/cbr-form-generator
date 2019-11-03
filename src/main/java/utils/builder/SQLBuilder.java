package utils.builder;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import es.ucm.fdi.gaia.ontobridge.OntoBridge;
import model.InputFields;
import utils.OntologyConnector;

public class SQLBuilder {
  private OntoBridge ontoBridge;

  public SQLBuilder(String owlPath, String owlUrl) {
    this.ontoBridge = OntologyConnector.getInstance(owlUrl, owlPath).getOntoBridge();
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
    Iterator<String> it = ontoBridge.listPropertyValue(elm, "isATypeOf");
    while (it.hasNext()) {
      type = ontoBridge.getShortName(it.next());
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
}
