package App.BusinessLogic.MenuManagement;

import App.BusinessLogic.RecipeManagement.Recipe;
import App.persistence.BatchUpdateHandler;
import App.persistence.PersistenceManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class MenuItem {
    private int id;
    private String description;
    private Recipe itemRecipe;

    public MenuItem(Recipe rec) {
        this(rec, rec.getName());
    }

    public MenuItem(Recipe rec, String desc) {
        id = 0;
        itemRecipe = rec;
        description = desc;
    }

    public String toString() {
        return description;
    }

    // STATIC METHODS FOR PERSISTENCE

    public static void saveAllNewItems(int menuid, int sectionid, List<MenuItem> items) {
        String itemInsert = "INSERT INTO catering.MenuItems (menu_id, section_id, description, recipe_id, position) VALUES (?, ?, ?, ?, ?);";
        PersistenceManager.executeBatchUpdate(itemInsert, items.size(), new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, menuid);
                ps.setInt(2, sectionid);
                ps.setString(3, PersistenceManager.escapeString(items.get(batchCount).description));
                ps.setInt(4, items.get(batchCount).itemRecipe.getId());
                ps.setInt(5, batchCount);
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                items.get(count).id = rs.getInt(1);
            }
        });
    }

    public static void saveNewItem(int menuid, int sectionid, MenuItem mi, int pos) {
        String itemInsert = "INSERT INTO catering.MenuItems (menu_id, section_id, description, recipe_id, position) VALUES (" +
                menuid +
                ", " +
                sectionid +
                ", " +
                "'" + PersistenceManager.escapeString(mi.description) + "', " +
                + mi.itemRecipe.getId() + ", " +
                + pos + ");";
        PersistenceManager.executeUpdate(itemInsert);

        mi.id = PersistenceManager.getLastId();
    }
}
