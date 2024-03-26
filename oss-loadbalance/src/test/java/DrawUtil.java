import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.util.ArrayList;
import java.util.List;

public class DrawUtil {

    public static void draw(String title,String X,List<Double> xData,String Y,List<Double> yData) {
        // Create Chart
        XYChart chart = new XYChartBuilder()
                .width(800)
                .height(600)
                .title(title)
                .xAxisTitle(X)
                .yAxisTitle(Y)
                .build();
        // Customize Chart
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
        chart.getStyler().setChartTitleVisible(true);
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
        chart.getStyler().setMarkerSize(8);
        chart.getStyler().setToolTipsEnabled(true);
        chart.getStyler().setYAxisMin(0.0);
        chart.getStyler().setYAxisMax(100.0);
        // Series
        chart.addSeries("结果", xData, yData).setMarker(SeriesMarkers.CIRCLE);

        // Show the chart
        new SwingWrapper<>(chart).displayChart();
    }


    public static void main(String[] args) {
        // Generate data
        List<Double> xData = new ArrayList<>();
        List<Double> yData = new ArrayList<>();
        for (int i = 0; i <= 10; i++) {
            xData.add((double) i);
            yData.add(Math.sin(i));
        }
        draw("test","x",xData,"y",yData);
    }
}
