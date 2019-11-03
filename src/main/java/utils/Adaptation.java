package utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import edu.smu.tspell.wordnet.WordNetDatabase;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCase;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRQuery;
import es.ucm.fdi.gaia.jcolibri.exception.NoApplicableSimilarityFunctionException;
import es.ucm.fdi.gaia.jcolibri.exception.OntologyAccessException;
import es.ucm.fdi.gaia.ontobridge.OntoBridge;
import model.ControlButtons;
import model.FormDescription;
import model.FormSolution;
import model.GMembers;
import model.Groups;
import model.HLMembers;
import model.InputFields;
import model.LOResult;
import model.OMembers;
import model.Orders;
import model.OutputFields;
import model.VLMembers;
import model.XLabel;
import utils.adapt.Compose;
import utils.adapt.Grouping;
import utils.adapt.Layouting;
import utils.adapt.Ordering;

public class Adaptation {

  final String op = "(";
  final String cp = ")";
  final String openCol = "{";
  final String closeCol = "}";
  final String openGroup = "[";
  final String closeGroup = "]";

  OntoBridge ob;
  WordNetDatabase database;
  HashMap<String, String> inrowelm;
  private Logger logger = LoggerFactory.getLogger(Adaptation.class);

  public Adaptation(WordNetDatabase wNetDatabase, OntoBridge ontoBridge) {
    database = wNetDatabase;
    ob = ontoBridge;
  }

  public CBRCase adapt(CBRQuery query, CBRCase inputCase)
      throws NoApplicableSimilarityFunctionException, NoSuchMethodException, SecurityException,
      IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      OntologyAccessException, InstantiationException {

    CBRCase adaptedCase = new CBRCase();
    FormDescription queryDesc = (FormDescription) query.getDescription();
    FormDescription caseDesc = (FormDescription) inputCase.getDescription();
    FormDescription solutionDesc = new FormDescription();

    // set new form name
    solutionDesc.setId((int) inputCase.getID());
    logger.info("Adapted Case ID: " + solutionDesc.getId());
    logger.info("Spec: " + caseDesc.toString());

    // set new form name
    String formName = queryDesc.getFormName();
    solutionDesc.setFormName(formName);

    Set<InputFields> queryInputs = queryDesc.getInputFields();
    Set<InputFields> caseInput = caseDesc.getInputFields();
    Set<OutputFields> queryOutputs = queryDesc.getOutputFields();
    Set<OutputFields> caseOutputs = caseDesc.getOutputFields();
    Set<ControlButtons> queryButtons = queryDesc.getControlButtons();
    Set<ControlButtons> caseButtons = caseDesc.getControlButtons();

    // specs to be added
    Set<InputFields> inputsToAdd = buildSpecToAdd(formName, queryInputs, caseInput);
    Set<OutputFields> outputsToAdd = buildSpecToAdd(formName, queryOutputs, caseOutputs);
    Set<ControlButtons> buttonsToAdd = buildSpecToAdd(formName, queryButtons, caseButtons);

    logger.info("Added Input Field Spec: " + inputsToAdd.size());
    for (InputFields ifl : inputsToAdd) {
      logger.info(ifl.getName());
    }
    logger.info("Added Output Field Spec: " + outputsToAdd.size());
    for (OutputFields ofl : outputsToAdd) {
      logger.info(ofl.getName());
    }
    logger.info("Added Control Field Spec: " + buttonsToAdd.size());
    for (ControlButtons cb : buttonsToAdd) {
      logger.info(cb.getName());
    }

    FormSolution formSolution = (FormSolution) inputCase.getSolution();

    // Fields to be deleted
    Set<InputFields> inputsToDel = buildSpecToDel(formName, caseInput, queryInputs);
    Set<OutputFields> outputsToDel = buildSpecToDel(formName, caseOutputs, queryOutputs);
    Set<ControlButtons> buttonsToDel = buildSpecToDel(formName, caseButtons, queryButtons);

    logger.info("Unused IF");
    for (InputFields ifl : inputsToDel) {
      logger.info(ifl.getName());
    }
    logger.info("Unused OF");
    for (OutputFields ofl : outputsToDel) {
      logger.info(ofl.getName());
    }
    logger.info("Unused CB");
    for (ControlButtons cb : buttonsToDel) {
      logger.info(cb.getName());
    }

    // composing
    solutionDesc.setInputFields(Compose.compose(caseInput, inputsToAdd, inputsToDel));
    solutionDesc.setOutputFields(Compose.compose(caseOutputs, outputsToAdd, outputsToDel));
    solutionDesc.setControlButtons(Compose.compose(caseButtons, buttonsToAdd, buttonsToDel));
    logger.info("Composition: " + solutionDesc.toString());

    // Add labels for new entry
    Map<String, XLabel> lbl = formSolution.getlabel();
    for (InputFields inputAdd : inputsToAdd)
      lbl.put(inputAdd.getName(), new XLabel(makeLabel(inputAdd.getName())));
    for (OutputFields outputAdd : outputsToAdd)
      lbl.put(outputAdd.getName(), new XLabel(makeLabel(outputAdd.getName())));
    for (ControlButtons buttonAdd : buttonsToAdd)
      lbl.put(buttonAdd.getName(), new XLabel(makeLabel(buttonAdd.getName())));

    // delete label del
    for (InputFields ifl : inputsToDel) {
      if (lbl.containsKey(ifl.getName())) {
        lbl.remove(ifl.getName());
      }
    }

    for (OutputFields ofl : outputsToDel) {
      if (lbl.containsKey(ofl.getName())) {
        lbl.remove(ofl.getName());
      }
    }

    for (ControlButtons cb : buttonsToDel) {
      if (lbl.containsKey(cb.getName())) {
        lbl.remove(cb.getName());
      }
    }

    // update group
    List<Groups> newGroup = formSolution.getGroup();

    // delete member/group
    for (InputFields ifl : inputsToDel) {
      for (Iterator<Groups> itg = newGroup.iterator(); itg.hasNext();) {
        for (Iterator<GMembers> itgm = itg.next().getgMembers().iterator(); itgm.hasNext();) {
          if (itgm.next().getName().equals(ifl.getName())) {
            // Remove the current element from the iterator and the list.
            itgm.remove();
          }
        }
      }
    }

    for (OutputFields ofl : outputsToDel) {
      for (Iterator<Groups> itg = newGroup.iterator(); itg.hasNext();) {
        for (Iterator<GMembers> itgm = itg.next().getgMembers().iterator(); itgm.hasNext();) {
          if (itgm.next().getName().equals(ofl.getName())) {
            // Remove the current element from the iterator and the list.
            itgm.remove();
          }
        }
      }
    }

    for (ControlButtons cb : buttonsToDel) {
      for (Iterator<Groups> itg = newGroup.iterator(); itg.hasNext();) {
        for (Iterator<GMembers> itgm = itg.next().getgMembers().iterator(); itgm.hasNext();) {
          if (itgm.next().getName().equals(cb.getName())) {
            // Remove the current element from the iterator and the list.
            itgm.remove();
          }
        }
      }
    }

    logger.info("Old Group");
    for (Groups grp : newGroup) {
      logger.info("Group id " + grp.getId());
      for (GMembers gmm : grp.getgMembers())
        logger.info("Members: " + gmm.getName());
    }

    logger.info("Remove empty groups");
    // remove empty grouping
    List<Groups> lgrp = new ArrayList<Groups>();
    lgrp.addAll(newGroup);
    for (Groups grp : lgrp) {
      if (grp.getgMembers().isEmpty()) {
        newGroup.remove(grp);
      }
    }

    logger.info("Start grouping");
    newGroup = Grouping.grouping(inputsToAdd, newGroup, ob);
    newGroup = Grouping.grouping(outputsToAdd, newGroup, ob);
    newGroup = Grouping.grouping(buttonsToAdd, newGroup, ob);

    logger.info("Adapted Groups");
    for (Groups grp : newGroup) {
      logger.info("Grp id " + grp.getId());
      for (GMembers gmm : grp.getgMembers())
        logger.info(" Members: " + gmm.getName());
    }

    // set group, grouping done
    formSolution.setGroup(newGroup);

    List<Orders> caseOrders = formSolution.getOrder();

    // delete orders
    for (InputFields inputFields : inputsToDel) {
      for (Iterator<Orders> inputIterators = caseOrders.iterator(); inputIterators.hasNext();) {
        for (Iterator<OMembers> orderMembers =
            inputIterators.next().getoMembers().iterator(); orderMembers.hasNext();) {
          if (orderMembers.next().getMemberName().equals(inputFields.getName())) {
            // Remove the current element from the iterator and the list.
            orderMembers.remove();
          }
        }
      }
    }

    for (OutputFields orderFields : outputsToDel) {
      for (Iterator<Orders> outputIterators = caseOrders.iterator(); outputIterators.hasNext();) {
        for (Iterator<OMembers> outputMembers =
            outputIterators.next().getoMembers().iterator(); outputMembers.hasNext();) {
          if (outputMembers.next().getMemberName().equals(orderFields.getName())) {
            // Remove the current element from the iterator and the list.
            outputMembers.remove();
          }
        }
      }
    }

    for (ControlButtons controlButtons : buttonsToDel) {
      for (Iterator<Orders> controlIterators = caseOrders.iterator(); controlIterators.hasNext();) {
        for (Iterator<OMembers> controlMembers =
            controlIterators.next().getoMembers().iterator(); controlMembers.hasNext();) {
          if (controlMembers.next().getMemberName().equals(controlButtons.getName())) {
            // Remove the current element from the iterator and the list.
            controlMembers.remove();
          }
        }
      }
    }

    // remove empty orderings
    List<Orders> orderList = new ArrayList<Orders>();
    orderList.addAll(caseOrders);
    for (Orders order : orderList) {
      if (order.getoMembers().isEmpty()) {
        caseOrders.remove(order);
      }
    }

    caseOrders = Ordering.ordering(inputsToAdd, newGroup, caseOrders, ob);
    caseOrders = Ordering.ordering(outputsToAdd, newGroup, caseOrders, ob);
    caseOrders = Ordering.ordering(buttonsToAdd, newGroup, caseOrders, ob);

    // set ordering, ordering done
    formSolution.setOrder(caseOrders);

    // layouting
    List<HLMembers> nhlo = formSolution.gethlMember();
    List<VLMembers> nvlo = formSolution.getvlMember();
    LOResult layoutingResults = new LOResult(nvlo, nhlo);

    // delete unused layout
    layoutingResults = Layouting.deleteLayouting(inputsToDel, layoutingResults);
    layoutingResults = Layouting.deleteLayouting(outputsToDel, layoutingResults);
    layoutingResults = Layouting.deleteLayouting(buttonsToDel, layoutingResults);

    layoutingResults = Layouting.setLayouting(inputsToAdd, layoutingResults, caseOrders);
    layoutingResults = Layouting.setLayouting(outputsToAdd, layoutingResults, caseOrders);
    layoutingResults = Layouting.setLayouting(buttonsToAdd, layoutingResults, caseOrders);

    // set layout, layouting done
    formSolution.setvlMember(layoutingResults.getVLMember());
    formSolution.sethlMember(layoutingResults.getHLMember());

    formSolution.setId((int) inputCase.getID());

    // put new solution
    adaptedCase.setSolution(formSolution);
    adaptedCase.setDescription(solutionDesc);

    return adaptedCase;
  }

  /**
   * Build Specs that are not in the case but present in the query
   *
   * @param <T>       a generic field (input, output, buttons)
   * @param formName
   * @param query
   * @param inputCase
   * @return
   * @throws NoSuchMethodException
   * @throws SecurityException
   * @throws IllegalAccessException
   * @throws IllegalArgumentException
   * @throws InvocationTargetException
   */
  private <T> Set<T> buildSpecToAdd(String formName, Set<T> query, Set<T> inputCase)
      throws NoSuchMethodException, SecurityException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException {
    Set<T> specadd = new HashSet<T>();
    Set<T> specaddloop = new HashSet<T>();

    // add all the fields in query
    specadd.addAll(query); // combine
    specaddloop.addAll(specadd);

    for (T cm : inputCase) {
      for (T qm : query) {
        Method mq = qm.getClass().getDeclaredMethod("getName");
        Method mc = cm.getClass().getDeclaredMethod("getName");
        String nmq = (String) mq.invoke(qm);
        String nmc = (String) mc.invoke(cm);
        if (nmq.equalsIgnoreCase(nmc) || isWeaker(nmq, nmc)) {
          specadd.remove(qm);
        }
      }
    }

    // add mandatory fields
    for (T cm : inputCase) {
      Method mc = cm.getClass().getDeclaredMethod("getName");
      String nmc = (String) mc.invoke(cm);
      if (isRequired(formName, nmc) && (!containsLayout(inputCase, cm))) {
        logger.info("Adding mandatory " + nmc + " to spec");
        specadd.add(cm);
      }
    }

    // remove prohibited fields
    Set<String> odds = getOddElements(formName);
    for (String odmem : odds) {
      for (T qm : specaddloop) {
        Method mq = qm.getClass().getDeclaredMethod("getName");
        String nmq = (String) mq.invoke(qm);
        if (nmq.trim().equalsIgnoreCase(odmem)) {
          specadd.remove(qm);
        }
      }
    }

    return specadd;
  }

  /**
   * Remove fields that are not present in the query
   *
   * @param <T>       generic fields (input, output, buttons)
   * @param formName
   * @param inputCase
   * @param query
   * @return
   * @throws NoSuchMethodException
   * @throws SecurityException
   * @throws IllegalAccessException
   * @throws IllegalArgumentException
   * @throws InvocationTargetException
   */
  private <T> Set<T> buildSpecToDel(String formName, Set<T> inputCase, Set<T> query)
      throws NoSuchMethodException, SecurityException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException {
    Set<T> specdel = new HashSet<T>();

    // add all case fields
    specdel.addAll(inputCase);

    for (T delel : inputCase) {
      for (T qelm : query) {
        Method metq = qelm.getClass().getDeclaredMethod("getName");
        Method metd = delel.getClass().getDeclaredMethod("getName");
        String nmq = (String) metq.invoke(qelm);
        String nmd = (String) metd.invoke(delel);
        if (nmq.equalsIgnoreCase(nmd)) {
          specdel.remove(delel);
        }
      }
    }

    return specdel;
  }

  public String makeLabel(String input) {
    StringBuffer result = new StringBuffer();
    String[] strArr = input.split("_");
    if (input.contains("_rd") || input.contains("_dd") || input.contains("_cb")) {
      // set default label for radio, select dropdown, and checkbox
      result.append("xnDefault Label,Op 1,Op2,Op3");
    } else {
      for (String str : strArr) {
        char[] stringArray = str.trim().toCharArray();
        stringArray[0] = Character.toUpperCase(stringArray[0]);
        str = new String(stringArray);
        result.append(str).append(" ");
      }
    }
    return result.toString().trim();
  }

  private boolean isWeaker(String wk, String stg) {
    if (!ob.existsInstance(wk) || !ob.existsInstance(stg))
      return false;

    Iterator<String> it = ob.listPropertyValue(stg, "isPreferredOver");
    while (it.hasNext()) {
      String elm = ob.getShortName(it.next());
      if (elm.equalsIgnoreCase(wk))
        return true;
    }

    return false;
  }

  private boolean isRequired(String fname, String fldname) {
    if (!ob.existsInstance(fldname) || !ob.existsInstance(fname))
      return false;
    Iterator<String> it = ob.listPropertyValue(fname, "hasMandatoryElements");
    while (it.hasNext()) {
      String elm = ob.getShortName(it.next());
      if (elm.equalsIgnoreCase(fldname))
        return true;
    }

    return false;
  }

  private Set<String> getOddElements(String fname) {
    Set<String> oddFields = new HashSet<String>();
    if (!ob.existsInstance(fname))
      return oddFields;
    Iterator<String> it = ob.listPropertyValue(fname, "hasOddElements");
    while (it.hasNext()) {
      String elm = ob.getShortName(it.next());
      oddFields.add(elm);
    }

    return oddFields;
  }

  private <T> boolean containsLayout(Set<T> lvlm, T vlm)
      throws NoSuchMethodException, SecurityException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException {
    for (T lmem : lvlm) {
      Method m = lmem.getClass().getDeclaredMethod("getName");
      String fln = (String) m.invoke(lmem);
      Method me = vlm.getClass().getDeclaredMethod("getName");
      String flne = (String) me.invoke(vlm);

      if (fln.startsWith(op))
        fln = fln.replace(op, "").replace("{", "").replace("[", "");
      if (fln.endsWith(cp))
        fln = fln.replace(cp, "").replace("}", "").replace("]", "");
      if (fln.equals(flne))
        return true;
    }

    return false;
  }
}
