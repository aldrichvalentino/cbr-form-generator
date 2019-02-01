package formgenerator;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.WordNetDatabase;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRQuery;
import es.ucm.fdi.gaia.jcolibri.util.OntoBridgeSingleton;
import es.ucm.fdi.gaia.ontobridge.OntoBridge;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.QueryModel;
import model.InputFields;
import model.OutputFields;
import model.ControlButtons;

@Controller
public class QueryController {

    Logger logger = LoggerFactory.getLogger(QueryController.class);

    @GetMapping("/query")
    public String handleGet(Model model) {
        logger.info("Query Page: Waiting for query...");
        model.addAttribute("query", new QueryModel());
        return "query";
    }

    @PostMapping("/query")
    public String handlePost(@ModelAttribute QueryModel queryModel) {
        logger.info("Query Page: Normalizing Query");
        CBRQuery query = new CBRQuery();
        // TODO: konversi queryModel ke casecomponent yang di dalem CBRQuery
        System.out.println(queryModel);
        return "queryResult";
    }

    private CBRQuery normalize(CBRQuery query) throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        OntoBridge ob;
        WordNetDatabase database;

        ob = OntoBridgeSingleton.getOntoBridge();
        File f = new File("C:\\Program Files (x86)\\WordNet\\2.1\\dict");
        System.setProperty("wordnet.database.dir", f.toString());
        // setting path for the WordNet Directory
        database = WordNetDatabase.getFileInstance();

        QueryModel fd = (QueryModel) query.getDescription();

        ////////////////////
        // Normalisasi Form Name

        String fn = fd.getFormName();
        // fn=fn.replaceAll("\\d",""); //menghapus numerik di belakang nama form
        // if (fn.endsWith("_")) fn=fn.replace("_", "");
        System.out.println("Nama Form " + fn);
        fn = cekOnto2(fn, "FormName", ob, database); // kelas FormName harus sesuai dgn yng di ontologi (case sensitive)
        fd.setFormName(fn);
        System.out.println("Selesai normalisasi nama form: " + fn);

        ////////////////////
        // Normalisasi Input field

        Set<InputFields> infld = fd.getInputFields();
        Set<InputFields> tifl = new HashSet<InputFields>();
        tifl.addAll(infld);

        for (InputFields if1 : tifl) {
            String ifn = if1.getName();
            String oif = cekOnto2(ifn, "InputFields", ob, database);
            String[] oifa = oif.split("\\s+"); // jika mengandung spasi, berarti kompon
            if (oifa.length > 1) {
                System.out.println("ada komponen bro ");
                infld.remove(if1);
                for (int i = 0; i < oifa.length; i++) {
                    // Out.println("Komponen " + oifa[i]);
                    if (ob.isInstanceOf(oifa[i], "InputFields")) {
                        // Out.println(oifa[i] + " sbg IF");
                        fd.getInputFields().add(new InputFields(oifa[i]));
                    } else if (ob.isInstanceOf(oifa[i], "OutputFields")) {
                        // Out.println(oifa[i] + " sbg OF");
                        fd.getOutputFields().add(new OutputFields(oifa[i]));
                    } else if (ob.isInstanceOf(oifa[i], "ControlButtons")) {
                        // Out.println(oifa[i] + " sbg CB");
                        fd.getControlButtons().add(new ControlButtons(oifa[i]));
                    }
                }
            } else {
                if (!ifn.equals(oifa[0]))
                    if1.setName(oifa[0]);
            }

        }

        // Menghapus elemen dgn nama yang sama
        // Tapi awas, objek dgn nama sama bukan objek yang sama
        // caranya, dibuat set nama2 elemen kemudian dijadikan set IF baru dan masukkan
        // ke kueri
        // normalisasi.
        Set<String> namaflds = hapuskembar(infld);
        // Out.println("nama2 flds t " + namaflds);
        namaflds = hapuskembar(infld);
        // bikin set input field yg baru
        infld.removeAll(infld);
        for (String namafld : namaflds) {
            infld.add(new InputFields(namafld));
        }
        fd.setInputFields(infld);
        // Out.println("Nama di tifl " + fd.itoString() + "\n nama2 flds a " +
        // namaflds);

        // for (String st:namaflds) {
        // InputFields nInFld=new InputFields (st);

        // }

        /*
         * System.out.println("Selesai normalisasi Input Field "); for (InputFields
         * ifi:infld) System.out.print(ifi.getName()+" ");
         * System.out.println("\nSelesai cetak infld ");
         *
         * for (InputFields ifi:tifl) System.out.print(ifi.getName()+" ");
         * System.out.println("\nSelesai cetak tifl ");
         */

        ////////////////////
        // Output field
        Set<OutputFields> outfld = fd.getOutputFields();
        Set<OutputFields> tofl = new HashSet<OutputFields>();
        tofl.addAll(outfld);
        for (OutputFields of1 : outfld) {
            String ofn = of1.getName();
            String oof = cekOnto2(ofn, "OutputFields", ob, database);
            String[] oofa = oof.split("\\s+");
            if (oofa.length > 1) { // ada komponen
                of1.setName(oofa[0]);
                for (int i = 1; i < oofa.length; i++) {
                    InputFields ofi = new InputFields(oofa[i]);
                    infld.add(ofi);
                }
            } else {
                if (!ofn.equals(oofa[0]))
                    of1.setName(oofa[0]);
            }
        }
        fd.setOutputFields(outfld);

        ////////////////////
        // Control Button
        Set<ControlButtons> contbtn = fd.getControlButtons();
        Set<ControlButtons> tcfl = new HashSet<ControlButtons>();
        tcfl.addAll(contbtn);
        for (ControlButtons cb1 : contbtn) {
            String cbn = cb1.getName();
            String ocb = cekOnto2(cbn, "ControlButtons", ob, database);
            String[] cfa = ocb.split("\\s+");
            if (cfa.length > 1) {
                cb1.setName(cfa[0]);
                for (int i = 1; i < cfa.length; i++) {
                    ControlButtons cfi = new ControlButtons(cfa[i]);
                    contbtn.add(cfi);
                }
            } else {
                if (!cbn.equals(cfa[0]))
                    cb1.setName(cfa[0]);
            }
        }
        fd.setControlButtons(contbtn);
        query.setDescription(fd); // FIXME: throw a new object
        return query;
    }

    private String cekOnto2(String ins, String cls, OntoBridge ob, WordNetDatabase database) { // ins=instansi cls=kelas
        String ret = "";

        if (ob.existsInstance(ins, cls)) {
            // Out.println(ins + " ada di onto");
            // it adalah himp komponen ins
            Iterator<String> it = ob.listPropertyValue(ins, "hasComponents");
            Iterator<String> ist = ob.listPropertyValue(ins, "hasNorName");

            if (it.hasNext() && !cls.equals("FormName")) { // jika ins punya komponen dan bukan nama form
                while (it.hasNext())
                    // gabungkan komponen2 itu
                    ret = ret.concat(" " + ob.getShortName(it.next())).trim();
            } else if (ist.hasNext()) {
                ret = ob.getShortName(ist.next());
            } else {
                ret = ins;
            }
        } else {// jk tdk ada di ontologi form, cek di wordnet.
            Synset[] synsets = database.getSynsets(ins);
            if (synsets.length > 0) { // jika memiliki padanan di WN
                // Out.println(ins + " ada sinonim di wn");
                for (int i = 0; i < synsets.length; i++) {
                    String[] wordForms = synsets[i].getWordForms();
                    for (int j = 0; j < wordForms.length; j++) {
                        String rep = wordForms[j].replaceAll("\\s+", "_");
                        // Out.println("padanan " + ins + " : " + rep);
                        // cek di ontologi form
                        if (ob.existsInstance(rep, cls)) {
                            // Out.println("padanan ada di onto: " + rep);
                            return rep; // jika wordform ada di ontologi
                        } else {
                            ret = ins;
                            System.out.println("Sinonim tdk ada di onto");
                        }
                    }
                }
            } else { // jika tidak ada di Ontologi form dan tdk ada di WN
                // Out.println(ins + " tdk ada di onto dan tdk ada d wn");
                ret = ins;
            }
        }
        return ret;
    }

    // private <T> boolean berisi(Set<T> setfld, String elm) throws
    // NoSuchMethodException, SecurityException,
    // IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    // for (T elmc : setfld) {
    // Method m = elmc.getClass().getDeclaredMethod("getName");
    // String fln = (String) m.invoke(elmc); // mengambil nama elemen
    // Out.println("Nama Elemen di set " + fln);
    // if (fln.equals(elm)) {
    // Out.println("Berisi ..");
    // return true;
    // }
    // }
    // return false;
    // }

    private <T> Set<String> hapuskembar(Set<T> setfld) throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Set<String> setNama = new HashSet<String>();
        for (T elmc : setfld) {
            Method m = elmc.getClass().getDeclaredMethod("getName");
            String fln = (String) m.invoke(elmc); // mengambil nama elemen
            setNama.add(fln);
        }
        if (setNama.iterator().hasNext()) {
            // Out.println("Print set ");
            setNama.iterator().next().toString();
        }
        return setNama;
    }

}