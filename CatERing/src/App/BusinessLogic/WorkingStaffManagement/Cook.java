package App.BusinessLogic.WorkingStaffManagement;

import App.BusinessLogic.UserManagement.User;
import App.BusinessLogic.WorkshiftsManagement.*;
import App.persistence.PersistenceManager;

import java.util.ArrayList;
import java.util.List;

public class Cook{
    private int id = -1;
    private List<KitchenWorkshift> availabities;

    public  Cook(){ //Possibilmente incompleto, la sua creazione dipende da casi d'uso non approfonditi
        availabities = new ArrayList<KitchenWorkshift>();
    }

    public int getId() {return id;}

    public void setId(int id) {this.id = id;}

    public void addKitchenWorkshift(KitchenWorkshift shift){
        availabities.add(shift);
    }

    public boolean isAvaible(KitchenWorkshift workshift){
        return availabities.contains(workshift);
    }

    // STATIC METHODS FOR PERSISTENCE

    public static Cook getCookByIDPersistance(int Id_cook){
        List<Cook> cook = new ArrayList<Cook>();
        List<KitchenWorkshift> shifts = KitchenWorkshift.getShiftsByCookID(Id_cook);
        String getCook = "select id from users where ID = " + Id_cook;
        PersistenceManager.executeQuery(getCook, (rs)->{
            Cook temp = new Cook();
            temp.setId(rs.getInt(1));
            cook.add(temp);
        });
        if(cook.size() > 0){
            for (KitchenWorkshift shift: shifts) {
                cook.get(0).addKitchenWorkshift(shift);
            }
            return cook.get(0);
        }else  return  null;
    }
}
