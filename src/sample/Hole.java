package sample;

public class Hole implements Comparable<Hole>{
    private int startAddress;
    private int size;

    public Hole(int id) {
        size = startAddress = 0;
    }

    public Hole(int id, int startAddress, int size) {
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

    public int compareTo(Hole hole) {
        return this.startAddress - hole.startAddress;
    }
}


