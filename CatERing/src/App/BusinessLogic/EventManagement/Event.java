package App.BusinessLogic.EventManagement;

import App.BusinessLogic.WorkingStaffManagement.Chef;

public class Event {
    private String informations;
    private boolean isRecurrent;
    private int recursion;
    private Chef chef;

    public Chef getChef() {
        return chef;
    }

    public void setChef(Chef chef) {
        this.chef = chef;
    }

}
