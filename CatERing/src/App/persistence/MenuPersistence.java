package App.persistence;

import App.BusinessLogic.MenuManagement.Menu;
import App.BusinessLogic.MenuManagement.MenuEventReceiver;
import App.BusinessLogic.MenuManagement.MenuItem;
import App.BusinessLogic.MenuManagement.Section;

public class MenuPersistence implements MenuEventReceiver {

    @Override
    public void updateMenuCreated(Menu m) {
        Menu.saveNewMenu(m);
    }

    @Override
    public void updateSectionAdded(Menu m, Section sec) {
        Section.saveNewSection(m.getId(), sec, m.getSectionPosition(sec));
    }

    @Override
    public void updateMenuItemAdded(Menu m, MenuItem mi) {
        Section sec = m.getSectionForItem(mi);
        int sec_id = (sec == null ? 0 : sec.getId());
        int pos = (sec == null? m.getFreeItemPosition(mi) : sec.getItemPosition(mi));
        MenuItem.saveNewItem(m.getId(), sec_id, mi, pos);
    }

    @Override
    public void updateMenuFeaturesChanged(Menu m) {
        Menu.saveMenuFeatures(m);
    }

    @Override
    public void updateMenuTitleChanged(Menu currentMenu) {
        Menu.saveMenuTitle(currentMenu);
    }

    @Override
    public void updateMenuPublishedState(Menu currentMenu) {
        Menu.saveMenuPublished(currentMenu);
    }
}
