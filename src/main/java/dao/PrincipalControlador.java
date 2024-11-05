package dao;

	import dto.Actividades;
	import javafx.event.ActionEvent;
	import javafx.fxml.FXML;
	import javafx.fxml.Initializable;
	import javafx.scene.layout.FlowPane;
	import javafx.scene.layout.StackPane;
	import javafx.scene.paint.Color;
	import javafx.scene.shape.Rectangle;
	import javafx.scene.text.Text;

	import java.net.URL;
	import java.time.ZonedDateTime;
	import java.util.*;

public class PrincipalControlador {
	public class CalendarController implements Initializable {

	    ZonedDateTime dateFocus;
	    ZonedDateTime today;

	    @FXML
	    private Text year;

	    @FXML
	    private Text month;

	    @FXML
	    private FlowPane calendar;

	    @Override
	    public void initialize(URL url, ResourceBundle resourceBundle) {
	        dateFocus = ZonedDateTime.now();
	        today = ZonedDateTime.now();
	        drawCalendar();
	    }

	    @FXML
	    void backOneMonth(ActionEvent event) {
	        dateFocus = dateFocus.minusMonths(1);
	        calendar.getChildren().clear();
	        drawCalendar();
	    }

	    @FXML
	    void forwardOneMonth(ActionEvent event) {
	        dateFocus = dateFocus.plusMonths(1);
	        calendar.getChildren().clear();
	        drawCalendar();
	    }

	    private void drawCalendar() {
	        year.setText(String.valueOf(dateFocus.getYear()));
	        month.setText(String.valueOf(dateFocus.getMonth()));

	        double calendarWidth = calendar.getPrefWidth();
	        double calendarHeight = calendar.getPrefHeight();
	        double strokeWidth = 1;
	        double spacingH = calendar.getHgap();
	        double spacingV = calendar.getVgap();

	        Map<Integer, List<Actividades>> calendarActivityMap = getCalendarActivitiesMonth(dateFocus);

	        int monthMaxDate = dateFocus.getMonth().maxLength();
	        if (dateFocus.getYear() % 4 != 0 && monthMaxDate == 29) {
	            monthMaxDate = 28;
	        }

	        int dateOffset = ZonedDateTime.of(dateFocus.getYear(), dateFocus.getMonthValue(), 1, 0, 0, 0, 0, dateFocus.getZone()).getDayOfWeek().getValue();

	        for (int i = 0; i < 6; i++) {
	            for (int j = 0; j < 7; j++) {
	                StackPane stackPane = new StackPane();
	                Rectangle rectangle = new Rectangle();
	                rectangle.setFill(Color.TRANSPARENT);
	                rectangle.setStroke(Color.BLACK);
	                rectangle.setStrokeWidth(strokeWidth);
	                rectangle.setWidth((calendarWidth / 7) - strokeWidth - spacingH);
	                rectangle.setHeight((calendarHeight / 6) - strokeWidth - spacingV);
	                stackPane.getChildren().add(rectangle);

	                int calculatedDate = (j + 1) + (7 * i);
	                if (calculatedDate > dateOffset) {
	                    int currentDate = calculatedDate - dateOffset;
	                    if (currentDate <= monthMaxDate) {
	                        Text date = new Text(String.valueOf(currentDate));
	                        date.setTranslateY(-(rectangle.getHeight() / 2) * 0.75);
	                        stackPane.getChildren().add(date);

	                        List<Actividades> calendarActivities = calendarActivityMap.get(currentDate);
	                        if (calendarActivities != null) {
	                            createCalendarActivity(calendarActivities, rectangle.getHeight(), rectangle.getWidth(), stackPane);
	                        }

	                        if (today.getYear() == dateFocus.getYear() && today.getMonth() == dateFocus.getMonth() && today.getDayOfMonth() == currentDate) {
	                            rectangle.setStroke(Color.BLUE);
	                        }
	                    }
	                }
	                calendar.getChildren().add(stackPane);
	            }
	        }
	    }

	    private void createCalendarActivity(List<Actividades> calendarActivities, double rectangleHeight, double rectangleWidth, StackPane stackPane) {
	        // Lógica para añadir actividades al calendario en la fecha específica.
	    }

	    private Map<Integer, List<Actividades>> getCalendarActivitiesMonth(ZonedDateTime dateFocus) {
	        List<Actividades> calendarActivities = new ArrayList<>();
	        int year = dateFocus.getYear();
	        int month = dateFocus.getMonth().getValue();

	        Random random = new Random();
	        for (int i = 0; i < 50; i++) {
	            ZonedDateTime time = ZonedDateTime.of(year, month, random.nextInt(27) + 1, 16, 0, 0, 0, dateFocus.getZone());
	            calendarActivities.add(new Actividades(time, "Actividad " + i, i));
	        }

	        return createCalendarMap(calendarActivities);
	    }

	    private Map<Integer, List<Actividades>> createCalendarMap(List<Actividades> calendarActivities) {
	        Map<Integer, List<Actividades>> calendarActivityMap = new HashMap<>();
	        for (Actividades activity : calendarActivities) {
	            int activityDate = activity.getDate().getDayOfMonth();
	            calendarActivityMap.computeIfAbsent(activityDate, k -> new ArrayList<>()).add(activity);
	        }
	        return calendarActivityMap;
	    }
	}
}
