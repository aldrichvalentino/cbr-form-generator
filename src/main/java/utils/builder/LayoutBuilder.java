package utils.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.OMembers;
import model.Orders;
import model.VLMembers;
import model.XLabel;
import model.templates.FieldGroupTemplate;
import model.templates.FormFieldTemplate;

public class LayoutBuilder {
    private static final String startRow = "(";
    private static final String endRow = ")";
    private static final String startCol = "{";
    private static final String endCol = "}";
    private static final String startGroup = "[";
    private static final String endGroup = "]";

    public static ArrayList<FormFieldTemplate> buildLayout(List<VLMembers> formLayouts,
            Map<String, XLabel> formLabels) {
        ArrayList<FormFieldTemplate> formFieldTemplates = new ArrayList<>();
        for (VLMembers layoutMembers : formLayouts) {
            String layoutName = layoutMembers.getName();
            String pureName =
                    layoutName.replace(startRow, "").replace(startCol, "").replace(startGroup, "")
                            .replace(endRow, "").replace(endCol, "").replace(endGroup, "");
            boolean isFieldGenerated = false;
            for (int i = 0; i < layoutName.length(); i++) {
                switch (layoutName.substring(i, i + 1)) {
                    case startRow:
                        formFieldTemplates.add(new FormFieldTemplate("", "startRow", ""));
                        break;
                    case startCol:
                        formFieldTemplates.add(new FormFieldTemplate("", "startCol", ""));
                        break;
                    case startGroup:
                        formFieldTemplates.add(new FormFieldTemplate("", "startGroup", ""));
                        break;
                    case endRow:
                    case endCol:
                    case endGroup:
                        formFieldTemplates.add(new FormFieldTemplate("", "end", ""));
                        break;
                    default:
                        if (!isFieldGenerated) {
                            formFieldTemplates
                                    .add(new FormFieldTemplate(pureName, formLabels.get(pureName)));
                            isFieldGenerated = true;
                        }
                        break;
                }
            }
        }
        return formFieldTemplates;
    }

    public static ArrayList<FieldGroupTemplate> buildLayout(List<Orders> orders,
            List<VLMembers> layouts, Map<String, XLabel> labels) {
        ArrayList<FieldGroupTemplate> fieldGroupTemplates = new ArrayList<>();
        try {
            // copy the orders and layouts
            ArrayList<Orders> formOrders = new ArrayList<>();
            formOrders.addAll(orders);
            ArrayList<VLMembers> formLayouts = new ArrayList<>();
            formLayouts.addAll(layouts);

            while (!formLayouts.isEmpty()) {
                boolean found = false;
                int currentOrder = 0;
                while (!found) {
                    System.out.println(currentOrder);
                    List<OMembers> orderMembers = formOrders.get(currentOrder).getoMembers();
                    if (startWith(formLayouts, orderMembers)) {
                        found = true;
                        fieldGroupTemplates
                                .add(new FieldGroupTemplate(formLayouts, orderMembers, labels));
                        formLayouts = removeElements(formLayouts, orderMembers);
                        formOrders.remove(currentOrder);
                    } else {
                        currentOrder++;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return fieldGroupTemplates;
    }

    private static boolean startWith(ArrayList<VLMembers> formLayouts,
            List<OMembers> orderMembers) {
        int consecutiveLength = 0, index = 0;
        for (OMembers currentOrder : orderMembers) {
            String sanitizedName =
                    formLayouts.get(index).getName().replace("(", "").replace(")", "");
            if (sanitizedName.equals(currentOrder.getMemberName()))
                consecutiveLength++;
            index++;
        }
        return consecutiveLength == orderMembers.size();
    }

    private static ArrayList<VLMembers> removeElements(ArrayList<VLMembers> formLayouts,
            List<OMembers> orderMembers) {
        ArrayList<VLMembers> formLayoutResult = new ArrayList<>(formLayouts);
        if (formLayoutResult.size() == orderMembers.size()) {
            formLayoutResult.clear();
        } else {
            formLayoutResult = new ArrayList<VLMembers>(
                    formLayoutResult.subList(orderMembers.size(), formLayoutResult.size()));
        }

        return formLayoutResult;
    }

    // for testing only
    public static void main(String args[]) {
        List<Orders> orders = new ArrayList<Orders>();
        List<VLMembers> layouts = new ArrayList<>();
        Map<String, XLabel> labels = new HashMap<String, XLabel>();

        Orders bulkOrders1 = new Orders();
        ArrayList<OMembers> orderMembers1 = new ArrayList<>();
        orderMembers1.add(new OMembers("email"));
        orderMembers1.add(new OMembers("password"));
        bulkOrders1.setoMembers(orderMembers1);
        orders.add(bulkOrders1);

        Orders bulkOrders = new Orders();
        ArrayList<OMembers> orderMembers = new ArrayList<>();
        orderMembers.add(new OMembers("first_name"));
        orderMembers.add(new OMembers("last_name"));
        bulkOrders.setoMembers(orderMembers);
        orders.add(bulkOrders);

        layouts.add(new VLMembers("first_name"));
        layouts.add(new VLMembers("last_name"));
        layouts.add(new VLMembers("(email"));
        layouts.add(new VLMembers("password)"));

        labels.put("email", new XLabel("Isi Email"));
        labels.put("password", new XLabel("Isi Password"));
        labels.put("first_name", new XLabel("Isi Nama Depan"));
        labels.put("last_name", new XLabel("Isi nama belakang"));

        ArrayList<FieldGroupTemplate> finalTemplate =
                LayoutBuilder.buildLayout(orders, layouts, labels);

        for (FieldGroupTemplate group : finalTemplate) {
            System.out.println("Ini grup");
            for (FormFieldTemplate field : group.getFormFieldTemplates()) {
                System.out.println(field.getName());
                System.out.println(field.getLabel());
            }
        }
    }
}
