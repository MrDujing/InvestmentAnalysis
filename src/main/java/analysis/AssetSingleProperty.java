package analysis;

import dao.AssetHistoryValueDao;
import form.AssetHistoryValueForm;
import javafx.util.Pair;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.DefaultXYDataset;
import util.FundCodeTransfer;
import util.LoggerRecorder;
import util.MathCalculate;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class AssetSingleProperty {
    private ArrayList<AssetHistoryValueForm> historyValueArray = new ArrayList<>();
    private Logger logger = new LoggerRecorder().getLogger();
    private int fundCode, startDate, endDate;
    private String fundName;

    public AssetSingleProperty(int code, String name, int start, int end) {
        fundCode = code;
        fundName = name;
        startDate = start;
        endDate = end;
        if (endDate > startDate && startDate >= 0) {
            historyValueArray = new AssetHistoryValueDao().queryFunHistoryValue(fundCode, startDate, endDate);
        } else if (endDate <= 0) {
            historyValueArray = new AssetHistoryValueDao().queryFunHistoryValue(fundCode, startDate, endDate);
        } else {
            logger.severe(String.format("Invalid input for %d", fundCode));
        }
    }

    /**
     * Draw plot fig of day increase rate, of each fund code.
     */
    public void drawDayIncreasePlotFig() {
        if (historyValueArray.size() <= 0)
            return;
        //Combine 2D array, with date, dayIncreaseRate.
        double[] date = new double[historyValueArray.size()];
        double[] dayIncrease = new double[historyValueArray.size()];
        for (int i = 0; i < historyValueArray.size(); i++) {
            date[i] = (double) historyValueArray.get(i).getValueDate();
            dayIncrease[i] = historyValueArray.get(i).getDayIncreaseRate() * 100;
        }
        double[][] plotData = {date, dayIncrease};

        DefaultXYDataset dataSet = new DefaultXYDataset();
        dataSet.addSeries(FundCodeTransfer.intToString(fundCode), plotData);

        JFreeChart plotChart = ChartFactory.createScatterPlot(FundCodeTransfer.intToString(fundCode), "dateCount(D)", "dayIncreaseRate(%)", dataSet, PlotOrientation.VERTICAL, true, true, false);
        ChartFrame plotFrame = new ChartFrame(FundCodeTransfer.intToString(fundCode) + fundName, plotChart, true);
        XYPlot xyPlot = (XYPlot) plotChart.getPlot();
        xyPlot.setBackgroundPaint(Color.white);

        //Set x-axis.
        NumberAxis valueAxis = (NumberAxis) xyPlot.getDomainAxis();
        valueAxis.setTickUnit(new NumberTickUnit(100));
        valueAxis.setAxisLineStroke(new BasicStroke(1.5f));
        //Set Y-axis.
        NumberAxis numberAxis = (NumberAxis) xyPlot.getRangeAxis();
        numberAxis.setRange(-6, 6);
        numberAxis.setAutoTickUnitSelection(false);
        numberAxis.setTickUnit(new NumberTickUnit(0.6));
        //Set X/Y-AXIS visible.
        xyPlot.setRangeGridlinePaint(Color.BLUE);
        xyPlot.setRangeGridlinesVisible(true);
        xyPlot.setDomainGridlinePaint(Color.BLUE);
        xyPlot.setDomainGridlinesVisible(true);

        plotFrame.pack();
        plotFrame.setVisible(true);
    }

    /**
     * Draw pie fig of day increase rate.
     */
    public void drawDayIncreasePieFig() {
        int historySize = historyValueArray.size();
        if (historySize == 0)
            return;
        // Acquire data set for draw.
        double plusZeroToOne = 0, plusOneToTwo = 0, plusTwoToThree = 0, plusThreeToUp = 0;
        double minusZeroToOne = 0, minusOneToTwo = 0, minusTwoToThree = 0, minusThreeToUp = 0;
        for (AssetHistoryValueForm form : historyValueArray) {
            float rate = form.getDayIncreaseRate();
            if (0 <= rate && rate < 0.01)
                plusZeroToOne++;
            else if (0.01 <= rate && rate < 0.02)
                plusOneToTwo++;
            else if (0.02 <= rate && rate < 0.03)
                plusTwoToThree++;
            else if (0.03 <= rate)
                plusThreeToUp++;
            else if (-0.01 < rate && rate < 0)
                minusZeroToOne++;
            else if (-0.02 < rate && rate <= -0.01)
                minusOneToTwo++;
            else if (-0.03 < rate && rate <= -0.02)
                minusTwoToThree++;
            else if (rate <= -0.03)
                minusThreeToUp++;
        }
        DefaultPieDataset pieDataset = new DefaultPieDataset();
        pieDataset.setValue("[0,1)", plusZeroToOne / historySize);
        pieDataset.setValue("[1,2)", plusOneToTwo / historySize);
        pieDataset.setValue("[2,3)", plusTwoToThree / historySize);
        pieDataset.setValue("[3,+)", plusThreeToUp / historySize);
        pieDataset.setValue("(-1,0)", minusZeroToOne / historySize);
        pieDataset.setValue("(-2,-1]", minusOneToTwo / historySize);
        pieDataset.setValue("(-3,-2]", minusTwoToThree / historySize);
        pieDataset.setValue("(-,-3]", minusThreeToUp / historySize);

        //Draw fig.
        JFreeChart pieChart = ChartFactory.createPieChart("DayIncreaseRate", pieDataset, true, true, false);
        //Pie fig set.
        pieChart.setBackgroundPaint(Color.white);

        Font pieFont = new Font("黑体", Font.CENTER_BASELINE, 20);
        TextTitle pieTextTile = new TextTitle(fundName);
        pieTextTile.setFont(pieFont);
        pieChart.setTitle(pieTextTile);

        PiePlot piePlot = (PiePlot) pieChart.getPlot();
        piePlot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}={2}", NumberFormat.getNumberInstance(), new DecimalFormat("0.00%")));
        piePlot.setLegendLabelGenerator(new StandardPieSectionLabelGenerator("{0}={2}", NumberFormat.getNumberInstance(), new DecimalFormat("0.00%")));
        piePlot.setCircular(true);
        piePlot.setBackgroundPaint(Color.orange);

        ChartFrame pieFrame = new ChartFrame(fundCode + fundName, pieChart);
        pieFrame.pack();
        pieFrame.setVisible(true);
    }

    /**
     * Calculate Max Draw Down(MDD).
     */
    public ArrayList<Pair<Integer, Float>> calculateMaxDrawDown() {
        ArrayList<Pair<Integer, Float>> MDDMap = new ArrayList<>();
        int preMaxIndex = 0, preMinIndex = 0, maxIndex = 0;
        for (int i = 0; i < historyValueArray.size(); i++) {
            if (getIndexValue(i) >= getIndexValue(maxIndex))
                maxIndex = i;
            else if ((getIndexValue(maxIndex) - getIndexValue(i)) > (getIndexValue(preMaxIndex) - getIndexValue(preMinIndex))) {
                preMaxIndex = maxIndex;
                preMinIndex = i;
            }
        }
        MDDMap.add(new Pair<>(historyValueArray.get(preMaxIndex).getValueDate(), getIndexValue(preMaxIndex)));
        MDDMap.add(new Pair<>(historyValueArray.get(preMinIndex).getValueDate(), getIndexValue(preMinIndex)));
        return MDDMap;
    }

    /**
     * Calculate Max Profit. MPF.
     */
    public ArrayList<Pair<Integer, Float>> calculateMaxProfit() {
        ArrayList<Pair<Integer, Float>> MPFMap = new ArrayList<>();
        int preMinIndex = 0, preMaxIndex = 0, minIndex = 0;
        for (int i = 0; i < historyValueArray.size(); i++) {
            if (getIndexValue(i) <= getIndexValue(minIndex))
                minIndex = i;
            else if ((getIndexValue(i) - getIndexValue(minIndex)) > (getIndexValue(preMaxIndex) - getIndexValue(preMinIndex))) {
                preMaxIndex = i;
                preMinIndex = minIndex;
            }
        }
        MPFMap.add(new Pair<>(historyValueArray.get(preMinIndex).getValueDate(), getIndexValue(preMinIndex)));
        MPFMap.add(new Pair<>(historyValueArray.get(preMaxIndex).getValueDate(), getIndexValue(preMaxIndex)));
        return MPFMap;
    }

    //Calculate Max total profit. MTF.
    public float calculateMaxTotalProfit() {
        return (getIndexValue(historyValueArray.size() - 1) - getIndexValue(0)) / getIndexValue(0);
    }

    //Just for get totalValue of historyValueArray by index.
    private float getIndexValue(int index) {
        return historyValueArray.get(index).getTotalValue();
    }

    //Calculate population standard deviation.
    public double calculatePopSTDEV() {
        double proportionArray[] = new double[historyValueArray.size()];
        for (int i = 0; i < historyValueArray.size(); i++) {
            proportionArray[i] = historyValueArray.get(i).getDayIncreaseRate();
        }

        return new MathCalculate(proportionArray).popSTDEV();
    }
}
