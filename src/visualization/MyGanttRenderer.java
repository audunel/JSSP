package visualization;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRendererState;
import org.jfree.chart.renderer.category.GanttRenderer;
import org.jfree.data.gantt.GanttCategoryDataset;
import org.jfree.ui.RectangleEdge;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Created by audun on 25.04.17.
 */
public class MyGanttRenderer extends GanttRenderer {

    public MyGanttRenderer() {
        super();
    }

    public Paint getItemPaint(int row, int column) {
        //return DefaultDrawingSupplier.DEFAULT_PAINT_SEQUENCE[column];
        return Color.BLUE;
    }

    protected void drawTasks(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, GanttCategoryDataset dataset, int row, int column) {
        int count = dataset.getSubIntervalCount(row, column);
        if(count == 0) {
            this.drawTask(g2, state, dataArea, plot, domainAxis, rangeAxis, dataset, row, column);
        }

        PlotOrientation orientation = plot.getOrientation();

        for(int subinterval = 0; subinterval < count; ++subinterval) {
            RectangleEdge rangeAxisLocation = plot.getRangeAxisEdge();
            Number value0 = dataset.getStartValue(row, column, subinterval);
            if(value0 == null) {
                return;
            }

            double translatedValue0 = rangeAxis.valueToJava2D(value0.doubleValue(), dataArea, rangeAxisLocation);
            Number value1 = dataset.getEndValue(row, column, subinterval);
            if(value1 == null) {
                return;
            }

            double translatedValue1 = rangeAxis.valueToJava2D(value1.doubleValue(), dataArea, rangeAxisLocation);
            double rectStart;
            if(translatedValue1 < translatedValue0) {
                rectStart = translatedValue1;
                translatedValue1 = translatedValue0;
                translatedValue0 = rectStart;
            }

            rectStart = this.calculateBarW0(plot, plot.getOrientation(), dataArea, domainAxis, state, row, column);
            double rectLength = Math.abs(translatedValue1 - translatedValue0);
            double rectBreadth = state.getBarWidth();
            Rectangle2D bar = null;
            RectangleEdge barBase = null;
            if(plot.getOrientation() == PlotOrientation.HORIZONTAL) {
                bar = new Rectangle2D.Double(translatedValue0, rectStart, rectLength, rectBreadth);
                barBase = RectangleEdge.LEFT;
            } else if(plot.getOrientation() == PlotOrientation.VERTICAL) {
                bar = new Rectangle2D.Double(rectStart, translatedValue0, rectBreadth, rectLength);
                barBase = RectangleEdge.BOTTOM;
            }

            Rectangle2D completeBar = null;
            Rectangle2D incompleteBar = null;
            Number percent = dataset.getPercentComplete(row, column, subinterval);
            double start = this.getStartPercent();
            double end = this.getEndPercent();
            if(percent != null) {
                double p = percent.doubleValue();
                if(orientation == PlotOrientation.HORIZONTAL) {
                    completeBar = new Rectangle2D.Double(translatedValue0, rectStart + start, rectLength, rectBreadth);

                } else if(orientation == PlotOrientation.VERTICAL) {
                    completeBar = new Rectangle2D.Double(rectStart + start * rectBreadth, translatedValue0 + rectLength * (1.0D - p), rectBreadth * (end - start), rectLength * p);
                    incompleteBar = new Rectangle2D.Double(rectStart + start * rectBreadth, translatedValue0, rectBreadth * (end - start), rectLength * (1.0D - p));
                }
            }

            if(this.getShadowsVisible()) {
                this.getBarPainter().paintBarShadow(g2, this, row, column, bar, barBase, true);
            }

            this.getBarPainter().paintBar(g2, this, row, column, bar, barBase);
            if(completeBar != null) {
                g2.setPaint(DefaultDrawingSupplier.DEFAULT_PAINT_SEQUENCE[percent.intValue()]);
                g2.fill(completeBar);
            }

            if(incompleteBar != null) {
                g2.setPaint(this.getIncompletePaint());
                g2.fill(incompleteBar);
            }

            if(this.isDrawBarOutline() && state.getBarWidth() > 3.0D) {
                g2.setStroke(this.getItemStroke(row, column));
                g2.setPaint(this.getItemOutlinePaint(row, column));
                g2.draw(bar);
            }

            if(subinterval == count - 1) {
                int datasetIndex = plot.indexOf(dataset);
                Comparable columnKey = dataset.getColumnKey(column);
                Comparable rowKey = dataset.getRowKey(row);
                double xx = domainAxis.getCategorySeriesMiddle(columnKey, rowKey, dataset, this.getItemMargin(), dataArea, plot.getDomainAxisEdge());
                this.updateCrosshairValues(state.getCrosshairState(), dataset.getRowKey(row), dataset.getColumnKey(column), value1.doubleValue(), datasetIndex, xx, translatedValue1, orientation);
            }

            if(state.getInfo() != null) {
                EntityCollection entities = state.getEntityCollection();
                if(entities != null) {
                    this.addItemEntity(entities, dataset, row, column, bar);
                }
            }
        }
    }
}