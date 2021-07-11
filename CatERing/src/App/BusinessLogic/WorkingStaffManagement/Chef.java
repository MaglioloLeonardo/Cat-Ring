package App.BusinessLogic.WorkingStaffManagement;

import App.BusinessLogic.EventManagement.Service;
import App.BusinessLogic.KitchenTasksManagement.SummarySheet;
import App.BusinessLogic.UserManagement.User;

import java.util.ArrayList;
import java.util.List;

public class Chef{
    private int id = -1;
    private List<SummarySheet> summarySheets;


    public Chef(){//Possibilmente incompleto, la sua creazione dipende da casi d'uso non approfonditi
        summarySheets = new ArrayList<SummarySheet>();
    }

    public int getId() {return id;}

    public void setId(int id) {this.id = id;}

    public SummarySheet CreateSummarySheet(Service service){
        SummarySheet sheet = new SummarySheet(id);
        summarySheets.add(sheet);
        service.setSummarySheet(sheet);
        return sheet;
    }

    public SummarySheet DeleteSummarySheet(Service service){
        SummarySheet sheet = service.getSummarySheet();
        summarySheets.remove(sheet);
        service.setSummarySheet(null);
        return sheet;
    }

    public boolean isSummarySheetPresent(SummarySheet sheet){
        return summarySheets.contains(sheet);
    }

}
