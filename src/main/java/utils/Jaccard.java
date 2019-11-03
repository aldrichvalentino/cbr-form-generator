package utils;

import java.util.HashSet;
import java.util.Set;
import es.ucm.fdi.gaia.jcolibri.exception.NoApplicableSimilarityFunctionException;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.NNretrieval.similarity.LocalSimilarityFunction;

public class Jaccard implements LocalSimilarityFunction {
  @Override
  @SuppressWarnings("unchecked")
  public double compute(Object caseObject, Object queryObject)
      throws NoApplicableSimilarityFunctionException {
    Set<String> caseSet = (Set<String>) caseObject;
    Set<String> querySet = (Set<String>) queryObject;

    if (caseSet.isEmpty() || querySet.isEmpty())
      return 0;
    Set<String> union = new HashSet<String>(caseSet);
    union.addAll(querySet);
    double unionSize = union.size();

    caseSet.retainAll(querySet);
    double intersectionSize = caseSet.size();
    return intersectionSize / unionSize;
  }

  @Override
  public boolean isApplicable(Object caseObject, Object queryObject) {
    return caseObject instanceof Set && queryObject instanceof Set;
  }

}
