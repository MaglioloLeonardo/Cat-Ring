package App.BusinessLogic.MenuManagement;

import App.BusinessLogic.CatERing;
import App.BusinessLogic.UserManagement.UseCaseLogicException;
import App.BusinessLogic.RecipeManagement.Recipe;
import App.BusinessLogic.UserManagement.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class MenuManager {
    private String[] menuFeatures = {"Richiede cucina", "Richiede cuoco", "Finger food", "Buffet", "Piatti caldi"};
    private Menu currentMenu;
    private ArrayList<MenuEventReceiver> eventReceivers;

    public MenuManager() {
        eventReceivers = new ArrayList<>();
    }

    public Menu createMenu() throws UseCaseLogicException {
        return this.createMenu(null);
    }

    public Menu createMenu(String title) throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();

        if (!user.isChef()) {
            throw new UseCaseLogicException();
        }

        Menu m = new Menu(user, title, menuFeatures);
        this.setCurrentMenu(m);
        this.notifyMenuAdded(m);

        return m;
    }

    public Section defineSection(String name) throws UseCaseLogicException {
        if (currentMenu ==  null) {
            throw new UseCaseLogicException();
        }

        Section sec = this.currentMenu.addSection(name);

        this.notifySectionAdded(this.currentMenu, sec);

        return sec;
    }

    public MenuItem insertItem(Recipe recipe, Section sec, String desc) throws UseCaseLogicException {
        if (this.currentMenu == null) throw new UseCaseLogicException();
        if (sec != null && this.currentMenu.getSectionPosition(sec) < 0) throw new UseCaseLogicException();
        MenuItem mi = this.currentMenu.addItem(recipe, sec, desc);
        this.notifyMenuItemAdded(mi);
        return mi;
    }

    public MenuItem insertItem(Recipe recipe, Section sec) throws UseCaseLogicException {
        return this.insertItem(recipe, sec, recipe.getName());
    }

    public MenuItem insertItem(Recipe rec) throws UseCaseLogicException {
        return this.insertItem(rec, null, rec.getName());
    }

    public MenuItem insertItem(Recipe rec, String desc) throws UseCaseLogicException {
        return this.insertItem(rec, null, desc);
    }

    public void setAdditionalFeatures(String[] features, boolean[] values) throws UseCaseLogicException {
        if (this.currentMenu == null) throw new UseCaseLogicException();
        if (features.length != values.length) throw new UseCaseLogicException();
        for (int i = 0; i < features.length; i++) {
            this.currentMenu.setFeatureValue(features[i], values[i]);
        }
        this.notifyMenuFeaturesChanged();
    }

    public void changeTitle(String title) throws UseCaseLogicException {
        if (currentMenu == null) throw new UseCaseLogicException();
        currentMenu.setTitle(title);
        this.notifyMenuTitleChanged();
    }

    public void publish() throws UseCaseLogicException {
        if (currentMenu == null) throw new UseCaseLogicException();
        currentMenu.setPublished(true);
        this.notifyMenuPublishedState();
    }

    private void notifyMenuPublishedState() {
        for (MenuEventReceiver er: this.eventReceivers) {
            er.updateMenuPublishedState(this.currentMenu);
        }
    }

    private void notifyMenuTitleChanged() {
        for (MenuEventReceiver er: this.eventReceivers) {
            er.updateMenuTitleChanged(this.currentMenu);
        }
    }

    private void notifyMenuFeaturesChanged() {
        for (MenuEventReceiver er: this.eventReceivers) {
            er.updateMenuFeaturesChanged(this.currentMenu);
        }
    }

    private void notifyMenuItemAdded(MenuItem mi) {
        for (MenuEventReceiver er: this.eventReceivers) {
            er.updateMenuItemAdded(this.currentMenu, mi);
        }
    }

    private void notifySectionAdded(Menu m, Section sec) {
        for (MenuEventReceiver er: this.eventReceivers) {
            er.updateSectionAdded(m, sec);
        }
    }

    private void notifyMenuAdded(Menu m) {
        for (MenuEventReceiver er: this.eventReceivers) {
            er.updateMenuCreated(m);
        }
    }

    public void setCurrentMenu(Menu m) {
        this.currentMenu = m;
    }

    public Menu getCurrentMenu() {
        return this.currentMenu;
    }

    public ObservableList<Menu> getAllMenus() {
        // TODO: implementazione fittizia
        return FXCollections.observableArrayList();
    }

    public void addEventReceiver(MenuEventReceiver rec) {
        this.eventReceivers.add(rec);
    }

    public void removeEventReceiver(MenuEventReceiver rec) {
        this.eventReceivers.remove(rec);
    }
}
