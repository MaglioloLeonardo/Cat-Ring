package App.BusinessLogic.EventManagement;

import App.BusinessLogic.KitchenTasksManagement.SummarySheet;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

public class Service {
    private LocalDate day;
    private LocalTime start, end;
    private SummarySheet summarySheet;
    private Event event;

    public int duration(){
     return Math.toIntExact(Duration.between(start, end).getSeconds() / 60);
    }

    public void setSummarySheet(SummarySheet sheet){
        summarySheet = sheet;
    }

    public SummarySheet getSummarySheet(){
        return summarySheet;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

}
