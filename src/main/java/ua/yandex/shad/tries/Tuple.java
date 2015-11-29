package ua.yandex.shad.tries;

public class Tuple {
    public static final int NULL = -1;
    private int weight;
    private final String name;

    public Tuple(String name) {
        this.weight = NULL;
        this.name = name;
    }

    public Tuple(int weight, String name) throws IllegalArgumentException {
        if (weight < NULL) {
            throw new IllegalArgumentException("Negative weight");
        }
        this.weight = weight;
        this.name = name;
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

    public String getName() {
        return this.name;
    }
}

