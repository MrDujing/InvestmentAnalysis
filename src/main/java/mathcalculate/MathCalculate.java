package mathcalculate;

public class MathCalculate {
    private double[] data;

    public MathCalculate(double[] data) {
        this.data = data;
    }

    private double arrayMean() {
        double sum = 0;
        for (double d : data) {
            sum += d;
        }
        return sum / data.length;
    }

    private double arrayMean(double[] data) {
        double sum = 0;
        for (double d : data) {
            sum += d;
        }
        return sum / data.length;
    }

    //Calculate population standard deviation.
    public double popSTDEV() {
        double mean = arrayMean();
        double variance = 0;
        for (int i = 0; i < data.length; i++) {
            variance += Math.pow((data[i] - mean), 2);
        }
        variance /= data.length;
        return Math.sqrt(variance);
    }

    /**
     * Calculate Pearson correlation score.
     *
     * @param y , another array; data[] is x.
     * @return Pearson correlation rate.
     * @throws IndexOutOfBoundsException
     */
    public double calPearsonCorrelation(double[] y) throws IndexOutOfBoundsException {
        if (data.length != y.length)
            throw new IndexOutOfBoundsException("Size of two array is not equal!");
        if (y.length == 0)
            throw new IndexOutOfBoundsException("zero array");
        double xMean = arrayMean(), yMean = arrayMean(y);
        int count = data.length;
        //Calculate pearson numerator.
        double pearsonNumerator = 0;
        for (int i = 0; i < count; i++) {
            pearsonNumerator += (data[i] - xMean) * (y[i] - yMean);
        }
        //Calculate pearson denominator.
        double xSquareSum = 0, ySquareSum = 0;
        for (int i = 0; i < count; i++) {
            xSquareSum += Math.pow(data[i]-xMean, 2);
            ySquareSum += Math.pow(y[i]-yMean, 2);
        }

        return pearsonNumerator / Math.sqrt(xSquareSum * ySquareSum);
    }
}
