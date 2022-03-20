package mathcalculate;

public class MathCalculate {

    /**
     * Calculate mean of array.
     * @param data
     * @return
     */
    public static double arrayMean(double[] data) {
        double sum = 0;
        for (double d : data) {
            sum += d;
        }
        return sum / data.length;
    }

    //Calculate population standard deviation.
    public static double popSTDEV(double[] data) {
        double mean = arrayMean(data);
        double variance = 0;
        for (int i = 0; i < data.length; i++) {
            variance += Math.pow((data[i] - mean), 2);
        }
        variance /= data.length;
        return Math.sqrt(variance);
    }

    /**
     * Calculate pearson correlation of array x and array y.
     * @param x
     * @param y
     * @return pearson correlation.
     * @throws IndexOutOfBoundsException
     */
    public static double calPearsonCorrelation(double[] x, double[] y) throws IndexOutOfBoundsException {
        if (x.length != y.length)
            throw new IndexOutOfBoundsException("Size of two array is not equal!");
        if (y.length == 0)
            throw new IndexOutOfBoundsException("zero array");
        double xMean = arrayMean(x), yMean = arrayMean(y);
        int count = x.length;
        //Calculate pearson numerator.
        double pearsonNumerator = 0;
        for (int i = 0; i < count; i++) {
            pearsonNumerator += (x[i] - xMean) * (y[i] - yMean);
        }
        //Calculate pearson denominator.
        double xSquareSum = 0, ySquareSum = 0;
        for (int i = 0; i < count; i++) {
            xSquareSum += Math.pow(x[i]-xMean, 2);
            ySquareSum += Math.pow(y[i]-yMean, 2);
        }

        return pearsonNumerator / Math.sqrt(xSquareSum * ySquareSum);
    }
}
