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

    /*CPUProcess A = new CPUProcess(0,120,"First-Fit",60);
    CPUProcess B = new CPUProcess(1,120,"First-Fit",200);
    CPUProcess C = new CPUProcess(2,120,"First-Fit",380);

    Hole AA = new Hole (0,50,100);
    Hole BB = new Hole (1,300,60);
    Hole CC = new Hole (2,500,150);*/

    @FXML VBox RootVBox;
    @FXML HBox ChartHBox;

    @FXML Button InputButton,ProcessAllocateButton,ProcessDeallocateButton,HoleAllocateButton;

    @FXML TableView ProcessesTable,HolesTable;
    @FXML TableColumn<CPUProcess, Integer> ProcessIdColumn,ProcessSizeColumn,ProcessTypeOfAllocColumn;
    @FXML TableColumn <CPUProcess, Integer> HoleStartAddressColumn, HoleSizeColumn;

    @FXML TextField ProcessSizeTextField,HoleStartAddressTextField,HoleSizeTextField;
    @FXML TextField MemorySizeTextField;

    @FXML ChoiceBox TypeOfAllocChoiceBox;

    public void initialize(URL location, ResourceBundle resources){

        initializeInputButton();                //defined at line 89
        initializeHoleAllocateButton();         //defined at line 109
        initializeProcessAllocateButton();      //defined at line 144
        initializeProcessDeallocateButton();    //defined at line 179
        initializeTypeOfAllocChoiceBox();       //defined at line 204
        initializeProcessesTable();             //defined at line 208
        initializeHolesTable();                 //defined at line 221
        initializeGanttChart();                 //defined at line 265
        buildGanttChart();                      //defined at line 234
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
                    MemorySizeTextField.clear();
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
                    NumberOfHoles++;

                    chart.getData().clear();
                    buildGanttChart();
                    HolesTable.getItems().clear();
                    HolesTable.getItems().addAll(Holes);
                }
                catch (NumberFormatException e){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Missing Hole Data");
                    alert.setContentText("Please make sure to enter all hole data correctly.");

                    alert.showAndWait();
                    HoleSizeTextField.clear();
                    HoleStartAddressTextField.clear();
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
                    AllocateProcess(process);

                    //process.setStartAddress(parseInt(ProcessAddTextField.getText()));//////////////////

                    chart.getData().clear();
                    buildGanttChart();
                    ProcessesTable.getItems().clear();
                    ProcessesTable.getItems().addAll(Processes);
                    HolesTable.getItems().clear();
                    HolesTable.getItems().addAll(Holes);
                }
                catch (NumberFormatException e){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Missing Process Data");
                    alert.setContentText("Please make sure to enter all process data correctly.");

                    alert.showAndWait();
                    ProcessSizeTextField.clear();
                }
                ProcessSizeTextField.clear();
            }
        });

    }
    public void initializeProcessDeallocateButton(){
        ProcessDeallocateButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ObservableList<CPUProcess> processSelected;
                processSelected = ProcessesTable.getSelectionModel().getSelectedItems();
                Hole hole = new Hole (NumberOfHoles, processSelected.get(0).getStartAddress(),processSelected.get(0).getSize());
                processSelected.forEach(Processes::remove);
                NumberOfProcesses--;

                Holes.add(hole);
                NumberOfHoles++;
                executeCompaction();

                chart.getData().clear();
                buildGanttChart();
                ProcessesTable.getItems().clear();
                ProcessesTable.getItems().addAll(Processes);
                HolesTable.getItems().clear();
                HolesTable.getItems().addAll(Holes);

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
                i--;
            }
        }
        HolesTable.getItems().clear();
        HolesTable.getItems().addAll(Holes);
    }

    public void AllocateProcess(CPUProcess process){
        if(process.getTypeOfAlloc()=="First-Fit") First_Fit_Allocate(process);
        else if(process.getTypeOfAlloc()=="Best-Fit") Best_Fit_Allocate(process);
        else if(process.getTypeOfAlloc()=="Worst-Fit") Worst_Fit_Allocate(process);
        else System.out.println("Error with allocation");

    }
    public void First_Fit_Allocate (CPUProcess process1 )
    {
        int flag1 = 0;
        for (int j = 0; j < Holes.size(); j++) {
            if (Holes.get(j).getSize() >= process1.getSize()) {
                process1.setStartAddress(Holes.get(j).getStartAddress());
                flag1 = 1;
                Holes.get(j).setSize(Holes.get(j).getSize()-process1.getSize());
                if (Holes.get(j).getSize()==0)
                    Holes.remove(Holes.get(j));
                else Holes.get(j).setStartAddress(Holes.get(j).getStartAddress()+process1.getSize());
                ProcessId++;
                NumberOfProcesses++;
                Processes.add(process1);
                break;
            }
        }
        if (flag1 == 0){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No Free Space");
            alert.setContentText("There is not enough memory to allocate the process. Try to de-allocate memory and re-allocate the process.");

            alert.showAndWait();
        }
    }
    public void Best_Fit_Allocate ( CPUProcess process1) {
        int ind = 0 ;
        int flag2 =0;
        int min =0;
        for (int j=0;j<Holes.size();j++)
        {
            if (Holes.get(j).getSize()>=process1.getSize()&&flag2==0)
            {
                ind=j;
                min=Holes.get(ind).getSize();
                flag2=1;
            }
            else if (Holes.get(j).getSize()>=process1.getSize()&&Holes.get(j).getSize()<min)
            {
                ind =j ;
                min=Holes.get(ind).getSize();
            }
        }
        if(flag2>0) {
            process1.setStartAddress(Holes.get(ind).getStartAddress());
            Holes.get(ind).setSize(Holes.get(ind).getSize()-process1.getSize());
            if ( Holes.get(ind).getSize()==0)
                Holes.remove(ind);
            else Holes.get(ind).setStartAddress(Holes.get(ind).getStartAddress()+process1.getSize());
            ProcessId++;
            NumberOfProcesses++;
            Processes.add(process1);
        }
        else showMemoryError();
    }
    public void Worst_Fit_Allocate (CPUProcess process1) {
        int ind = 0;
        int flag = 0;
        int max = 0;
        for (int j = 0; j < Holes.size(); j++) {
            if (Holes.get(j).getSize() >= process1.getSize() && Holes.get(j).getSize() > max) {
                flag = 1;
                ind = j;
                max = Holes.get(ind).getSize();
            }
        }
        if (flag > 0) {
            process1.setStartAddress(Holes.get(ind).getStartAddress());
            Holes.get(ind).setSize(Holes.get(ind).getSize()-process1.getSize());
            if (Holes.get(ind).getSize() == 0)
                Holes.remove(Holes.get(ind));
            else Holes.get(ind).setStartAddress(Holes.get(ind).getStartAddress()+process1.getSize());
            ProcessId++;
            NumberOfProcesses++;
            Processes.add(process1);
        }
        else showMemoryError();
    }

    public void showMemoryError(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("No Free Space");
        alert.setContentText("There is not enough memory to allocate the process." +
                "\nTry to de-allocate memory or allocate more holes," +
                " then re-allocate the process.");

        alert.showAndWait();
    }
}
