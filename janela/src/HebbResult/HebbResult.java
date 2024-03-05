package HebbResult;

import java.util.Arrays;

public class HebbResult {
    public float[] w;
    public float b;

    public HebbResult(float[] w, float b) {
        this.w = w;
        this.b = b;
    }

    public float[] getW() {
        return w;
    }

    public void setW(float[] w) {
        this.w = w;
    }

    public float getB() {
        return b;
    }

    public void setB(float b) {
        this.b = b;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(w);
        result = prime * result + Float.floatToIntBits(b);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        HebbResult other = (HebbResult) obj;
        if (!Arrays.equals(w, other.w))
            return false;
        if (Float.floatToIntBits(b) != Float.floatToIntBits(other.b))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "HebbResult [w=" + Arrays.toString(w) + ", b=" + b + "]";
    }

}
