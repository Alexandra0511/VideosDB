package entertainment;

import java.util.Objects;

public class Pair<T1, T2> {
    private T1 t1;
    private T2 t2;

    public void setT1(final T1 t1) {
        this.t1 = t1;
    }

    public void setT2(final T2 t2) {
        this.t2 = t2;
    }

    public Pair(final T1 t1, final T2 t2) {
        this.t1 = t1;
        this.t2 = t2;
    }

    public T1 getT1() {
        return t1;
    }

    public T2 getT2() {
        return t2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(t1, pair.t1) && Objects.equals(t2, pair.t2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(t1, t2);
    }
}
