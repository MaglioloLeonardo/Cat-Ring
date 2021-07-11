package App.BusinessLogic.MenuManagement;

public interface MenuEventReceiver {
    public void updateMenuCreated(Menu m);
    public void updateSectionAdded(Menu m, Section sec);
    public void updateMenuItemAdded(Menu m, MenuItem mi);
    public void updateMenuFeaturesChanged(Menu m);
    public void updateMenuTitleChanged(Menu currentMenu);
    public void updateMenuPublishedState(Menu currentMenu);
}
