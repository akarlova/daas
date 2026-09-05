package generics;

public class Journal extends Book implements Comparable<Journal>{
    @Override
    public int compareTo(Journal o) {
        return 0;
    }
}
