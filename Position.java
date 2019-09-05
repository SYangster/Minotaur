
public class Position {
    public int x;
    public int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public String toString () {
        return "(" + this.x + "," + this.y + ")";
    }

    @Override
    public boolean equals(Object other) {
        Position otherPos = (Position) other;
        return (this.x == otherPos.x && this.y == otherPos.y);
    }

    @Override
    public int hashCode() {
        int result = this.x;
        result = 31 * result + this.y;
        return result;
    }

    public boolean contains(Position other, int window) {
        return (other.x > (this.x - window) && other.x < (this.x + window) && other.y > (this.y - window) && other.y < (this.y + window));
    }
}
