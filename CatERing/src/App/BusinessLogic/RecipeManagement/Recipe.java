package App.BusinessLogic.RecipeManagement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import App.persistence.PersistenceManager;
import App.persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Recipe extends Mixture{
    private static Map<Integer, Recipe> all = new HashMap<>();

    public Recipe(String name) {
        super(name);
        id = 0;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String toString() {
        return name;
    }

    // STATIC METHODS FOR PERSISTENCE

    public static Recipe getRecipeFromIDPersistance(int id_recipe){
        String getRecipe = "select * from recipes where id = " + id_recipe;
        List<Recipe> recipe = new ArrayList<Recipe>();
        PersistenceManager.executeQuery(getRecipe, (rs)->{
            Recipe temp = new Recipe(rs.getString(2));
            temp.setId(rs.getInt(1));
            recipe.add(temp);
        });
        if(recipe.size() > 0){
            return recipe.get(0);
        }else return null;
    }

    public static ObservableList<Recipe> loadAllRecipes() {
        all.clear();
        ObservableList<Recipe> result = FXCollections.observableArrayList();
        String query = "SELECT * FROM Recipes";
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                Recipe rec = new Recipe(rs.getString("name"));
                rec.id = rs.getInt("id");
                result.add(rec);
                all.put(rec.id, rec);
            }
        });
        return result;
    }

    public static ObservableList<Recipe> getAll() {
        return FXCollections.observableArrayList(all.values());
    }
}
