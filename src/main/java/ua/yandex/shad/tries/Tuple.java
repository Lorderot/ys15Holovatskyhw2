package ua.yandex.shad.tries;

public class Tuple {
    public static final int NULL = -1;
    private int weight;
    private Tuple[] children;

    public Tuple() {
        this.weight = NULL;
        this.children = new Tuple[RWayTrie.R];
    }

    public Tuple(int weight) throws IllegalArgumentException {
        if (weight < NULL) {
            throw new IllegalArgumentException("Negative weight");
        }
        this.weight = weight;
        this.children = new Tuple[RWayTrie.R];
    }

    public Tuple next(int i)throws IndexOutOfBoundsException {
        if (i < 0 || i >= RWayTrie.R) {
            throw new IndexOutOfBoundsException();
        }
        return children[i];
    }

    public void setChild(int index, Tuple child)
            throws IndexOutOfBoundsException, IllegalArgumentException {
        if (index < 0 || index >= RWayTrie.R) {
            throw new IndexOutOfBoundsException();
        }
        if (children[index] != null && child != null) {
            throw new IllegalArgumentException("Such child already exists");
        }
        children[index] = child;
    }

    public void setWeight(int value) throws IllegalArgumentException {
        if (value < NULL) {
            throw new IllegalArgumentException("Negative Weight");
        }
        this.weight = value;
    }

    public int getWeight() {
        return this.weight;
    }
}

