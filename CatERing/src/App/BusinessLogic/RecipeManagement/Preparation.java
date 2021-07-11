package App.BusinessLogic.RecipeManagement;

import App.persistence.PersistenceManager;

import java.util.ArrayList;
import java.util.List;

public class Preparation extends Mixture{
    public Preparation(String name) {
        super(name);
    }

    public static Preparation getPreparationFromIDPersistance(int id_preparation){
        String getPreparation = "select * from preparations where id = " + id_preparation;
        List<Preparation> preparation = new ArrayList<Preparation>();
        PersistenceManager.executeQuery(getPreparation, (rs)->{
            Preparation temp = new Preparation(rs.getString(2));
            temp.setId(rs.getInt(1));
            preparation.add(temp);
        });
        if(preparation.size() > 0){
            return preparation.get(0);
        }else return null;
    }
}
