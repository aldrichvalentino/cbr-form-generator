package core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCase;
import model.FormDescription;
import model.FormSolution;
import model.InputFields;
import model.VLMembers;
import model.XLabel;
import model.templates.FormFieldTemplate;
import model.templates.ServerTemplate;
import utils.Zipper;
import utils.builder.HTMLBuilder;
import utils.builder.LayoutBuilder;
import utils.builder.SQLBuilder;
import utils.builder.TemplateBuilder;

@Controller
public class GeneratorController {
    private Logger logger = LoggerFactory.getLogger(GeneratorController.class);

    @RequestMapping(value = "/generate", method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String handleGet(@RequestParam("isRetrievedCase") boolean isRetrievedCase,
            @RequestParam("caseId") String caseId, HttpServletResponse response) {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        HTMLBuilder builder = new HTMLBuilder();
        if (isRetrievedCase) {
            String result = null;
            Iterator<CBRCase> caseIterator = RetrieveController.retrievedCases.iterator();
            while (caseIterator.hasNext()) {
                CBRCase _case = (CBRCase) caseIterator.next();
                if (((FormDescription) _case.getDescription()).getId() == Integer
                        .parseInt(caseId)) {
                    FormSolution solution = (FormSolution) _case.getSolution();
                    result = builder.genCoba(solution.getvlMember(), solution.getlabel());
                }
            }
            return result;
        } else {
            // build HTML from adaptedCase
            FormSolution solution = (FormSolution) AdaptationController.adaptedCase.getSolution();
            return builder.genCoba(solution.getvlMember(), solution.getlabel());
        }
    }

    @RequestMapping(value = "/zip", produces = "application/zip")
    public void zipFiles(HttpServletResponse response) throws IOException {
        try {
            response.setStatus(HttpServletResponse.SC_OK);
            response.addHeader("Content-Disposition",
                    "attachment; filename=\"form_application.zip\"");

            TemplateBuilder templateBuilder = TemplateBuilder.getInstance();
            FormDescription formDescription =
                    (FormDescription) AdaptationController.adaptedCase.getDescription();
            FormSolution formSolution =
                    (FormSolution) AdaptationController.adaptedCase.getSolution();
            Set<InputFields> inputFields = formDescription.getInputFields();
            Map<String, XLabel> formLabels = formSolution.getlabel();
            List<VLMembers> formLayouts = formSolution.getvlMember();

            // Step 1 Generate Form
            logger.info("Step 1: Generate Form");
            Map<Object, Object> formContent = new HashMap<Object, Object>();
            ArrayList<FormFieldTemplate> fieldGroupTemplates =
                    LayoutBuilder.buildLayout(formLayouts, formLabels);
            formContent.put("fields", fieldGroupTemplates);
            templateBuilder.generateTemplate(formContent, true, "form.template.hbs",
                    System.getProperty("user.dir") + "/src/main/resources/templates/web/views",
                    "form.hbs");

            ArrayList<FormFieldTemplate> getAllTemplates = new ArrayList<FormFieldTemplate>();
            for (InputFields field : inputFields) {
                getAllTemplates.add(new FormFieldTemplate(field));
            }
            formContent.put("fields", getAllTemplates);
            templateBuilder.generateTemplate(formContent, true, "getAll.template.hbs",
                    System.getProperty("user.dir") + "/src/main/resources/templates/web/views",
                    "getAll.hbs");

            // Step 2 Generate SQL
            logger.info("Step 2: Generate SQL");
            Map<String, String> sqlContent = new HashMap<String, String>();
            SQLBuilder sqlBuilder = new SQLBuilder();
            sqlContent.put("content", sqlBuilder.buildSQL(inputFields));
            templateBuilder.generateTemplate(sqlContent, false, "database.template.sql",
                    System.getProperty("user.dir") + "/src/main/resources/templates/web",
                    "database.sql");

            // Step 3 Generate backend files
            logger.info("Step 3: Generate Backend Files");
            Map<String, Object> serverContent = new HashMap<String, Object>();
            ArrayList<ServerTemplate> serverData = new ArrayList<ServerTemplate>();
            for (InputFields field : inputFields) {
                serverData.add(new ServerTemplate(field, formLabels.get(field.getName())));
            }
            serverContent.put("content", serverData);
            templateBuilder.generateTemplate(serverContent, false, "entity.js.tpl",
                    System.getProperty("user.dir") + "/src/main/resources/templates/web/routes",
                    "entity.js");

            // Step 4 Zip form and SQL with the rest of the web files and return the zip file
            logger.info("Step 4: Zipping Files");
            String sourceFile =
                    System.getProperty("user.dir") + "\\src\\main\\resources\\templates\\web";
            ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream());
            File fileToZip = new File(sourceFile);

            Zipper zipper = new Zipper();
            zipper.zipFile(fileToZip, fileToZip.getName(), zipOutputStream);

            zipOutputStream.close();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
    }
}
