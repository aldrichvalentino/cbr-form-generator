package core;

import java.util.Iterator;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCase;
import model.FormDescription;
import utils.builder.HTMLBuilder;

@Controller
public class GeneratorController {

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
                    result = builder.buildHTML(_case);
                    // result = "woy ini ketemu";
                }
            }
            // return Builder.buildHTML(RetrieveController.retrievedCases);
            return result;
        } else {
            // build HTML from adaptedCase
            return builder.buildHTML(AdaptationController.adaptedCase);
        }
    }
}
