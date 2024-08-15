package bgu.spl.net.impl.stomp;

public class Pair {
    private Integer first;
    private String second;
    
    public Pair(Integer first, String second){
        this.first = first;
        this.second = second;
    }
    public boolean equals(Pair other){
        return first.equals(other.first) && second.equals(other.second);
    }

    
}
