package App.BusinessLogic.KitchenTasksManagement;

import App.BusinessLogic.RecipeManagement.Mixture;

import java.util.List;

public interface KitchenEventReceiver {
    void updateSheetAdded(SummarySheet sheet);
    void updateSheetRemoved(SummarySheet sheet);
    void updateMixtureAdded(SummarySheet sheet, Mixture mix);
    void updateMixtureRemoved(SummarySheet sheet, Mixture mix);
    void updateTaskAdded(SummarySheet sheet, Task task);
    void updateTaskModified(SummarySheet sheet, Task task);
    void updateTaskRemoved(SummarySheet sheet, Task task);
    void updateSheetOrdered(SummarySheet sheet, List<Task> order);
}
