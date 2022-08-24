import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.geometry.Pos;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;

public class SavingsCalculator extends Application {
    public static void main(String[] args) {
        launch(SavingsCalculator.class);
    }

    @Override
    public void start(javafx.stage.Stage window) {
        // 1. Main User Interface
        BorderPane mainLayout = new BorderPane();

        // 1.1 Dashboard
        VBox dashboard = new VBox();
        dashboard.setAlignment(Pos.CENTER);
        dashboard.setSpacing(10);

        // 1.1.1 Monthly savings
        BorderPane monthlySavings = new BorderPane();
        monthlySavings.setLeft(new Label("Monthly Savings"));
        Slider savingSlider = new Slider(25, 250, 50);
        savingSlider.setShowTickLabels(true);
        savingSlider.setShowTickMarks(true);
        savingSlider.setMajorTickUnit(25);
        savingSlider.setBlockIncrement(25);
        monthlySavings.setCenter(savingSlider);
        monthlySavings.setRight(new Label(savingSlider.getValue() + ""));

        // 1.1.2 Yearly interest rate
        BorderPane yearlyInterestRate = new BorderPane();
        yearlyInterestRate.setLeft(new Label("Yearly Interest Rate"));
        Slider interestSlider = new Slider(0, 10, 0);
        interestSlider.setShowTickLabels(true);
        interestSlider.setShowTickMarks(true);
        yearlyInterestRate.setCenter(interestSlider);
        yearlyInterestRate.setRight(new Label(interestSlider.getValue() + ""));

        dashboard.getChildren().addAll(monthlySavings, yearlyInterestRate);

        // 1.2 Line Chart
        // 1.2.1 Define axes
        NumberAxis xAxis = new NumberAxis(0, 30, 1);
        NumberAxis yAxis = new NumberAxis(0, 125000, 25000);

        LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis);
        lineChart.setTitle("Savings");
        // Generate default data series based on default monthly savings
        XYChart.Series series = new XYChart.Series();
        for(int i = 0; i <= 30; i++) {
            double monthlySavingsInYears = savingSlider.getValue() * 12;
            series.getData().add(new XYChart.Data(i, monthlySavingsInYears * i));
        }
        lineChart.getData().add(series);

        // 2. Event listeners for sliders
        // 2.1 Saving Slider
        // Only gets value after the drag is completed: https://techhelpnotes.com/graph-javafx-slider-how-to-only-change-value-when-dragging-is-done-while-also-maintaining-keyboard-touch-support-3/
        savingSlider.valueChangingProperty().addListener(((observable, wasChanging, isNowChanging) -> {
            if(!isNowChanging) {
                // Get the final value
                double value = savingSlider.getValue();
                // Generate new data series for line chart
                XYChart.Series newSeries = new XYChart.Series();

                // calc compounded interest savings for 30 years
                double interest = interestSlider.getValue();
                double monthlySavingsInYears = value * 12;
                Double[] compounds = new Double[30];
                compounds[0] = monthlySavingsInYears * 1.05;
                newSeries.getData().add(new XYChart.Data(0, compounds[0]));
                for(int i = 1; i <= compounds.length; i++) {
                    if(i <= compounds.length - 1) {
                        compounds[i] = (compounds[i - 1] + monthlySavingsInYears) * (1 + interest);
                    }
                    newSeries.getData().add(new XYChart.Data(i, compounds[i - 1]));
                }

                lineChart.getData().add(newSeries);
            }
        }));

        // 2.2 Interest Slider
        interestSlider.valueChangingProperty().addListener((observable, isChanging, isNowChanging) -> {
            if(!isNowChanging) {
                // Get the final value
                double interest = interestSlider.getValue();
                XYChart.Series newSeries = new XYChart.Series();

                // Generate new data series based on new interest rate
                // calc compounded interest savings for 30 years
                double monthlySavingsInYears = savingSlider.getValue() * 12;
                Double[] compounds = new Double[30];
                compounds[0] = monthlySavingsInYears * 1.05;
                newSeries.getData().add(new XYChart.Data(0, compounds[0]));
                for(int i = 1; i <= compounds.length; i++) {
                    if(i <= compounds.length - 1) {
                        compounds[i] = (compounds[i - 1] + monthlySavingsInYears) * (1 + interest);
                    }
                    newSeries.getData().add(new XYChart.Data(i, compounds[i - 1]));
                }

                lineChart.getData().add(newSeries);
            }
        });

        // Add components to main layout
        mainLayout.setTop(dashboard);
        mainLayout.setCenter(lineChart);
        mainLayout.setPadding(new javafx.geometry.Insets(20, 20, 20, 20));

        Scene view = new Scene(mainLayout, 640, 480);
        window.setScene(view);
        window.show();
    }
}
