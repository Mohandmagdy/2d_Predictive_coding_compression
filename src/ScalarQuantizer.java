public class ScalarQuantizer {
    private int numLevels;
    private int mn = -255, mx = 255;
    private int[] levels;

    public ScalarQuantizer(int numBits) {
        this.numLevels = (int) Math.pow(2, numBits);
        this.levels = new int[numLevels];
        int range = mx - mn;
        int step = range / numLevels;
        for (int i = 0; i < numLevels; i++) {
            levels[i] = mn + (i * step);
        }
    }

    public int quantize(int value) {
        int level = (int) Math.round((double) (value - mn) / (mx - mn) * (numLevels - 1));
        return Math.min(level, numLevels - 1);
    }



    public int dequantize(int level) {
        int lowerBound = levels[level];
        int upperBound = (level == numLevels - 1) ? mx : levels[level + 1];
        return Math.round((lowerBound + upperBound) / 2.0f);
    }
}
