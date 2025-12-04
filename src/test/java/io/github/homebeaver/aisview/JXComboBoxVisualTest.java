package io.github.homebeaver.aisview;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.MutableComboBoxModel;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXComboBox;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXFrame.StartPosition;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;

import org.jdesktop.swingx.binding.DisplayInfo;
import org.jdesktop.swingx.combobox.ListComboBoxModel;
import org.jdesktop.swingx.icon.RadianceIcon;

import io.github.homebeaver.aisview.LaFUtils;

@SuppressWarnings("serial")
public class JXComboBoxVisualTest extends JXPanel {

	protected static final boolean exitOnClose = true; // used in JXFrame of the demo

	public static void main(String[] args) {
		if (args.length > 0) {
			List<String> a = Arrays.asList(args);
			LaFUtils.setLAFandTheme(a);
		}
		SwingUtilities.invokeLater(() -> {
			JXComboBoxVisualTest panel = new JXComboBoxVisualTest(new BorderLayout());
			JXFrame frame = new JXFrame("JXComboBoxVisualTest", exitOnClose);
			frame.setTitle("frame.title");
			frame.setStartPosition(StartPosition.CenterInScreen);
			frame.getContentPane().add(panel);
			frame.pack();
			frame.setVisible(true);
		});
	}

	static private RadianceIcon map = io.github.homebeaver.icon.Map.of(RadianceIcon.SMALL_ICON,
			RadianceIcon.SMALL_ICON);
	public static Dimension VGAP15 = new Dimension(1, 15);

	private JXComboBox<DisplayInfo<Regions.Region>> positionChooserCombo;
	JXComboBox<String> hairCB;

	public JXComboBoxVisualTest(LayoutManager layout) {
		super(layout);
//    	Window w = (Window)MainJXframe.getInstance();
//    	if (w!=null) super.setDefaultLocale(w.getLocale());

		this.add(createPositionChooserCombo(), BorderLayout.NORTH);

		JXLabel l = new JXLabel("hair_description");
		l.setAlignmentX(JXLabel.LEFT_ALIGNMENT);
		this.add(l, BorderLayout.CENTER);
		hairCB = createHairComboBox();
		hairCB.setAlignmentX(JXComboBox.LEFT_ALIGNMENT);
		this.add(hairCB, BorderLayout.CENTER);
		l.setLabelFor(hairCB);
//		this.add(Box.createRigidArea(VGAP15), BorderLayout.SOUTH);
	}

	JComponent createPositionChooserCombo() {
		if (positionChooserCombo != null)
			return positionChooserCombo;
		positionChooserCombo = new JXComboBox<DisplayInfo<Regions.Region>>();
		positionChooserCombo.setName("positionChooserCombo");
		positionChooserCombo.setModel(createCBM());
		positionChooserCombo.setAlignmentX(LEFT_ALIGNMENT);
		positionChooserCombo.setComboBoxIcon(map);
		positionChooserCombo.setBorder(BorderFactory.createEmptyBorder(5, 50, 10, 50));

		positionChooserCombo.addActionListener(ae -> {
			int index = positionChooserCombo.getSelectedIndex();
			@SuppressWarnings("unchecked")
			DisplayInfo<Regions.Region> item = (DisplayInfo<Regions.Region>) positionChooserCombo.getSelectedItem();
//			LOG.info("Combo.SelectedItem=" + item.getDescription());
//			mapKit.setAddressLocation(item.getValue().getGeoPosition());
//			mapKit.setZoom(item.getValue().getZoom());
			positionChooserCombo.setSelectedIndex(index);
		});
		return positionChooserCombo;
	}

	private ComboBoxModel<DisplayInfo<Regions.Region>> createCBM() {
		MutableComboBoxModel<DisplayInfo<Regions.Region>> model = new DefaultComboBoxModel<DisplayInfo<Regions.Region>>();
		Regions.getInstance().getRegions().forEach((k, v) -> {
			model.addElement(new DisplayInfo<Regions.Region>(k, v));
		});
		return model;
	}

	JXComboBox<String> createHairComboBoxA() {
		JXComboBox<String> cb = new JXComboBox<String>();
//        void fillComboBox(JXComboBox<String> cb) {
		cb.addItem("brent");
		cb.addItem("georges");
		cb.addItem("hans");
		cb.addItem("howard");
		cb.addItem("james");
		cb.addItem("jeff");
		cb.addItem("jon");
		cb.addItem("lara");
		cb.addItem("larry");
		cb.addItem("lisa");
		cb.addItem("michael");
		cb.addItem("philip");
		cb.addItem("scott");
//    cb.addActionListener(this);
		return cb;
	}
	JXComboBox<String> createHairComboBox() {
		JXComboBox<String> cb = new JXComboBox<String>(createHairComboBoxModel());
		return cb;
	}
	
    protected ListComboBoxModel<String> createHairComboBoxModel() {
        return new ListComboBoxModel<String>(Arrays.asList(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"}));
    }
    protected ListComboBoxModel<Integer> createComboBoxModel() {
        return new ListComboBoxModel<Integer>(Arrays.asList(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}));
    }

}
