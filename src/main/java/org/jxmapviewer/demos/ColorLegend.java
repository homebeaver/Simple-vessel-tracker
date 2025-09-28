package org.jxmapviewer.demos;

import java.awt.Color;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.demos.svg.FeatheRnavigation_grey;
import org.jdesktop.swingx.icon.PlayIcon;
import org.jdesktop.swingx.icon.RadianceIcon;

import dk.dma.ais.message.ShipTypeCargo;
import dk.dma.ais.message.ShipTypeColor;
import swingset.AbstractDemo;

public class ColorLegend extends AbstractDemo {

	/*
	// this works for up to 10 elements:
	Map<String, String> test1 = Map.of(
	    "a", "b",
	    "c", "d"
	);
	 */
	private static Map<ShipTypeColor, Color> typeToColor = Map.of(
		ShipTypeColor.BLUE, Color.BLUE,
		ShipTypeColor.GREY, Color.LIGHT_GRAY,
		ShipTypeColor.GREEN, Color.GREEN,
		ShipTypeColor.ORANGE, Color.ORANGE,
		ShipTypeColor.PURPLE, Color.MAGENTA,
		ShipTypeColor.RED, Color.RED,
		ShipTypeColor.TURQUOISE, Color.CYAN,
		ShipTypeColor.YELLOW, Color.YELLOW
	);
	public static Color typeToColor(int shipType) {
		ShipTypeCargo stype = new ShipTypeCargo(shipType);
		ShipTypeColor stc = ShipTypeColor.getColor(stype.getShipType());
		return typeToColor.get(stc);
	}

	public static ColorLegend SINGLETON = new ColorLegend();
	private ColorLegend() {}

    public JButton blueButton() {
    	JButton b = new JButton();
    	b.setBorder(new EmptyBorder(5,0,5,10));
    	b.setName("blue");
    	b.setText(getBundleString("PASSENGER"));
//    	b.setVerticalTextPosition(SwingConstants.BOTTOM);
//    	b.setHorizontalTextPosition(SwingConstants.CENTER);
    	RadianceIcon icon = FeatheRnavigation_grey.of(RadianceIcon.M, RadianceIcon.M);
    	icon.setColorFilter(color -> Color.BLUE);
    	b.setIcon(icon);
    	return b;
    }

    public JButton greyButton() {
    	JButton b = new JButton();
    	b.setBorder(new EmptyBorder(5,0,5,10));
    	b.setName("grey");
    	b.setText(getBundleString("UNKNOWN"));
    	RadianceIcon icon = FeatheRnavigation_grey.of(RadianceIcon.M, RadianceIcon.M);
    	icon.setColorFilter(color -> Color.LIGHT_GRAY);
    	b.setIcon(icon);
    	return b;
    }

    public JButton greenButton() {
    	JButton b = new JButton();
    	b.setBorder(new EmptyBorder(5,0,5,10));
    	b.setName("green");
    	b.setText(getBundleString("CARGO"));
    	RadianceIcon icon = FeatheRnavigation_grey.of(RadianceIcon.M, RadianceIcon.M);
    	icon.setColorFilter(color -> Color.GREEN);
    	b.setIcon(icon);
    	return b;
    }

    public JButton orangeButton() {
    	JButton b = new JButton();
    	b.setBorder(new EmptyBorder(5,0,5,10));
    	b.setName("orange");
    	b.setText(getBundleString("FISHING"));
    	RadianceIcon icon = FeatheRnavigation_grey.of(RadianceIcon.M, RadianceIcon.M);
    	icon.setColorFilter(color -> Color.ORANGE);
    	b.setIcon(icon);
    	return b;
    }

    public JButton magentaButton() {
    	JButton b = new JButton();
    	b.setBorder(new EmptyBorder(5,0,5,10));
    	b.setName("magenta");
    	b.setText(getBundleString("PLEASURE"));
    	RadianceIcon icon = FeatheRnavigation_grey.of(RadianceIcon.M, RadianceIcon.M);
    	icon.setColorFilter(color -> Color.MAGENTA);
    	b.setIcon(icon);
    	return b;
    }

    public JButton redButton() {
    	JButton b = new JButton();
    	b.setBorder(new EmptyBorder(5,0,5,10));
    	b.setName("red");
    	b.setText(getBundleString("TANKER"));
    	RadianceIcon icon = FeatheRnavigation_grey.of(RadianceIcon.M, RadianceIcon.M);
    	icon.setColorFilter(color -> Color.RED);
    	b.setIcon(icon);
    	return b;
    }
    public JButton cyanButton() {
    	JButton b = new JButton();
    	b.setBorder(new EmptyBorder(5,0,5,10));
    	b.setName("cyan");
    	b.setText(getBundleString("Pilot,Tug,SAR"));
    	RadianceIcon icon = FeatheRnavigation_grey.of(RadianceIcon.M, RadianceIcon.M);
    	icon.setColorFilter(color -> Color.CYAN);
    	b.setIcon(icon);
    	return b;
    }
    public JButton yellowButton() {
    	JButton b = new JButton();
    	b.setBorder(new EmptyBorder(5,0,5,10));
    	b.setName("yellow");
    	b.setText(getBundleString("High Speed"));
    	RadianceIcon icon = FeatheRnavigation_grey.of(RadianceIcon.M, RadianceIcon.M);
    	icon.setColorFilter(color -> Color.YELLOW);
    	b.setIcon(icon);
    	return b;
    }
@Override
public JXPanel getControlPane() {
	// TODO Auto-generated method stub
	return null;
}
}
