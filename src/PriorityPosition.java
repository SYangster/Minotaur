
public class PriorityPosition implements Comparable<PriorityPosition>{
    Position position;
    int priority;

    public PriorityPosition(int priority, Position position) {
        this.position = position;
        this.priority = priority;
    }

    public int compareTo(PriorityPosition other) {
        if (this.priority < other.priority) {
            return -1;
        } else if (this.priority > other.priority) {
            return 1;
        } else {
            return 0;
        }
    }
}

