package sample;

import javafx.collections.FXCollections;
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

    int MemorySize = 0;
    int NumberOfProcesses = 0;
    int NumberOfHoles = 0;
    int ProcessId = 0;

    public GanttChart<Number, String> chart;
    public NumberAxis xAxis;
    public CategoryAxis yAxis;

    ArrayList<CPUProcess> Processes = new ArrayList<>();
    ArrayList<Hole> Holes = new ArrayList<>();
    String[] colors = new String[]{"status-yellow", "status-aqua",
            "status-orange", "status-deeppink","status-teal",
            "status-purple", "status-green", "status-deepskyblue",
            "status-tomato", "status-violet"};

    CPUProcess A = new CPUProcess(0,120,"First-Fit",60);
    CPUProcess B = new CPUProcess(1,120,"First-Fit",200);
    CPUProcess C = new CPUProcess(2,120,"First-Fit",380);

    Hole AA = new Hole (0,180,10);
    Hole BB = new Hole (1,320,30);
    Hole CC = new Hole (2,360,15);



    @FXML VBox RootVBox;
    @FXML HBox ChartHBox;

    @FXML Button InputButton,ProcessAllocateButton,ProcessDeallocateButton,HoleAllocateButton;

    @FXML TableView ProcessesTable,HolesTable;
    @FXML TableColumn<CPUProcess, Integer> ProcessIdColumn,ProcessSizeColumn,ProcessTypeOfAllocColumn;
    @FXML TableColumn <CPUProcess, Integer> HoleStartAddressColumn, HoleSizeColumn;

    @FXML TextField ProcessSizeTextField,HoleStartAddressTextField,HoleSizeTextField;
    @FXML TextField MemorySizeTextField;
    @FXML TextField ProcessAddTextField;

    @FXML ChoiceBox TypeOfAllocChoiceBox;

    public void initialize(URL location, ResourceBundle resources){

        //Processes.add(A);
        //Processes.add(B);
        //Processes.add(C);
        //Holes.add(AA);
        //Holes.add(BB);
        //Holes.add(CC);

        initializeInputButton();
        initializeProcessAllocateButton();
        initializeProcessDeallocateButton();
        initializeHoleAllocateButton();
        initializeTypeOfAllocChoiceBox();
        initializeProcessesTable();
        initializeHolesTable();
        initializeGanttChart();
        buildGanttChart();
    }

    public void initializeInputButton(){
        InputButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    MemorySize = parseInt(MemorySizeTextField.getText());
                    buildGanttChart();
                }
                catch (NumberFormatException e){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Invalid Memory Size");
                    alert.setContentText("Please make sure to enter a valid memory size.");
                    alert.showAndWait();
                }
            }
        });
    }

    public void initializeHoleAllocateButton(){
        HoleAllocateButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Hole hole = new Hole(NumberOfHoles);

                try {
                    hole.setSize(parseInt(HoleSizeTextField.getText()));
                    hole.setStartAddress(parseInt(HoleStartAddressTextField.getText()));
                    Holes.add(hole);
                    executeCompaction();
                    buildGanttChart();
                    NumberOfHoles++;
                }
                catch (NumberFormatException e){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Missing Hole Data");
                    alert.setContentText("Please make sure to enter all hole data.");

                    alert.showAndWait();
                }
                HoleSizeTextField.clear();
                HoleStartAddressTextField.clear();
            }
        });

    }
    public void initializeProcessAllocateButton(){
        ProcessAllocateButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                CPUProcess process = new CPUProcess();

                try {
                    process.setId(ProcessId);
                    process.setSize(parseInt(ProcessSizeTextField.getText()));
                    process.setTypeOfAlloc(TypeOfAllocChoiceBox.getValue().toString());
                    ProcessId++;
                    NumberOfProcesses++;

                    process.setStartAddress(parseInt(ProcessAddTextField.getText()));//////////////////
                    ProcessesTable.getItems().add(process);
                    Processes.add(process);
                    buildGanttChart();
                }
                catch (NumberFormatException e){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Missing Process Data");
                    alert.setContentText("Please make sure to enter all process data.");

                    alert.showAndWait();
                }
                ProcessSizeTextField.clear();
            }
        });

    }
    public void initializeProcessDeallocateButton(){
        ProcessDeallocateButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ObservableList<CPUProcess> processSelected;//, allProcesses;
                //allProcesses = ProcessesTable.getItems();
                processSelected = ProcessesTable.getSelectionModel().getSelectedItems();
                Hole hole = new Hole (NumberOfHoles, processSelected.get(0).getStartAddress(),processSelected.get(0).getSize());
                //processSelected.forEach(allProcesses::remove);
                processSelected.forEach(Processes::remove);
                //if(NumberOfProcesses==0){Processes=new ArrayList<>(); ProcessesTable=new TableView();}
                ProcessesTable.getItems().clear();
                ProcessesTable.getItems().addAll(Processes);
                Holes.add(hole);
                NumberOfHoles++;
                executeCompaction();
                NumberOfProcesses--;////////////////////////////////////
                chart.getData().clear();
                buildGanttChart();
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

        //ProcessesTable.getItems().addAll(Processes);//
        /*ObservableList<CPUProcess> observableArrayList =
                FXCollections.observableArrayList(Processes);
        ProcessesTable.setItems(observableArrayList);*/
    }
    public void initializeHolesTable(){

        //table.setItems(getProcesses());

        HoleSizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
        HoleStartAddressColumn.setCellValueFactory(new PropertyValueFactory<>("startAddress"));

        //HolesTable.getItems().addAll(Holes);
        /*ObservableList<Hole> observableArrayList =
                FXCollections.observableArrayList(Holes);
        HolesTable.setItems(observableArrayList);*/

    }
    private void buildGanttChart() {
        chart.getData().clear();
        //ChartHBox.setMaxWidth(RootVBox.getWidth() - 300);
        //ChartHBox.setMinWidth(RootVBox.getWidth() - 300);
        //chart.setMinWidth(RootVBox.getWidth() - 300);
        //chart.setMaxWidth(RootVBox.getWidth());

        XYChart.Series series = new XYChart.Series();
        xAxis.setUpperBound(MemorySize);

        if(NumberOfProcesses==0) {
            chart.getData().clear();
            //Processes = new ArrayList<>();
            series.getData().add(new XYChart.Data(0, "",
                    new ExtraData((int) MemorySize, "status-gray")));
        }
        else {
            series.getData().add(new XYChart.Data(0, "",
                    new ExtraData((int) MemorySize, "status-gray")));
            for (int i = 0; i < Processes.size(); i++) {
                series.getData().add(new XYChart.Data(Processes.get(i).getStartAddress(), "",
                        new ExtraData((int) Processes.get(i).getSize(), colors[Processes.get(i).getId() % 10])));
            }
        }
        for (int i = 0; i < Holes.size(); i++) {
            series.getData().add(new XYChart.Data(Holes.get(i).getStartAddress(), "",
                    new ExtraData((int) Holes.get(i).getSize(), "status-lavender")));
        }
        //else chart.getData().clear();
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
        //ChartHBox.setMaxWidth(RootVBox.getMaxWidth());
        chart.setMinWidth(700);
        //chart.setMaxWidth(RootVBox.getMaxWidth());
        chart.setLegendVisible(false);
    }
    public void executeCompaction(){

        Collections.sort(Holes);
        for (int i = 1; i<Holes.size(); i++){
            if( (Holes.get(i-1).getStartAddress()+Holes.get(i-1).getSize())
                >=Holes.get(i).getStartAddress() ){
                Holes.get(i-1).setSize(Holes.get(i).getStartAddress()-Holes.get(i-1).getStartAddress()+Holes.get(i).getSize());
                Holes.remove(Holes.get(i));
                NumberOfHoles--;
            }
        }
        HolesTable.getItems().clear();
        HolesTable.getItems().addAll(Holes);
    }

}
