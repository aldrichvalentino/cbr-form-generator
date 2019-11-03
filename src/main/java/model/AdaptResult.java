package model;

/**
 * Stores the retrieval information. It contais a <case, evaluation> pair.
 */
public class AdaptResult implements Comparable {

  /** Constant used to retrieve all the cases in the retrieval methods. */
  public static final int RETRIEVE_ALL = Integer.MAX_VALUE;

  private Groups grp;
  private Orders ord;
  private VLMembers vlm;
  private HLMembers hlm;
  private double eval;

  /**
   * Constructor
   *
   * @param _case retrieved
   * @param eval  is the similiarty with the query
   */
  public AdaptResult() {
  }

  public AdaptResult(Groups grp, Double eval) {
    this.grp = grp;
    this.eval = eval;
  }

  public AdaptResult(Orders ord, Double eval) {
    this.ord = ord;
    this.eval = eval;
  }

  public AdaptResult(VLMembers vlm, Double eval) {
    this.vlm = vlm;
    this.eval = eval;
  }

  public AdaptResult(HLMembers hlm, Double eval) {
    this.hlm = hlm;
    this.eval = eval;
  }

  /**
   * @return Returns the _case.
   */
  public Groups getGroup() {
    return grp;
  }

  /**
   * @return Returns the eval.
   */
  public double getEval() {
    return eval;
  }

  /**
   * @param eval The eval to set.
   */
  public void setEval(double eval) {
    this.eval = eval;
  }

  public Orders getOrder() {
    return this.ord;
  }

  public VLMembers getVLMember() {
    return this.vlm;
  }

  public HLMembers getHLMember() {
    return this.hlm;
  }

  public String toString() {
    return grp + " -> " + eval;
  }

  public int compareTo(Object o) {
    if (!(o instanceof AdaptResult))
      return 0;
    AdaptResult other = (AdaptResult) o;
    if (other.getEval() < eval)
      return -1;
    else if (other.getEval() > eval)
      return 1;
    else
      return 0;
  }
}
