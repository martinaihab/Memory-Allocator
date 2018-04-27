package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.collections.ObservableList;

import java.util.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import sample.CPUProcess;
import sample.Hole;
import sample.GanttChart.ExtraData;
import sun.plugin2.gluegen.runtime.CPU;

import static java.lang.Integer.parseInt;

public class Controller implements Initializable {

    int MemorySize = 12000;
    int NumberOfProcesses = 0;
    int NumberOfHoles = 0;

    public GanttChart<Number, String> chart;
    public NumberAxis xAxis;
    public CategoryAxis yAxis;

    ArrayList<CPUProcess> Processes = new ArrayList<>();
    ArrayList<Hole> Holes = new ArrayList<>();
    String[] colors = new String[]{"status-red", "status-green", "status-blue", "status-tomato",
            "status-violet", "status-purple", "status-yellow", "status-brown", "status-black","status-purple2"};

    @FXML VBox RootVBox;
    @FXML HBox ChartHBox;

    @FXML Button InputButton,ProcessAllocateButton,ProcessDeallocateButton,HoleAllocateButton;

    @FXML TableView ProcessesTable,HolesTable;
    @FXML TableColumn<CPUProcess, Integer> ProcessIdColumn,ProcessSizeColumn,ProcessTypeOfAllocColumn;
    @FXML TableColumn <CPUProcess, Integer> HoleStartAddressColumn, HoleSizeColumn;

    @FXML TextField ProcessSizeTextField,HoleStartAddressTextField,HoleSizeTextField;
    @FXML TextField MemorySizeTextField,NumberOfProcessesTextField,NumberOfHolesTextField;

    @FXML ChoiceBox TypeOfAllocChoiceBox;

    public void initialize(URL location, ResourceBundle resources){
        initializeInputButton();
        initializeProcessAllocateButton();
        initializeHoleAllocateButton();
        initializeTypeOfAllocChoiceBox();
        initializeProcessesTable();
        initializeGanttChart();
        buildGanttChart();
    }

    public void initializeInputButton(){
        InputButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    MemorySize = parseInt(MemorySizeTextField.getText());
                    NumberOfProcesses = parseInt(NumberOfProcessesTextField.getText());
                    NumberOfHoles = parseInt(NumberOfHolesTextField.getText());
                }
                catch (NumberFormatException e){
                    System.out.println("Must Enter All Three Input Data.");
                }
            }
        });
    }

    public void initializeHoleAllocateButton(){
        HoleAllocateButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Hole hole = new Hole();

                try {
                    ///ProcessID
                    hole.setSize(parseInt(HoleSizeTextField.getText()));
                    hole.setStartAddress(parseInt(HoleStartAddressTextField.getText()));
                }
                catch (NumberFormatException e){
                    System.out.println("Must Enter All Hole Data.");
                }
            }
        });

    }
    public void initializeProcessAllocateButton(){
        ProcessAllocateButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                CPUProcess process = new CPUProcess();

                try {
                    ///ProcessID
                    process.setSize(parseInt(ProcessSizeTextField.getText()));
                    process.setTypeOfAlloc(TypeOfAllocChoiceBox.getValue().toString());
                }
                catch (NumberFormatException e){
                    System.out.println("Must Enter All Process Data.");
                }

                Processes.add(process);

            }
        });

    }

    public void initializeTypeOfAllocChoiceBox(){
        TypeOfAllocChoiceBox.getItems().addAll("First-Fit", "Best-Fit", "Worst-Fit");
        TypeOfAllocChoiceBox.setValue("First-Fit");
    }
    public void initializeProcessesTable(){

        //table.setItems(getProcesses());

        ProcessIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        ProcessSizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
        ProcessTypeOfAllocColumn.setCellValueFactory(new PropertyValueFactory<>("typeOfAlloc"));

    }
    private void buildGanttChart() {
        chart.getData().clear();
        ChartHBox.setMaxWidth(RootVBox.getWidth() - 300);
        ChartHBox.setMinWidth(RootVBox.getWidth() - 300);
        chart.setMinWidth(RootVBox.getWidth() - 300);
        chart.setMaxWidth(RootVBox.getWidth());

        XYChart.Series series = new XYChart.Series();
        xAxis.setUpperBound(100);

        series.getData().add(new XYChart.Data(0, "",
                new ExtraData((int) 50, colors[0])));
        chart.getData().add(series);
    }
    private void initializeGanttChart() {


        xAxis = new NumberAxis();
        yAxis = new CategoryAxis();

        chart = new GanttChart<Number, String>(xAxis, yAxis);
        xAxis.setLabel("");
        xAxis.setTickLabelFill(Color.CHOCOLATE);
        xAxis.setMinorTickCount(1);

        xAxis.setAutoRanging(false);

        yAxis.setLabel("");
        yAxis.setTickLabelFill(Color.CHOCOLATE);
        yAxis.setTickLabelGap(10);

        //chart.setTitle("Machine Monitoring");
        chart.setLegendVisible(false);
        chart.setBlockHeight(50);


        URL url = this.getClass().getResource("ganttchart.css");
        if (url == null) {
            System.out.println("Resource not found. Aborting.");
            System.exit(-1);
        }
        String css = url.toExternalForm();

        chart.setBlockHeight(50);
        chart.getStylesheets().add(css);
        chart.setAnimated(true);

        ChartHBox.getChildren().add(chart);

        ChartHBox.setMaxWidth(700);
        ChartHBox.setMaxWidth(RootVBox.getMaxWidth());
        chart.setMinWidth(700);
        chart.setMaxWidth(RootVBox.getMaxWidth());
        chart.setLegendVisible(false);
    }

}
