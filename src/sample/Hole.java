package sample;

public class Hole {
    private int startAddress;
    private int size;

    public Hole() {
        size = startAddress = 0;
    }

    public Hole(int startAddress, int size) {
        this.size = size;
        this.startAddress = startAddress;
    }

    public int getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(int startAddress) {
        this.startAddress = startAddress;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}


