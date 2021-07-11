package App.BusinessLogic.WorkshiftsManagement;

import App.BusinessLogic.WorkingStaffManagement.Cook;
import App.persistence.PersistenceManager;

import java.util.ArrayList;
import java.util.List;

public class KitchenWorkshift extends WorkShift{


    public static List<KitchenWorkshift> getShiftsByCookID(int Id_cook){
        List<KitchenWorkshift> shifts = new ArrayList<KitchenWorkshift>();
        String getWorkshifts = "select id_workshift from cook_workshift where Id_cook = " + Id_cook;
        PersistenceManager.executeQuery(getWorkshifts, (rs)->{
            KitchenWorkshift temp = new KitchenWorkshift();
            temp.setId(rs.getInt(1));
            shifts.add(temp);
        });
        return shifts;
    }
}
