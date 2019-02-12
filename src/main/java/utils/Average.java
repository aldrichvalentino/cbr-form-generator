/**
 * Average.java
 * jCOLIBRI2 framework.
 * @author Juan A. Recio-García.
 * GAIA - Group for Artificial Intelligence Applications
 * http://gaia.fdi.ucm.es
 * 03/01/2007
 */
package utils;

import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCase;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRQuery;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CaseComponent;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.NNretrieval.NNConfig;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.NNretrieval.similarity.StandardGlobalSimilarityFunction;

/**
 * This function computes the average of the similarites of its subattributes.
 *
 * @author Juan A. Recio-Garcia
 * @version 1.0
 */
public abstract class Average extends StandardGlobalSimilarityFunction {

    // private static int j=0;
    // public double computeSimilarity(double[] values, double[] weigths, int
    // ivalue) {
    // double acum = 0;
    // double weigthsAcum = 0;
    // for (int i = 0; i < ivalue; i++) {
    // acum += values[i] * weigths[i];
    // weigthsAcum += weigths[i];
    // // j=i+1;
    // }
    // System.out.println("Total nilai " + acum + " total bobot " + weigthsAcum);
    // return acum / weigthsAcum;
    // }
}
