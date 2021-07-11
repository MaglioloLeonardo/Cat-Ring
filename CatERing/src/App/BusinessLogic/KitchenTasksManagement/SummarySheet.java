package App.BusinessLogic.KitchenTasksManagement;

import App.BusinessLogic.MenuManagement.Menu;
import App.BusinessLogic.RecipeManagement.Mixture;
import App.BusinessLogic.RecipeManagement.Preparation;
import App.BusinessLogic.RecipeManagement.Recipe;
import App.BusinessLogic.WorkingStaffManagement.Cook;
import App.BusinessLogic.WorkshiftsManagement.KitchenWorkshift;
import App.persistence.BatchUpdateHandler;
import App.persistence.PersistenceManager;

import javax.print.attribute.standard.SheetCollate;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SummarySheet {
    private int id = -1;
    private int id_chef = -1;
    private List<Task> tasks;
    private List<Mixture> mixtures;

    public SummarySheet(int id_chef) {
        this.id_chef = id_chef;
        tasks = new ArrayList<Task>();
        mixtures = new ArrayList<Mixture>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_chef() {
        return id_chef;
    }

    public void setId_chef(int id_chef) {
        this.id_chef = id_chef;
    }

    public void addMixture(Mixture mix) {
        mixtures.add(mix);
    }

    public void removeMixture(Mixture mix) {
        mixtures.remove(mix);
    }

    public Task addTask(Mixture mix, Cook cook, KitchenWorkshift kitchenWorkshift, Integer time, String qnty, Integer portion) {
        assert mix != null;
        Task task = new Task(time, portion, qnty, mix, cook, kitchenWorkshift);
        tasks.add(task);
        return task;
    }

    public Task addTask(Task task) {
        tasks.add(task);
        return task;
    }

    public void modifyTask(Task task, Mixture mix, Cook cook, KitchenWorkshift kitchenWorkshift, Integer time, String qnty, Integer portion) {
        assert task != null;

        if (mix != null)
            task.setMixture(mix);

        if (cook != null)
            task.setCook(cook);

        if (kitchenWorkshift != null)
            task.setWorkshift(kitchenWorkshift);

        if (time != null)
            task.setTime(time);

        if (qnty != null)
            task.setQuantity(qnty);

        if (portion != null)
            task.setPortion(portion);
    }

    public void deleteTask(Task task) {
        tasks.remove(task);
    }

    public boolean setSort(List<Task> order) {
        boolean contained1, contained2, areEqual;
        contained1 = tasks.containsAll(order);
        contained2 = order.containsAll(tasks);
        areEqual = contained1 && contained2;
        if(areEqual)
            tasks = order;
        return areEqual;
    }

    public boolean isMixturePresent(Mixture mix) {
        return mixtures.contains(mix);
    }

    public boolean isTaskPresent(Task task) {
        return tasks.contains(task);
    }

    public String toString(){
        return  "< ID: " + id + ", id_chef: " + id_chef +
                "\nTasks: " + tasks + "\nMixs:" + mixtures + ">";
    }

    //STATIC METHODS FOR PERSISTENCE

    private static int getNextAutoIncID(){
        return PersistenceManager.getNextAutoIncID("summarysheets");
    }

    public static void saveNewSheetPersistance(SummarySheet sheet){
        sheet.setId(getNextAutoIncID());
        String sheetInsert = "INSERT IGNORE INTO catering.summarysheets (id_chef) VALUES (" + sheet.getId_chef() +")";
        PersistenceManager.executeUpdate(sheetInsert);


        for(Task task: sheet.tasks){
            Task.saveNewTask(sheet.getId(), task);
        }

        for(Mixture mix: sheet.mixtures){
            addMixPersistance(sheet, mix);
        }
    }

    public static void deleteSheetPersistance(SummarySheet sheet){
        String sheetDelete = "DELETE FROM catering.summarysheets WHERE ID=" + sheet.id;
        PersistenceManager.executeUpdate(sheetDelete);
    }

    public static void addMixPersistance(SummarySheet sheet, Mixture mix){
        String sheetRecipeInsert = "INSERT INTO catering.summarysheet_recipes (id_recipe, id_sheet) VALUES (?, ?)";
        String sheetPreparationsInsert = "INSERT INTO catering.summarysheet_preparations (id_preparation, id_sheet) VALUES (?, ?)";
        String toExecute = sheetPreparationsInsert;
        if(mix instanceof Recipe)toExecute = sheetRecipeInsert;
        PersistenceManager.executeBatchUpdate(toExecute, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, mix.getId());
                ps.setString(2, Integer.toString(sheet.getId()));
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {

            }
        });
    }

    public static void removeMixPersistance(SummarySheet sheet, Mixture mix){
        String sheetRecipeDelete = "DELETE FROM catering.summarysheet_recipes WHERE id_sheet=" + sheet.id + " and id_recipe=" + mix.getId();
        String sheetPreparationDelete = "DELETE FROM catering.summarysheet_preparations WHERE id_sheet=" + sheet.id + " and id_preparation=" + mix.getId();
        String toExecute = sheetPreparationDelete;
        if(mix instanceof Recipe)toExecute = sheetRecipeDelete;
        PersistenceManager.executeUpdate(toExecute);
    }

    public static void addTaskPersistance(SummarySheet sheet, Task task){
        Task.saveNewTask(sheet.getId(), task);
    }

    public static void modifyTaskPersistance(SummarySheet sheet, Task task){
        Task.modifyTask(sheet.getId(), task);
    }

    public static void removeTaskPersistance(SummarySheet sheet, Task task){
        Task.removeTask(task);
    }

    public static void changeOrderPersistance(SummarySheet sheet, List<Task> order){
        for (int i = 1; i<= order.size(); i++) {
            Task task = order.get(i-1);
            task.setOrder(i);
            Task.modifyTask(sheet.getId(), task);
        }
    }

    public static List<SummarySheet> getSheetListByChefIDPersistance(int chefID) {
        List<SummarySheet> sheets = new ArrayList<SummarySheet>();
        String getSheets = "select  * from summarysheets where Id_chef = " + chefID;
        PersistenceManager.executeQuery(getSheets, (rs)->{
            SummarySheet temp = new SummarySheet(rs.getInt(2));
            temp.setId(rs.getInt(1));
            temp.setId_chef(chefID);
            sheets.add(temp);
        });

        for(SummarySheet sheet: sheets){
            String getPreparationsSheet = "select id, name from summarysheet_preparations join preparations on (id_preparation = id)where Id_sheet = " + sheet.getId();
            String getRecipesSheet = "select  id, name from summarysheet_recipes join recipes on (id_recipe = id) where Id_sheet = " + sheet.getId();
            String getTasks = "select * from tasks where Id_sheet = " + sheet.getId();

            PersistenceManager.executeQuery(getPreparationsSheet, (rs)->{
                Mixture temp = new Preparation(rs.getString(2));
                temp.setId(rs.getInt(1));
                sheet.addMixture(temp);
            });

            PersistenceManager.executeQuery(getRecipesSheet, (rs)->{
                Mixture temp = new Recipe(rs.getString(2));
                temp.setId(rs.getInt(1));
                sheet.addMixture(temp);
            });

            PersistenceManager.executeQuery(getTasks, (rs)->{
                Mixture mix = null;
                if(rs.getInt(4) != 0){
                    mix =  Preparation.getPreparationFromIDPersistance(rs.getInt(4));
                }else mix = Recipe.getRecipeFromIDPersistance(rs.getInt(5));

                Cook cook = null;
                if(rs.getInt(3) != 0){
                    cook = Cook.getCookByIDPersistance(rs.getInt(3));
                }

                KitchenWorkshift shift = new KitchenWorkshift();
                if( rs.getInt(6) != 0){
                    shift.setId(rs.getInt(6));
                }else shift = null;

                Task temp = new Task(rs.getInt(7), rs.getInt(9), rs.getString(8)
                                , mix, cook, shift);
                temp.setID(rs.getInt(1));
                temp.setOrder(rs.getInt(10));
                temp.setID_sheet(sheet.getId());
                sheet.addTask(temp);
            });
        }

        return sheets;
    }
}

