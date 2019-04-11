package utils.builder;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

public class TemplateBuilder {

    private static TemplateBuilder templateBuilder = null;
    private Configuration configuration;

    private TemplateBuilder() {
        configuration = new Configuration(Configuration.VERSION_2_3_27);
        try {
            configuration.setDefaultEncoding("UTF-8");
            configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            configuration.setLogTemplateExceptions(false);
            configuration.setWrapUncheckedExceptions(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static TemplateBuilder getInstance() {
        if (templateBuilder == null) {
            templateBuilder = new TemplateBuilder();
        }
        return templateBuilder;
    }

    /**
     * Generate Template from a template file
     *
     * @param dataModel           Object to be written in the template
     * @param isClient            True if the document to be generated is a Form
     * @param templateName        Template to be used for generation
     * @param outputDirectoryPath Directory output path, without "/" at the end
     * @param outputFileName      Output file name
     */
    public void generateTemplate(Object dataModel, boolean isClient, String templateName,
            String outputDirectoryPath, String outputFileName) {
        try {
            String templateDirectory = isClient ? "client" : "server";
            configuration.setDirectoryForTemplateLoading(new File(System.getProperty("user.dir")
                    + "/src/main/resources/templates/" + templateDirectory));

            Writer file = new FileWriter(new File(outputDirectoryPath + "/" + outputFileName));
            Template template = configuration.getTemplate(templateName);
            template.process(dataModel, file);
            file.flush();
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
