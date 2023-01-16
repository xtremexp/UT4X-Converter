/*
 * Viewer.java - This file is part of Java DDS ImageIO Plugin
 *
 * Copyright (C) 2011 Niklas Kyster Rasmussen
 * 
 * COPYRIGHT NOTICE:
 * Java DDS ImageIO Plugin is based on code from the DDS GIMP plugin.
 * Copyright (C) 2004-2010 Shawn Kirst <skirst@insightbb.com>,
 * Copyright (C) 2003 Arne Reuter <homepage@arnereuter.de>
 *
 * Java DDS ImageIO Plugin is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * Java DDS ImageIO Plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Java DDS ImageIO Plugin; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * FILE DESCRIPTION:
 * TODO Write File Description for Viewer.java
 */

package net.nikr.dds;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;


public class Viewer{
	
	private static final String ACTION_OPEN = "ACTION_OPEN";
	private static final String ACTION_RBG = "ACTION_RBG";
	private static final String ACTION_YCOCG = "ACTION_YCOCG";
	private static final String ACTION_YCOCG_SCALED = "ACTION_YCOCG_SCALED";
	private static final String ACTION_ALPHA_EXPONENT = "ACTION_ALPHA_EXPONENT";
	private static final String ACTION_SHOW_ALPHA = "ACTION_SHOW_ALPHA";
	private static final String ACTION_SHOW_RED = "ACTION_SHOW_RED";
	private static final String ACTION_SHOW_GREEN = "ACTION_SHOW_GREEN";
	private static final String ACTION_SHOW_BLUE = "ACTION_SHOW_BLUE";
	private static final String ACTION_BACKGROUND_COLOR = "ACTION_BACKGROUND_COLOR";
	
	private enum ColorType{
		RBG,
		YCOCG,
		YCOCG_SCALED,
		ALPHA_EXPONENT
	}
	private boolean alpha = true;
	private boolean red = true;
	private boolean green = true;
	private boolean blue = true;
	
	//Frame
	private JFrame jFrame;
	private JLabel jImageLabel;
	private JLabel jSpaceLabel;
	private JLabel jModeLabel;
	private JLabel jFormatLabel;
	private JLabel jDimensionLabel;
	private JLabel jMipMapLabel;
	private JLabel jAlphaLabel;
	private JLabel jRedLabel;
	private JLabel jGreenLabel;
	private JLabel jBlueLabel;
	private JLabel jPosLabel;
	
	private List<File> files = new ArrayList<File>();
	private Item item;
	private ColorType type;
	private int fileIndex = 0;
	private int mipMap;
	private int mipMapMax;
	private String format;
	private boolean updating = false;
	
	private final JFileChooser jFileChooser = new JFileChooser();
	
	public Viewer() {
		
		Listener listener = new Listener();
		
		jFileChooser.setAcceptAllFileFilterUsed(false);
		jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jFileChooser.setFileFilter(new DdsFilter(true));
		try {
			File dir = new File(Viewer.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
			dir = new File(dir.getAbsolutePath() + File.separator + "test-classes\\net\\nikr\\dds");
			System.out.println(dir.getAbsolutePath());
			if (dir.exists()) {
				jFileChooser.setCurrentDirectory(dir);
			}
		} catch (URISyntaxException ex) {
			//No problem :)
		}
		
		JPanel jPanel = new JPanel();
		GroupLayout groupLayout = new GroupLayout(jPanel);
		jPanel.setLayout(groupLayout);
		
		JPanel jImagePanel = new JPanel();
		GroupLayout imageGroupLayout = new GroupLayout(jImagePanel);
		jImagePanel.setLayout(imageGroupLayout);
		
		JScrollPane jImageScroll = new JScrollPane(jImagePanel);
		
		jImageLabel = new JLabel();
		jImageLabel.setBackground(Color.magenta);
		jImageLabel.setOpaque(true);
		jImageLabel.addMouseMotionListener(listener);
		jImageLabel.addMouseListener(listener);
		
		imageGroupLayout.setHorizontalGroup(
			imageGroupLayout.createSequentialGroup()
				.addGap(10, 10, Integer.MAX_VALUE)
				.addComponent(jImageLabel)
				.addGap(10, 10, Integer.MAX_VALUE)
				
		);
		
		imageGroupLayout.setVerticalGroup(
			imageGroupLayout.createSequentialGroup()
				.addGap(10, 10, Integer.MAX_VALUE)
				.addComponent(jImageLabel)
				.addGap(10, 10, Integer.MAX_VALUE)
		);
		
		JSeparator jSeparator = new JSeparator();
		
		Border border = BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 1, jPanel.getBackground().brighter())
				,
				BorderFactory.createMatteBorder(1, 1, 0, 0, jPanel.getBackground().darker())
				);
		
		
		jSpaceLabel = new JLabel();
		
		jModeLabel = new JLabel("  Mode: RBG");
		jModeLabel.setBorder(border);
		
		jFormatLabel = new JLabel();
		jFormatLabel.setBorder(border);
		
		jAlphaLabel = new JLabel();
		jAlphaLabel.setBorder(border);
		
		jRedLabel = new JLabel();
		jRedLabel.setBorder(border);
		
		jGreenLabel = new JLabel();
		jGreenLabel.setBorder(border);
		
		jBlueLabel = new JLabel();
		jBlueLabel.setBorder(border);
		
		jDimensionLabel = new JLabel();
		jDimensionLabel.setBorder(border);
		
		jMipMapLabel = new JLabel();
		jMipMapLabel.setBorder(border);
		
		jPosLabel = new JLabel();
		jPosLabel.setBorder(border);
	
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup()
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(jImageScroll)
				)
				.addComponent(jSeparator)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(2)
					.addComponent(jFormatLabel, 200, 200, 200)
					.addGap(2)
					.addComponent(jModeLabel, 150, 150, 150)
					.addGap(2)
					.addComponent(jDimensionLabel, 100, 100, 100)
					.addGap(2)
					.addComponent(jMipMapLabel, 100, 100, 100)
					.addGap(2)
					.addComponent(jPosLabel, 100, 100, 100)
					.addGap(2)
					.addComponent(jAlphaLabel, 40, 40, 40)
					.addGap(2)
					.addComponent(jRedLabel, 40, 40, 40)
					.addGap(2)
					.addComponent(jGreenLabel, 40, 40, 40)
					.addGap(2)
					.addComponent(jBlueLabel, 40, 40, 40)
					.addGap(2)
					.addComponent(jSpaceLabel, 0, 0, Integer.MAX_VALUE)
				)
				
		);
		groupLayout.setVerticalGroup(
			groupLayout.createSequentialGroup()
				.addComponent(jImageScroll)
				.addComponent(jSeparator, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addGap(2)
				.addGroup(groupLayout.createParallelGroup()
					.addComponent(jSpaceLabel, 22, 22, 22)
					.addComponent(jFormatLabel, 22, 22, 22)
					.addComponent(jModeLabel, 22, 22, 22)
					.addComponent(jDimensionLabel, 22, 22, 22)
					.addComponent(jMipMapLabel, 22, 22, 22)
					.addComponent(jAlphaLabel, 22, 22, 22)
					.addComponent(jRedLabel, 22, 22, 22)
					.addComponent(jGreenLabel, 22, 22, 22)
					.addComponent(jBlueLabel, 22, 22, 22)
					.addComponent(jPosLabel, 22, 22, 22)
				)
				.addGap(2)
		);
		
		JCheckBoxMenuItem jCheckBoxMenuItem;
		JMenuBar jMenuBar;
		JMenu jMenu, jSubMenu;
		JMenuItem jMenuItem;
		JRadioButtonMenuItem jRadioButtonMenuItem;
		
		jMenuBar = new JMenuBar();
		
		jMenu = new JMenu("File");
		jMenuBar.add(jMenu);
		
		jMenuItem = new JMenuItem("Open");
		jMenuItem.setActionCommand(ACTION_OPEN);
		jMenuItem.addActionListener(listener);
		jMenu.add(jMenuItem);
		
				
		jMenu = new JMenu("Colors");
		jMenuBar.add(jMenu);
		
		ButtonGroup group = new ButtonGroup();
		
		jSubMenu = new JMenu("Mode");
		jMenu.add(jSubMenu);
		
		jRadioButtonMenuItem = new JRadioButtonMenuItem("RBG");
		jRadioButtonMenuItem.setSelected(true);
		jRadioButtonMenuItem.setActionCommand(ACTION_RBG);
		jRadioButtonMenuItem.addActionListener(listener);
		jSubMenu.add(jRadioButtonMenuItem);
		group.add(jRadioButtonMenuItem);
		
		jRadioButtonMenuItem = new JRadioButtonMenuItem("YCoCg");
		jRadioButtonMenuItem.setActionCommand(ACTION_YCOCG);
		jRadioButtonMenuItem.addActionListener(listener);
		jSubMenu.add(jRadioButtonMenuItem);
		group.add(jRadioButtonMenuItem);
		
		jRadioButtonMenuItem = new JRadioButtonMenuItem("YCoCg Scaled");
		jRadioButtonMenuItem.setActionCommand(ACTION_YCOCG_SCALED);
		jRadioButtonMenuItem.addActionListener(listener);
		jSubMenu.add(jRadioButtonMenuItem);
		group.add(jRadioButtonMenuItem);
		
		jRadioButtonMenuItem = new JRadioButtonMenuItem("Alpha Exponent");
		jRadioButtonMenuItem.setActionCommand(ACTION_ALPHA_EXPONENT);
		jRadioButtonMenuItem.addActionListener(listener);
		jSubMenu.add(jRadioButtonMenuItem);
		group.add(jRadioButtonMenuItem);
		
		jMenu.addSeparator();
		
		jMenuItem = new JMenuItem("Background Color...");
		jMenuItem.setActionCommand(ACTION_BACKGROUND_COLOR);
		jMenuItem.addActionListener(listener);
		jMenu.add(jMenuItem);
		
		jMenu.addSeparator();
		
		jCheckBoxMenuItem = new JCheckBoxMenuItem("Show alpha channel");
		jCheckBoxMenuItem.setActionCommand(ACTION_SHOW_ALPHA);
		jCheckBoxMenuItem.addActionListener(listener);
		jCheckBoxMenuItem.setSelected(alpha);
		jMenu.add(jCheckBoxMenuItem);
		
		jCheckBoxMenuItem = new JCheckBoxMenuItem("Show red channel");
		jCheckBoxMenuItem.setActionCommand(ACTION_SHOW_RED);
		jCheckBoxMenuItem.addActionListener(listener);
		jCheckBoxMenuItem.setSelected(red);
		jMenu.add(jCheckBoxMenuItem);
		
		jCheckBoxMenuItem = new JCheckBoxMenuItem("Show green channel");
		jCheckBoxMenuItem.setActionCommand(ACTION_SHOW_GREEN);
		jCheckBoxMenuItem.addActionListener(listener);
		jCheckBoxMenuItem.setSelected(green);
		jMenu.add(jCheckBoxMenuItem);
		
		jCheckBoxMenuItem = new JCheckBoxMenuItem("Show blue channel");
		jCheckBoxMenuItem.setActionCommand(ACTION_SHOW_BLUE);
		jCheckBoxMenuItem.addActionListener(listener);
		jCheckBoxMenuItem.setSelected(blue);
		jMenu.add(jCheckBoxMenuItem);
		
		jFrame = new JFrame("DDS Viewer");
		jFrame.setSize(850, 600);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.getContentPane().add(jPanel);
		jFrame.setJMenuBar(jMenuBar);
		jFrame.addKeyListener(listener);
		jFrame.setMinimumSize(jPanel.getMinimumSize());
		jFrame.setVisible(true);

		clearFile();
	}
	
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
					createAndShowGUI();
			}
		});
	}
	
	private static void createAndShowGUI() {
		initLookAndFeel();

		//Make sure we have nice window decorations.
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);

		Viewer viewer = new Viewer();
	}

	private static void initLookAndFeel() {
		String lookAndFeel = UIManager.getSystemLookAndFeelClassName(); //System
		try {
			UIManager.setLookAndFeel(lookAndFeel);
		} catch (ClassNotFoundException e) {
			//Use default, no problem
		} catch (InstantiationException e) {
			//Use default, no problem
		} catch (IllegalAccessException e) {
			//Use default, no problem
		} catch (UnsupportedLookAndFeelException e) {
			//Use default, no problem
		}
	}
	
	private void openFile(final File file){
		files = Arrays.asList(file.getParentFile().listFiles(new DdsFilter(false)));
		Collections.sort(files);
		fileIndex = files.indexOf(file);
		loadFile(file);
	}
	
	private void loadFile(){
		if (!files.isEmpty()) {
			loadFile(files.get(fileIndex), mipMap);
		}
	}
	
	private void loadFile(File file){
		if (!files.isEmpty()) {
			loadFile(file, 0);
		}
	}
	
	private void loadFile(File file, int imageIndex){
		LoadFile load = new LoadFile(file, imageIndex);
		load.execute();
	}

	private void clearFile() {
		jFormatLabel.setText("  Format:  -");
		jAlphaLabel.setText("  A:  -");
		jRedLabel.setText("  R:  -");
		jGreenLabel.setText("  G:  -");
		jBlueLabel.setText("  B:  -");
		jDimensionLabel.setText("  Size:  -");
		jMipMapLabel.setText("  MipMap:  -");
		jPosLabel.setText("  Pos:  -");
		jImageLabel.setIcon(null);
		jFrame.setTitle("DDS Viewer");
	}
	
	private void update(){
		jFrame.setTitle("DDS Viewer - "+item.getFile().getName());
		jImageLabel.setIcon(new ImageIcon(item.getImage()));
		//jNameLabel.setText("  Name: "+item.getFile().getName());
		jDimensionLabel.setText("  Size: "+item.getImage().getWidth()+"x"+item.getImage().getHeight());
		jMipMapLabel.setText("  MipMap: "+(mipMap+1)+"/"+mipMapMax);
		jFormatLabel.setText("  Format: "+format);
		
		//Force mouse movment...
		Point b = MouseInfo.getPointerInfo().getLocation();
        int x = (int) b.getX();
        int y = (int) b.getY();
		try {
			Robot r = new Robot();
			r.mouseMove(x, y-1);
			r.mouseMove(x, y);
		} catch (AWTException ex) {
			
		}
	}
	
	public class Listener implements KeyListener, ActionListener, MouseMotionListener, MouseListener{
	
		@Override
		public void actionPerformed(ActionEvent e) {
			if (ACTION_OPEN.equals(e.getActionCommand())){
				int returnVal = jFileChooser.showOpenDialog(jFrame);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = jFileChooser.getSelectedFile();
					openFile(file);
				}
			}
			if (ACTION_RBG.equals(e.getActionCommand())){
				jModeLabel.setText("  Mode: RBG");
				type = ColorType.RBG;
				loadFile();
			}
			if (ACTION_YCOCG.equals(e.getActionCommand())){
				jModeLabel.setText("  Mode: YCoCg");
				type = ColorType.YCOCG;
				loadFile();
			}
			if (ACTION_YCOCG_SCALED.equals(e.getActionCommand())){
				jModeLabel.setText("  Mode: YCoCg Scaled");
				type = ColorType.YCOCG_SCALED;
				loadFile();
			}
			if (ACTION_ALPHA_EXPONENT.equals(e.getActionCommand())){
				jModeLabel.setText("  Mode: Alpha Exponent");
				type = ColorType.ALPHA_EXPONENT;
				loadFile();
			}
			if (ACTION_SHOW_ALPHA.equals(e.getActionCommand())){
				alpha = !alpha;
				loadFile();
			}
			if (ACTION_SHOW_RED.equals(e.getActionCommand())){
				red = !red;
				loadFile();
			}
			if (ACTION_SHOW_GREEN.equals(e.getActionCommand())){
				green = !green;
				loadFile();
			}
			if (ACTION_SHOW_BLUE.equals(e.getActionCommand())){
				blue = !blue;
				loadFile();
			}
			if (ACTION_BACKGROUND_COLOR.equals(e.getActionCommand())){
				Color color = JColorChooser.showDialog(jFrame, "Choose Background Color", jImageLabel.getBackground());
				if (color != null){
					jImageLabel.setBackground(color);
				}
			}
		}
	
		@Override
		public void mouseDragged(MouseEvent e) {}

		@Override
		public void mouseMoved(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			int rgb = item.getImage().getRGB(x, y);
			jAlphaLabel.setText("  A:"+((rgb>>24) & 0xff));
			jRedLabel.setText("  R:"+((rgb>>16) & 0xff));
			jGreenLabel.setText("  G:"+((rgb>>8) & 0xff));
			jBlueLabel.setText("  B:"+(rgb & 0xff));
			jPosLabel.setText("  Pos: "+x+"x"+y);
		}

		@Override
		public void mouseClicked(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {
			jAlphaLabel.setText("  A:  -");
			jRedLabel.setText("  R:  -");
			jGreenLabel.setText("  G:  -");
			jBlueLabel.setText("  B:  - ");
			jPosLabel.setText("  Pos:  -");
		}

		@Override
		public void keyTyped(KeyEvent e) {}

		@Override
		public void keyPressed(KeyEvent e) {
			switch (e.getKeyCode()){
				case KeyEvent.VK_RIGHT:
					if (!updating){
						updating = true;
						fileIndex++;
						if (fileIndex >= files.size()) fileIndex = 0;
						mipMap = 0;
						loadFile();
					} else {
						Toolkit.getDefaultToolkit().beep();
					}
					break;
				case KeyEvent.VK_LEFT:
					if (!updating){
						updating = true;
						fileIndex--;
						if (fileIndex < 0) fileIndex = files.size()-1;
						mipMap = 0;
						loadFile();
					} else {
						Toolkit.getDefaultToolkit().beep();
					}
					break;
				case KeyEvent.VK_UP:
					if (!updating){
						updating = true;
						mipMap--;
						if (mipMap >= 0 && mipMap < item.getMapMapCount()){
							loadFile();
						} else {
							mipMap++;
							updating = false;
						}
					} else {
						Toolkit.getDefaultToolkit().beep();
					}
					break;
				case KeyEvent.VK_DOWN:
					if (!updating){
						updating = true;
						mipMap++;
						if (mipMap >= 0 && mipMap < item.getMapMapCount()){
							loadFile();
						} else {
							mipMap--;
							updating = false;
						}
					} else {
						Toolkit.getDefaultToolkit().beep();
					}
					break;
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {}
	}
	
	public static class DdsFilter extends javax.swing.filechooser.FileFilter implements FileFilter {
		
		private boolean acceptDirectories;

		public DdsFilter() {
			this(true);
		}
		
		public DdsFilter(boolean acceptDirectories) {
			this.acceptDirectories = acceptDirectories;
		}
		
		@Override
		public boolean accept(File file) {
			if (file.isDirectory()) return acceptDirectories;
			int index = file.getName().lastIndexOf(".");
			if (index < 0) return false;
			return file.getName().substring(index).toLowerCase().equals(".dds");
		}

		@Override
		public String getDescription() {
			return "DDS files";
		}
	}
	
	private static class Item{
		private final BufferedImage image;
		private final File file;
		private final int mapMapCount;

		public Item(BufferedImage image, int mapMapCount, File file) {
			this.image = image;
			this.mapMapCount = mapMapCount;
			this.file = file;
		}

		public File getFile() {
			return file;
		}

		public BufferedImage getImage() {
			return image;
		}

		public int getMapMapCount() {
			return mapMapCount;
		}
	}
	
	private class LoadFile extends SwingWorker<Void, Void> {

		private final File file;
		private final int imageIndex;

		public LoadFile(File file, int imageIndex) {
			this.file = file;
			this.imageIndex = imageIndex;
		}
		
		@Override
		protected Void doInBackground() throws Exception {
			if (files.isEmpty()) {
				return null;
			}
			Iterator<ImageReader> iterator = ImageIO.getImageReadersBySuffix("dds");
			if (iterator.hasNext()){
				ImageReader imageReader = iterator.next();
				imageReader.setInput(new FileImageInputStream(file));
				mipMapMax = imageReader.getNumImages(true);
				if (imageIndex > mipMapMax || imageIndex < 0) {
					throw new IOException("imageIndex ("+imageIndex+") not found");
				}
				BufferedImage image = imageReader.read(imageIndex);
				format = imageReader.getFormatName();
				if (type == ColorType.YCOCG) DDSUtil.decodeYCoCg(image);
				if (type == ColorType.YCOCG_SCALED) DDSUtil.decodeYCoCgScaled(image);
				if (type == ColorType.ALPHA_EXPONENT) DDSUtil.decodeAlphaExponent(image);
				if (!alpha || !red || !green || !blue) DDSUtil.showColors(image, alpha, red, green, blue);
				item = new Item(image, mipMapMax, file);
			}
			return null;
		}

		@Override
		protected void done() {
			updating = false;
			super.done();
			try {
				get();
				update();
			} catch (Exception ex) {
				clearFile();
				JOptionPane.showMessageDialog(jFrame, "Failed to load image", "Error loading image", JOptionPane.WARNING_MESSAGE);
			}
		}
	}
}
