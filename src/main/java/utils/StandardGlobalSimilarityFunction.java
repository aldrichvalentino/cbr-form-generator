package utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import es.ucm.fdi.gaia.jcolibri.cbrcore.Attribute;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCase;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRQuery;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CaseComponent;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.NNretrieval.NNConfig;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.NNretrieval.similarity.GlobalSimilarityFunction;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.NNretrieval.similarity.LocalSimilarityFunction;
import es.ucm.fdi.gaia.jcolibri.util.AttributeUtils;
import model.FormDescription;

/**
 * Utility class to compute global similarities. The implemented compute(...)
 * method computes the similarity of the sub-attributes of this compound
 * attribute and then calls the computeSimilarity() abstract method to obtain
 * the similarity value.<br>
 * That way, the computeSimilarity() method is a hook to easly compute global
 * similarities.
 *
 * @author Juan A. Recio-Garcia
 * @version 1.0
 *
 */
public class StandardGlobalSimilarityFunction implements GlobalSimilarityFunction {
    int thisCaseId = -1;

    @Override
    public double compute(CaseComponent cCase, CaseComponent cQuery, CBRCase _case, CBRQuery _query,
            NNConfig numSimConfig) {
        // public double compute(CaseComponent cCase, CaseComponent cQuery, CBRCase
        // _case, CBRQuery _query, NNConfig numSimConfig)
        GlobalSimilarityFunction gsf = null;
        LocalSimilarityFunction lsf = null;

        Attribute[] attributes = AttributeUtils.getAttributes(cCase.getClass());

        double[] values = new double[attributes.length];
        double[] weights = new double[attributes.length];

        int ivalue = 0;
        thisCaseId = -1;

        for (int i = 0; i < attributes.length; i++) {
            Attribute at1 = attributes[i];
            Attribute at2 = new Attribute(at1.getName(), cQuery.getClass());

            try {
                if (i == 0) {
                    // System.out.println(thisCaseId + "\natribut " + at1.getName() + " " +
                    // at1.getValue(cCase));
                    thisCaseId = (int) at1.getValue(cCase);
                } else {
                    // System.out.println(thisCaseId + "atribut " + at1.getName());
                }

                if ((gsf = numSimConfig.getGlobalSimilFunction(at2)) != null) {
                    values[ivalue] = gsf.compute((FormDescription) at1.getValue(cCase),
                            (FormDescription) at2.getValue(cQuery), _case, _query, numSimConfig);
                    weights[ivalue++] = numSimConfig.getWeight(at1);
                } else if ((lsf = numSimConfig.getLocalSimilFunction(at2)) != null) {
                    // System.out.println(thisCaseId + "Fungsi Lokal " +
                    // lsf.getClass().getSimpleName());

                    if ((at1.getName().equals("formName"))) {
                        // System.out.println(
                        // "Nama form kasus " + at1.getValue(cCase) + "Nama form kueri " +
                        // at2.getValue(cQuery));
                        values[ivalue] = lsf.compute(at1.getValue(cCase).toString(), at2.getValue(cQuery).toString());
                        weights[ivalue++] = numSimConfig.getWeight(at1);
                        // System.out.println(
                        // "Nilai eval form name " + values[ivalue - 1] + " Bobot " + weights[ivalue -
                        // 1]);
                    } else {
                        Set<String> qset = new HashSet<String>();
                        Set<String> cset = new HashSet<String>();

                        FormDescription formDescription = (FormDescription) cCase;

                        if (at1.getName().equals("inputFields")) {
                            qset.addAll(composeSet(((FormDescription) cQuery).getInputFields()));
                            cset.addAll(composeSet(formDescription.getInputFields()));
                        }

                        if (at1.getName().equals("outputFields")) {
                            qset.addAll(composeSet(((FormDescription) cQuery).getOutputFields()));
                            cset.addAll(composeSet(formDescription.getOutputFields()));
                        }

                        if (at1.getName().equals("controlButtons")) {
                            qset.addAll(composeSet(((FormDescription) cQuery).getControlButtons()));
                            cset.addAll(composeSet(formDescription.getControlButtons()));
                        }

                        // System.out.print("kueri ");
                        // if (thisCaseId == 13 || thisCaseId == 12) {
                        // for (String nama : qset) {
                        // System.out.print(nama + " ");
                        // }
                        // System.out.print("\nkasus ");
                        // for (String nama : cset) {
                        // System.out.print(nama + " ");
                        // }
                        // }

                        values[ivalue] = lsf.compute(cset, qset);
                        weights[ivalue++] = numSimConfig.getWeight(at1);
                        // System.out.println(thisCaseId + "Nilai eval form element " + values[ivalue -
                        // 1] + " Bobot "
                        // + weights[ivalue - 1]);

                    }
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
        }

        return computeSimilarity(values, weights, ivalue); // memanggil computeSimilarity yg ada di Average

    }

    private <T> Set<String> composeSet(Set<T> flds) throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Set<String> rset = new HashSet<String>();

        for (T fld : flds) {
            Method mqry = fld.getClass().getDeclaredMethod("getName");
            String nedel = (String) mqry.invoke(fld);
            rset.add(nedel);
        }

        return rset;
    }

    /**
     * Hook method that must be implemented by subclasses returned the global
     * similarity value.
     *
     * @param values         of the similarity of the sub-attributes
     * @param weigths        of the sub-attributes
     * @param numberOfvalues (or sub-attributes) that were obtained (some
     *                       subattributes may not compute for the similarity).
     * @return a value between [0..1]
     */
    public double computeSimilarity(double[] values, double[] weigths, int numberOfvalues) {
        double acum = 0;
        double weigthsAcum = 0;
        for (int i = 0; i < numberOfvalues; i++) {
            acum += values[i] * weigths[i];
            weigthsAcum += weigths[i];
            // j=i+1;
        }
        // System.out.println("Total nilai " + acum + " total bobot " + weigthsAcum);
        return acum / weigthsAcum;
    }
}
