package App.BusinessLogic.WorkshiftsManagement;

import java.util.List;

public class TimeTable {
    private static TimeTable currentInstance;
    public static TimeTable getInstance(){return currentInstance;}


    private List<WorkShift> workShifts;
    private TimeTable() {

    }
    public List<WorkShift> getWorkShifts(){return workShifts;}
}
