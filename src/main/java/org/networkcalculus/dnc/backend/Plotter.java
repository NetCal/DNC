package org.networkcalculus.dnc.backend;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Plotter {

    private record ColoredSegment(Segment segment, Color color) {
    }

    private List<ColoredSegment> segmentsToPlot = new ArrayList<>();
    private final String PLOT_TITLE = "Plot";
    private String label = PLOT_TITLE;
    private final int PLOT_WIDTH = 800;
    private final int PLOT_HEIGHT = 600;

    public Plotter() {
    }

    // Original method — defaults to black for backwards compatibility
    public void addCurveToPlotter(Curve curve) {
        addCurveToPlotter(curve, Color.BLACK);
    }

    // New overload with explicit color
    public void addCurveToPlotter(Curve curve, Color color) {
        for (Segment s : curve.getSequence().getSegments()) {
            segmentsToPlot.add(new ColoredSegment(s, color));
        }
    }

    public void addSegmentsToPlotter(List<Segment> segments) {
        for (Segment s : segments) {
            segmentsToPlot.add(new ColoredSegment(s, Color.BLACK));
        }
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void plotCurves() {
        XYChart chart = new XYChartBuilder()
                .width(PLOT_WIDTH)
                .height(PLOT_HEIGHT)
                .title(label)
                .xAxisTitle("Time")
                .yAxisTitle("Value")
                .build();

        chart.getStyler().setDefaultSeriesRenderStyle(org.knowm.xchart.XYSeries.XYSeriesRenderStyle.Line);
        chart.getStyler().setMarkerSize(0);

        int i = 1;
        for (ColoredSegment cs : segmentsToPlot) {
            Segment s = cs.segment();
            double[] xData = {s.getStartTime().toDouble(), s.getEndTime().toDouble()};
            double[] yData = {
                    s.getRightLimitAtStartTime().toDouble(),
                    s.getRightLimitAtStartTime().add(s.getSlope().mul(s.getEndTime().sub(s.getStartTime()))).toDouble()
            };

            var series = chart.addSeries("Segment " + i, xData, yData);
            series.setLineColor(cs.color());
            i++;
        }

        new SwingWrapper<>(chart).displayChart();
    }
}