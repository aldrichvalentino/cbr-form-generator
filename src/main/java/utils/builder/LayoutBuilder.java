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
                        fieldGroupTemplates.add(new FieldGroupTemplate(orderMembers,
                                getOrientation(formLayouts), labels));
                        formLayouts = removeElements(formLayouts, orderMembers);
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

    private static String getOrientation(ArrayList<VLMembers> formLayouts) {
        // check for horizontal orientation, ex: (elm1 elm2 etc.)
        return formLayouts.get(0).getName().contains("(") ? "HORIZONTAL" : "VERTICAL";
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
            System.out.println(group.getLayout());
            for (FormFieldTemplate field : group.getFormFieldTemplates()) {
                System.out.println(field.getName());
                System.out.println(field.getLabel());
            }
        }
    }
}
