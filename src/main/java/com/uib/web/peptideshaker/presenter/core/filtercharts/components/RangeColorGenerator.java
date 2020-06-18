package com.uib.web.peptideshaker.presenter.core.filtercharts.components;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is responsible for generating Colour Gradient as HTML hashed
 * colour code for the heat map
 *
 * @author Yehia Farag
 */
public class RangeColorGenerator {

    private final double max;
    private final Color lowerColor = new Color(13, 99, 196);
    private final Color upperColor = new Color(207, 227, 249);
    private final HorizontalLayout colorScale;
    private final Map<Double, String> colorCategories;
    private final DecimalFormat df = new DecimalFormat("0.00E00");

    /**
     * Constructor to initialise the main attributes
     *
     * @param max the maximum value
     */
    public RangeColorGenerator(double max) {
        this.max = max;
        colorScale = new HorizontalLayout();
        colorScale.setHeight(20, Unit.PIXELS);
        colorScale.setWidth(100, Unit.PERCENTAGE);
        colorScale.setStyleName("stacked");
        colorScale.addStyleName("colorscale");
        Label l = new Label("<center style= 'margin-left:-10px;font-size: 1vmin;width:15px !important; height:15px !important;letter-spacing: 10px !important;'>0</center>", ContentMode.HTML);
        l.setSizeFull();
        colorScale.addComponent(l);
        for (double x = 0; x < 50; x++) {
            l = new Label("<center style= ' margin-top: 5px;background-color: " + RangeColorGenerator.this.getColor(x * 0.02 * max) + ";width:100% !important; height:5px !important;'></center>", ContentMode.HTML);
            l.setWidth(100, Unit.PERCENTAGE);
            l.setHeight(5, Unit.PIXELS);
            l.setStyleName(ValoTheme.LABEL_NO_MARGIN);
            colorScale.addComponent(l);
            colorScale.setComponentAlignment(l, Alignment.TOP_CENTER);
        }
        String maxStr;
        if (max > 1000) {
            maxStr = df.format(max);
        } else {
            maxStr = ((int) max) + "";
        }
        l = new Label("<center style= 'font-size: 1vmin;width:25px !important; height:15px !important;'>" + maxStr + "</center>", ContentMode.HTML);
        l.setSizeFull();
        colorScale.addComponent(l);

        colorCategories = new HashMap<>();
        colorCategories.put(20.0, "rgb(230,242,255)");
        colorCategories.put(40.0, "rgb(153,197,255)");
        colorCategories.put(60.0, "rgb(77,154,255)");
        colorCategories.put(80.0, "rgb(0,110,255)");
        colorCategories.put(100.0, "rgb(0,77,179)");
        colorCategories.put(1000.0, "rgb(0,44,102)");

    }

    /**
     * Get vertical grade scale indicator.
     *
     * @param gradeScale grade scale or colour scale
     * @return grade scale layout
     */
    public VerticalLayout getVerticalScale(boolean gradeScale) {

        VerticalLayout scal = new VerticalLayout();
        scal.setSizeFull();
        scal.setStyleName("verticalgradescal");
        Label l = new Label("<div>Max</div>", ContentMode.HTML);
        l.setSizeFull();
        scal.addComponent(l);
        double step = max / 50.0;
        for (double x = max; x > 0;) {
            if (gradeScale) {
                l = new Label("<center style= ' background-color: " + RangeColorGenerator.this.getGradeColor(x, max, 0) + ";'></center>", ContentMode.HTML);
                x -= step;
            } else {
                l = new Label("<center style= ' background-color: " + RangeColorGenerator.this.getColor(x * 0.02 * max) + ";'></center>", ContentMode.HTML);
                x--;
            }
            l.setSizeFull();
            l.setStyleName(ValoTheme.LABEL_NO_MARGIN);
            scal.addComponent(l);
            scal.setComponentAlignment(l, Alignment.TOP_CENTER);

        }
        l = new Label("<div >Min</div>", ContentMode.HTML);
        l.setSizeFull();
        scal.addComponent(l);
        return scal;

    }

    /**
     * Get colour scale label as layout
     *
     * @return layout
     */
    public HorizontalLayout getColorScale() {
        return colorScale;
    }

    /**
     * Get the colour for the input value
     *
     * @param value double value to be converted to colour
     * @return HTML hashed colour for the input value
     */
    public String getColor(double value) {
        if (value < 1) {
            return "RGB(" + 245 + "," + 245 + "," + 245 + ")";
        }
        double n = (value) / max;
        double R1 = lowerColor.getRed() * n + upperColor.getRed() * (1 - n);
        double G1 = lowerColor.getGreen() * n + upperColor.getGreen() * (1 - n);
        double B1 = lowerColor.getBlue() * n + upperColor.getBlue() * (1 - n);
        String rgb = "RGB(" + (int) R1 + "," + (int) G1 + "," + (int) B1 + ")";
        return rgb;
    }

    /**
     * Get the colour for the input value
     *
     * @param value double value to be converted to colour
     * @return HTML hashed colour for the input value
     */
    public Color getAWTColor(double value) {
        if (value < 1) {
            return new Color(245, 245, 245);
        }
        double n = (value) / max;
        double R1 = Math.max(lowerColor.getRed() * n + upperColor.getRed() * (1 - n),0);
        double G1 = Math.max(lowerColor.getGreen() * n + upperColor.getGreen() * (1 - n),0);
        double B1 = Math.max(lowerColor.getBlue() * n + upperColor.getBlue() * (1 - n),0);
        return new Color((int) R1, (int) G1, (int) B1);
    }

    /**
     * Get colour value from grade scale
     *
     * @param value the value to be converted into colour
     * @param max1 max value
     * @param min minimum value
     * @return RGB colour as string
     */
    public String getGradeColor(double value, double max1, double min) {

        if (value == 0.0) {
            return "RGB(" + 245 + "," + 245 + "," + 245 + ")";
        }
        int h = 209;
        int s = 100;
        int l = 100 - ((int) ((value * 100.0) / max));
        l = scaleValueInRange(0.0, 100.00, 10.0, 95, l);
        if (max < value) {
            System.out.println("at l " + l + "  " + max + "  " + value);
        }
        Color c = toRGB(h, s, l);
        return "RGB(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ")";

    }

    private int scaleValueInRange(double rMin, double rMax, double tMin, double tMax, double value) {
        value = (((value - rMin) / (rMax - rMin)) * (tMax - tMin)) + tMin;
        return (int) Math.round(value);
    }

    /**
     * Convert HSL values to a RGB Colour with a default alpha value of 1.
     *
     * @param h Hue is specified as degrees in the range 0 - 360.
     * @param s Saturation is specified as a percentage in the range 1 - 100.
     * @param l Lumanance is specified as a percentage in the range 1 - 100.
     *
     * @returns the RGB Colour object
     */
    private Color toRGB(float h, float s, float l) {
        return toRGB(h, s, l, 1.0f);
    }

    /**
     * Convert HSL values to a RGB Colour.
     *
     * @param h Hue is specified as degrees in the range 0 - 360.
     * @param s Saturation is specified as a percentage in the range 1 - 100.
     * @param l Lumanance is specified as a percentage in the range 1 - 100.
     * @param alpha the alpha value between 0 - 1
     *
     * @returns the RGB Colour object
     */
    private Color toRGB(float h, float s, float l, float alpha) {
        if (s < 0.0f || s > 100.0f) {
            String message = "Color parameter outside of expected range - Saturation";
            throw new IllegalArgumentException(message);
        }

        if (l < 0.0f || l > 100.0f) {
            String message = "Color parameter outside of expected range - Luminance";
            throw new IllegalArgumentException(message);
        }

        if (alpha < 0.0f || alpha > 1.0f) {
            String message = "Color parameter outside of expected range - Alpha";
            throw new IllegalArgumentException(message);
        }

        //  Formula needs all values between 0 - 1.
        h = h % 360.0f;
        h /= 360f;
        s /= 100f;
        l /= 100f;

        float q;
        if (l < 0.5) {
            q = l * (1 + s);
        } else {
            q = (l + s) - (s * l);
        }

        float p = 2 * l - q;

        float r = Math.max(0, HueToRGB(p, q, h + (1.0f / 3.0f)));
        float g = Math.max(0, HueToRGB(p, q, h));
        float b = Math.max(0, HueToRGB(p, q, h - (1.0f / 3.0f)));

        r = Math.min(r, 1.0f);
        g = Math.min(g, 1.0f);
        b = Math.min(b, 1.0f);

        return new Color(r, g, b, alpha);
    }

    private float HueToRGB(float p, float q, float h) {
        if (h < 0) {
            h += 1;
        }

        if (h > 1) {
            h -= 1;
        }

        if (6 * h < 1) {
            return p + ((q - p) * 6 * h);
        }

        if (2 * h < 1) {
            return q;
        }

        if (3 * h < 2) {
            return p + ((q - p) * 6 * ((2.0f / 3.0f) - h));
        }

        return p;
    }

}
