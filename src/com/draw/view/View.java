package com.draw.view;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.DefaultCaret;

import com.draw.execution.ExecutionEnvironment;
import com.draw.execution.Main;
import com.draw.execution.Publisher;
import com.draw.util.CircularList;
import com.draw.util.geometry.Point;
import com.draw.util.geometry.Trapezoid;

/**
 *
 * @author ABF Ampt
 */
public class View extends javax.swing.JFrame
{

	public enum PrintType
	{
		PRINT, PRINTLN, PRINTEXCEPTION
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 7325367166522348917L;
	/** the file chooser used when opening/saving files */
	private JFileChooser fileChooser;

	private String activeAlgorithmName = "";
	private String activeFilePath = "";

	/**
	 * Creates new form MainFrame
	 */
	public View()
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		catch (UnsupportedLookAndFeelException e)
		{
			e.printStackTrace();
		}
		initComponents();
		initFileChooser();
		executeDefault();
	}

	/**
	 * Execute the default algorithm if config.conf exists and is configured correctly
	 */
	private void executeDefault()
	{
		File confFile = new File("src/config.conf");

		if (!confFile.exists())
		{
			println("Missing config.conf, no default executed");
			return;
		}

		Scanner scanner = null;
		try
		{
			// create new Polygon
			scanner = new Scanner(confFile);

			if (scanner.hasNextLine())
			{
				String name = scanner.nextLine().trim();
				if (!Main.algorithms.containsKey(name))
				{
					throw new IllegalArgumentException(
							"The algorithm in the config.conf file is not known");
				}
				this.algorithmMenuItems.get(name).setSelected(true);
				activeAlgorithmName = name;
			}
			else
				throw new IllegalArgumentException(
						"config.conf is empty, no default executed");

			if (scanner.hasNextLine())
			{
				String filePath = scanner.nextLine().trim();

				File file = new File(filePath);
				if (!file.exists())
				{
					throw new IllegalArgumentException(
							"File in the config.conf file is not known");
				}
				executeAlgorithm(file);
			}
			else
				throw new IllegalArgumentException(
						"Cannot open this config.conf file, not the right format");
		}
		catch (FileNotFoundException | IllegalArgumentException ex)
		{
			println(ex.getMessage());
			PrintWriter writer = null;
			try
			{
				writer = new PrintWriter(confFile);
				writer.print("");
			}
			catch (FileNotFoundException e)
			{
				printStackTrace(e);
			}
			finally
			{
				writer.close();
			}
		}
		finally
		{
			scanner.close();
		}
	}

	/**
	 * Execute the active Algorithm for a given file 
	 * @param file
	 */
	private void executeAlgorithm(File file)
	{
		canvas.scale = 1;
		canvas.translateX = 0;
		canvas.translateY = 0;
		progressIndicator.setVisible(true);

		this.activeFilePath = file.getAbsolutePath();

		this.setTitle("Trapezoidal maps: " + activeFilePath);

		SwingWorker<CircularList<Point>, String> worker = this
				.algorithmInputReader(file);

		worker.execute();
	}

	/**
	 * Background worker for reading the input file
	 * @param file
	 * @return
	 */
	private SwingWorker<CircularList<Point>, String> algorithmInputReader(
			File file)
	{
		return new SwingWorker<CircularList<Point>, String>()
		{
			ExecutionEnvironment e;

			@Override
			protected CircularList<Point> doInBackground() throws Exception
			{
				e = new ExecutionEnvironment();
				return e.readInput(file);
			}

			@Override
			protected void done()
			{
				CircularList<Point> points;
				try
				{
					points = get();
				}
				catch (InterruptedException | ExecutionException e1)
				{
					println("Cannot proccess file: " + activeFilePath);
					printStackTrace(e1);
					progressIndicator.setVisible(false);
					return;
				}

				canvas.setPoints(points);
				sizeLabel.setText(Integer.toString(points.size()));

				SwingWorker<Set<Trapezoid>, Entry<PrintType, Object>> worker = algorithmWorker(
						e, points);

				worker.execute();
			}
		};
	}

	/**
	 * Retrieve the algorithm work
	 * @return
	 */
	private SwingWorker<Set<Trapezoid>, Entry<PrintType, Object>> algorithmWorker(
			ExecutionEnvironment e, CircularList<Point> simplePolygon)
	{
		return new SwingWorker<Set<Trapezoid>, Entry<PrintType, Object>>()
		{

			@Override
			protected Set<Trapezoid> doInBackground() throws Exception
			{
				publish(new AbstractMap.SimpleEntry<PrintType, Object>(
						PrintType.PRINTLN, "Start algorithm: "
								+ activeAlgorithmName));

				Publisher publisher = new Publisher()
				{

					@Override
					public void print(Object obj)
					{
						publish(new AbstractMap.SimpleEntry<PrintType, Object>(
								PrintType.PRINT, obj));
					}

					@Override
					public void println(Object obj)
					{
						publish(new AbstractMap.SimpleEntry<PrintType, Object>(
								PrintType.PRINTLN, obj));
					}

					@Override
					public void printStackTrace(Exception e)
					{
						publish(new AbstractMap.SimpleEntry<PrintType, Object>(
								PrintType.PRINTEXCEPTION, e));
					}

				};

				Set<Trapezoid> result = e.executeAlgorithm(
						Main.algorithms.get(activeAlgorithmName), publisher,
						simplePolygon);

				publish(new AbstractMap.SimpleEntry<PrintType, Object>(
						PrintType.PRINTLN, "Stop algorithm: "
								+ activeAlgorithmName));
				return result;
			}

			@Override
			protected void process(List<Entry<PrintType, Object>> chunks)
			{
				super.process(chunks);
				for (Entry<PrintType, Object> chunck : chunks)
					switch (chunck.getKey())
					{
					case PRINT:
						print(chunck.getValue());
						break;
					case PRINTLN:
						println(chunck.getValue());
						break;
					case PRINTEXCEPTION:
						printStackTrace((Exception) chunck.getValue());
						break;
					}
			}

			@Override
			protected void done()
			{
				super.done();
				try
				{
					Set<Trapezoid> result = get();
					canvas.setTrapezoids(result);

					View.this.trapezoidsLabel.setText(Integer.toString(result
							.size()));
					View.this.memoryLabel.setText(String.format("%.3f",
							e.getExecutionMemory() / (1024d * 1024d)));
					View.this.timeLabel.setText(String.format("%.3f",
							e.getExecutionTime() / 1000.0));
					println("done");
				}
				catch (InterruptedException | ExecutionException e)
				{
					printStackTrace(e);
				}
				finally
				{
					progressIndicator.setVisible(false);
				}
			}
		};
	}

	private void initFileChooser()
	{
		fileChooser = new JFileChooser();
		// Makes sure only .txt files can be selected
		// Other directories are also allowed
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.addChoosableFileFilter(new FileFilter()
		{
			public boolean accept(File f)
			{
				return (f.isDirectory() || f.getName().toLowerCase()
						.endsWith(".txt"));
			}

			public String getDescription()
			{
				return "Text Documents (*.txt)";
			}
		});
	}

	public void print(Object text)
	{
		if (text == null)
			console.append("(null)");
		else
			console.append(text.toString());
	}

	public void println(Object text)
	{
		print(text);
		console.append("\n");
	}

	public void printStackTrace(Exception e)
	{
		StringWriter writer = new StringWriter();
		e.printStackTrace(new PrintWriter(writer));
		console.append(writer.toString());
	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	private void initComponents()
	{
		algorithmGroup = new javax.swing.ButtonGroup();
		jSplitPane1 = new javax.swing.JSplitPane();
		jPanel1 = new javax.swing.JPanel();
		scrollPane = new javax.swing.JScrollPane();
		jPanel2 = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		sizeLabel = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		trapezoidsLabel = new javax.swing.JLabel();
		jLabel3 = new javax.swing.JLabel();
		timeLabel = new javax.swing.JLabel();
		jLabel4 = new javax.swing.JLabel();
		progressIndicator = new javax.swing.JLabel();
		memoryLabel = new javax.swing.JLabel();
		timeUnitLabel = new javax.swing.JLabel();
		memoryUnitLabel = new javax.swing.JLabel();
		console = new javax.swing.JTextArea();
		clearButton = new javax.swing.JButton();
		canvas = new Canvas();
		menuBar = new javax.swing.JMenuBar();
		menuFile = new javax.swing.JMenu();
		menuItemOpen = new javax.swing.JMenuItem();
		menuItemSave = new javax.swing.JMenuItem();
		menuAlgorithm = new javax.swing.JMenu();
		viewMenu = new javax.swing.JMenu();
		polygonBorderMenuItem = new javax.swing.JCheckBoxMenuItem();
		indicateTrapezoidsMenuItem = new javax.swing.JCheckBoxMenuItem();
		menuItemCenter = new javax.swing.JMenuItem();

		algorithmMenuItems = new LinkedHashMap<String, javax.swing.JRadioButtonMenuItem>();

		for (String key : Main.algorithms.keySet())
			algorithmMenuItems.put(key, new javax.swing.JRadioButtonMenuItem());

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle("Trapezoidal maps");
		setIconImage(Toolkit.getDefaultToolkit().getImage(
				getClass().getResource("/images/logo.png")));

		jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
		jSplitPane1.setResizeWeight(0.8);

		canvas.setBorder(javax.swing.BorderFactory
				.createLineBorder(new java.awt.Color(0, 0, 0)));

		javax.swing.GroupLayout canvasLayout = new javax.swing.GroupLayout(
				canvas);
		canvas.setLayout(canvasLayout);
		canvasLayout.setHorizontalGroup(canvasLayout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 347,
				Short.MAX_VALUE));
		canvasLayout.setVerticalGroup(canvasLayout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 187,
				Short.MAX_VALUE));

		jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
		jLabel1.setText("Points:");

		jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
		jLabel2.setText("Trapezoids:");

		jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
		jLabel3.setText("Time:");

		jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
		jLabel4.setText("Memory:");

		timeUnitLabel.setText("sec");

		memoryUnitLabel.setText("MB");

		progressIndicator
				.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		progressIndicator.setIcon(new javax.swing.ImageIcon(getClass()
				.getResource("/images/spinner.gif"))); // NOI18N
		progressIndicator.setVisible(false);

		clearButton.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/images/clear.png"))); // NOI18N
		clearButton.setFocusable(false);
		clearButton.setBorder(BorderFactory.createEmptyBorder());
		clearButton.setContentAreaFilled(false);

		clearButton.setToolTipText("Clear console");
		clearButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				console.setText("");
			}
		});

		javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(
				jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout
				.setHorizontalGroup(jPanel2Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								javax.swing.GroupLayout.Alignment.TRAILING,
								jPanel2Layout
										.createSequentialGroup()
										.addGroup(
												jPanel2Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.TRAILING)
														.addGroup(
																jPanel2Layout
																		.createSequentialGroup()
																		.addContainerGap()
																		.addComponent(
																				progressIndicator,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				Short.MAX_VALUE))
														.addGroup(
																javax.swing.GroupLayout.Alignment.LEADING,
																jPanel2Layout
																		.createSequentialGroup()
																		.addComponent(
																				timeLabel)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				Short.MAX_VALUE)
																		.addComponent(
																				timeUnitLabel))
														.addGroup(
																javax.swing.GroupLayout.Alignment.LEADING,
																jPanel2Layout
																		.createSequentialGroup()
																		.addGroup(
																				jPanel2Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addComponent(
																								sizeLabel)
																						.addComponent(
																								jLabel1)
																						.addComponent(
																								jLabel2)
																						.addComponent(
																								trapezoidsLabel)
																						.addComponent(
																								jLabel3)
																						.addComponent(
																								jLabel4))
																		.addGap(0,
																				0,
																				Short.MAX_VALUE))
														.addGroup(
																javax.swing.GroupLayout.Alignment.LEADING,
																jPanel2Layout
																		.createSequentialGroup()
																		.addComponent(
																				memoryLabel)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				Short.MAX_VALUE)
																		.addComponent(
																				memoryUnitLabel)))
										.addContainerGap())
						.addGroup(
								javax.swing.GroupLayout.Alignment.TRAILING,
								jPanel2Layout.createSequentialGroup()
										.addGap(0, 0, Short.MAX_VALUE)
										.addComponent(clearButton)));
		jPanel2Layout
				.setVerticalGroup(jPanel2Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel2Layout
										.createSequentialGroup()
										.addComponent(jLabel1)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(sizeLabel)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jLabel2)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(trapezoidsLabel)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jLabel3)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												jPanel2Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(timeLabel)
														.addComponent(
																timeUnitLabel))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jLabel4)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												jPanel2Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																memoryLabel)
														.addComponent(
																memoryUnitLabel))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(progressIndicator)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED,
												163, Short.MAX_VALUE)
										.addComponent(clearButton)));

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(
				jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout
				.setHorizontalGroup(jPanel1Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGap(0, 0, Short.MAX_VALUE)
						.addGroup(
								jPanel1Layout
										.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
										.addGroup(
												jPanel1Layout
														.createSequentialGroup()
														.addContainerGap()
														.addComponent(
																canvas,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(
																jPanel2,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addContainerGap())));
		jPanel1Layout
				.setVerticalGroup(jPanel1Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGap(0, 0, Short.MAX_VALUE)
						.addGroup(
								jPanel1Layout
										.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
										.addGroup(
												jPanel1Layout
														.createSequentialGroup()
														.addContainerGap()
														.addGroup(
																jPanel1Layout
																		.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.LEADING)
																		.addComponent(
																				canvas,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				Short.MAX_VALUE)
																		.addComponent(
																				jPanel2,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				Short.MAX_VALUE))
														.addContainerGap())));

		jSplitPane1.setTopComponent(jPanel1);

		console.setEditable(false);
		console.setColumns(20);
		console.setRows(5);
		console.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
		DefaultCaret caret = (DefaultCaret) console.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		scrollPane.setViewportView(console);

		jSplitPane1.setBottomComponent(scrollPane);

		menuFile.setText("File");

		menuItemOpen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_O,
				java.awt.event.InputEvent.CTRL_MASK));
		menuItemOpen.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/images/open.png"))); // NOI18N
		menuItemOpen.setText("Open");
		menuItemOpen.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				menuItemOpenActionPerformed(evt);
			}
		});
		menuFile.add(menuItemOpen);

		menuItemSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_S,
				java.awt.event.InputEvent.CTRL_MASK));
		menuItemSave.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/images/save.png"))); // NOI18N
		menuItemSave.setText("Save as default configuration");
		menuItemSave.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				menuItemSaveActionPerformed(evt);
			}
		});
		menuFile.add(menuItemSave);

		menuBar.add(menuFile);

		menuAlgorithm.setText("Algorithm");

		boolean first = true;

		for (Entry<String, javax.swing.JRadioButtonMenuItem> entry : algorithmMenuItems
				.entrySet())
		{
			javax.swing.JRadioButtonMenuItem item = entry.getValue();
			algorithmGroup.add(item);
			if (first)
			{
				item.setSelected(true);
				activeAlgorithmName = entry.getKey();
			}
			item.setText(entry.getKey());
			item.setActionCommand(entry.getKey());
			item.addActionListener(this.radioButtonActionListener());
			menuAlgorithm.add(item);

			first = false;
		}

		menuBar.add(menuAlgorithm);
		viewMenu.setText("View");
		polygonBorderMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_1, java.awt.event.InputEvent.CTRL_MASK));
		polygonBorderMenuItem.setSelected(false);
		polygonBorderMenuItem.setText("Show original polygon border");
		polygonBorderMenuItem
				.addActionListener(new java.awt.event.ActionListener()
				{
					public void actionPerformed(java.awt.event.ActionEvent evt)
					{
						polygonBorderMenuItemActionPerformed(evt);
					}
				});
		viewMenu.add(polygonBorderMenuItem);
		
		indicateTrapezoidsMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_2, java.awt.event.InputEvent.CTRL_MASK));
		indicateTrapezoidsMenuItem.setSelected(false);
		indicateTrapezoidsMenuItem.setText("Show trapezoid endpoints");
		indicateTrapezoidsMenuItem
				.addActionListener(new java.awt.event.ActionListener()
				{
					public void actionPerformed(java.awt.event.ActionEvent evt)
					{
						indicateTrapezoidsMenuItemActionPerformed(evt);
					}
				});
		viewMenu.add(indicateTrapezoidsMenuItem);
		
		menuItemCenter.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_G,
				java.awt.event.InputEvent.CTRL_MASK));
		menuItemCenter.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/images/center.png"))); // NOI18N
		menuItemCenter.setText("Back to center");
		menuItemCenter.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				menuItemCenterActionPerformed(evt);
			}
		});
		
		viewMenu.add(menuItemCenter);

		menuBar.add(viewMenu);

		setJMenuBar(menuBar);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup()
						.addContainerGap()
						.addComponent(jSplitPane1,
								javax.swing.GroupLayout.DEFAULT_SIZE, 445,
								Short.MAX_VALUE).addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup()
						.addContainerGap()
						.addComponent(jSplitPane1,
								javax.swing.GroupLayout.DEFAULT_SIZE, 485,
								Short.MAX_VALUE).addContainerGap()));

		pack();
	}

	private ActionListener radioButtonActionListener()
	{
		return new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				activeAlgorithmName = e.getActionCommand();
				println(activeAlgorithmName + " set as the active algorithm");
				executeAlgorithm(new File(activeFilePath));
			}
		};
	}

	private void menuItemOpenActionPerformed(java.awt.event.ActionEvent evt)
	{
		int action = fileChooser.showOpenDialog(this);
		// if open button is pressed, open the file.
		if (action == JFileChooser.APPROVE_OPTION)
		{
			File file = fileChooser.getSelectedFile();
			executeAlgorithm(file);
		}
	}

	private void menuItemSaveActionPerformed(java.awt.event.ActionEvent evt)
	{
		File theDir = new File("src");

		try
		{
			theDir.mkdir();
		}
		catch (SecurityException se)
		{
			printStackTrace(se);
		}

		File file = new File(theDir, "config.conf");

		PrintWriter writer = null;
		try
		{
			writer = new PrintWriter(file, "UTF-8");
			writer.println(activeAlgorithmName);
			writer.print(activeFilePath);

			println("configuration using " + activeAlgorithmName
					+ " with input " + activeFilePath + " saved in "
					+ file.getPath());
		}
		catch (FileNotFoundException | UnsupportedEncodingException e)
		{
			printStackTrace(e);
		}
		finally
		{
			writer.close();
		}
	}

	private void polygonBorderMenuItemActionPerformed(
			java.awt.event.ActionEvent evt)
	{
		canvas.showPolygonBorder = polygonBorderMenuItem.isSelected();
		canvas.repaint();
	}

	private void indicateTrapezoidsMenuItemActionPerformed(
			java.awt.event.ActionEvent evt)
	{
		canvas.indicateTrapezoidEndPoints = indicateTrapezoidsMenuItem.isSelected();
		canvas.repaint();
	}
	
	private void menuItemCenterActionPerformed(java.awt.event.ActionEvent evt)
	{
		canvas.scale = 1;
		canvas.translateX = 0;
		canvas.translateY = 0;
		canvas.repaint();
	}

	// Variables declaration - do not modify
	private javax.swing.ButtonGroup algorithmGroup;
	private LinkedHashMap<String, javax.swing.JRadioButtonMenuItem> algorithmMenuItems;
	private Canvas canvas;
	private javax.swing.JTextArea console;
	private javax.swing.JButton clearButton;
	private javax.swing.JCheckBoxMenuItem indicateTrapezoidsMenuItem;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel progressIndicator;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JSplitPane jSplitPane1;
	private javax.swing.JLabel memoryLabel;
	private javax.swing.JLabel memoryUnitLabel;
	private javax.swing.JMenu menuAlgorithm;
	private javax.swing.JMenuBar menuBar;
	private javax.swing.JMenu menuFile;
	private javax.swing.JMenuItem menuItemCenter;
	private javax.swing.JMenuItem menuItemOpen;
	private javax.swing.JMenuItem menuItemSave;
	private javax.swing.JCheckBoxMenuItem polygonBorderMenuItem;
	private javax.swing.JScrollPane scrollPane;
	private javax.swing.JLabel sizeLabel;
	private javax.swing.JLabel timeLabel;
	private javax.swing.JLabel timeUnitLabel;
	private javax.swing.JLabel trapezoidsLabel;
	private javax.swing.JMenu viewMenu;
	// End of variables declaration
}