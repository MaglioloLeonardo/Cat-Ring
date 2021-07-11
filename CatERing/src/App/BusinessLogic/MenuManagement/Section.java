package App.BusinessLogic.MenuManagement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import App.persistence.BatchUpdateHandler;
import App.persistence.PersistenceManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Section {
    private int id;
    private String name;
    private ObservableList<MenuItem> sectionItems;

    public Section(String name) {
        id = 0;
        this.name = name;
        sectionItems = FXCollections.observableArrayList();
    }


    public void addItem(MenuItem mi) {
        this.sectionItems.add(mi);
    }


    public int getItemPosition(MenuItem mi) {
        return this.sectionItems.indexOf(mi);
    }

    public int getId() {
        return id;
    }

    public String testString() {
        String result = name + "\n";
        for (MenuItem mi: sectionItems) {
            result += "\t" + mi.toString() + "\n";
        }
        return result;
    }

    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public ObservableList<MenuItem> getItems() {
        return FXCollections.unmodifiableObservableList(this.sectionItems);
    }

    // STATIC METHODS FOR PERSISTENCE

    public static void saveNewSection(int menuid, Section sec, int posInMenu) {
        String secInsert = "INSERT INTO catering.MenuSections (menu_id, name, position) VALUES (" +
                menuid + ", " +
                "'" + PersistenceManager.escapeString(sec.name) + "', " +
                posInMenu +
                ");";
        PersistenceManager.executeUpdate(secInsert);
        sec.id = PersistenceManager.getLastId();

        if (sec.sectionItems.size() > 0) {
            MenuItem.saveAllNewItems(menuid, sec.id, sec.sectionItems);
        }
    }

    public static void saveAllNewSections(int menuid, List<Section> sections) {
        String secInsert = "INSERT INTO catering.MenuSections (menu_id, name, position) VALUES (?, ?, ?);";
        PersistenceManager.executeBatchUpdate(secInsert, sections.size(), new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, menuid);
                ps.setString(2, PersistenceManager.escapeString(sections.get(batchCount).name));
                ps.setInt(3, batchCount);
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                sections.get(count).id = rs.getInt(1);
            }
        });

        // salva le voci delle sezioni
        for (Section s: sections) {
            if (s.sectionItems.size() > 0) {
                MenuItem.saveAllNewItems(menuid, s.id, s.sectionItems);
            }
        }
    }
}
