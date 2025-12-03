package io.github.homebeaver.aisview;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.ComboBoxModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.binding.DisplayInfo;
import org.jdesktop.swingx.icon.RadianceIcon;
import org.jdesktop.swingx.painter.AlphaPainter;
import org.jdesktop.swingx.painter.BusyPainter;

import io.github.homebeaver.aismodel.AisStreamKeyProvider;
import io.github.homebeaver.icon.Pause;
import io.github.homebeaver.icon.Play;

/*

	private JPanel jPanel2;
	private JXBusyLabel busyLabel;
	jPanel2 = new JPanel();
	jPanel2.setOpaque(false);
	jPanel2.setLayout(new GridBagLayout());
//	jPanel2.setBorder(new javax.swing.border.BevelBorder(javax.swing.border.BevelBorder.LOWERED)); // to show the jPanel1
		try {
			this.liveButton.setIcon(setLiveIcon());
			this.liveButton.setText("");
		} catch (Throwable thr) {
			LOG.warning(thr.getMessage());
			thr.printStackTrace();
		}
		liveButton = new JXButton();
		liveButton.setIcon(setLiveIcon());
		liveButton.setMargin(new Insets(2, 2, 2, 2));
		liveButton.setMaximumSize(new Dimension(48, 48));
		liveButton.setMinimumSize(new Dimension(48, 48));
		liveButton.setOpaque(false);
		liveButton.setBackground(new Color(0, 0, 0, 5));
		AlphaPainter<JComponent> ap = new AlphaPainter<>();
		ap.setAlpha(255);
		liveButton.setBackgroundPainter(ap);
		liveButton.setPreferredSize(new Dimension(48, 48));
		liveButton.addActionListener(evt -> {
			liveButtonActionPerformed(evt);
		});
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		jPanel2.add(liveButton, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		jPanel2.add(busyLabel(), gridBagConstraints);
...
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.insets = new Insets(4, 4, 4, 4);
		mainMap.add(jPanel2, gridBagConstraints);

im MapKit ein unsichtbarer Panel mit
- icon (start oder stop)
- wenn gestartet ein busyLabel über dem icon
 */
public class StartStopComponent extends JXPanel {

	private static final long serialVersionUID = 9141987064436884464L;

	static private RadianceIcon start = startA();
	static private RadianceIcon stop = stopA();
	static private RadianceIcon startA() {
		RadianceIcon ri = Play.of(RadianceIcon.ACTION_ICON, RadianceIcon.ACTION_ICON);
		ri.setColorFilter(color -> new Color(0, 0, 0, 128));
		return ri;
	}
	static private RadianceIcon stopA() {
		RadianceIcon ri = Pause.of(RadianceIcon.ACTION_ICON, RadianceIcon.ACTION_ICON);
		ri.setColorFilter(color -> new Color(0, 0, 0, 128));
		return ri;
	}

	private JXBusyLabel busyLabel;
	private JXButton liveButton;

	public StartStopComponent(String liveButtonText) {
		super(new GridBagLayout());
		setOpaque(false); // statdessen: setAlpha(.95f)
//		setAlpha(.95f);
		// uncomment to show the Panel
//		setBorder(new javax.swing.border.BevelBorder(javax.swing.border.BevelBorder.LOWERED)); 
		
		add(busyLabel(), gridBagConstraints(0, 0));
		add(liveButton(liveButtonText), gridBagConstraints(0, 0));
	}

	private GridBagConstraints gridBagConstraints(int x, int y) {
		GridBagConstraints gridBagConstraints;
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = x;
		gridBagConstraints.gridy = y;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		return gridBagConstraints;
	}

	private Component liveButton(String liveButtonText) {
		liveButton = new JXButton(liveButtonText);
		if (liveButtonText==null) {
			liveButton.setOpaque(false);
			liveButton.setBackground(new Color(0, 0, 0, 5));
			AlphaPainter<JComponent> ap = new AlphaPainter<>();
			ap.setAlpha(0.95f);
			liveButton.setBackgroundPainter(ap);
		} else {
			liveButton.setVerticalTextPosition(SwingConstants.BOTTOM);
			liveButton.setHorizontalTextPosition(SwingConstants.CENTER);
//			liveButton.setMaximumSize(new Dimension(48, 48));
			liveButton.setMinimumSize(new Dimension(48, 48));
//			liveButton.setPreferredSize(new Dimension(48, 48));
		}
		liveButton.setIcon(setLiveIcon());
		liveButton.setMargin(new Insets(2, 2, 2, 2));
		liveButton.addActionListener(evt -> {
			liveButtonActionPerformed(evt);
		});
		return liveButton;
	}
	private Icon setLiveIcon() {
		return start;
	}
	
	SwingWorker<?, ?> swingWorker;
	MessageLoader swingWorker1;
	ComboBoxModel<?> cbmWithRegions;
	SwingWorker<?, ?> swingWorker2; // alternativer swingWorker ohne key
	public void setSwingWorker(MessageLoader sw1, ComboBoxModel<?> regions, SwingWorker<?, ?> sw2) {
		swingWorker1 = sw1;
		this.cbmWithRegions = regions;
		swingWorker2 = sw2;
	}
	private void liveButtonActionPerformed(ActionEvent evt) {
		if (liveButton.getIcon() == start) {
			AisStreamKeyProvider keyProvider = AisStreamKeyProvider.getInstance();
			if(keyProvider.getKey()==null) {
				// get the key - showInputDialog
				String result = showInputDialog();
				if ((result != null) && (result.length() > 0)) {
					keyProvider.setKey(result);
					Object o = cbmWithRegions.getSelectedItem();
					if (o instanceof DisplayInfo di) {
						Object r = di.getValue();
						if (r instanceof Regions.Region region) {
							swingWorker1.setBoundingBox(region.getBoundingBox());
						}
					}
					swingWorker = swingWorker1;
					busyLabel.setVisible(true); // initially not visible, busyLabel nur bei live
				} else {
					swingWorker = swingWorker2;
				}
			} else {
				// TODO unschön - dupl.code
				Object o = cbmWithRegions.getSelectedItem();
				if (o instanceof DisplayInfo di) {
					Object r = di.getValue();
					if (r instanceof Regions.Region region) {
						swingWorker1.setBoundingBox(region.getBoundingBox());
					}
				}
				swingWorker = swingWorker1;
				busyLabel.setVisible(true); // initially not visible, busyLabel nur bei live
			}
			// TODO was wenn key invalid?
			try {
				swingWorker.execute();
				liveButton.setIcon(stop);
				busyLabel.setBusy(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			if (swingWorker.cancel(true)) {
//				LOG.info("canceled.");
			}
			liveButton.setIcon(start); // TODO SwingWorker.StateValue keine Pause, denn restart nicht möglich
			busyLabel.setBusy(false);
		}
	}
	
	private static final String OPTIONPANE_TITLETEXT = "OptionPane.titleText";
	private static final String OPTIONPANE_MESSAGE   = "OptionPane.messageDialogTitle";
	private static final String OPTIONPANE_INPUT     = "OptionPane.inputDialogTitle";
	private boolean isNimbus() {
		return UIManager.getLookAndFeel().getClass().getName().contains("Nimbus");
	}
	private String getUIString(Object key) {
		return UIManager.getString(key, getLocale());
	}

	public String showInputDialog() {
		String result = (String)JOptionPane.showInputDialog
				( (Component)null
				, "Enter your AISStream API key:" // in DE TODO
				, getUIString(OPTIONPANE_INPUT)
				, JOptionPane.QUESTION_MESSAGE
				// the Icon to display: ( bei Nimbus OK, sonst unschön - ? auf grünem Quadrat)
//				, isNimbus() ? null : Key.of(RadianceIcon.BUTTON_ICON, RadianceIcon.BUTTON_ICON)
//				, null, null  // selectionValues, initialSelectionValue
				);
		return result;
	}

	// TODO die positionen stimmen nur für nimbus
	private Component busyLabel() {
//		busyLabel = new JXBusyLabel(new Dimension(48, 48)); // simple
		// complicated:
		BusyPainter painter = new BusyPainter(ShapeFactory.createEllipticalPoint(10, 10),
				new RoundRectangle2D.Float(10.0f, 2.0f, 35.0f, 35.0f, 10, 10) // trajectory
		);
		painter.setTrailLength(10);
		painter.setPoints(31);
		painter.setFrame(1);
		
		Dimension dim = new Dimension(48, 48);
		busyLabel = new JXBusyLabel(dim); // 100, 84
		busyLabel.setPreferredSize(dim);
		busyLabel.setBusyPainter(painter);
		busyLabel.setBusy(false);
		busyLabel.setVisible(false); // Schweif beim initialisieren vermeiden
		return busyLabel;
	}

}
