package App.BusinessLogic.KitchenTasksManagement;

import App.BusinessLogic.MenuManagement.Menu;
import App.BusinessLogic.RecipeManagement.Mixture;
import App.BusinessLogic.RecipeManagement.Recipe;
import App.BusinessLogic.WorkingStaffManagement.Cook;
import App.BusinessLogic.WorkshiftsManagement.KitchenWorkshift;
import App.persistence.BatchUpdateHandler;
import App.persistence.PersistenceManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

public class Task {

    private int time;
    private int portion;
    private int ID = -1;
    private int ID_sheet = -1;
    private int order = 0;
    private String quantity;
    private Mixture mixture;
    private Cook cook;
    private KitchenWorkshift workshift;

    public Task(int time, int portion, String quantity, Mixture mixture, Cook cook, KitchenWorkshift workshift){
        assert mixture != null;
        setTime(time);
        setPortion(portion);
        setQuantity(quantity);
        setMixture(mixture);
        setCook(cook);
        setWorkshift(workshift);
    }
    public int getID() {return ID;}

    public void setID(int ID) {this.ID = ID;}

    public int getID_sheet() {
        return ID_sheet;
    }

    public void setID_sheet(int ID_sheet) {
        this.ID_sheet = ID_sheet;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getPortion() {
        return portion;
    }

    public void setPortion(int portion) {
        this.portion = portion;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public Mixture getMixture() {
        return mixture;
    }

    public void setMixture(Mixture mixture) {
        this.mixture = mixture;
    }

    public Cook getCook() {
        return cook;
    }

    public void setCook(Cook cook) {
        this.cook = cook;
    }

    public KitchenWorkshift getWorkshift() {
        return workshift;
    }

    public void setWorkshift(KitchenWorkshift workshift) {
        this.workshift = workshift;
    }

    public int getOrder() {return order;}

    public void setOrder(int order) {this.order = order;}

    //STATIC METHODS FOR PERSISTENCE

    public static void saveNewTask(int id_sheet, Task task){
        task.setID(PersistenceManager.getNextAutoIncID("tasks"));
        String taskInsert = "INSERT INTO catering.tasks (id_sheet, id_cook, id_preparation, id_recipe, id_workshift, time, quantity, portion, `order`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PersistenceManager.executeBatchUpdate(taskInsert, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, id_sheet);

                if(task.getCook() != null) {
                    ps.setInt(2, task.getCook().getId());
                }else ps.setNull(2, Types.NULL);

                if(task.getMixture() != null) {
                    if(task.getMixture() instanceof Recipe) {
                        ps.setInt(4, task.getMixture().getId());
                        ps.setNull(3, Types.NULL);
                    }else{
                        ps.setInt(3, task.getMixture().getId());
                        ps.setNull(4, Types.NULL);
                    }
                }else{
                    ps.setNull(3, Types.NULL);
                    ps.setNull(4, Types.NULL);
                }

                if(task.getWorkshift() != null) {
                    ps.setInt(5, task.getWorkshift().getId());
                }else ps.setNull(5, Types.NULL);

                ps.setInt(6, task.getTime());
                ps.setString(7, task.getQuantity());
                ps.setInt(8, task.getPortion());
                ps.setInt(9, task.getOrder());
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {

            }
        });

    }
    public static void modifyTask(int id_sheet, Task task){
        String setClause = "id_sheet = " + id_sheet + " , id_cook = ";

        if(task.getCook() != null) {
            setClause += task.getCook().getId();
        }else setClause += "null";

        if(task.getMixture() != null) {
            if(task.getMixture() instanceof Recipe){
                setClause += " , id_preparation = null , id_recipe = " + task.getMixture().getId();
            }else{
                setClause += " , id_preparation = "+ task.getMixture().getId() + " , id_recipe = null ";
            }
        }else setClause += ", id_preparation = null , id_recipe = null ";

        setClause += " , id_workshift = ";
        if(task.getWorkshift() != null) {
            setClause += task.getWorkshift().getId();
        }else setClause += "null";

        setClause += " , time = " + task.getTime() + " , quantity = '" + task.getQuantity()
                    + "' , portion = " + task.getPortion() + " , `order` = " + task.getOrder();
        PersistenceManager.executeUpdate("UPDATE catering.tasks SET " + setClause + " WHERE id = " + task.getID());
    }

    public static void removeTask(Task task){
        String sheetDelete = "DELETE FROM catering.tasks WHERE ID =" + task.getID();
        PersistenceManager.executeUpdate(sheetDelete);
    }
    public String toString(){
        return "<time: " + time + ", portion: " + portion + ", quantity: " + quantity +
                "\nID: " + ID + ", ID_sheet: " + ID_sheet  + ", order: " + order +
                "\nmixture:" + mixture + "\ncook: " + cook + "\nshift: " + workshift+">\n";
    }

}
