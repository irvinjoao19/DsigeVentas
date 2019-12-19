package com.dsige.dsigeventas.data.tableview;


import com.dsige.dsigeventas.data.local.model.Personal;
import com.dsige.dsigeventas.data.tableview.model.CellModel;
import com.dsige.dsigeventas.data.tableview.model.ColumnHeaderModel;
import com.dsige.dsigeventas.data.tableview.model.RowHeaderModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by evrencoskun on 4.02.2018.
 */

class MyTableViewModel {
    // View Types
    private static final int GENDER_TYPE = 1;
    private static final int MONEY_TYPE = 2;

    private List<ColumnHeaderModel> mColumnHeaderModelList;
    private List<RowHeaderModel> mRowHeaderModelList;
    private List<List<CellModel>> mCellModelList;

    int getCellItemViewType(int column) {
        switch (column) {
            case 5:
                // 5. column header is gender.
                return GENDER_TYPE;
            case 8:
                // 8. column header is Salary.
                return MONEY_TYPE;
            default:
                return 0;
        }
    }

     /*
       - Each of Column Header -
            "Id"
            "Name"
            "Nickname"
            "Email"
            "Birthday"
            "Gender"
            "Age"
            "Job"
            "Salary"
            "CreatedAt"
            "UpdatedAt"
            "Address"
            "Zip Code"
            "Phone"
            "Fax"
     */

    private List<ColumnHeaderModel> createColumnHeaderModelList() {
        List<ColumnHeaderModel> list = new ArrayList<>();

        // Create Column Headers
        list.add(new ColumnHeaderModel("Personal"));
        list.add(new ColumnHeaderModel("Cant. Pedidos"));
        list.add(new ColumnHeaderModel("Cant. Clientes"));
        list.add(new ColumnHeaderModel("Cant. Productos"));
        list.add(new ColumnHeaderModel("Total"));

        return list;
    }

    private List<List<CellModel>> createCellModelList(List<Personal> userList) {
        List<List<CellModel>> lists = new ArrayList<>();

        // Creating cell model list from User list for Cell Items
        // In this example, User list is populated from web service

        for (int i = 0; i < userList.size(); i++) {
            Personal p = userList.get(i);

            List<CellModel> list = new ArrayList<>();

            // The order should be same with column header list;
            list.add(new CellModel("1-" + i, p.getNombrePersonal()));          // "Id"
            list.add(new CellModel("2-" + i, p.getCountPedidos()));        // "Name"
            list.add(new CellModel("3-" + i, p.getCountClientes()));    // "Nickname"
            list.add(new CellModel("4-" + i, p.getCountProductos()));       // "Email"
            list.add(new CellModel("5-" + i,p.getTotal()));   // "BirthDay"

            // Add
            lists.add(list);
        }

        return lists;
    }

    private List<RowHeaderModel> createRowHeaderList(int size) {
        List<RowHeaderModel> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            // In this example, Row headers just shows the index of the TableView List.
            list.add(new RowHeaderModel(String.valueOf(i + 1)));
        }
        return list;
    }


    List<ColumnHeaderModel> getColumHeaderModeList() {
        return mColumnHeaderModelList;
    }

    List<RowHeaderModel> getRowHeaderModelList() {
        return mRowHeaderModelList;
    }

    List<List<CellModel>> getCellModelList() {
        return mCellModelList;
    }

    void generateListForTableView(List<Personal> users) {
        mColumnHeaderModelList = createColumnHeaderModelList();
        mCellModelList = createCellModelList(users);
        mRowHeaderModelList = createRowHeaderList(users.size());
    }
}