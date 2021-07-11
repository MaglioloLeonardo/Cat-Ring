package App.persistence;

import App.BusinessLogic.KitchenTasksManagement.KitchenEventReceiver;
import App.BusinessLogic.KitchenTasksManagement.SummarySheet;
import App.BusinessLogic.KitchenTasksManagement.Task;
import App.BusinessLogic.RecipeManagement.Mixture;

import java.util.List;

public class KitchenTasksPersistence implements KitchenEventReceiver {
    public void updateSheetAdded(SummarySheet sheet){SummarySheet.saveNewSheetPersistance(sheet);}
    public void updateSheetRemoved(SummarySheet sheet){
        SummarySheet.deleteSheetPersistance(sheet);
    }
    public void updateMixtureAdded(SummarySheet sheet, Mixture mix){
        SummarySheet.addMixPersistance(sheet, mix);
    }
    public void updateMixtureRemoved(SummarySheet sheet, Mixture mix){
        SummarySheet.removeMixPersistance(sheet, mix);
    }
    public void updateTaskAdded(SummarySheet sheet, Task task){
        SummarySheet.addTaskPersistance(sheet, task);
    }
    public void updateTaskModified(SummarySheet sheet, Task task){
        SummarySheet.modifyTaskPersistance(sheet, task);
    }
    public void updateTaskRemoved(SummarySheet sheet, Task task){
        SummarySheet.removeTaskPersistance(sheet, task);
    }
    public void updateSheetOrdered(SummarySheet sheet, List<Task> order){SummarySheet.changeOrderPersistance(sheet, order);}
}
