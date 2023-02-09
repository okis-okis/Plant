package plant.controllers;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Constructor;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

import javafx.application.Platform;
import plant.IFrame;
import plant.Main;
import plant.lib.CustomComboBox;
import plant.reports.ReportStacks;
import plant.reports.ReportVans;
import plant.reports.ReportWorkers;

public class ManagementController {
	
	private static JLabel dateLabel;
	/**
	 * @param mainFrame Variable using for build MDI Frame
	 * @see JFrame   
	 */
	static JFrame mainFrame;
	
	/**
	 * @param desktop Variable using for control MDI </br>
	 * and add new frames;
	 * @see JDesktopPane
	 */
	private JDesktopPane desktop;
	
	public ManagementController() {	
//		BorderPane borderPane = new BorderPane();
//		
//		SwingNode menuBar = new SwingNode();
//		
//		SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//            	menuBar.setContent(initializationMenu());
//            }
//        });
//		
//		borderPane.setTop(menuBar);
//		
//		SwingNode content = new SwingNode();
//		
//		SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//            	content.setContent(initializationContent());
//            }
//        });
//		
//	    borderPane.setCenter(content);
//	    
//		scene = new Scene(borderPane);
//		stage = new Stage();
//		stage.setScene(scene);
//		stage.setMaximized(true);
//		stage.show();
		
		//Init main frame
		mainFrame = new JFrame("СУБД фиксации обработки сырья для металлургического комбината");
							
		//Add to close program event operation for close database connection
		mainFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				Main.getDB().closeConnection();
		        // Terminate the program after the close button is clicked.
				System.exit(0);
			}
		});
							
		//Add new components of interface
		mainFrame.setJMenuBar(initializationMenu());
							
		mainFrame.setContentPane(initializationContent());
					
		mainFrame.setSize(700, 400);
		// this method display the JFrame to center position of a screen
		mainFrame.setLocationRelativeTo(null); 
							
		mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
							
		mainFrame.setVisible(true);
	}
	
	/**
	 * Public function for add new components to MDI
	 * @param component New component, that will add to MDI
	 */
	public void desktopAddFrame(Component component) {
		desktop.add(component);
	}

	/**
	 * Set enable/disable main form
	 * @param enable Mode of main form enable</br>
	 * 		  true - enable </br>
	 * 		  fasle - disable
	 */
	public void setEnable(Boolean enable) {
		mainFrame.setEnabled(enable);
	}
	
	/**
	 * Check if form enabled
	 * @return true if form enabled</br>
	 * 			false if form disabled
	 */
	public static Boolean isEnabled() {
		return mainFrame.isEnabled();
	}

	/**
	 * Initialization JMenu of program
	 * @return JMenuBar Return component with customized JMenu
	 * @see JMenuBar
	 */
	private JMenuBar initializationMenu(){
		//Create JMenu
		JMenuBar result = new JMenuBar();

		//Create Table JMenu and Sub JMenu
		JMenu tableJMenu = new JMenu("Таблицы");
		
		JMenu subJMenu = initRawMaterialJMenu(); 
		if(subJMenu!=null) {
			tableJMenu.add(subJMenu);
		}
		
		subJMenu = initMembersJMenu();
		if(subJMenu!=null) {
			tableJMenu.add(subJMenu);
		}
		
		subJMenu = initFormingSinterJMenu();
		if(subJMenu!=null) {
			tableJMenu.add(subJMenu);
		}
		
		subJMenu = initPrerationMaterialJMenu(); 
		if(subJMenu!=null) {
			tableJMenu.add(subJMenu);
		}
		
		JMenuItem mitem = createSubJMenu("MaterialComposition", "Таблица составов");
		if(mitem!=null) {
			tableJMenu.add(mitem);
		}
		
		//Add to JMenu bar all JMenus
		if(tableJMenu.getItemCount()!=0) {
			result.add(tableJMenu);
		}
		
		result.add(initRaportJMenu());
		
		result.add(initAdditionalInfoJMenu());
		
		return result;
	}
	
	private JMenu initAdditionalInfoJMenu() {
		JMenu additionalyJMenu = new JMenu("Дополнительно");
		
		JMenuItem docJMenu = new JMenuItem("Документация");
		docJMenu.addActionListener(getDocActionListener());
		additionalyJMenu.add(docJMenu);
		
		JMenuItem aboutJMenu = new JMenuItem("Об авторе");
		aboutJMenu.addActionListener(getAboutActionListener());
		additionalyJMenu.add(aboutJMenu);
		
		return additionalyJMenu;
	}
	
	private JMenu initRaportJMenu() {
		JMenu reportsJMenu = new JMenu("Отчёты");
		
		JMenuItem workersReportSubJMenu = new JMenuItem("Отчёт по работникам");
		workersReportSubJMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){  
				new ReportWorkers();
		    }
		});
		reportsJMenu.add(workersReportSubJMenu);
		
		JMenuItem vansReportSubJMenu = new JMenuItem("Паспорта на вагоны");
		vansReportSubJMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){  
				new ReportVans();
		    }
		});
		reportsJMenu.add(vansReportSubJMenu);
		
		JMenuItem stacksReportSubJMenu = new JMenuItem("Паспорта на штабели");
		stacksReportSubJMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){  
				new ReportStacks();
		    }
		});
		reportsJMenu.add(stacksReportSubJMenu);
		
		return reportsJMenu;
	}
	/**
	 * Create JMenu section "raw material"
	 * @return Customized JMenu with sub JMenus
	 */
	private JMenu initRawMaterialJMenu() {
		try {
			JMenu rawMaterial = new JMenu("Стадия получения сырья");
			
			JMenuItem subJMenu = createSubJMenu("Mines", "Таблица рудников");
			if(subJMenu!=null) {
				rawMaterial.add(subJMenu);
			}
			
			subJMenu = createSubJMenu("Echelons", "Таблица эшелонов");
			if(subJMenu!=null) {
				rawMaterial.add(subJMenu);
			}
			
			subJMenu = createSubJMenu("Vans", "Таблица вагонов");
			if(subJMenu!=null) {
				rawMaterial.add(subJMenu);
			}
			
			if(rawMaterial.getItemCount()==0){
				return null;
			}
			return rawMaterial;
		}catch(Exception e) {}
		return null;
	}
	
	/**
	 * Create JMenu section "members JMenu"
	 * @return Customized JMenu with sub JMenus
	 */
	private JMenu initMembersJMenu() {
		try {
			JMenu members = new JMenu("Работники");
			
			JMenuItem subJMenu = createSubJMenu("Positions", "Таблица должностей");
			if(subJMenu!=null) {
				members.add(subJMenu);
			}
			
			
			subJMenu = createSubJMenu("Workers", "Таблица работников");
			if(subJMenu!=null) {
				members.add(subJMenu);
			}
			
			if(members.getItemCount()==0){
				return null;
			}
			
			return members;
		}catch(Exception e) {}
		return null;
	}
    
	/**
	 * Create JMenu section "forming sinter"
	 * @return Customized JMenu with sub JMenus
	 */
	private JMenu initFormingSinterJMenu() {
		try {
			JMenu formSinter = new JMenu("Формирование аглошихты");
			
			JMenuItem subJMenu = createSubJMenu("Elements", "Таблица элементов");
			if(subJMenu!=null) {
				formSinter.add(subJMenu);
			}
			
			subJMenu = createSubJMenu("AnalyzedElements", "Таблица анализируемых элементов");
			if(subJMenu!=null) {
				formSinter.add(subJMenu);
			}
			
			subJMenu = createSubJMenu("SinterTypes", "Таблица типов агломерата");
			if(subJMenu!=null) {
				formSinter.add(subJMenu);
			}
			
			
			if(formSinter.getItemCount()==0){
				return null;
			}
			
			return formSinter;
		}catch(Exception e) {}
		return null;
	}
	
	/**
	 * Create JMenu section "preparation material"
	 * @return Customized JMenu with sub JMenus
	 */
	private JMenu initPrerationMaterialJMenu() {
		try {
			JMenu materialPrepare = new JMenu("Подготовка сырья");
			
			JMenuItem subJMenu = createSubJMenu("BunkersPurpose", "Таблица назначений бункеров");
			if(subJMenu!=null) {
				materialPrepare.add(subJMenu);
			}
			
			subJMenu = createSubJMenu("Bunkers", "Таблица бункеров");
			if(subJMenu!=null) {
				materialPrepare.add(subJMenu);
			}
			
			subJMenu = createSubJMenu("BunkersFilling", "Таблица наполнения бункеров");
			if(subJMenu!=null) {
				materialPrepare.add(subJMenu);
			}
			
			subJMenu = createSubJMenu("Stacks", "Таблица штабелей");
			if(subJMenu!=null) {
				materialPrepare.add(subJMenu);
			}
			
			if(materialPrepare.getItemCount()==0){
				return null;
			}
			
			return materialPrepare;
		}catch(Exception e) {}
		return null;
	}
	
	private JMenuItem createSubJMenu(String className, String JMenuTitle){
		
		if(!Main.getUser().getPrivilege(className+" view")) {
			return null;
		}
		
		try {
			JMenuItem result = new JMenuItem(JMenuTitle);
			Class<?> clazz = null;
			try {
				System.out.println("Attempt connect the class "+className);
				clazz = Class.forName("plant.frames."+className);
				System.out.println("Class "+className+" was connected");
			} catch(Exception e) {
				System.out.println("Error connect the class "+e.getMessage());
				return null;
			}
			
			Constructor<?> ctor = clazz.getConstructor();
			
			result.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){   
					try {
						addDesktopFrame((IFrame)ctor.newInstance());
					} catch (Exception e1) {
						System.out.println(e1);
					}
			    }
			});
			
			return result;
		} catch(Exception e) {
			System.out.println(e);
		}
		return null;
	}
	
	/**Initialization content of program </br>
	 * Create MDI interface
	 * @return Container Customized container with MDI components
	 * @see Container
	 */
	/*private SwingNode initializationContent(){	      
		desktop = new JDesktopPane();
		
		desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
		
		desktop.setBackground(Color.lightGray);
		
		JScrollPane scrollPane = new JScrollPane();

		JViewport viewport = new JViewport();

		viewport.setView(desktop);

		scrollPane.setViewport(viewport);

		desktop.setPreferredSize(new Dimension(1600,1200));
		
		JPanel panel = new JPanel(new BorderLayout());
		
		panel.add(scrollPane, BorderLayout.CENTER);
		
		//panel.add(getToolBar(), BorderLayout.NORTH);
		
		JPanel statusPanel = new JPanel(new FlowLayout());
		dateLabel = new JLabel(java.time.LocalDate.now().toString());
		statusPanel.add(dateLabel);
		panel.add(statusPanel, BorderLayout.SOUTH);
		
		final SwingNode swingNode = new SwingNode();
		
		/*SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                swingNode.setContent(panel);
            }
        });
		
		swingNode.setContent( desktop );
		
		return swingNode;
	}*/
	
	/**Initialization content of program </br>
	 * Create MDI interface
	 * @return Container Customized container with MDI components
	 * @see Container
	 */
	private JPanel initializationContent() {
		JPanel panel = new JPanel(new BorderLayout());
		desktop = new JDesktopPane();
		
		desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
		
		desktop.setBackground(Color.LIGHT_GRAY);
		
		JScrollPane scrollPane = new JScrollPane();

		JViewport viewport = new JViewport();

		viewport.setView(desktop);

		scrollPane.setViewport(viewport);

		desktop.setPreferredSize(new Dimension(1600,1200));
		
		panel.add(scrollPane, BorderLayout.CENTER);
		
		//panel.add(getToolBar(), BorderLayout.NORTH);
		
		JPanel statusPanel = new JPanel(new FlowLayout());
		dateLabel = new JLabel(java.time.LocalDate.now().toString());
		statusPanel.add(dateLabel);
		panel.add(statusPanel, BorderLayout.SOUTH);
		
		return panel;
	}
	
	/*protected Container getToolBar() {
		JToolBar toolBar = new JToolBar();
		toolBar.setOrientation(JToolBar.HORIZONTAL);
		Boolean sep = false;
		
		JButton button;
		if(Main.getUser().getPrivilege("Workers view")) {
			button = getToolButton("Открыть таблицу работников", "workers", "IFrames", "Workers");
			if(button!=null) {
				toolBar.add(button);
			}
			sep = true;
		}
		
		if(Main.getUser().getPrivilege("Positions view")) {
			button = getToolButton("Открыть таблицу должностей", "positions", "IFrames", "Positions");
			if(button!=null) {
				toolBar.add(button);
			}
			sep = true;
		}
		
		if(sep) {
			toolBar.addSeparator();
			sep = !sep;
		}
		
		if(Main.getUser().getPrivilege("Mines view")) {
			button = getToolButton("Открыть таблицу рудников", "mines", "IFrames", "Mines");
			if(button!=null) {
				toolBar.add(button);
			}
			sep = true;
		}
		
		if(Main.getUser().getPrivilege("Vans view")) {
			button = getToolButton("Открыть таблицу вагонов", "vans", "IFrames", "Vans");
			if(button!=null) {
				toolBar.add(button);
			}
			sep = true;
		}
		
		if(sep) {
			toolBar.addSeparator();
			sep = !sep;
		}
		
		if(Main.getUser().getPrivilege("AnalyzedElements view")) {
			button = getToolButton("Открыть таблица анализируемых элементов", "chemicalElements", "IFrames", "AnalyzedElements");
			if(button!=null) {
				toolBar.add(button);
			}
			sep = true;
		}
		
		if(Main.getUser().getPrivilege("MaterialComposition view")) {
			button = getToolButton("Открыть таблицу составов материала", "composition", "IFrames", "MaterialComposition");
			if(button!=null) {
				toolBar.add(button);
			}
			sep = true;
		}
		
		if(sep) {
			toolBar.addSeparator();
			sep = !sep;
		}
		
		button = getToolButton("Паспорта с хим.составом на штабеля", "stack_raport", "Reports", "ReportStacks");
		if(button!=null) {
			toolBar.add(button);
		}
		
		button = getToolButton("Паспорта с хим.составом на содержимое вагонов", "vagons_raport", "Reports", "ReportVans");
		if(button!=null) {
			toolBar.add(button);
		}
		
		button = getToolButton("Отчёт о работниках", "workers_raport", "Reports", "ReportWorkers");
		if(button!=null) {
			toolBar.add(button);
		}
		
		toolBar.addSeparator();
		
			
		JButton doc = new JButton();
		
		doc.setToolTipText("Открыть документацию");
			
		ImageIcon icon;
		
		try {
			icon = new ImageIcon(Main.class.getResource("/images/doc.png"));
			doc.setIcon(new ImageIcon(icon.getImage().getScaledInstance(30, 30,  java.awt.Image.SCALE_SMOOTH)));
		}catch(Exception e) {
			System.out.println(e);
		}
		doc.addActionListener(getDocActionListener());
		
		toolBar.add(doc);
		
		JButton about = new JButton();
		
		about.setToolTipText("Открыть окно \"Об авторе\"");
		try {
			icon = new ImageIcon(Main.class.getResource("/images/about.png"));
			about.setIcon(new ImageIcon(icon.getImage().getScaledInstance(30, 30,  java.awt.Image.SCALE_SMOOTH)));
		}catch(Exception e) {
			System.out.println(e);
		}
		about.addActionListener(getAboutActionListener());
		
		toolBar.add(about);
		
		return toolBar;
	}
	
	private JButton getToolButton(String tooltip, String imageFileTitle, String packageTitle, String className) {
		JButton result = new JButton();
		ImageIcon icon;
		
		result.setToolTipText(tooltip);
		try {
			icon = new ImageIcon(Main.class.getResource("/images/"+imageFileTitle+".png"));
			result.setIcon(new ImageIcon(icon.getImage().getScaledInstance(30, 30,  java.awt.Image.SCALE_SMOOTH)));
		}catch(Exception e) {
			System.out.println(e);
		}
		
		Class<?> clazz = null;
		try {
			clazz = Class.forName(packageTitle+"."+className);
		} catch(ClassNotFoundException cnf) {
			System.out.println(cnf);
			return null;
		}
		
		Constructor<?> ctor;
		try {
			ctor = clazz.getConstructor();	
		
			if(ctor == null) {
				return null;
			}
			
			result.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){  
					try {
						ctor.newInstance();
					}catch(Exception exp) {
						return;
					}
			    }
			});
			
			return result;
		} catch(Exception e) {}
		return null;
	}*/
	
	public ActionListener getDocActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e){
				Platform.setImplicitExit(false);
				System.out.println("Click doc");
				openComplexWindow();
		    }
		};
	}
	
	private void openComplexWindow() {
		Platform.runLater(new Runnable() { public void run() {
			System.out.println("Show complex frame");
			Main.getComplexController().show(); 
		}});
	}
	
	protected ActionListener getAboutActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e){   
				JOptionPane.showMessageDialog(null, "Заведение: ДонГТИ, СКС\nШифр группы: СКС-19\nАвтор: Кисельник О.Ю.\nПреподаватель: Бизянов Е.Е. проф., к.т.н., д.э.н\nГод производства: 2022\nПроект: Информационно-измерительная и управляющая \nподсистема "
						+ "аналитическим комплексом \nпоточного анализатора химического \nсостава"
						+ " аглошихты", "Об авторе", JOptionPane.INFORMATION_MESSAGE);
		    }
		};
	}
	
	/**
	 * Function for generait constraint for GridBagLayout
	 * @param x number of X axis
	 * @param y number of Y axis
	 * @return GridBagConstraints with configured parameters
	 */
	public GridBagConstraints createGbc(int x, int y) {
        return createGbc(x, y, 1, 1);
    }
	
	/**
	 * Function for generait constraint for GridBagLayout
	 * @param x number of X axis
	 * @param y number of Y axis
	 * @param width value of component width 
	 * @param height value of component height
	 * @return GridBagConstraints with configured parameters
	 */
	public GridBagConstraints createGbc(int x, int y, int width, int height) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridheight = height;
        gbc.gridwidth = width;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(5, 5, 5, 5);
        return gbc;
    }
	
	/**
	 * Get custom ComboBox with workers and workers ID list
	 * @return CustomComboBox with workers list
	 */
	public CustomComboBox getWorkersComboBox() {
		CustomComboBox workers = new CustomComboBox();
		
		for(Object[] row: Main.getDB().getWorkers()) {
			workers.addID(Integer.parseInt((String) row[0]));
			workers.addItem((String)row[1]);
		}
		
		return workers;
	}
	
	/**
	 * Add to desktop new frame and set focus on this frame
	 * @param f Child of IFrame for add to desktop
	 */
	private void addDesktopFrame(IFrame f) {
		f.pack(); 
	    f.show(); 
	    desktop.add(f);
	    
	    try {
			f.setSelected(true);
		} catch (java.beans.PropertyVetoException e) {
		}
	}
}
