package App.Test;

import App.BusinessLogic.CatERing;
import App.BusinessLogic.EventManagement.Event;
import App.BusinessLogic.EventManagement.Service;
import App.BusinessLogic.KitchenTasksManagement.KitchenTasksManager;
import App.BusinessLogic.KitchenTasksManagement.SummarySheet;
import App.BusinessLogic.KitchenTasksManagement.Task;
import App.BusinessLogic.RecipeManagement.Mixture;
import App.BusinessLogic.RecipeManagement.Recipe;
import App.BusinessLogic.UserManagement.UseCaseLogicException;
import App.persistence.KitchenTasksPersistence;

import java.util.ArrayList;
import java.util.List;


public class TestKitchenTasks {

    public static void main(String[] args) {
        try {
            /* System.out.println("TEST DATABASE CONNECTION");
            PersistenceManager.testSQLConnection();*/
            System.out.println("TEST FAKE LOGIN");
            CatERing.getInstance().getUserManager().fakeLogin("Lidia");
            System.out.println(CatERing.getInstance().getUserManager().getCurrentUser());

            KitchenTasksManager tasksManager = new KitchenTasksManager();
            tasksManager.addReceivers(new KitchenTasksPersistence());

            System.out.println("\nCreate summary sheet:_______________");
            Service service = genService();
            SummarySheet sheet = tasksManager.CreateSummarySheet(service);
            System.out.println("\nSheet:" + sheet);
            System.out.println("\n____________________________________");

            System.out.println("\nDelete summary sheet:_______________");
            tasksManager.DeleteSummarySheet(service);
            System.out.println("\nService:" + service);
            System.out.println("\n____________________________________");

            System.out.println("\nCreate summary sheet:_______________");
            sheet = tasksManager.CreateSummarySheet(service);
            System.out.println("\nSheet:" + sheet);
            System.out.println("\n____________________________________");

            System.out.println("\nSelect summary sheet");
            tasksManager.WorkOnSummarySheet(sheet);

            System.out.println("\nAdd mix:________________________");
            Mixture mix = new Recipe("prova");
            mix.setId(1);
            tasksManager.addMixture(mix);
            System.out.println("\nSheet:" + sheet);
            System.out.println("\n_______________________________");

            System.out.println("\nAdd task:_________________________");
            Task task = tasksManager.addTask(mix, null, null, 10, "100 grammi", 2);
            System.out.println("\nSheet:" + sheet);
            System.out.println("\n__________________________________");

            System.out.println("\nDelete task:_________________________");
            tasksManager.deleteTask(task);
            System.out.println("\nSheet:" + sheet);
            System.out.println("\n____________________________________");

            System.out.println("\nRemove mix:_____________________");
            tasksManager.removeMixture(mix);
            System.out.println("\nSheet:" + sheet);
            System.out.println("\n_______________________________");

            System.out.println("\nAdd mix:________________________");
            tasksManager.addMixture(mix);
            System.out.println("\nSheet:" + sheet);
            System.out.println("\n_______________________________");

            System.out.println("\nAdd task:_________________________");
            task = tasksManager.addTask(mix, null, null, 10, "100 grammi", 2);
            System.out.println("\nSheet:" + sheet);
            System.out.println("\n__________________________________");

            System.out.println("\nModify task:______________________");
            tasksManager.modifyTask(task, null, null, null, 1, "1 kg", 3);
            System.out.println("\nSheet:" + sheet);
            System.out.println("\n_______________________________");

            System.out.println("\nAdd and reoder_________________");
            List<Task> tasks = new ArrayList<Task>();
            for (int i = 1; i<9 ; i++){
                mix = new Recipe("prova" + i);
                mix.setId(i);
                sheet.addMixture(mix);

                task = new Task(i, i, "2" , mix, null, null);
                task.setID(i);
                task.setOrder(231);
                sheet.addTask(task);
                tasks.add(task);
                Task.saveNewTask(sheet.getId(), task);
            }
            SummarySheet.changeOrderPersistance(sheet, tasks);
            System.out.println("\nSheet:" + sheet);
            System.out.println("\n_______________________________");

            System.out.println("\nLoad sheet from db_____________");
            List<SummarySheet> sheetsLoaded = SummarySheet.getSheetListByChefIDPersistance(CatERing.getInstance().getUserManager().getCurrentUser().getId());
            System.out.println("\nSheets loaded:" + sheetsLoaded);
            System.out.println("\n_______________________________");


        } catch (UseCaseLogicException e) {
            System.out.println("Errore di logica nello use case");
        }
    }

    private static Service genService() throws UseCaseLogicException {
        Service service = new Service();
        Event event = new Event();
        event.setChef(CatERing.getInstance().getUserManager().getCurrentUser().getChef());
        service.setEvent(event);
        return service;
    }

}
