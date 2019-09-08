package core;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import model.ControlButtons;
import model.FormDescription;
import model.FormSolution;
import model.Forms;
import model.GMembers;
import model.Groups;
import model.HLMembers;
import model.InputFields;
import model.OMembers;
import model.Orders;
import model.OutputFields;
import model.VLMembers;
import model.XLabel;
import utils.DatabaseConnector;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.util.*;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import com.opencsv.CSVReader;

@Controller
public class MigrationController {

    @Autowired
    private Environment env;
    Logger logger = LoggerFactory.getLogger(QueryController.class);

    @GetMapping("/clear")
    public String handleGet() {
        return "clear";
    }

    @RequestMapping(value = "/erase", method = RequestMethod.POST,
            produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String handlePost() {
        Transaction transaction = null;
        try {
            DatabaseConnector database = DatabaseConnector.getInstance(
                    env.getProperty("HIBERNATE_DRIVER"), env.getProperty("HIBERNATE_CONNECTION"),
                    env.getProperty("HIBERNATE_DIALECT"), env.getProperty("DB_USERNAME"),
                    env.getProperty("DB_PASSWORD"));

            SessionFactory sessionFactory = database.getSession();
            Session session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            // Empty the database
            session.createNativeQuery("TRUNCATE `controlbuttons`").executeUpdate();
            session.createNativeQuery("TRUNCATE `formdes`").executeUpdate();
            session.createNativeQuery("TRUNCATE `forms`").executeUpdate();
            session.createNativeQuery("TRUNCATE `formsol`").executeUpdate();
            session.createNativeQuery("TRUNCATE `gmembers`").executeUpdate();
            session.createNativeQuery("TRUNCATE `groups`").executeUpdate();
            session.createNativeQuery("TRUNCATE `hlmembers`").executeUpdate();
            session.createNativeQuery("TRUNCATE `inputfields`").executeUpdate();
            session.createNativeQuery("TRUNCATE `labels`").executeUpdate();
            session.createNativeQuery("TRUNCATE `mytable`").executeUpdate();
            session.createNativeQuery("TRUNCATE `omembers`").executeUpdate();
            session.createNativeQuery("TRUNCATE `orders`").executeUpdate();
            session.createNativeQuery("TRUNCATE `outputfields`").executeUpdate();
            session.createNativeQuery("TRUNCATE `vlmembers`").executeUpdate();

            String csvPath = getClass()
                    .getResource("/database/seed/" + env.getProperty("SEED_FILE")).getPath();
            buildDB(new URI(csvPath).getPath(), session);

            transaction.commit();
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
            transaction.rollback();
        }
        return "success";
    }

    public void buildDB(String CSVFileName, Session session)
            throws IOException, IllegalAccessException, ReflectiveOperationException {
        CSVReader csvReader = new CSVReader(new FileReader(CSVFileName), ',');
        List<String[]> content = csvReader.readAll();

        for (String[] baris : content) {
            Forms frm = new Forms();
            FormDescription fd = new FormDescription();
            FormSolution fs = new FormSolution();
            String[] col = (String[]) baris; // obj diubah menjadi array col yg dibatasi ';'
            for (int i = 0; i < col.length; i++) {
                switch (i) {
                    case 0:
                        System.out.println("Id " + col[i]);
                        break;
                    case 1:
                        System.out.println("col 1 " + col[i]);
                        fd.setFormName(col[i]);
                        break;
                    case 2:
                        fd.setInputFields(addDesc(InputFields.class, col[i]));
                        System.out.println("col 2 " + col[i]);
                        break;
                    case 3:
                        fd.setOutputFields(addDesc(OutputFields.class, col[i]));
                        break;
                    case 4:
                        fd.setControlButtons(addDesc(ControlButtons.class, col[i]));
                        break;
                    case 5:
                        fs.setlabel(addLabel(col[i]));
                        break;
                    case 6:
                        fs.setGroup(addGroup(col[i]));
                        break;
                    case 7:
                        fs.setOrder(addOrder(col[i]));
                        break;
                    case 8:
                        fs.setvlMember(addVL(col[i]));
                        break;
                    case 9:
                        fs.sethlMember(addHL(col[i]));
                        break;
                }
            }

            HashSet<FormDescription> hs = new HashSet<FormDescription>();
            hs.add(fd);
            frm.setFormDes(hs); // set form desc

            HashSet<FormSolution> hs2 = new HashSet<FormSolution>();
            hs2.add(fs);
            frm.setFormSol(hs2); // set form solution

            // add case and print out the case id
            System.out.println("List cases no " + addCase(frm, session));
        }
        csvReader.close();
    }

    /* Method to add a case record in the database */
    public Integer addCase(Forms frm, Session session) {
        Integer caseID = null;
        try {
            caseID = (Integer) session.save(frm);
            System.out.println("Case ID " + caseID);
        } catch (HibernateException e) {
            e.printStackTrace();
        }
        return caseID;
    }

    /* Add input fld, output fld, control button */
    public <T> HashSet<T> addDesc(Class<T> c, String elm)
            throws ReflectiveOperationException, IllegalAccessException {
        HashSet<T> eset = new HashSet<T>();
        StringTokenizer st = new StringTokenizer(elm, ",.", false);
        while (st.hasMoreTokens()) {
            Constructor<T> ctor = c.getConstructor(String.class);
            ctor.setAccessible(true);
            T if1 = ctor.newInstance(st.nextToken());
            eset.add(if1);
        }
        return eset;
    }

    public HashMap<String, XLabel> addLabel(String elm) {
        HashMap<String, XLabel> eset = new HashMap<String, XLabel>();
        StringTokenizer st = new StringTokenizer(elm, ".", false);
        while (st.hasMoreTokens()) {
            StringTokenizer st2 = new StringTokenizer(st.nextToken(), ":", false);
            eset.put(st2.nextToken(), new XLabel(st2.nextToken()));
        }
        return eset;
    }

    public ArrayList<Groups> addGroup(String elm) {
        ArrayList<Groups> eset = new ArrayList<Groups>();
        StringTokenizer st = new StringTokenizer(elm, ".", false);
        while (st.hasMoreTokens()) {
            StringTokenizer st2 = new StringTokenizer(st.nextToken(), ",", false);
            HashSet<GMembers> hmem = new HashSet<GMembers>();
            while (st2.hasMoreTokens()) {
                hmem.add(new GMembers(st2.nextToken()));
            }
            eset.add(new Groups(hmem));
        }
        return eset;
    }

    public ArrayList<Orders> addOrder(String elm) {
        ArrayList<Orders> eset = new ArrayList<Orders>();
        StringTokenizer st = new StringTokenizer(elm, ".", false);
        while (st.hasMoreTokens()) {
            StringTokenizer st2 = new StringTokenizer(st.nextToken(), ",", false);
            ArrayList<OMembers> ord = new ArrayList<OMembers>();
            while (st2.hasMoreTokens()) {
                ord.add(new OMembers(st2.nextToken()));
            }
            eset.add(new Orders(ord));
        }
        return eset;
    }

    public ArrayList<VLMembers> addVL(String elm) {
        ArrayList<VLMembers> vl = new ArrayList<VLMembers>();
        StringTokenizer st = new StringTokenizer(elm, " ", false);
        while (st.hasMoreTokens()) {
            vl.add(new VLMembers(st.nextToken()));
        }
        return vl;
    }

    public ArrayList<HLMembers> addHL(String elm) {
        ArrayList<HLMembers> hl = new ArrayList<HLMembers>();
        StringTokenizer st = new StringTokenizer(elm, " ", false);
        while (st.hasMoreTokens()) {
            hl.add(new HLMembers(st.nextToken()));
        }
        return hl;
    }
}
