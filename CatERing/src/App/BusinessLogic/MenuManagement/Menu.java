package App.BusinessLogic.MenuManagement;

import App.BusinessLogic.CatERing;
import App.BusinessLogic.RecipeManagement.Recipe;
import App.BusinessLogic.UserManagement.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import App.persistence.BatchUpdateHandler;
import App.persistence.PersistenceManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Menu {
    private int id;
    private String title;
    private boolean published;
    private boolean inUse;

    private ObservableMap<String, Boolean> featuresMap;
    private ObservableList<MenuItem> freeItems;
    private ObservableList<Section> sections;

    private User owner;

    public Menu(User user, String title, String[] menuFeatures) {
        id = 0;

        if (title != null) {
            this.title = title;
        }

        this.owner = user;

        this.featuresMap = FXCollections.observableHashMap();


        for (String s : menuFeatures) {
            this.featuresMap.put(s, false);
        }

        this.sections = FXCollections.observableArrayList();
        this.freeItems = FXCollections.observableArrayList();

    }

    public boolean getFeatureValue(String feature) {
        return this.featuresMap.get(feature);
    }

    public void setFeatureValue(String feature, boolean val) {
        if (this.featuresMap.containsKey(feature)) {
            this.featuresMap.put(feature, val);
        }
    }

    public String testString() {
        String result = this.toString() +  "\n";
        for (String f : featuresMap.keySet()) {
            result += f + ": " + featuresMap.get(f) + "\n";
        }

        result += "\n";
        for (Section sec : sections) {
            result += sec.testString();
            result += "\n";
        }

        if (freeItems.size() > 0) {
            result += "\n" + "VOCI LIBERE:\n";
        }
        for (MenuItem mi : freeItems) {
            result += "\t" + mi.toString() + "\n";
        }

        return result;
    }

    public String toString() {
        return title + " (autore: " + owner.getUserName() + ")," + (published ? " " : " non ") +
                "pubblicato," + (inUse ? " " : " non ") + "in uso";
    }


    public int getId() {
        return id;
    }

    public String getTitle() {
        return this.title;
    }

    public void addFakeSections() {
        this.sections.add(new Section("Antipasti"));
        this.sections.add(new Section("Primi"));
        this.sections.add(new Section("Secondi"));
        this.sections.add(new Section("Dessert"));

        Recipe[] all = CatERing.getInstance().getRecipeManager().getRecipes().toArray(new Recipe[0]);
        freeItems.add(new MenuItem(all[3]));
        freeItems.add(new MenuItem(all[4]));
        freeItems.add(new MenuItem(all[5]));
    }


    public Section addSection(String name) {
        Section sec = new Section(name);
        this.sections.add(sec);
        return sec;
    }

    public MenuItem addItem(Recipe recipe, Section sec, String desc) {
        MenuItem mi = new MenuItem(recipe, desc);
        if (sec != null) {
            sec.addItem(mi);
        } else {
            this.freeItems.add(mi);
        }
        return mi;
    }

    public int getSectionPosition(Section sec) {
        return this.sections.indexOf(sec);
    }

    public ObservableList<Section> getSections() {
        return FXCollections.unmodifiableObservableList(this.sections);
    }

    public Section getSectionForItem(MenuItem mi) {
        for (Section sec : sections) {
            if (sec.getItemPosition(mi) >= 0)
                return sec;
        }
        if (freeItems.indexOf(mi) >= 0) return null;
        throw new IllegalArgumentException();
    }

    public int getFreeItemPosition(MenuItem mi) {
        return freeItems.indexOf(mi);
    }

    public ObservableList<MenuItem> getFreeItems() {
        return FXCollections.unmodifiableObservableList(this.freeItems);
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public void setPublished(boolean b) {
        published = b;
    }


    // STATIC METHODS FOR PERSISTENCE

    public static void saveNewMenu(Menu m) {
        String menuInsert = "INSERT INTO catering.Menus (title, owner_id, published) VALUES (?, ?, ?);";
        int[] result = PersistenceManager.executeBatchUpdate(menuInsert, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setString(1, PersistenceManager.escapeString(m.title));
                ps.setInt(2, m.owner.getId());
                ps.setBoolean(3, m.published);
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // should be only one
                if (count == 0) {
                    m.id = rs.getInt(1);
                }
            }
        });

        if (result[0] > 0) { // menu effettivamente inserito
            // salva le features
            featuresToDB(m);

            // salva le sezioni
            if (m.sections.size() > 0) {
                Section.saveAllNewSections(m.id, m.sections);
            }

            // salva le voci libere
            if (m.freeItems.size() > 0) {
                MenuItem.saveAllNewItems(m.id, 0, m.freeItems);
            }
        }
    }

    public static void saveMenuTitle(Menu m) {
        String upd = "UPDATE Menus SET title = '" + PersistenceManager.escapeString(m.getTitle()) + "'" +
                " WHERE id = " + m.getId();
        PersistenceManager.executeUpdate(upd);
    }

    public static void saveMenuFeatures(Menu m) {
        // Delete existing features if any
        String updDel = "DELETE FROM MenuFeatures WHERE menu_id = " + m.getId();
        int ret = PersistenceManager.executeUpdate(updDel);

        featuresToDB(m);
    }


    public static void saveMenuPublished(Menu m) {
        String upd = "UPDATE Menus SET published = " + m.published +
                " WHERE id = " + m.getId();
        PersistenceManager.executeUpdate(upd);
    }

    private static void featuresToDB(Menu m) {
        // Save features
        String featureInsert = "INSERT INTO catering.MenuFeatures (menu_id, name, value) VALUES (?, ?, ?)";
        String[] features = m.featuresMap.keySet().toArray(new String[0]);
        PersistenceManager.executeBatchUpdate(featureInsert, features.length, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, m.id);
                ps.setString(2, PersistenceManager.escapeString(features[batchCount]));
                ps.setBoolean(3, m.featuresMap.get(features[batchCount]));
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // non ci sono id autogenerati in MenuFeatures
            }
        });
    }

    public ObservableMap<String, Boolean> getFeatures() {
        return FXCollections.unmodifiableObservableMap(this.featuresMap);
    }
}
