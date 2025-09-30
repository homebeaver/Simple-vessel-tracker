package org.jxmapviewer.demos;

import java.awt.Color;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.demos.svg.FeatheRnavigation_grey;
import org.jdesktop.swingx.icon.RadianceIcon;

import dk.dma.ais.message.ShipTypeCargo;
import dk.dma.ais.message.ShipTypeColor;
import swingset.AbstractDemo;

@SuppressWarnings("serial")
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

	private JLabel emptyBorderLabel() {
		JLabel b = new JLabel();
		b.setBorder(new EmptyBorder(5, 0, 5, 5));
		b.setVerticalTextPosition(SwingConstants.CENTER);
		b.setHorizontalTextPosition(SwingConstants.RIGHT);
		return b;
	}

	public JLabel blueLabel() {
		JLabel b = emptyBorderLabel();
		b.setName("blue");
		b.setText(getBundleString("PASSENGER"));
		RadianceIcon icon = FeatheRnavigation_grey.of(RadianceIcon.S, RadianceIcon.S);
		icon.setColorFilter(color -> Color.BLUE);
		b.setIcon(icon);
		return b;
	}

	public JLabel greyLabel() {
		JLabel b = emptyBorderLabel();
		b.setName("grey");
		b.setText(getBundleString("UNKNOWN"));
		RadianceIcon icon = FeatheRnavigation_grey.of(RadianceIcon.S, RadianceIcon.S);
		icon.setColorFilter(color -> Color.LIGHT_GRAY);
		b.setIcon(icon);
		return b;
	}

	public JLabel greenLabel() {
		JLabel b = emptyBorderLabel();
		b.setName("green");
		b.setText(getBundleString("CARGO"));
		RadianceIcon icon = FeatheRnavigation_grey.of(RadianceIcon.S, RadianceIcon.S);
		icon.setColorFilter(color -> Color.GREEN);
		b.setIcon(icon);
		return b;
	}

	public JLabel orangeLabel() {
		JLabel b = emptyBorderLabel();
		b.setName("orange");
		b.setText(getBundleString("FISHING"));
		RadianceIcon icon = FeatheRnavigation_grey.of(RadianceIcon.S, RadianceIcon.S);
		icon.setColorFilter(color -> Color.ORANGE);
		b.setIcon(icon);
		return b;
	}

	public JLabel magentaLabel() {
		JLabel b = emptyBorderLabel();
		b.setName("magenta");
		b.setText(getBundleString("PLEASURE"));
		RadianceIcon icon = FeatheRnavigation_grey.of(RadianceIcon.S, RadianceIcon.S);
		icon.setColorFilter(color -> Color.MAGENTA);
		b.setIcon(icon);
		return b;
	}

	public JLabel redLabel() {
		JLabel b = emptyBorderLabel();
		b.setName("red");
		b.setText(getBundleString("TANKER"));
		RadianceIcon icon = FeatheRnavigation_grey.of(RadianceIcon.S, RadianceIcon.S);
		icon.setColorFilter(color -> Color.RED);
		b.setIcon(icon);
		return b;
	}

	public JLabel cyanLabel() {
		JLabel b = emptyBorderLabel();
		b.setName("cyan");
		b.setText(getBundleString("Pilot,Tug,SAR"));
		RadianceIcon icon = FeatheRnavigation_grey.of(RadianceIcon.S, RadianceIcon.S);
		icon.setColorFilter(color -> Color.CYAN);
		b.setIcon(icon);
		return b;
	}

	public JLabel yellowLabel() {
		JLabel b = emptyBorderLabel();
		b.setName("yellow");
		b.setText(getBundleString("High Speed"));
		RadianceIcon icon = FeatheRnavigation_grey.of(RadianceIcon.S, RadianceIcon.S);
		icon.setColorFilter(color -> Color.YELLOW);
		b.setIcon(icon);
		return b;
	}

	private JButton emptyBorderButton() {
		JButton b = new JButton();
		b.setBorder(new EmptyBorder(0, 0, 0, 0));
//    	b.setPreferredSize(new Dimension(220,30));
//    	b.setSize(120, 30);
		b.setVerticalTextPosition(SwingConstants.BOTTOM);
		b.setHorizontalTextPosition(SwingConstants.CENTER);
		return b;
	}

	public JButton blueButton() {
		JButton b = emptyBorderButton();
		b.setName("blue");
		b.setText(getBundleString("PASSENGER"));
		RadianceIcon icon = FeatheRnavigation_grey.of(RadianceIcon.S, RadianceIcon.S);
		icon.setColorFilter(color -> Color.BLUE);
		icon.setRotation(RadianceIcon.EAST);
		b.setIcon(icon);
		return b;
	}

	public JButton greyButton() {
		JButton b = emptyBorderButton();
		b.setName("grey");
		b.setText(getBundleString("UNKNOWN"));
		RadianceIcon icon = FeatheRnavigation_grey.of(RadianceIcon.S, RadianceIcon.S);
		icon.setColorFilter(color -> Color.LIGHT_GRAY);
		icon.setRotation(RadianceIcon.EAST);
		b.setIcon(icon);
		return b;
	}

	public JButton greenButton() {
		JButton b = emptyBorderButton();
		b.setName("green");
		b.setText(getBundleString("CARGO"));
		RadianceIcon icon = FeatheRnavigation_grey.of(RadianceIcon.S, RadianceIcon.S);
		icon.setColorFilter(color -> Color.GREEN);
		icon.setRotation(RadianceIcon.EAST);
		b.setIcon(icon);
		return b;
	}

	public JButton orangeButton() {
		JButton b = emptyBorderButton();
		b.setName("orange");
		b.setText(getBundleString("FISHING"));
		RadianceIcon icon = FeatheRnavigation_grey.of(RadianceIcon.S, RadianceIcon.S);
		icon.setColorFilter(color -> Color.ORANGE);
		icon.setRotation(RadianceIcon.EAST);
		b.setIcon(icon);
		return b;
	}

	public JButton magentaButton() {
		JButton b = emptyBorderButton();
		b.setName("magenta");
		b.setText(getBundleString("PLEASURE"));
		RadianceIcon icon = FeatheRnavigation_grey.of(RadianceIcon.S, RadianceIcon.S);
		icon.setColorFilter(color -> Color.MAGENTA);
		icon.setRotation(RadianceIcon.EAST);
		b.setIcon(icon);
		return b;
	}

	public JButton redButton() {
		JButton b = emptyBorderButton();
		b.setName("red");
		b.setText(getBundleString("TANKER"));
		RadianceIcon icon = FeatheRnavigation_grey.of(RadianceIcon.S, RadianceIcon.S);
		icon.setColorFilter(color -> Color.RED);
		icon.setRotation(RadianceIcon.EAST);
		b.setIcon(icon);
		return b;
	}

	public JButton cyanButton() {
		JButton b = emptyBorderButton();
		b.setName("cyan");
		b.setText(getBundleString("Pilot,Tug,SAR"));
		RadianceIcon icon = FeatheRnavigation_grey.of(RadianceIcon.S, RadianceIcon.S);
		icon.setColorFilter(color -> Color.CYAN);
		icon.setRotation(RadianceIcon.EAST);
		b.setIcon(icon);
		return b;
	}

	public JButton yellowButton() {
		JButton b = emptyBorderButton();
		b.setName("yellow");
		b.setText(getBundleString("High Speed"));
		RadianceIcon icon = FeatheRnavigation_grey.of(RadianceIcon.S, RadianceIcon.S);
		icon.setColorFilter(color -> Color.YELLOW);
		icon.setRotation(RadianceIcon.EAST);
		b.setIcon(icon);
		return b;
	}

	@Override
	public JXPanel getControlPane() {
		// TODO Auto-generated method stub
		return null;
	}
}
