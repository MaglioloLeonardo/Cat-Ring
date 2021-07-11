package App.BusinessLogic.KitchenTasksManagement;

import App.BusinessLogic.CatERing;
import App.BusinessLogic.EventManagement.Service;
import App.BusinessLogic.RecipeManagement.Mixture;
import App.BusinessLogic.UserManagement.UseCaseLogicException;
import App.BusinessLogic.UserManagement.User;
import App.BusinessLogic.WorkingStaffManagement.Chef;
import App.BusinessLogic.WorkingStaffManagement.Cook;
import App.BusinessLogic.WorkshiftsManagement.KitchenWorkshift;
import App.BusinessLogic.WorkshiftsManagement.TimeTable;
import App.BusinessLogic.WorkshiftsManagement.WorkShift;

import java.util.ArrayList;
import java.util.List;

public class KitchenTasksManager {
    private SummarySheet currentSheet;
    private Chef currentChef;
    private TimeTable timeTable;
    private List<KitchenEventReceiver> eventReceivers;

    public KitchenTasksManager() throws UseCaseLogicException {
        eventReceivers = new ArrayList<KitchenEventReceiver>();
        currentChef = getLoggedChef();
    }

    //eventReceivers methods
    public void addReceivers(KitchenEventReceiver er){eventReceivers.add(er);}

    public void removeReceivers(KitchenEventReceiver er){eventReceivers.remove(er);}

    private void notifySheetAdded(SummarySheet sheet){
        for(KitchenEventReceiver er: eventReceivers){
            er.updateSheetAdded(sheet);
        }
    }

    private void notifySheetRemoved(SummarySheet sheet){
        for(KitchenEventReceiver er: eventReceivers){
            er.updateSheetRemoved(sheet);
        }
    }

    private void notifyAddedMixture(SummarySheet sheet, Mixture mix){
        for(KitchenEventReceiver er: eventReceivers){
            er.updateMixtureAdded(sheet, mix);
        }
    }

    private void notifyRemovedMixture(SummarySheet sheet, Mixture mix){
        for(KitchenEventReceiver er: eventReceivers){
            er.updateMixtureRemoved(sheet, mix);
        }
    }

    private void notifyTaskAdded(SummarySheet sheet, Task task){
        for(KitchenEventReceiver er: eventReceivers){
            er.updateTaskAdded(sheet, task);
        }
    }

    private void notifyTaskModified(SummarySheet sheet, Task task){
        for(KitchenEventReceiver er: eventReceivers){
            er.updateTaskModified(sheet, task);
        }
    }

    private void notifyTaskRemoved(SummarySheet sheet, Task task){
        for(KitchenEventReceiver er: eventReceivers){
            er.updateTaskRemoved(sheet, task);
        }
    }

    private void notifySheetOrdered(SummarySheet sheet, List<Task> order){
        for(KitchenEventReceiver er: eventReceivers){
            er.updateSheetOrdered(sheet, order);
        }
    }

    private Chef getLoggedChef() throws UseCaseLogicException{
        return CatERing.getInstance().getUserManager().getCurrentUser().getChef();
    }
    //_________________________________________________________


    public SummarySheet CreateSummarySheet(Service service) throws UseCaseLogicException {
        if(service != null && service.getSummarySheet() == null &&  service.getEvent().getChef() == currentChef){
           SummarySheet added =  currentChef.CreateSummarySheet(service);
           notifySheetAdded(added);
           service.setSummarySheet(added);
           return added;
        }else throw new UseCaseLogicException();
    }

    public SummarySheet DeleteSummarySheet(Service service) throws UseCaseLogicException {
        if(service != null && service.getSummarySheet() != null &&  service.getEvent().getChef() == currentChef){
            SummarySheet removed =  currentChef.DeleteSummarySheet(service);
            notifySheetRemoved(removed);
            return removed;
        }else throw new UseCaseLogicException();
    }

    public void WorkOnSummarySheet(SummarySheet sheet) throws UseCaseLogicException{
        if(currentChef.isSummarySheetPresent(sheet)){
            currentSheet = sheet;
        }else{
            throw new UseCaseLogicException();
        }
    }

    public void addMixture(Mixture mix) throws UseCaseLogicException{
        if(currentSheet != null){
            currentSheet.addMixture(mix);
            notifyAddedMixture(currentSheet, mix);
        }else{
            throw new UseCaseLogicException();
        }
    }

    public void removeMixture(Mixture mix) throws UseCaseLogicException{
        if(currentSheet != null && currentSheet.isMixturePresent(mix)){
            currentSheet.removeMixture(mix);
            notifyRemovedMixture(currentSheet, mix);
        }else{
            throw new UseCaseLogicException();
        }
    }

    public List<WorkShift> showKitchenTimeTable(){return TimeTable.getInstance().getWorkShifts();}

    public Task addTask(Mixture mix, Cook cook, KitchenWorkshift kitchenWorkshift,
                        Integer time, String qnty, Integer portion)throws UseCaseLogicException {
        if(currentSheet != null && mix != null && currentSheet.isMixturePresent(mix) && (!(cook != null && kitchenWorkshift != null) || (cook.isAvaible(kitchenWorkshift)))){
            Task task = currentSheet.addTask(mix, cook, kitchenWorkshift, time, qnty, portion);
            notifyTaskAdded(currentSheet, task);
            return task;
        }else{
            throw new UseCaseLogicException();
        }
    }

    public void modifyTask(Task task, Mixture mix, Cook cook, KitchenWorkshift kitchenWorkshift,
                        Integer time, String qnty, Integer portion)throws UseCaseLogicException {
        if(currentSheet != null && (mix == null || currentSheet.isMixturePresent(mix)) && (!(cook != null && kitchenWorkshift != null) || (cook.isAvaible(kitchenWorkshift))) && ((mix == null) || (currentSheet.isMixturePresent(mix)))){
           currentSheet.modifyTask(task, mix, cook, kitchenWorkshift, time, qnty, portion);
           notifyTaskModified(currentSheet, task);
        }else{
            throw new UseCaseLogicException();
        }
    }

    public void deleteTask(Task task) throws UseCaseLogicException {
        if(currentSheet != null && currentSheet.isTaskPresent(task)){
            currentSheet.deleteTask(task);
            notifyTaskRemoved(currentSheet, task);
        }else{
            throw new UseCaseLogicException();
        }
    }

    public void setSort(List<Task> order) throws UseCaseLogicException {
        if(currentSheet != null){
            if(currentSheet.setSort(order)){
                notifySheetOrdered(currentSheet, order);
            }else{
                throw new UseCaseLogicException();
            }
        }else{
            throw new UseCaseLogicException();
        }
    }
}
