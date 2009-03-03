package UI;

import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.ActionEvent;
import java.awt.Event;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLDrawable;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.GLException;
import javax.media.opengl.GLPbuffer;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.*;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import javax.swing.JSplitPane;
import java.awt.GridBagConstraints;
import javax.swing.JCheckBox;
import java.awt.Insets;
import nehe.GLDisplayPanel;
import javax.swing.JTextField;
import javax.swing.JSlider;
import java.awt.Font;
import java.util.HashMap;
import java.util.TreeSet;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import com.sun.opengl.util.Screenshot;

import data.*;

import semaGL.*;
import sun.security.krb5.internal.UDPClient;
import UI.SimButton;
import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;


/**
 * This code was edited or generated using CloudGarden's Jigloo
 * SWT/Swing GUI Builder, which is free for non-commercial
 * use. If Jigloo is being used commercially (ie, by a corporation,
 * company or business for any purpose whatever) then you
 * should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details.
 * Use of Jigloo implies acceptance of these licensing terms.
 * A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
 * THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
 * LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class SwingSema {

	{
		//Set Look & Feel
		try {
			javax.swing.UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");

		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Launches this application
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			private SwingSema application;
			private SemaSpace space;
			private GLDisplayPanel semaGLDisplay;

			public void run() {
				semaGLDisplay = GLDisplayPanel.createGLDisplay("SemaSpace");

				space = new SemaSpace();
				semaGLDisplay.addGLEventListener(space);
				application = new SwingSema();
				application.setSema(space);
				application.getMainWindow().setVisible(true);
				application.jSplitPane.setRightComponent(semaGLDisplay.getJPanel());
				semaGLDisplay.start();
			}
		});
	}

	SemaSpace app = null;  //  @jve:decl-index=0:
	private JMenuBar jJMenuBar = null;
	private JList edgeAttList;
	private JScrollPane edgeAttPane;
	private JSplitPane AttributeSplitPane1;
	private JList nodeAttList;
	private JScrollPane nodeAttPane;
	private JFileChooser openPicDir = null;
	private JFileChooser openFile = null;
	private JFileChooser saveFile = null;
	private DefaultListModel nodeListModel;
	private JTable edgeJTable;
	private JScrollPane eListScrollPane;
	private DefaultTableModel eTableModel;
	protected String friendID;
	private JSplitPane edgeWndSplitPane = null;
	private JPanel edgeListPanel = null;
	private JTextField columnAField = null;
	private JTextField columnBField = null;
	private SimButton selectAButton = null;
	private SimButton selectBButton = null;
	private SimButton addEdgeButton = null;
	private JMenuItem exitMenuItem;
	private JMenuItem saveMenuItem;
	private JMenuItem openNodeMenuItem;
	private JMenuItem openMenuItem;
	private JMenuItem openPicMenuItem;
	private JMenu fileMenu;
	private JLabel jLabel6;
	private JPanel dummyPanel;
	private JLabel jLabel5;
	private JLabel jLabel4;
	private JLabel jLabel3;
	private JCheckBox repellBox1;
	private JCheckBox repNeighbors;
	private JCheckBox treeBox;
	private JCheckBox timeBox;
	private JCheckBox clusters;
	private JLabel forces;
	private JLabel strengthLabel;
	private JLabel pushLabel11;
	private JLabel pushLabel1;
	private JLabel pushLabel;
	private JLabel group;
	private JLabel distLabel;
	private JLabel valenzLabel;
	private JSlider strengthSlider;
	private JSlider stretchSlider;
	private JSlider repellStSlider;
	private JSlider pushSlider;
	private JSlider groupRadius;
	private JSlider distanceSlider;
	private JSlider valenzSlider;
	private JPanel layoutTab;
	private JLabel sizeLabel;
	private JSlider sizeSlider;
	private JLabel display;
	private JCheckBox noRender;
	private JCheckBox drawedges;
	private SimButton texButton1;
	private JPanel viewTab;
	private JLabel jLabel2;
	private SimButton SimButton2;
	private SimButton expandnet;
	private SimButton expand2;
	private SimButton showAll;
	private SimButton randomCenter;
	private SimButton interrupt;
	private SimButton elimButton1;
	private JLabel search;
	private SimButton seachSelButton;
	private JLabel depthLabel;
	private JSlider depth;
	private SimButton searchButton;
	private SimButton simButton19;
	private SimButton simButton18;
	private JLabel jLabel18;
	private JCheckBox drawgroups;
	private JCheckBox jFileFormat;
	private JLabel jLabel17;
	private JSlider jSlider4;
	private JSlider jSlider3;
	private JLabel jLabel16;
	private SimButton simButton16;
	private SimButton simButton15;
	private JCheckBox tiltBox;
	private SimButton simButton17;
	private SimButton saveNetButton;
	private JLabel jLabel15;
	private JLabel jLabel12;
	private JLabel jLabel14;
	private JLabel jLabel13;
	private SimButton simButton14;
	private JCheckBox directedGraph;
	private JCheckBox draw3d;
	private SimButton simButton13;
	private SimButton simButton12;
	private SimButton simButton11;
	private SimButton shuffleButton;
	private SimButton simButton6;
	private SimButton boxButton;
	private SimButton inflateButton;
	private SimButton simButton10;
	private JCheckBox drawclusters;
	private JScrollPane nets;
	private SimButton clear;
	private SimButton saveNet;
	private SimButton imgDir;
	private SimButton loadNodeAtt;
	private JButton loadNet;
	private JPanel file;
	private JCheckBox jCheckBox1;
	private SimButton simButton9;
	private JCheckBox renderTextures;
	private SimButton simButton8;
	private SimButton simButton7;
	private JCheckBox add;
	private JLabel jLabel11;
	private SimButton simButton5;
	private SimButton simButton4;
	private JTextField searchTerm;
	private JPanel dataTab;
	private JScrollPane nodes;
	private JTabbedPane inspectors;
	private JSplitPane jSplitPane1;
	private JTabbedPane controlPanel;
	private JSplitPane jSplitPane;
	private JList nodeList;
	private JTextArea jTextFieldMsg;
	private JFrame statusMessage;
	private JFrame mainWindow;
	private DefaultListModel nodeAttModel;
	private DefaultListModel edgeAttModel;
	private JCheckBox forceBox;
	private JLabel jLabel1;
	private JSlider fontslider;
	private JCheckBox fadenodes;
	private JPanel midPanels;
	private JSplitPane jSplitPane2;
	private JLabel jLabel10;
	private JSlider jSlider2;
	private JLabel jLabel9;
	private JSlider picSizeSlider;
	private JLabel jLabel8;
	private SimButton simButton3;
	private SimButton simButton2;
	private SimButton simButton1;
	private JLabel jLabel7;
	private JSlider jSlider1;
	private JList netList;
	private DefaultListModel netListModel;
	protected boolean change=true;
	/**
	 * This method initializes SimButton3	
	 * 	
	 * @return javax.swing.SimButton	
	 */
	private SimButton getAddEdgeButton() {
		if (addEdgeButton == null) {
			addEdgeButton = new SimButton();
			addEdgeButton.setText("add Edge");
			addEdgeButton.setFont(new Font("Dialog", Font.PLAIN, 10));
			addEdgeButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					app.addEdge(columnAField.getText(), columnBField.getText());
				}
			});
		}
		return addEdgeButton;
	}


	/**
	 * This method initializes columnAField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getColumnAField() {
		if (columnAField == null) {
			columnAField = new JTextField();
		}
		return columnAField;
	}

	/**
	 * This method initializes columnBField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getColumnBField() {
		if (columnBField == null) {
			columnBField = new JTextField();
		}
		return columnBField;
	}


	/**
	 * This method initializes edgeListPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getEdgeListPanel() {
		if (edgeListPanel == null) {
			GridBagConstraints gridBagConstraints52 = new GridBagConstraints();
			gridBagConstraints52.gridx = 1;
			gridBagConstraints52.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints52.gridy = 2;
			GridBagConstraints gridBagConstraints50 = new GridBagConstraints();
			gridBagConstraints50.gridx = 1;
			gridBagConstraints50.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints50.gridy = 0;
			GridBagConstraints gridBagConstraints49 = new GridBagConstraints();
			gridBagConstraints49.gridx = 0;
			gridBagConstraints49.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints49.gridy = 0;
			GridBagConstraints gridBagConstraints48 = new GridBagConstraints();
			gridBagConstraints48.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints48.gridy = 1;
			gridBagConstraints48.weightx = 1.0;
			gridBagConstraints48.gridx = 1;
			GridBagConstraints gridBagConstraints47 = new GridBagConstraints();
			gridBagConstraints47.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints47.gridy = 1;
			gridBagConstraints47.weightx = 1.0;
			gridBagConstraints47.gridx = 0;
			edgeListPanel = new JPanel();
			edgeListPanel.setLayout(new GridBagLayout());
			edgeListPanel.setPreferredSize(new Dimension(166, 80));
			//			edgeListPanel.setBackground(new java.awt.Color(192,192,192));
			edgeListPanel.add(getColumnAField(), gridBagConstraints47);
			edgeListPanel.add(getColumnBField(), gridBagConstraints48);
			edgeListPanel.add(getSelectAButton(), gridBagConstraints49);
			edgeListPanel.add(getSelectBButton(), gridBagConstraints50);
			edgeListPanel.add(getAddEdgeButton(), gridBagConstraints52);
		}
		return edgeListPanel;
	}

	/**
	 * This method initializes edgeWndSplitPane	
	 * 	
	 * @return javax.swing.JSplitPane	
	 */
	private JSplitPane getEdgeWndSplitPane() {
		if (edgeWndSplitPane == null) {
			edgeWndSplitPane = new JSplitPane();
			edgeWndSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			edgeWndSplitPane.setDividerLocation(75);
			edgeWndSplitPane.setDividerSize(0);
			//			edgeWndSplitPane.setBackground(new java.awt.Color(192,192,192));
			edgeWndSplitPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
			edgeWndSplitPane.setTopComponent(getEdgeListPanel());
			eTableModel = new DefaultTableModel();
			newETable();
			edgeJTable = new JTable(eTableModel);
			edgeJTable.setAutoCreateColumnsFromModel(false);
			edgeJTable.setName("edges");
			edgeJTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			edgeJTable.setRowSelectionAllowed(false);
			edgeJTable.setFocusable(false);
			eListScrollPane = new JScrollPane(edgeJTable);
			edgeWndSplitPane.setBottomComponent(eListScrollPane);
			//			eListScrollPane.setBackground(new java.awt.Color(192,192,192));
			eListScrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
			eListScrollPane.setName("edges");
		}
		return edgeWndSplitPane;
	}


	/**
	 * This method initializes mainWindow
	 * 
	 * @return javax.swing.JFrame
	 */

	public JFrame getMainWindow() {
		if (mainWindow == null) {
			mainWindow = new JFrame();
			mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			mainWindow.setSize(1000,600);
			mainWindow.setTitle("SemaSpace");
			mainWindow.setContentPane(getJSplitPane());
			openFile = new JFileChooser();
			openPicDir = new JFileChooser();
			openPicDir.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			saveFile = new JFileChooser();
		}
		return mainWindow;
	}



	private JFrame getStatusMessage() {
		if(statusMessage == null) {
			statusMessage = new JFrame();
			statusMessage.getContentPane().setLayout(new BorderLayout());
			statusMessage.setTitle("RenderStatus");
			statusMessage.getContentPane().add(getJTextFieldMsg(), BorderLayout.CENTER);
			statusMessage.setBounds(400, 650, 400, 200);
			statusMessage.setSize(400, 200);
			getJTextFieldMsg().setSize(statusMessage.getSize());
		}
		return statusMessage;
	}

	/**
	 * This method initializes jJMenuBar	
	 * 	
	 * @return javax.swing.JMenuBar	
	 */
	JMenuBar getJJMenuBar() {
		if (jJMenuBar == null) {
			jJMenuBar = new JMenuBar();
			jJMenuBar.addFocusListener(new FocusAdapter() {
				public void focusGained(FocusEvent evt) {
					jJMenuBar.paintAll(jJMenuBar.getGraphics());
				}
			});
			jJMenuBar.add(getFileMenu());
		}
		return jJMenuBar;
	}



	private JTextArea getJTextFieldMsg() {
		if(jTextFieldMsg == null) {
			jTextFieldMsg = new JTextArea();
			jTextFieldMsg.setText("jTextField1");
			jTextFieldMsg.setLineWrap(true);
			jTextFieldMsg.setWrapStyleWord(true);
			getStatusMessage().setVisible(true);
		}
		return jTextFieldMsg;
	}



	/**
	 * This method initializes selectAButton	
	 * 	
	 * @return javax.swing.SimButton	
	 */
	private SimButton getSelectAButton() {
		if (selectAButton == null) {
			selectAButton = new SimButton();
			selectAButton.setText("> selected");
			selectAButton.setFont(new Font("Dialog", Font.PLAIN, 10));
			selectAButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					columnAField.setText(app.getPicked().name); //  Auto-generated Event stub actionPerformed()
				}
			});
		}
		return selectAButton;
	}

	/**
	 * This method initializes selectBButton	
	 * 	
	 * @return javax.swing.SimButton	
	 */
	private SimButton getSelectBButton() {
		if (selectBButton == null) {
			selectBButton = new SimButton();
			selectBButton.setText("> selected");
			selectBButton.setFont(new Font("Dialog", Font.PLAIN, 10));
			selectBButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					columnBField.setText(app.getPicked().name); //  Auto-generated Event stub actionPerformed()
				}
			});
		}
		return selectBButton;
	}



	private void newETable() {
		eTableModel.setColumnCount(2);
		while (eTableModel.getRowCount() > 0) {
			eTableModel.removeRow(0);
		}
	}

	public void setCounter() {
		String title = "SemaSpace - "+app.ns.global.nNodes.size()+" Nodes, "+app.ns.global.nEdges.size()+" Edges";
		if (app.ns.view!=app.ns.global) title+="; visible: "+app.ns.view.nNodes.size()+" Nodes, "+app.ns.view.nEdges.size()+" Edges";
		getMainWindow().setTitle(title);
	}

	public void setMsg(String msg){
		getJTextFieldMsg().setText(msg);
	}
	public void setSema(SemaSpace space) {
		app = space;
		app.setSwing(this);
	}

	private void initSliders() {
		depth.setValue((int) (app.getDepth()));
		fontslider.setValue(app.getFonttype());
		sizeSlider.setValue((int)(app.getSize()));
		valenzSlider.setValue((int)(app.getVal()*100));
		groupRadius.setValue((int)(app.getClusterRad()*10));
		pushSlider.setValue((int)(app.getRepell()));
		stretchSlider.setValue((int)(app.getPermInflate()*100));
		strengthSlider.setValue((int)(app.getStrength()*100));
		distanceSlider.setValue((int)app.getDistance());
		picSizeSlider.setValue(app.getPicSize());
		jSlider2.setValue((int)app.getNodevar()*10);
		jSlider3.setValue((int)app.getLabelsize()*10);
		jSlider4.setValue((int)app.getLabelVar()*10);
	}

	private JScrollPane getNodeAttPane() {
		if(nodeAttPane == null) {
			nodeAttPane = new JScrollPane();
			nodeAttPane.setViewportView(getNodeAttList());
		}
		return nodeAttPane;
	}

	private JList getNodeAttList() {
		if(nodeAttList == null) {
			nodeAttModel =  new DefaultListModel();
			nodeAttModel.addElement("none");
			nodeAttList = new JList(nodeAttModel);

			nodeAttList.setName("nodeAttributes");
			nodeAttList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			nodeAttList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
				public void valueChanged(
						javax.swing.event.ListSelectionEvent e) {
					String out = (String) nodeAttList.getSelectedValue();
					if (out!=null) {
						if (change) {
							app.setAttribute(out);
							change = false;
							if (edgeAttModel.contains(out))	edgeAttList.setSelectedValue(out, true);
							else edgeAttList.setSelectedIndex(0);
							getSearchTerm().setText("");
						} else change = true;
					}
				}
			});
		} else {
			nodeAttModel.clear();
			nodeAttModel.addElement("none");
			for (String s : app.ns.global.nodeattributes) {
				nodeAttModel.addElement(s);
			}
		}
		return nodeAttList;
	}

	private JSplitPane getAttributeSplitPane1() {
		if(AttributeSplitPane1 == null) {
			AttributeSplitPane1 = new JSplitPane();
			AttributeSplitPane1.setDividerLocation(100);
			AttributeSplitPane1.setDividerSize(2);
			AttributeSplitPane1.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
			//	AttributeSplitPane1.setBackground(new java.awt.Color(192,192,192));
			AttributeSplitPane1.add(getNodeAttPane(), JSplitPane.RIGHT);
			AttributeSplitPane1.add(getEdgeAttPane(), JSplitPane.LEFT);
		}
		return AttributeSplitPane1;
	}

	private JScrollPane getEdgeAttPane() {
		if(edgeAttPane == null) {
			edgeAttPane = new JScrollPane();
			edgeAttPane.setViewportView(getEdgeAttList());
		}
		return edgeAttPane;
	}

	private JList getEdgeAttList() {
		if(edgeAttList == null) {
			edgeAttModel =  new DefaultListModel();
			edgeAttModel.addElement("none");
			edgeAttList = new JList();
			edgeAttList.setModel(edgeAttModel);
			edgeAttList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			edgeAttList.setName("edgeAttributes");
			edgeAttList.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					String out = (String) edgeAttList.getSelectedValue();
					if (out!=null) {
						if (change) {
							app.setAttribute(out);
							change = false;
							if (nodeAttModel.contains(out)) nodeAttList.setSelectedValue(out, true);
							else nodeAttList.setSelectedIndex(0);
							getSearchTerm().setText("");
						} else change = true;
					} 
					//					textHilight();
				}
			});
		} 
		return edgeAttList;
	}
	private JSplitPane getJSplitPane() {
		if (jSplitPane == null) {
			jSplitPane = new JSplitPane();
			jSplitPane.setResizeWeight(0.0D);
			jSplitPane.setDividerSize(0);
			jSplitPane.setPreferredSize(new Dimension(1000,800));
			jSplitPane.add(getJSplitPane1(), JSplitPane.LEFT);
			jSplitPane.add(getDummyPanel(), JSplitPane.RIGHT);
		}
		return jSplitPane;
	}
	private JTabbedPane getControlPanel() {
		if (controlPanel == null) {
			controlPanel = new JTabbedPane();
			controlPanel.setMinimumSize(new Dimension(217, 320));
			controlPanel.addTab("files", null, getFile(), null);
			controlPanel.addTab("view", null, getDataTab(), null);
			controlPanel.addTab("render", null, getViewTab(), null);
			controlPanel.addTab("layout", null, getLayoutTab(), null);
		}
		return controlPanel;
	}
	private JSplitPane getJSplitPane1() {
		if (jSplitPane1 == null) {
			jSplitPane1 = new JSplitPane();
			jSplitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);
			jSplitPane1.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
			jSplitPane1.setDividerSize(0);
			jSplitPane1.setDoubleBuffered(true);
			jSplitPane1.setMinimumSize(new java.awt.Dimension(230, 400));
			jSplitPane1.add(getJSplitPane2(), JSplitPane.TOP);
			jSplitPane1.add(getInspectors(), JSplitPane.BOTTOM);
		}
		return jSplitPane1;
	}
	private JTabbedPane getInspectors() {
		if (inspectors == null) {
			inspectors = new JTabbedPane();
			inspectors.setPreferredSize(new java.awt.Dimension(212, 160));
			inspectors.addTab("net", null, getNets(), null);
			inspectors.addTab("node", null, getNodes(), null);
			inspectors.addTab("edge", null, getEdgeWndSplitPane(), null);
			inspectors.addTab("attrib", null, getAttributeSplitPane1(), null);
		}
		return inspectors;
	}
	private JScrollPane getNodes() {
		if (nodes == null) {
			nodes = new JScrollPane();
			nodes.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
			nodes.setViewportView(getNodeList());
		}
		return nodes;
	}
	private JList getNodeList() {
		if (nodeList == null) {
			nodeListModel = new DefaultListModel();
			nodeList = new JList();
			nodeList.setModel(nodeListModel);
			nodeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			nodeList.setName("nodes");
			nodeList.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					String out = (String) nodeList.getSelectedValue();
					if (out!=null) app.setPickID(out.hashCode());
				}
			});
		}
		return nodeList;
	}
	private JList getNetList() {
		if (netList == null) {
			netListModel = new DefaultListModel();
			netList = new JList();
			netList.setModel(netListModel);
			netList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			netList.setName("nets");
			netList.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					String out = (String) netList.getSelectedValue();
					if (out!=null) app.setSubnet(out);
				}
			});
		}
		return netList;
	}

	private JPanel getDataTab() {
		if (dataTab == null) {
			dataTab = new JPanel();
			dataTab.setLayout(null);
			dataTab.setFont(new java.awt.Font("Dialog",0,10));
			dataTab.add(getSearchTerm());
			dataTab.add(getSearchButton());
			dataTab.add(getDepth());
			dataTab.add(getDepthLabel());
			dataTab.add(getSeachSelButton());
			dataTab.add(getSearch());
			dataTab.add(getElimButton1());
			dataTab.add(getInterrupt());
			dataTab.add(getRandomCenter());
			dataTab.add(getShowAll());
			dataTab.add(getExpand2());
			dataTab.add(getExpandnet());
			dataTab.add(getSimButton1());
			dataTab.add(getSimButton2());
			dataTab.add(getJLabel2());
			dataTab.add(getJSlider1());
			dataTab.add(getJLabel7());
			dataTab.add(getSimButton2x());
			dataTab.add(getSimButton3());
			dataTab.add(getJLabel8());
			dataTab.add(getJCheckBox2());
			dataTab.add(getSimButton7());
			dataTab.add(getSimButton8());
			dataTab.add(getSimButton9());
			dataTab.add(getSimButton12());
			dataTab.add(getSimButton13());
			dataTab.add(getSimButton14());
			dataTab.add(getJLabel13());
			dataTab.add(getSimButton17());
		}
		return dataTab;
	}
	private JTextField getSearchTerm() {
		if (searchTerm == null) {
			searchTerm = new JTextField();
			searchTerm.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
			searchTerm.setBounds(2, 19, 116, 15);
			searchTerm.addKeyListener(new KeyAdapter() {
				public void keyReleased(java.awt.event.KeyEvent e) {
					if (e.getKeyCode()==10) app.netSearchSubstring(searchTerm.getText(),add.isSelected());
					textHilight();
				}


			});
		}
		return searchTerm;
	}

	private SimButton getSearchButton() {
		if (searchButton == null) {
			searchButton = new SimButton();
			searchButton.setText("search");
			searchButton.setFont(new java.awt.Font("Dialog",0,10));
			searchButton.setVerticalAlignment(SwingConstants.CENTER);
			searchButton.setVerticalTextPosition(SwingConstants.CENTER);
			searchButton.setBounds(166, 19, 45, 15);
			searchButton.addActionListener(new ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					app.netSearchSubstring(searchTerm.getText(), add.isSelected());
					textHilight();
				}
			});
		}
		return searchButton;
	}
	private void textHilight() {
		app.clearFrames(app.ns.view);
		if (searchTerm.getText().length()>0) app.findSubstringAttributes(searchTerm.getText(), app.getAttribute());
	}
	private JSlider getDepth() {
		if (depth == null) {
			depth = new JSlider();
			depth.setName("depth");
			depth.setMaximum(10);
			depth.setPaintLabels(true);
			depth.setValue((int) (app.getDepth()));
			depth.setMajorTickSpacing(1);
			depth.setPaintTrack(false);
			depth.setSnapToTicks(true);
			depth.setFont(new java.awt.Font("Dialog",0,7));
			depth.setInverted(false);
			depth.setToolTipText("searchdepth");
			depth.setOpaque(false);
			depth.setBounds(0, 59, 140, 31);
			depth.addChangeListener(new ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					app.setDepth(depth.getValue());
				}
			});
		}
		return depth;
	}
	private JLabel getDepthLabel() {
		if (depthLabel == null) {
			depthLabel = new JLabel();
			depthLabel.setText("search steps");
			depthLabel.setFont(new java.awt.Font("Dialog",0,10));
			depthLabel.setBounds(144, 75, 61, 13);
		}
		return depthLabel;
	}

	private SimButton getSeachSelButton() {
		if (seachSelButton == null) {
			seachSelButton = new SimButton();
			seachSelButton.setFont(new java.awt.Font("Dialog",0,10));
			seachSelButton.setText("picked");
			seachSelButton.setVerticalAlignment(SwingConstants.CENTER);
			seachSelButton.setVerticalTextPosition(SwingConstants.CENTER);
			seachSelButton.setBounds(144, 40, 67, 15);
			seachSelButton.addActionListener(new ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					app.netSearchPicked(add.isSelected());
				}
			});
		}
		return seachSelButton;
	}
	private JLabel getSearch() {
		if (search == null) {
			search = new JLabel();
			search.setText("search");
			search.setFont(new java.awt.Font("Dialog",1,11));
			search.setBounds(0, 0, 210, 14);
			//			search.setBackground(new java.awt.Color(192,192,192));
		}
		return search;
	}
	private SimButton getElimButton1() {
		if (elimButton1 == null) {
			elimButton1 = new SimButton();
			elimButton1.setFont(new java.awt.Font("Dialog",0,10));
			elimButton1.setText("leaf nodes");
			elimButton1.setVerticalAlignment(SwingConstants.CENTER);
			elimButton1.setVerticalTextPosition(SwingConstants.CENTER);
			elimButton1.setToolTipText("remove leave nodes");
			elimButton1.setBounds(73, 190, 67, 15);
			elimButton1.addActionListener(new ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					app.netRemoveLeafs();
				}
			});
		}
		return elimButton1;
	}
	private SimButton getInterrupt() {
		if (interrupt == null) {
			interrupt = new SimButton();
			interrupt.setVisible(false);
			interrupt.setBounds(0, 0, 0, 0);
			interrupt.addActionListener(new ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					app.ns.view.interrupt();
				}
			});
		}
		return interrupt;
	}
	private SimButton getRandomCenter() {
		if (randomCenter == null) {
			randomCenter = new SimButton();
			randomCenter.setFont(new java.awt.Font("Dialog",0,10));
			randomCenter.setVerticalAlignment(SwingConstants.CENTER);
			randomCenter.setVerticalTextPosition(SwingConstants.CENTER);
			randomCenter.setText("random");
			randomCenter.setBounds(73, 40, 67, 15);
			randomCenter.addActionListener(new ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					app.netStartRandom(add.isSelected());
				}
			});
		}
		return randomCenter;
	}
	private SimButton getShowAll() {
		if (showAll == null) {
			showAll = new SimButton();
			showAll.setFont(new java.awt.Font("Dialog",0,10));
			showAll.setVerticalAlignment(SwingConstants.CENTER);
			showAll.setVerticalTextPosition(SwingConstants.CENTER);
			showAll.setText("show all");
			showAll.setBounds(2, 40, 67, 15);
			showAll.addActionListener(new ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					app.netShowAll(); //  Auto-generated Event stub actionPerformed()
				}
			});
		}
		return showAll;
	}
	private SimButton getExpand2() {
		if (expand2 == null) {
			expand2 = new SimButton();
			expand2.setFont(new java.awt.Font("Dialog",0,10));
			expand2.setText("picked");
			expand2.setVerticalAlignment(SwingConstants.CENTER);
			expand2.setVerticalTextPosition(SwingConstants.CENTER);
			expand2.setBounds(144, 114, 67, 15);
			expand2.addActionListener(new ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					app.netExpandNode(); //  Auto-generated Event stub actionPerformed()
				}
			});
		}
		return expand2;
	}
	private SimButton getExpandnet() {
		if (expandnet == null) {
			expandnet = new SimButton();
			expandnet.setText("all");
			expandnet.setVerticalAlignment(SwingConstants.CENTER);
			expandnet.setVerticalTextPosition(SwingConstants.CENTER);
			expandnet.setFont(new java.awt.Font("Dialog",0,10));
			expandnet.setMargin(new java.awt.Insets(2,2,2,2));
			expandnet.setBounds(2, 114, 67, 15);
			expandnet.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					app.netExpandAll();
				}
			});
		}
		return expandnet;
	}
	private SimButton getSimButton2() {
		if (SimButton2 == null) {
			SimButton2 = new SimButton();
			SimButton2.setText("isolated");
			SimButton2.setVerticalAlignment(SwingConstants.CENTER);
			SimButton2.setVerticalTextPosition(SwingConstants.CENTER);
			SimButton2.setFont(new java.awt.Font("Dialog",0,10));
			SimButton2.setToolTipText("remove isolated nodes");
			SimButton2.setBounds(2, 171, 67, 15);
			SimButton2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					app.delIsolated();
				}
			});
		}
		return SimButton2;
	}
	private JLabel getJLabel2() {
		if (jLabel2 == null) {
			jLabel2 = new JLabel();
			jLabel2.setText("remove");
			jLabel2.setFont(new java.awt.Font("Dialog",1,11));
			jLabel2.setBounds(1, 133, 51, 14);
		}
		return jLabel2;
	}
	private JPanel getViewTab() {
		if (viewTab == null) {
			viewTab = new JPanel();
			viewTab.setLayout(null);
			viewTab.add(getTexButton1());
			viewTab.add(getEdgBox1());
			viewTab.add(getNoRender());
			viewTab.add(getDisplay());
			viewTab.add(getSizeSlider());
			viewTab.add(getSizeLabel());
			viewTab.add(getPicSizeSlider());
			viewTab.add(getJLabel9());
			viewTab.add(getJSlider2());
			viewTab.add(getJLabel10());
			viewTab.add(getSimButton4());
			viewTab.add(getSimButton5());
			viewTab.add(getJLabel11());
			viewTab.add(getJCheckBox2x());
			viewTab.add(getDrawClusters());
			viewTab.add(getJLabel16());
			viewTab.add(getJSlider3());
			viewTab.add(getJSlider4());
			viewTab.add(getJLabel17());
			viewTab.add(getJCheckBox2xxx());
		}
		return viewTab;
	}
	private SimButton getTexButton1() {
		if (texButton1 == null) {
			texButton1 = new SimButton();
			texButton1.setFont(new java.awt.Font("Dialog",0,10));
			texButton1.setToolTipText("reload textures for current view");
			texButton1.setText("reload Tex");
			texButton1.setPreferredSize(new Dimension(60,20));
			texButton1.setMargin(new java.awt.Insets(2,2,2,2));
			texButton1.setVisible(true);
			texButton1.setBounds(0, 0, 0, 0);
			texButton1.addActionListener(new ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					app.fileIO.loadTexturesUrl( app.texfolder, app.ns.view, app.thumbsize);
				}
			});
		}
		return texButton1;
	}
	private JCheckBox getEdgBox1() {
		if (drawedges == null) {
			drawedges = new JCheckBox();
			drawedges.setText("edges");
			drawedges.setMargin(new java.awt.Insets(0,0,0,0));
			drawedges.setContentAreaFilled(false);
			drawedges.setFont(new java.awt.Font("Dialog",0,10));
			drawedges.setBounds(144, 36, 50, 17);
			drawedges.setSelected(app.isEdges());
			drawedges.addActionListener(new ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					app.setEdges(drawedges.isSelected());
				}
			});
		}
		return drawedges;
	}
	private JCheckBox getNoRender() {
		if (noRender == null) {
			noRender = new JCheckBox();
			noRender.setSelected(app.isRender());
			noRender.setText("render on / off");
			noRender.setToolTipText("allows to speed up operations by interupting rendering");
			noRender.setMargin(new java.awt.Insets(0,0,0,0));
			noRender.setContentAreaFilled(false);
			noRender.setFont(new java.awt.Font("Dialog",0,10));
			noRender.setBounds(1, 18, 93, 17);
			noRender.addActionListener(new ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					app.setRender(noRender.isSelected()); 
				}
			});
		}
		return noRender;
	}
	private JLabel getDisplay() {
		if (display == null) {
			display = new JLabel();
			display.setText("display");
			display.setFont(new java.awt.Font("Dialog",1,11));
			display.setBounds(0, 0, 210, 14);
		}
		return display;
	}
	private JSlider getSizeSlider() {
		if (sizeSlider == null) {
			sizeSlider = new JSlider();
			sizeSlider.setMaximum(100);
			sizeSlider.setOpaque(false);
			sizeSlider.setToolTipText("set base node size");
			sizeSlider.setName("sizeSlider");
			sizeSlider.setBounds(1, 70, 140, 16);
			sizeSlider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					app.setSize((float)sizeSlider.getValue());
				}
			});
		}
		return sizeSlider;
	}
	private JLabel getSizeLabel() {
		if (sizeLabel == null) {
			sizeLabel = new JLabel();
			sizeLabel.setText("nodeSize");
			sizeLabel.setFont(new java.awt.Font("Dialog",0,10));
			sizeLabel.setBounds(144, 70, 44, 13);
		}
		return sizeLabel;
	}
	private JPanel getLayoutTab() {
		if (layoutTab == null) {
			layoutTab = new JPanel();
			layoutTab.setLayout(null);
			//			layoutTab.setMinimumSize(new java.awt.Dimension(10, 280));
			layoutTab.add(getValenzSlider());
			layoutTab.add(getDistanceSlider());
			layoutTab.add(getGroupRadius());
			layoutTab.add(getPushSlider());
			layoutTab.add(getRepellStSlider());
			layoutTab.add(getStretchSlider());
			layoutTab.add(getStrengthSlider());
			layoutTab.add(getValenzLabel());
			layoutTab.add(getDistLabel());
			layoutTab.add(getGroup());
			layoutTab.add(getPushLabel());
			layoutTab.add(getPushLabel1());
			layoutTab.add(getPushLabel11());
			layoutTab.add(getStrengthLabel());
			layoutTab.add(getForces());
			layoutTab.add(getClusters());
			layoutTab.add(getRadialBox());
			layoutTab.add(getTreeBox());
			layoutTab.add(getRepNeighbors());
			layoutTab.add(getRepellBox1());
			layoutTab.add(getJLabel3());
			layoutTab.add(getJLabel4());
			layoutTab.add(getJLabel5());
			layoutTab.add(getJCheckBox1x());
			layoutTab.add(getSimButton10());
			layoutTab.add(getInflateButton());
			layoutTab.add(getBoxLayout());
			layoutTab.add(getCircle());
			layoutTab.add(getShuffleButton());
			layoutTab.add(getJCheckBox());
		}
		return layoutTab;
	}
	private JSlider getValenzSlider() {
		if (valenzSlider == null) {
			valenzSlider = new JSlider();
			valenzSlider.setMaximum(100);
			valenzSlider.setOpaque(false);
			valenzSlider.setToolTipText("note distance: set valence influence");
			valenzSlider.setName("valenz");
			valenzSlider.setBounds(0, 201, 151, 16);
			valenzSlider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					app.setVal((float)valenzSlider.getValue()/100f);
				}
			});
		}
		return valenzSlider;
	}
	private JSlider getDistanceSlider() {
		if (distanceSlider == null) {
			distanceSlider = new JSlider();
			distanceSlider.setMinimum(1);
			distanceSlider.setMaximum(1000);
			distanceSlider.setOpaque(false);
			distanceSlider.setToolTipText("set base node size");
			distanceSlider.setName("distance");
			distanceSlider.setBounds(0, 235, 151, 16);
			distanceSlider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					app.setDistance((float)distanceSlider.getValue());
				}
			});
		}
		return distanceSlider;
	}
	private JSlider getGroupRadius() {
		if (groupRadius == null) {
			groupRadius = new JSlider();
			groupRadius.setMaximum(500);
			groupRadius.setMinimum(0);
			groupRadius.setOpaque(false);
			groupRadius.setToolTipText("set cluster radius");
			groupRadius.setName("group radius");
			groupRadius.setBounds(0, 91, 151, 16);
			groupRadius.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					app.setClusterRad(groupRadius.getValue()/10f); //  Auto-generated Event stub stateChanged()
				}
			});
		}
		return groupRadius;
	}
	private JSlider getPushSlider() {
		if (pushSlider == null) {
			pushSlider = new JSlider();
			pushSlider.setMaximum(200);
			pushSlider.setMinimum(-50);
			pushSlider.setOpaque(false);
			pushSlider.setToolTipText("set repell distance factor");
			pushSlider.setName("repell");
			pushSlider.setBounds(0, 149, 151, 16);
			pushSlider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					app.setRepell((float)pushSlider.getValue());
				}
			});
		}
		return pushSlider;
	}
	private JSlider getRepellStSlider() {
		if (repellStSlider == null) {
			repellStSlider = new JSlider();
			repellStSlider.setMaximum(100);
			repellStSlider.setOpaque(false);
			repellStSlider.setValue((int)(app.getRepStr()*100));
			repellStSlider.setToolTipText("set repell strength factor");
			repellStSlider.setName("rep. st.");
			repellStSlider.setBounds(0, 165, 151, 16);
			repellStSlider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					app.setRepStr((float)repellStSlider.getValue()/100f);
				}
			});
		}
		return repellStSlider;
	}
	private JSlider getStretchSlider() {
		if (stretchSlider == null) {
			stretchSlider = new JSlider();
			stretchSlider.setMaximum(100);
			stretchSlider.setMinimum(0);
			stretchSlider.setOpaque(false);
			stretchSlider.setToolTipText("set permanent inflate force (only use if graph is connected)");
			stretchSlider.setName("permInfl");
			stretchSlider.setBounds(0, 21, 151, 16);
			stretchSlider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					app.setPermInflate(stretchSlider.getValue()/100f); //  Auto-generated Event stub stateChanged()
				}
			});
		}
		return stretchSlider;
	}
	private JSlider getStrengthSlider() {
		if (strengthSlider == null) {
			strengthSlider = new JSlider();
			strengthSlider.setMinimum(1);
			strengthSlider.setMaximum(200);
			strengthSlider.setOpaque(false);
			strengthSlider.setToolTipText("set repell distance factor");
			strengthSlider.setName("repell");
			strengthSlider.setBounds(0, 217, 151, 16);
			strengthSlider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					app.setStrength((float)strengthSlider.getValue()/100f);
				}
			});
		}
		return strengthSlider;
	}
	private JLabel getValenzLabel() {
		if (valenzLabel == null) {
			valenzLabel = new JLabel();
			valenzLabel.setText("degree f.");
			valenzLabel.setFont(new java.awt.Font("Dialog",0,10));
			valenzLabel.setBounds(148, 202, 44, 13);
		}
		return valenzLabel;
	}
	private JLabel getDistLabel() {
		if (distLabel == null) {
			distLabel = new JLabel();
			distLabel.setText("distance");
			distLabel.setFont(new java.awt.Font("Dialog",0,10));
			distLabel.setBounds(148, 236, 41, 13);
		}
		return distLabel;
	}
	private JLabel getGroup() {
		if (group == null) {
			group = new JLabel();
			group.setText("radius");
			group.setFont(new java.awt.Font("Dialog",0,10));
			group.setBounds(148, 92, 30, 13);
		}
		return group;
	}
	private JLabel getPushLabel() {
		if (pushLabel == null) {
			pushLabel = new JLabel();
			pushLabel.setText("distance");
			pushLabel.setFont(new java.awt.Font("Dialog",0,10));
			pushLabel.setBounds(148, 150, 41, 13);
		}
		return pushLabel;
	}
	private JLabel getPushLabel1() {
		if (pushLabel1 == null) {
			pushLabel1 = new JLabel();
			pushLabel1.setText("strength");
			pushLabel1.setFont(new java.awt.Font("Dialog",0,10));
			pushLabel1.setBounds(148, 166, 41, 13);
		}
		return pushLabel1;
	}
	private JLabel getPushLabel11() {
		if (pushLabel11 == null) {
			pushLabel11 = new JLabel();
			pushLabel11.setText("inflate");
			pushLabel11.setFont(new java.awt.Font("Dialog",0,10));
			pushLabel11.setBounds(148, 24, 32, 13);
		}
		return pushLabel11;
	}
	private JLabel getStrengthLabel() {
		if (strengthLabel == null) {
			strengthLabel = new JLabel();
			strengthLabel.setText("strength");
			strengthLabel.setFont(new java.awt.Font("Dialog",0,10));
			strengthLabel.setBounds(148, 218, 41, 13);
		}
		return strengthLabel;
	}
	private JLabel getForces() {
		if (forces == null) {
			forces = new JLabel();
			forces.setText("global layout");
			forces.setFont(new java.awt.Font("Dialog",1,11));
			forces.setBounds(0, 0, 151, 14);
		}
		return forces;
	}
	private JCheckBox getClusters() {
		if (clusters == null) {
			clusters = new JCheckBox();
			clusters.setText("on");
			clusters.setSelected(app.isCluster());
			clusters.setMargin(new java.awt.Insets(0,0,0,0));
			clusters.setContentAreaFilled(false);
			clusters.setFont(new java.awt.Font("Dialog",0,10));
			clusters.setBounds(0, 72, 33, 17);
			clusters.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					app.setCluster(clusters.isSelected());
					app.ns.view.updateNet();
				}
			});
		}
		return clusters;
	}
	private JCheckBox getRadialBox() {
		if (timeBox == null) {
			timeBox = new JCheckBox();
			timeBox.setText("timeline");
			timeBox.setSelected(app.isTime());
			timeBox.setMargin(new java.awt.Insets(0,0,0,0));
			timeBox.setContentAreaFilled(false);
			timeBox.setFont(new java.awt.Font("Dialog",0,10));
			timeBox.setBounds(61, 41, 61, 17);
			timeBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					app.setTime(timeBox.isSelected());
				}
			});
		}
		return timeBox;
	}
	private JCheckBox getTreeBox() {
		if (treeBox == null) {
			treeBox = new JCheckBox();
			treeBox.setText("radial");
			treeBox.setSelected(app.isTree());
			treeBox.setMargin(new java.awt.Insets(0,0,0,0));
			treeBox.setContentAreaFilled(false);
			treeBox.setFont(new java.awt.Font("Dialog",0,10));
			treeBox.setBounds(145, 41, 49, 17);
			treeBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					app.setTree(treeBox.isSelected());
				}
			});
		}
		return treeBox;
	}
	private JCheckBox getRepNeighbors() {
		if (repNeighbors == null) {
			repNeighbors = new JCheckBox();
			repNeighbors.setText("local on");
			repNeighbors.setSelected(app.isRepN());
			repNeighbors.setMargin(new java.awt.Insets(0,0,0,0));
			repNeighbors.setContentAreaFilled(false);
			repNeighbors.setFont(new java.awt.Font("Dialog",0,10));
			repNeighbors.setBounds(61, 130, 59, 17);
			repNeighbors.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					app.setRepN(!app.isRepN());
					repNeighbors.setSelected(app.isRepN());
				}
			});
		}
		return repNeighbors;
	}
	private JCheckBox getRepellBox1() {
		if (repellBox1 == null) {
			repellBox1 = new JCheckBox();
			repellBox1.setText("on");
			repellBox1.setMargin(new java.awt.Insets(0,0,0,0));
			repellBox1.setContentAreaFilled(false);
			repellBox1.setFont(new java.awt.Font("Dialog",0,10));
			repellBox1.setBounds(0, 130, 33, 17);
			repellBox1.setSelected(app.repell);
			repellBox1.addActionListener(new ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					app.repell=!app.repell;
					repellBox1.setSelected(app.repell);
				}
			});
		}
		return repellBox1;
	}
	private JLabel getJLabel3() {
		if (jLabel3 == null) {
			jLabel3 = new JLabel();
			jLabel3.setText("clusters");
			jLabel3.setFont(new java.awt.Font("Dialog",1,11));
			jLabel3.setBounds(0, 58, 151, 14);
		}
		return jLabel3;
	}
	private JLabel getJLabel4() {
		if (jLabel4 == null) {
			jLabel4 = new JLabel();
			jLabel4.setText("repulsive forces (slow)");
			jLabel4.setFont(new java.awt.Font("Dialog",1,11));
			jLabel4.setBounds(0, 112, 151, 14);
		}
		return jLabel4;
	}
	private JLabel getJLabel5() {
		if (jLabel5 == null) {
			jLabel5 = new JLabel();
			jLabel5.setText("attractive forces");
			jLabel5.setFont(new java.awt.Font("Dialog",1,11));
			jLabel5.setBounds(0, 184, 151, 14);
		}
		return jLabel5;
	}
	private JPanel getDummyPanel() {
		if (dummyPanel == null) {
			dummyPanel = new JPanel();
			dummyPanel.setLayout(null);
			dummyPanel.setPreferredSize(new java.awt.Dimension(600,10));
			dummyPanel.add(getJLabel6());
		}
		return dummyPanel;
	}
	private JLabel getJLabel6() {
		if (jLabel6 == null) {
			jLabel6 = new JLabel();
			jLabel6.setText("openGL error!");
			jLabel6.setLayout(null);
			jLabel6.setFont(new java.awt.Font("Dialog",0,36));
			jLabel6.setBounds(33, 28, 224, 47);
		}
		return jLabel6;
	}
	private JMenu getFileMenu() {
		if (fileMenu == null) {
			fileMenu = new JMenu();
			fileMenu.setText("File");
			fileMenu.add(getOpenPicMenuItem());
			fileMenu.add(getOpenMenuItem());
			fileMenu.add(getOpenNodeMenuItem());
			fileMenu.add(getSaveMenuItem());
			fileMenu.add(getExitMenuItem());
		}
		return fileMenu;
	}
	private JMenuItem getOpenPicMenuItem() {
		if (openPicMenuItem == null) {
			openPicMenuItem = new JMenuItem();
			openPicMenuItem.setText("Set Image Folder");
			openPicMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,Event.CTRL_MASK,true));
			openPicMenuItem.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					setImageDir();
				}
			});
		}
		return openPicMenuItem;
	}


	private JMenuItem getOpenMenuItem() {
		if (openMenuItem == null) {
			openMenuItem = new JMenuItem();
			openMenuItem.setText("Load Network");
			openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,Event.CTRL_MASK,true));
			openMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					loadNetwork(true);
				}
			});
		}
		return openMenuItem;
	}

	private void loadNetwork(boolean tab) {
		boolean calc = app.calculate;
		boolean rnd = app.render;
		app.calculate = false;
		app.render = false;
		int returnVal = openFile.showOpenDialog(openFile);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File filename =openFile.getSelectedFile();
			app.loadNetwork(filename, tab);
			setCounter();
			app.calculate=calc;
			app.render=rnd;
		}
	}

	private JMenuItem getOpenNodeMenuItem() {
		if (openNodeMenuItem == null) {
			openNodeMenuItem = new JMenuItem();
			openNodeMenuItem.setText("Load Node Properties file");
			openNodeMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					loadNodeAttributes();
				}

			});
		}
		return openNodeMenuItem;
	}

	private void loadNodeAttributes() {
		boolean calc = app.calculate;
		boolean rnd = app.render;
		app.calculate = false;
		app.render = false;
		int returnVal = openFile.showOpenDialog(openFile);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			app.nodeListLoad(openFile.getSelectedFile(), app.isTabular());
			setCounter();
			app.calculate=calc;
			app.render=rnd;
		}
	}
	private JMenuItem getSaveMenuItem() {
		if (saveMenuItem == null) {
			saveMenuItem = new JMenuItem();
			saveMenuItem.setText("Save Network");
			saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,Event.CTRL_MASK,true));
			saveMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					exportNetwork(true);
				}
			});
		}
		return saveMenuItem;
	}
	private JMenuItem getExitMenuItem() {
		if (exitMenuItem == null) {
			exitMenuItem = new JMenuItem();
			exitMenuItem.setText("Exit");
			exitMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			});
		}
		return exitMenuItem;
	}

	public void redrawUI(){
		this.getMainWindow().paintAll(this.getMainWindow().getGraphics());
	}

	public void updateUI(NetStack n) {
		initNetList(n);
		initNodeList(n.getView());
		initEdgeList(n.view);
		initAttList();
		setCounter();
		initSliders();
		initCheckboxes();
	}

	private void initAttList() {
		nodeAttModel.clear();
		nodeAttModel.addElement("none");
		for (String s : app.ns.global.nodeattributes) {
			nodeAttModel.addElement(s);
		}
		edgeAttModel.clear();
		edgeAttModel.addElement("none");
		for (String s : app.ns.global.edgeattributes) {
			edgeAttModel.addElement(s);
		}

		String att = app.getAttribute();
		if (att!=null) {
			edgeAttList.setSelectedValue(app.getAttribute(), true);
			nodeAttList.setSelectedValue(app.getAttribute(), true);
		}
		else {
			edgeAttList.setSelectedValue(0, true);
			nodeAttList.setSelectedValue(0, true);
		}
	}

	private void initEdgeList(Net globalNet) {
		HashMap<String,Edge> edgeList = globalNet.eTable;
		while (eTableModel.getRowCount()>0)   eTableModel.removeRow(0);

		for (Edge tmp:edgeList.values()) {
			String[] newRow = { tmp.getA().name, tmp.getB().name };
			eTableModel.addRow(newRow);
		}
	}

	private void initNodeList(Net n) {
		nodeListModel.clear();
		TreeSet<String> sorted = new TreeSet<String>();
		for (Node tmp:n.nNodes) sorted.add(tmp.name);
		for (String tmp : sorted) {
			nodeListModel.addElement(tmp);
		}
	}

	private void initNetList(NetStack n) {
		netListModel.clear();
		TreeSet<String> sorted = new TreeSet<String>();
		for (String tmp:n.getStack()) sorted.add(tmp);
		for (String tmp : sorted) {
			netListModel.addElement(tmp);
		}
	}
	private void initCheckboxes() {
		tiltBox.setSelected(app.tilt);
		repellBox1.setSelected(app.repell);
		repNeighbors.setSelected(app.isRepN());
		treeBox.setSelected(app.isTree());
		timeBox.setSelected(app.isTime());
		clusters.setSelected(app.isCluster());
		noRender.setSelected(app.isRender());
		drawedges.setSelected(app.isEdges());
		draw3d.setSelected(!app.get3D());
		fadenodes.setSelected(app.fadeNodes);
		forceBox.setSelected(app.getCalc());
		renderTextures.setSelected(app.textures);
		directedGraph.setSelected(app.directed);
		drawclusters.setSelected(app.drawClusters);
		jFileFormat.setSelected(app.isTabular());
		drawgroups.setSelected(app.isGroups());
	}
	private JSlider getJSlider1() {
		if(jSlider1 == null) {
			jSlider1 = new JSlider();
			jSlider1.setValue((int)(app.getPickdepth()));
			jSlider1.setMaximum(6);
			jSlider1.setInverted(false);
			jSlider1.setMajorTickSpacing(1);
			jSlider1.setSnapToTicks(true);
			jSlider1.setPaintTrack(false);
			jSlider1.setPaintLabels(true);
			jSlider1.setFont(new java.awt.Font("Dialog",0,7));
			jSlider1.setToolTipText("searchdepth");
			jSlider1.setOpaque(false);
			jSlider1.setName("jSlider1");
			jSlider1.setBounds(0, 214, 134, 35);
			jSlider1.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					app.setPickdepth(jSlider1.getValue());
					app.updatePick();
				}
			});
		}
		return jSlider1;
	}

	private JLabel getJLabel7() {
		if(jLabel7 == null) {
			jLabel7 = new JLabel();
			jLabel7.setText("pick steps");
			jLabel7.setFont(new java.awt.Font("Dialog",0,10));
			jLabel7.setBounds(146, 231, 49, 13);
		}
		return jLabel7;
	}

	private SimButton getSimButton1() {
		if(simButton1 == null) {
			simButton1 = new SimButton();
			simButton1.setText("picked");
			simButton1.setVerticalAlignment(SwingConstants.CENTER);
			simButton1.setVerticalTextPosition(SwingConstants.CENTER);
			simButton1.setFont(new java.awt.Font("Dialog",0,10));
			simButton1.setToolTipText("remove the picked node");
			simButton1.setBounds(2, 152, 67, 15);
			simButton1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					app.delSelected();
				}
			});
		}
		return simButton1;
	}

	private SimButton getSimButton2x() {
		if(simButton2 == null) {
			simButton2 = new SimButton();
			simButton2.setText("free all");
			simButton2.setVerticalAlignment(SwingConstants.CENTER);
			simButton2.setVerticalTextPosition(SwingConstants.CENTER);
			simButton2.setFont(new java.awt.Font("Dialog",0,10));
			simButton2.setBounds(144, 273, 67, 15);
			simButton2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					app.locksRemove();
				}
			});
		}
		return simButton2;
	}

	private SimButton getSimButton3() {
		if(simButton3 == null) {
			simButton3 = new SimButton();
			simButton3.setText("lock all");
			simButton3.setVerticalAlignment(SwingConstants.CENTER);
			simButton3.setVerticalTextPosition(SwingConstants.CENTER);
			simButton3.setFont(new java.awt.Font("Dialog",0,10));
			simButton3.setBounds(73, 273, 67, 15);
			simButton3.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					app.lockAll();
				}
			});
		}
		return simButton3;
	}

	private JLabel getJLabel8() {
		if(jLabel8 == null) {
			jLabel8 = new JLabel();
			jLabel8.setText("locks");
			jLabel8.setFont(new java.awt.Font("Dialog",1,11));
			jLabel8.setBounds(0, 255, 51, 14);
		}
		return jLabel8;
	}

	private JSlider getPicSizeSlider() {
		if(picSizeSlider == null) {
			picSizeSlider = new JSlider();
			picSizeSlider.setMinimum(1);
			picSizeSlider.setMaximum(10);
			picSizeSlider.setToolTipText("set pic size as multiplier of node size");
			picSizeSlider.setOpaque(false);
			picSizeSlider.setName("jSlider2");
			picSizeSlider.setBounds(1, 90, 140, 16);
			picSizeSlider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					app.setPicSize(picSizeSlider.getValue());
				}
			});
		}
		return picSizeSlider;
	}

	private JLabel getJLabel9() {
		if(jLabel9 == null) {
			jLabel9 = new JLabel();
			jLabel9.setText("picSize");
			jLabel9.setFont(new java.awt.Font("Dialog",0,10));
			jLabel9.setBounds(144, 90, 34, 13);
		}
		return jLabel9;
	}

	private JSlider getJSlider2() {
		if(jSlider2 == null) {
			jSlider2 = new JSlider();
			jSlider2.setMaximum(100);
			jSlider2.setMinimum(0);
			jSlider2.setToolTipText("set pic size as multiplier of node size");
			jSlider2.setOpaque(false);
			jSlider2.setName("jSlider2");
			jSlider2.setBounds(1, 110, 140, 16);
			jSlider2.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					app.setNodeVar(jSlider2.getValue()/10f);
				}
			});
		}
		return jSlider2;
	}

	private JLabel getJLabel10() {
		if(jLabel10 == null) {
			jLabel10 = new JLabel();
			jLabel10.setText("nodeVar");
			jLabel10.setFont(new java.awt.Font("Dialog",0,10));
			jLabel10.setBounds(144, 110, 41, 13);
		}
		return jLabel10;
	}

	private JSplitPane getJSplitPane2() {
		if(jSplitPane2 == null) {
			jSplitPane2 = new JSplitPane();
			jSplitPane2.setOrientation(JSplitPane.VERTICAL_SPLIT);
			jSplitPane2.setDividerSize(0);
			jSplitPane2.setMinimumSize(new Dimension(230,375));
			jSplitPane2.add(getControlPanel(), JSplitPane.TOP);
			jSplitPane2.add(getJPanel1(), JSplitPane.BOTTOM);
		}
		return jSplitPane2;
	}

	private JPanel getJPanel1() {
		if(midPanels == null) {
			midPanels = new JPanel();
			midPanels.setLayout(null);
			midPanels.add(getFadeNodes());
			midPanels.add(getFontslider());
			midPanels.add(getJLabel1());
			midPanels.add(getForceBox());
			midPanels.add(getJCheckBox2xx());
		}
		return midPanels;
	}
	private JCheckBox getFadeNodes() {
		if (fadenodes == null) {
			fadenodes = new JCheckBox();
			fadenodes.setText("fade nodes");
			fadenodes.setMargin(new java.awt.Insets(0,0,0,0));
			fadenodes.setContentAreaFilled(false);
			fadenodes.setFont(new java.awt.Font("Dialog",0,10));
			fadenodes.setBounds(2, 1, 75, 17);
			fadenodes.setSelected(app.fadeNodes);
			fadenodes.addActionListener(new ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					app.fadeNodes=fadenodes.isSelected();
					app.layout.applyPickColors();
				}
			});
		}
		return fadenodes;
	}
	private JSlider getFontslider() {
		if (fontslider == null) {
			fontslider = new JSlider();
			fontslider.setFont(new Font("Dialog",Font.PLAIN,10));
			fontslider.setMinimum(0);
			fontslider.setMaximum(3);
			fontslider.setMajorTickSpacing(1);
			fontslider.setSnapToTicks(true);
			fontslider.setToolTipText("font");
			fontslider.setName("fontslider");
			fontslider.setPaintTrack(false);
			fontslider.setPaintTicks(true);
			fontslider.setValue(app.getFonttype());
			fontslider.setPreferredSize(new java.awt.Dimension(20,40));
			fontslider.setInverted(true);
			fontslider.setValue(1);
			fontslider.setOpaque(false);
			fontslider.setBounds(0, 20, 103, 30);
			fontslider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					app.setFonttype(fontslider.getValue());
				}
			});
		}
		return fontslider;
	}
	private JLabel getJLabel1() {
		if (jLabel1 == null) {
			jLabel1 = new JLabel();
			jLabel1.setText("labeltype");
			jLabel1.setFont(new java.awt.Font("Dialog",0,10));
			jLabel1.setBounds(125, 28, 45, 13);
		}
		return jLabel1;
	}
	private JCheckBox getForceBox() {
		if (forceBox == null) {
			forceBox = new JCheckBox();
			forceBox.setText("layout on/off");
			forceBox.setToolTipText("Force driven Layout active");
			forceBox.setSelected(app.getCalc());
			forceBox.setMargin(new java.awt.Insets(0,0,0,0));
			forceBox.setContentAreaFilled(false);
			forceBox.setFont(new java.awt.Font("Dialog",0,10));
			forceBox.setBounds(85, 1, 85, 17);
			forceBox.addActionListener(new ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					app.setCalc(!app.getCalc());
					forceBox.setSelected(app.getCalc());
				}
			});
		}
		return forceBox;
	}

	private SimButton getSimButton4() {
		if(simButton4 == null) {
			simButton4 = new SimButton();
			simButton4.setText("reset");
			simButton4.setFont(new Font("Dialog",Font.PLAIN,10));
			simButton4.setToolTipText("set the camera to 0,0");
			simButton4.setBounds(1, 189, 65, 15);
			simButton4.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					app.resetCam();
				}
			});
		}
		return simButton4;
	}

	private SimButton getSimButton5() {
		if(simButton5 == null) {
			simButton5 = new SimButton();
			simButton5.setText("to picked");
			simButton5.setFont(new Font("Dialog",Font.PLAIN,10));
			simButton5.setToolTipText("set the camera to selected node");
			simButton5.setBounds(71, 189, 65, 15);
			simButton5.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					app.camOnSelected();
				}
			});
		}
		return simButton5;
	}

	private JLabel getJLabel11() {
		if(jLabel11 == null) {
			jLabel11 = new JLabel();
			jLabel11.setText("viewpoint");
			jLabel11.setFont(new java.awt.Font("Dialog",1,11));
			jLabel11.setBounds(0, 170, 73, 14);
		}
		return jLabel11;
	}

	private JCheckBox getJCheckBox2() {
		if(add == null) {
			add = new JCheckBox();
			add.setText("add");
			add.setSelected(false);
			add.setMargin(new java.awt.Insets(0,0,0,0));
			add.setContentAreaFilled(false);
			add.setFont(new java.awt.Font("Dialog",0,10));
			add.setBounds(122, 19, 39, 15);
		}
		return add;
	}

	private SimButton getSimButton7() {
		if(simButton7 == null) {
			simButton7 = new SimButton();
			simButton7.setText("all");
			simButton7.setVerticalAlignment(SwingConstants.CENTER);
			simButton7.setVerticalTextPosition(SwingConstants.CENTER);
			simButton7.setFont(new java.awt.Font("Dialog",0,10));
			simButton7.setToolTipText("remove all nodes from view");
			simButton7.setBounds(144, 190, 67, 15);
			simButton7.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					app.delAll();
				}
			});
		}
		return simButton7;
	}

	private SimButton getSimButton8() {
		if(simButton8 == null) {
			simButton8 = new SimButton();
			simButton8.setText("pickregion");
			simButton8.setVerticalAlignment(SwingConstants.CENTER);
			simButton8.setVerticalTextPosition(SwingConstants.CENTER);
			simButton8.setFont(new java.awt.Font("Dialog",0,10));
			simButton8.setToolTipText("remove nodes within pickrange");
			simButton8.setBounds(73, 152, 67, 15);
			simButton8.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					app.delRegion(false);
				}
			});
		}
		return simButton8;
	}

	private JCheckBox getJCheckBox2x() {
		if(renderTextures == null) {
			renderTextures = new JCheckBox();
			renderTextures.setText("textures");
			renderTextures.setMargin(new java.awt.Insets(0,0,0,0));
			renderTextures.setContentAreaFilled(false);
			renderTextures.setSelected(app.textures);
			renderTextures.setFont(new java.awt.Font("Dialog",0,10));
			renderTextures.setBounds(1, 36, 62, 17);
			renderTextures.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					app.textures=renderTextures.isSelected();
				}
			});
		}
		return renderTextures;
	}

	private SimButton getSimButton9() {
		if(simButton9 == null) {
			simButton9 = new SimButton();
			simButton9.setText("inv.region");
			simButton9.setVerticalAlignment(SwingConstants.CENTER);
			simButton9.setVerticalTextPosition(SwingConstants.CENTER);
			simButton9.setFont(new java.awt.Font("Dialog",0,10));
			simButton9.setToolTipText("remove the nodes outside the pickregion");
			simButton9.setBounds(144, 152, 67, 15);
			simButton9.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					app.delRegion(true);
				}
			});
		}
		return simButton9;
	}

	private JCheckBox getJCheckBox1x() {
		if(jCheckBox1 == null) {
			jCheckBox1 = new JCheckBox();
			jCheckBox1.setText("circular");
			jCheckBox1.setMargin(new java.awt.Insets(0,0,0,0));
			jCheckBox1.setContentAreaFilled(false);
			jCheckBox1.setFont(new java.awt.Font("Dialog",0,10));
			jCheckBox1.setVisible(false);
			jCheckBox1.setBounds(0, 0, 0, 0);
			jCheckBox1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					app.setTree(treeBox.isSelected());
				}
			});
		}
		return jCheckBox1;
	}

	private JPanel getFile() {
		if(file == null) {
			file = new JPanel();
			file.setLayout(null);
			file.add(getLoadNet());
			file.add(getLoadNodeAtt());
			file.add(getSetImgDir());
			file.add(getSaveNetButton());
			file.add(getClear());
			file.add(getSimButton11());
			file.add(getJCheckBox1());
			file.add(getJLabel14());
			file.add(getJLabel12());
			file.add(getJLabel15());
			file.add(getSimButton15());
			file.add(getSimButton15x());
			file.add(getSimButton16());
			file.add(getJFileFormat());
			file.add(getJLabel18());
			file.add(getSimButton18());
			file.add(getSimButton19());
		}
		return file;
	}

	private JButton getLoadNet() {
		if(loadNet == null) {
			loadNet = new SimButton();
			loadNet.setText("add net");
			loadNet.setBounds(2, 18, 67, 15);
			loadNet.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					loadNetwork(app.isTabular());
				}
			});
		}
		return loadNet;
	}

	private SimButton getLoadNodeAtt() {
		if(loadNodeAtt == null) {
			loadNodeAtt = new SimButton();
			loadNodeAtt.setText("add attrib");
			loadNodeAtt.setBounds(73, 18, 67, 15);
			loadNodeAtt.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					loadNodeAttributes();
				}
			});
		}
		return loadNodeAtt;
	}

	private void setImageDir() {
		boolean calc = app.calculate;
		boolean rnd = app.render;
		app.calculate = false;
		app.render = false;
		int returnVal = openPicDir.showOpenDialog(openPicDir);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			app.setTexFolder(openPicDir.getSelectedFile().getAbsolutePath()+"/");
			app.fileIO.loadTextures(app.texfolder, app.ns.view);
		}
		app.calculate=calc;
		app.render=rnd;
	}

	private SimButton getSetImgDir() {
		if(imgDir == null) {
			imgDir = new SimButton();
			imgDir.setText("img dir");
			imgDir.setBounds(144, 18, 67, 15);
			imgDir.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					setImageDir();
				}
			});
		}
		return imgDir;
	}

	private void exportNetwork(boolean tab) {
		boolean calc = app.calculate;
		boolean rnd = app.render;
		app.calculate = false;
		app.render = false;
		int returnVal = saveFile.showSaveDialog(saveFile);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String filename = saveFile.getSelectedFile().toString();
			app.ns.exportNet(filename , tab);
		}
		app.calculate=calc;
		app.render=rnd;
	}

	private SimButton getSaveNetButton() {
		if(saveNet == null) {
			saveNet = new SimButton();
			saveNet.setText("visible net");
			saveNet.setBounds(2, 195, 67, 15);
			saveNet.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					exportNetwork(app.isTabular());
				}
			});
		}
		return saveNet;
	}
	
	private SimButton getClear() {
		if (clear == null) {
			clear = new SimButton();
			clear.setText("delete by att");
			clear.setBounds(2, 83, 67, 15);
			clear.addActionListener(new ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					app.delNodesAtt();
					updateUI(app.ns);
				}
			});
		}
		return clear;
	}

	private JScrollPane getNets() {
		if(nets == null) {
			nets = new JScrollPane();
			nets.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
			nets.setViewportView(getNetList());
		}
		return nets;
	}

	private JCheckBox getDrawClusters() {
		if(drawclusters == null) {
			drawclusters = new JCheckBox();
			drawclusters.setText("clusters");
			drawclusters.setMargin(new java.awt.Insets(0,0,0,0));
			drawclusters.setContentAreaFilled(false);
			drawclusters.setFont(new java.awt.Font("Dialog",0,10));
			drawclusters.setBounds(72, 36, 59, 17);
			drawclusters.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					app.drawClusters=drawclusters.isSelected();
				}
			});
		}
		return drawclusters;
	}
	private SimButton getSimButton10() {
		if(simButton10 == null) {
			simButton10 = new SimButton();
			simButton10.setText("force");
			simButton10.setFont(new Font("Dialog",Font.PLAIN,10));
			simButton10.setToolTipText("arrange nodes with force based algorithm");
			simButton10.setBounds(144, 277, 67, 15);
			simButton10.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					app.layoutForce();
				}
			});
		}
		return simButton10;
	}
	private SimButton getInflateButton() {
		if (inflateButton == null) {
			inflateButton = new SimButton();
			inflateButton.setFont(new Font("Dialog",Font.PLAIN,10));
			inflateButton.setToolTipText("stretch the whole layout");
			inflateButton.setText("inflate");
			inflateButton.setBounds(1, 259, 67, 15);
			inflateButton.addMouseListener(new MouseAdapter() {
				public void mousePressed(java.awt.event.MouseEvent e) {
					app.setInflate(true);
				}
			});
		}
		return inflateButton;
	}
	private SimButton getBoxLayout() {
		if(boxButton == null) {
			boxButton = new SimButton();
			boxButton.setText("box");
			boxButton.setFont(new Font("Dialog",Font.PLAIN,10));
			boxButton.setToolTipText("arrange nodes in boxshape");
			boxButton.setBounds(1, 277, 67, 15);
			boxButton.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					app.layoutBox();
				}
			});
		}
		return boxButton;
	}
	private SimButton getCircle() {
		if(simButton6 == null) {
			simButton6 = new SimButton();
			simButton6.setText("circle");
			simButton6.setFont(new Font("Dialog",Font.PLAIN,10));
			simButton6.setToolTipText("arrange nodes in circular shape");
			simButton6.setBounds(72, 277, 67, 15);
			simButton6.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					app.layoutCircle();
				}
			});
		}
		return simButton6;
	}
	private SimButton getShuffleButton() {
		if (shuffleButton == null) {
			shuffleButton = new SimButton();
			shuffleButton.setFont(new Font("Dialog",Font.PLAIN,10));
			shuffleButton.setToolTipText("randomize layout");
			shuffleButton.setText("shuffle");
			shuffleButton.setBounds(72, 259, 67, 15);
			shuffleButton.addActionListener(new ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					app.layout.layoutNodePosRandomize();
				}
			});
		}
		return shuffleButton;
	}

	private SimButton getSimButton11() {
		if(simButton11 == null) {
			simButton11 = new SimButton();
			simButton11.setText("delete all");
			simButton11.setBounds(73, 83, 67, 15);
			simButton11.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					app.clearNets();
					updateUI(app.ns);
				}
			});
		}
		return simButton11;
	}

	private SimButton getSimButton12() {
		if(simButton12 == null) {
			simButton12 = new SimButton();
			simButton12.setText("framed");
			simButton12.setVerticalAlignment(SwingConstants.CENTER);
			simButton12.setVerticalTextPosition(SwingConstants.CENTER);
			simButton12.setFont(new java.awt.Font("Dialog",0,10));
			simButton12.setToolTipText("remove framed Graphelements");
			simButton12.setBounds(73, 171, 67, 15);
			simButton12.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					app.delFramed(false);
				}
			});
		}
		return simButton12;
	}

	private SimButton getSimButton13() {
		if(simButton13 == null) {
			simButton13 = new SimButton();
			simButton13.setText("inv. framed");
			simButton13.setVerticalAlignment(SwingConstants.CENTER);
			simButton13.setVerticalTextPosition(SwingConstants.CENTER);
			simButton13.setFont(new java.awt.Font("Dialog",0,10));
			simButton13.setToolTipText("remove graphElements not framed");
			simButton13.setBounds(144, 171, 67, 15);
			simButton13.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					app.delFramed(true);
				}
			});
		}
		return simButton13;
	}
	private JCheckBox getJCheckBox() {
		if (draw3d == null) {
			draw3d = new JCheckBox();
			draw3d.setText("3D");
			draw3d.setMargin(new java.awt.Insets(0,0,0,0));
			draw3d.setContentAreaFilled(false);
			draw3d.setFont(new java.awt.Font("Dialog",0,10));
			draw3d.setBounds(0, 41, 60, 17);
			draw3d.setSelected(!app.get3D());
			draw3d.addActionListener(new ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					app.toggle3D();
					draw3d.setSelected(!app.get3D());
				}
			});
		}
		return draw3d;
	}
	private JCheckBox getJCheckBox1() {
		if (directedGraph == null) {
			directedGraph = new JCheckBox();
			directedGraph.setText("directed graph");
			directedGraph.setMargin(new java.awt.Insets(0,0,0,0));
			directedGraph.setContentAreaFilled(false);
			directedGraph.setFont(new java.awt.Font("Dialog",0,10));
			directedGraph.setBounds(119, 36, 92, 17);
			directedGraph.setSelected(app.directed);
			directedGraph.addActionListener(new ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					app.directed=directedGraph.isSelected();
					app.ns.view.updateAdLists(app.directed);
					app.updatePick();
				}
			});
		}
		return directedGraph;
	}

	private SimButton getSimButton14() {
		if(simButton14 == null) {
			simButton14 = new SimButton();
			simButton14.setText("framed");
			simButton14.setVerticalAlignment(SwingConstants.CENTER);
			simButton14.setVerticalTextPosition(SwingConstants.CENTER);
			simButton14.setFont(new java.awt.Font("Dialog",0,10));
			simButton14.setBounds(73, 114, 67, 15);
			simButton14.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					app.netExpandFramed(); //  Auto-generated Event stub actionPerformed()
				}
			});
		}
		return simButton14;
	}

	private JLabel getJLabel13() {
		if(jLabel13 == null) {
			jLabel13 = new JLabel();
			jLabel13.setText("expand");
			jLabel13.setFont(new java.awt.Font("Dialog",1,11));
			jLabel13.setBounds(0, 95, 210, 14);
		}
		return jLabel13;
	}

	private JLabel getJLabel14() {
		if(jLabel14 == null) {
			jLabel14 = new JLabel();
			jLabel14.setText("source files");
			jLabel14.setFont(new java.awt.Font("Dialog",1,11));
			jLabel14.setBounds(2, 0, 210, 14);
		}
		return jLabel14;
	}

	private JLabel getJLabel12() {
		if(jLabel12 == null) {
			jLabel12 = new JLabel();
			jLabel12.setText("delete");
			jLabel12.setFont(new java.awt.Font("Dialog",1,11));
			jLabel12.setBounds(2, 65, 210, 14);
		}
		return jLabel12;
	}

	private JLabel getJLabel15() {
		if(jLabel15 == null) {
			jLabel15 = new JLabel();
			jLabel15.setText("subnets");
			jLabel15.setFont(new java.awt.Font("Dialog",1,11));
			jLabel15.setBounds(2, 108, 210, 14);
		}
		return jLabel15;
	}

	private SimButton getSimButton15() {
		if(saveNetButton == null) {
			saveNetButton = new SimButton();
			saveNetButton.setText("save");
			saveNetButton.setBounds(2, 128, 67, 15);
			saveNetButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					app.saveNet();
				}
			});
		}
		return saveNetButton;
	}

	private SimButton getSimButton17() {
		if(simButton17 == null) {
			simButton17 = new SimButton();
			simButton17.setText("clusters");
			simButton17.setVerticalAlignment(SwingConstants.CENTER);
			simButton17.setVerticalTextPosition(SwingConstants.CENTER);
			simButton17.setFont(new java.awt.Font("Dialog",0,10));
			simButton17.setToolTipText("remove clusters");
			simButton17.setBounds(1, 190, 67, 15);
			simButton17.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					app.netRemoveClusters();
				}
			});
		}
		return simButton17;
	}
	
	private JCheckBox getJCheckBox2xx() {
		if(tiltBox == null) {
			tiltBox = new JCheckBox();
			tiltBox.setText("30¡");
			tiltBox.setMargin(new java.awt.Insets(0,0,0,0));
			tiltBox.setContentAreaFilled(false);
			tiltBox.setFont(new java.awt.Font("Dialog",0,10));
			tiltBox.setBounds(176, 11, 85, 47);
			tiltBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					app.tilt=tiltBox.isSelected();
				}
			});
		}
		return tiltBox;
	}
	
	private SimButton getSimButton15x() {
		if(simButton15 == null) {
			simButton15 = new SimButton();
			simButton15.setText("remove");
			simButton15.setBounds(73, 128, 67, 15);
			simButton15.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					app.removeNet((String)netList.getSelectedValue());
				}
			});
		}
		return simButton15;
	}
	
	private SimButton getSimButton16() {
		if(simButton16 == null) {
			simButton16 = new SimButton();
			simButton16.setText("show");
			simButton16.setBounds(2, 147, 67, 15);
			simButton16.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					app.setView((String)netList.getSelectedValue());
				}
			});
		}
		return simButton16;
	}
	
	private JLabel getJLabel16() {
		if(jLabel16 == null) {
			jLabel16 = new JLabel();
			jLabel16.setText("labelSize");
			jLabel16.setFont(new java.awt.Font("Dialog",0,10));
			jLabel16.setBounds(144, 130, 60, 15);
		}
		return jLabel16;
	}
	
	private JSlider getJSlider3() {
		if(jSlider3 == null) {
			jSlider3 = new JSlider();
			jSlider3.setMaximum(200);
			jSlider3.setMinimum(0);
			jSlider3.setToolTipText("set label size");
			jSlider3.setOpaque(false);
			jSlider3.setName("jSlider3");
			jSlider3.setBounds(1, 130, 140, 16);
			jSlider3.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					app.setLabelsize(jSlider3.getValue()/10f);
				}
			});
		}
		return jSlider3;
	}
	
	private JSlider getJSlider4() {
		if(jSlider4 == null) {
			jSlider4 = new JSlider();
			jSlider4.setMaximum(20);
			jSlider4.setMinimum(0);
			jSlider4.setToolTipText("set label variance");
			jSlider4.setOpaque(false);
			jSlider4.setName("jSlider4");
			jSlider4.setBounds(1, 150, 140, 16);
			jSlider4.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					app.setLabelVar(jSlider4.getValue()/10f);
				}
			});
		}
		return jSlider4;
	}
	
	private JLabel getJLabel17() {
		if(jLabel17 == null) {
			jLabel17 = new JLabel();
			jLabel17.setText("labelVar");
			jLabel17.setFont(new java.awt.Font("Dialog",0,10));
			jLabel17.setBounds(144, 150, 60, 15);
		}
		return jLabel17;
	}

	private JCheckBox getJFileFormat() {
		if(jFileFormat == null) {
			jFileFormat = new JCheckBox();
			jFileFormat.setText("tabular file format");
			jFileFormat.setFont(new java.awt.Font("Dialog",0,10));
			jFileFormat.setSelected(app.isTabular());
			jFileFormat.setBounds(-1, 33, 120, 23);
			jFileFormat.addActionListener(new ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					app.setTabular(jFileFormat.isSelected());
				}
			});
		}
		return jFileFormat;
	}
	
	private JCheckBox getJCheckBox2xxx() {
		if(drawgroups == null) {
			drawgroups = new JCheckBox();
			drawgroups.setText("groups");
			drawgroups.setMargin(new java.awt.Insets(0,0,0,0));
			drawgroups.setContentAreaFilled(false);
			drawgroups.setFont(new java.awt.Font("Dialog",0,10));
			drawgroups.setBounds(144, 18, 58, 17);
			drawgroups.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					app.setGroups(drawgroups.isSelected());
				}
			});
		}
		return drawgroups;
	}
	
	private JLabel getJLabel18() {
		if(jLabel18 == null) {
			jLabel18 = new JLabel();
			jLabel18.setText("export");
			jLabel18.setFont(new java.awt.Font("Dialog",1,11));
			jLabel18.setBounds(2, 175, 60, 14);
		}
		return jLabel18;
	}
	
	private SimButton getSimButton18() {
		if(simButton18 == null) {
			simButton18 = new SimButton();
			simButton18.setText("tga file");
			simButton18.setBounds(74, 195, 67, 15);
			simButton18.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					boolean calc = app.calculate;
					boolean rnd = app.render;
					app.calculate = false;
					app.render = false;
					int returnVal = saveFile.showSaveDialog(saveFile);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						String filename = saveFile.getSelectedFile().toString();
						if (!filename.endsWith(".tga")) filename+=".tga";
						app.calculate=calc;
						app.render=rnd;
						app.screenshot(app.shotres, app.shotres, filename);
					}
				}
			});
		}
		return simButton18;
	}
	
	private SimButton getSimButton19() {
		if(simButton19 == null) {
			simButton19 = new SimButton();
			simButton19.setText("svg file");
			simButton19.setBounds(146, 195, 67, 15);
			simButton19.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					boolean calc = app.calculate;
					boolean rnd = app.render;
					app.calculate = false;
					app.render = false;
					int returnVal = saveFile.showSaveDialog(saveFile);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						String filename = saveFile.getSelectedFile().toString();
						if (!filename.endsWith(".svg")) filename+=".svg";
						app.calculate=calc;
						app.render=rnd;
						app.exportSVG (filename);
					}
				}
			});
		}
		return simButton19;
	}
}	
