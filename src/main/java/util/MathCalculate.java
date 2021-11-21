package util;

public class MathCalculate {
    private double[] data;

    public MathCalculate(double[] data) {
        this.data = data;
    }

    private double arraySum(double[] data) {
        double sum = 0;
        for (int i = 0; i < data.length; i++)
            sum += data[i];
        return sum;
    }

    private double arrayMean(double[] data) {
        double mean = 0;
        mean = arraySum(data) / data.length;
        return mean;
    }

    //Calculate population standard deviation.
    public double popSTDEV() {
        double mean = arrayMean(data);
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
    public double calculatePearsonCorrelation(double[] y) throws IndexOutOfBoundsException {
        if (data.length != y.length)
            throw new IndexOutOfBoundsException("Size of two array is not equal!");
        if (y.length == 0)
            throw new IndexOutOfBoundsException("zero array");
        double xMean = arrayMean(data), yMean = arrayMean(y);
        //Calculate pearson numerator.
        double pearsonNumerator = 0;
        for (int i = 0; i < y.length; i++) {
            pearsonNumerator += (data[i] - xMean) * (y[i] - yMean);
        }
        //Calculate pearson denominator.
        double xSquareSum = 0;
        for (int i = 0; i < data.length; i++) {
            xSquareSum += ((data[i] - xMean) * (data[i] - xMean));
        }
        double ySquareSum = 0;
        for (int i = 0; i < y.length; i++) {
            ySquareSum += ((y[i] - yMean) * (y[i] - yMean));
        }

        double pearsonCorrelation = pearsonNumerator / Math.sqrt(xSquareSum * ySquareSum);
        return pearsonCorrelation;
    }
}
