package sample;

public class CPUProcess {
    private int id;
    private int size;
    private String typeOfAlloc;
    private int startAddress;

    public CPUProcess() {
        id = size = startAddress = 0;
        typeOfAlloc = "First-Fit";
    }

    public CPUProcess(int id, int size, String typeOfAlloc) {
        this.id = id;
        this.size = size;
        this.typeOfAlloc = typeOfAlloc;
        this.startAddress = -1;
    }

    public CPUProcess(int id, int size, String typeOfAlloc, int startAddress) {
        this.id = id;
        this.size = size;
        this.typeOfAlloc = typeOfAlloc;
        this.startAddress = startAddress;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getTypeOfAlloc() {
        return typeOfAlloc;
    }

    public void setTypeOfAlloc(String typeOfAlloc) {
        this.typeOfAlloc = typeOfAlloc;
    }

    public int getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(int startAddress) {
        this.startAddress = startAddress;
    }
}
