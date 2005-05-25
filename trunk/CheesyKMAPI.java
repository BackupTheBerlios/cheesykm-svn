/*
This file is part of the CheesyKM project, a graphical
client for Easy KM, which is a solution for knowledge
sharing and management.
Copyright (C) 2005  Samuel Hervé

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Library General Public
License as published by the Free Software Foundation; either
version 2 of the License, or any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Library General Public License for more details.

You should have received a copy of the GNU Library General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*/
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.lang.reflect.*;
/**
*Application main Frame.<br>
*Contains the two tabbed panes, the two toolbars, and a menuBar
*/
class CheesyKMAPI extends JFrame{
	/**{@link Login} prompt dialog*/
	Login login;
	/**Topic tree view ({@link Thematique})*/
	static volatile Thematique thematique;
	/**News list ({@link Nouveaute})*/
	static Nouveaute nouveaute;
	/**Search Toolbar ({@link QuickSearchToolBar})*/
	static QuickSearchToolBar qstb;
	
	static int tabIndex=-2;
	//menu Connexion
	JMenuItem menuDeconnecter;
	JButton jtbStop;
	JButton jtbQuitter;
	
	
	
	//menu Document/Fichier attaché
	JMenu menuDocument;
	JMenuItem menuVoirDocument;
	JButton jtbVoirDocument;
	JMenuItem menuTelechargerDocument;
	JButton jtbTelechargerDocument;
	JMenuItem menuDeposerDocument;
	JMenuItem menuImporterDossier;
	JMenuItem menuMettreAJourDocument;
	JButton jtbMettreAJourDocument;
	JMenuItem menuModifierDocument;
	JButton jtbModifierDocument;
	JMenuItem menuSupprimerDocument;
	JButton jtbSupprimerDocument;
	
	//menu Site Web
	JMenu menuWeb;
	JMenuItem menuVoirSiteWeb;
	JButton jtbVoirSiteWeb;
	JMenuItem menuCopierAdresseWeb;
	JButton jtbCopierAddresseWeb;
	
	//menu Onglets/View (possibiliter de mettre la liste des Onglets ouverts dans ce menu)
	JMenu menuOnglet;
	JMenuItem menuFermerOnglet;
	JButton jtbFermerOnglet;
	JMenuItem menuFermerTousLesOnglets;
	JButton jtbFermerTousLesOnglets;
	JPanel grandPanel;
	JPanel petitPanel;
	/**Left Tabbedpane*/
	JTabbedPane jtpG;
	/**Right tabbedpane*/
	JTabbedPane jtpD;
	private boolean nePasFermer=false;
	/**
	*Creates a new main frame, with empty tabbedpanes, and shows a modal {@link Login} prompt.
	*/
	public CheesyKMAPI() {
		Container cp=(Container)this.getContentPane();
		cp.setLayout(new BorderLayout());
		setBounds(CheesyKM.INITX,CheesyKM.INITY,CheesyKM.INITWIDTH,CheesyKM.INITHEIGHT);
		
		this.setIconImage(CheesyKM.loadIcon("./ressources/EasyKMTreeIcon.png").getImage());
		
		JMenuBar menuBar=new JMenuBar();
		JMenu menuConnexion=new JMenu(CheesyKM.getLabel("connection"));
		menuConnexion.setMnemonic(KeyEvent.VK_C);
		menuDeconnecter=new JMenuItem(CheesyKM.getLabel("disconnect"),KeyEvent.VK_D);
		menuDeconnecter.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D,ActionEvent.CTRL_MASK));
		menuDeconnecter.setEnabled(false);
		JMenuItem menuExit=new JMenuItem(CheesyKM.getLabel("quit"),KeyEvent.VK_Q);
		menuExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,ActionEvent.CTRL_MASK));
		
		
		class frameListener extends WindowAdapter{
			public void windowClosing(WindowEvent e){
				if(JOptionPane.showConfirmDialog(CheesyKMAPI.this,CheesyKM.getLabel("doYouReallyWannaLeaveMe"), CheesyKM.getLabel("confirmQuit"), JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
					CheesyKM.INITHEIGHT=CheesyKM.api.getHeight();
					CheesyKM.INITWIDTH=CheesyKM.api.getWidth();
					CheesyKM.INITX=CheesyKM.api.getX();
					CheesyKM.INITY=CheesyKM.api.getY();
					CheesyKM.BUTTONTOOLBARLOCATION=CheesyKM.api.buttonToolBarLocation();
					CheesyKM.SEARCHTOOLBARLOCATION=CheesyKM.api.searchToolBarLocation();
					CheesyKM.config.saveConfig();
					System.exit(0);
				} else {
					nePasFermer=true;
				}
			}
		}
		this.addWindowListener(new frameListener());
		
		class menuExitListener implements ActionListener{
			public void actionPerformed(ActionEvent ae){
				if(JOptionPane.showConfirmDialog(CheesyKMAPI.this,CheesyKM.getLabel("doYouReallyWannaLeaveMe"), CheesyKM.getLabel("confirmQuit"), JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
					CheesyKM.INITHEIGHT=CheesyKM.api.getHeight();
					CheesyKM.INITWIDTH=CheesyKM.api.getWidth();
					CheesyKM.INITX=CheesyKM.api.getX();
					CheesyKM.INITY=CheesyKM.api.getY();
					CheesyKM.BUTTONTOOLBARLOCATION=CheesyKM.api.buttonToolBarLocation();
					CheesyKM.SEARCHTOOLBARLOCATION=CheesyKM.api.searchToolBarLocation();
					CheesyKM.config.saveConfig();
					System.exit(0);
				}
			}
		}
		menuExit.addActionListener(new menuExitListener());
		
		
		class menuDeconnecterListener implements ActionListener{
			public void actionPerformed(ActionEvent ae){
				int confirm=JOptionPane.showConfirmDialog(CheesyKMAPI.this,CheesyKM.getLabel("doYouReallyWannaPlugOut"), CheesyKM.getLabel("confirmDisconnect"), JOptionPane.YES_NO_OPTION);
				if(confirm==JOptionPane.YES_OPTION){
					deconnecter();
				}
			}
		}
		menuDeconnecter.addActionListener(new menuDeconnecterListener());
		
		menuConnexion.add(menuDeconnecter);
		menuConnexion.addSeparator();
		menuConnexion.add(menuExit);
		menuBar.add(menuConnexion);
		
		
		menuDocument=new JMenu(CheesyKM.getLabel("document"));
		menuDocument.setMnemonic(KeyEvent.VK_D);
		menuDocument.setEnabled(false);
		
		menuVoirDocument=new JMenuItem(CheesyKM.getLabel("seeDocument"),KeyEvent.VK_I);
		menuVoirDocument.setEnabled(false);
		menuVoirDocument.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,ActionEvent.CTRL_MASK));
		menuTelechargerDocument=new JMenuItem(CheesyKM.getLabel("downloadDocument"),KeyEvent.VK_L);
		menuTelechargerDocument.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,ActionEvent.CTRL_MASK));
		menuTelechargerDocument.setEnabled(false);
		menuDeposerDocument=new JMenuItem(CheesyKM.getLabel("registerNewDoc"),KeyEvent.VK_N);
		menuDeposerDocument.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,ActionEvent.CTRL_MASK));
		menuImporterDossier=new JMenuItem(CheesyKM.getLabel("importFolders"),KeyEvent.VK_S);
		menuMettreAJourDocument=new JMenuItem(CheesyKM.getLabel("updateThisDoc"),KeyEvent.VK_V);
		menuMettreAJourDocument.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,ActionEvent.CTRL_MASK));
		menuMettreAJourDocument.setEnabled(false);
		menuModifierDocument=new JMenuItem(CheesyKM.getLabel("editDocument"),KeyEvent.VK_E);
		menuModifierDocument.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,ActionEvent.CTRL_MASK));
		menuModifierDocument.setEnabled(false);
		menuSupprimerDocument=new JMenuItem(CheesyKM.getLabel("deleteDocument"),KeyEvent.VK_T);
		menuSupprimerDocument.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,ActionEvent.CTRL_MASK));
		menuSupprimerDocument.setEnabled(false);
		
		class MenuVoirDocumentListener implements ActionListener{
			public void actionPerformed(ActionEvent ae){
				CheesyKM.download((Doc)getDisplayedTopic(),false);
			}
		}
		menuVoirDocument.addActionListener(new MenuVoirDocumentListener());
		
		class MenuTelechargerDocumentListener implements ActionListener{
			public void actionPerformed(ActionEvent ae){
				CheesyKM.download((Doc)getDisplayedTopic(),true);
			}
		}
		menuTelechargerDocument.addActionListener(new MenuTelechargerDocumentListener());
		
		class MenuDeposerDocumentListener implements ActionListener{
			public void actionPerformed(ActionEvent ae){
				new RegisterDocWizard(null);
			}
		}
		menuDeposerDocument.addActionListener(new MenuDeposerDocumentListener());
		
		class MenuImporterDossierListener implements ActionListener{
			public void actionPerformed(ActionEvent ae){
				new ImportFiles();
			}
		}
		menuImporterDossier.addActionListener(new MenuImporterDossierListener());
		
		class MenuMettreAJourDocumentListener implements ActionListener{
			public void actionPerformed(ActionEvent ae){
				new RegisterDocWizard((Doc)getDisplayedTopic());
			}
		}
		menuMettreAJourDocument.addActionListener(new MenuMettreAJourDocumentListener());
		
		class MenuModifierDocumentListener implements ActionListener{
			public void actionPerformed(ActionEvent ae){
				new RegisterDocWizard((Doc)getDisplayedTopic(),true);
			}
		}
		menuModifierDocument.addActionListener(new MenuModifierDocumentListener());
		
		class MenuSupprimerDocumentListener implements ActionListener{
			public void actionPerformed(ActionEvent ae){
				CheesyKM.deleteDoc((Doc)getDisplayedTopic());
			}
		}
		menuSupprimerDocument.addActionListener(new MenuSupprimerDocumentListener());
		
		menuDocument.add(menuVoirDocument);
		menuDocument.add(menuTelechargerDocument);
		menuDocument.addSeparator();
		menuDocument.add(menuMettreAJourDocument);
		menuDocument.add(menuModifierDocument);
		menuDocument.add(menuSupprimerDocument);
		menuDocument.addSeparator();
		//menuDocument.add(menuDeposerDocument);
		//menuDocument.add(menuImporterDossier);
		menuBar.add(menuDocument);
		
		menuWeb=new JMenu(CheesyKM.getLabel("webSite"));
		menuWeb.setMnemonic(KeyEvent.VK_I);
		menuWeb.setEnabled(false);
		menuVoirSiteWeb=new JMenuItem(CheesyKM.getLabel("visitSite"),KeyEvent.VK_S);
		menuVoirSiteWeb.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,ActionEvent.CTRL_MASK));
		menuVoirSiteWeb.setEnabled(false);
		menuCopierAdresseWeb=new JMenuItem(CheesyKM.getLabel("copySiteURL"),KeyEvent.VK_U);
		menuCopierAdresseWeb.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U,ActionEvent.CTRL_MASK));
		menuCopierAdresseWeb.setEnabled(false);
		class MenuVoirSiteWebListener implements ActionListener{
			public void actionPerformed(ActionEvent ae){
				CheesyKM.openURL((Doc)getDisplayedTopic());
			}
		}
		menuVoirSiteWeb.addActionListener(new MenuVoirSiteWebListener());
		
		class MenuCopierAdresseWebListener implements ActionListener{
			public void actionPerformed(ActionEvent ae){
				CheesyKM.copyToClipBoard(((Doc)(getDisplayedTopic())).url);
			}
		}
		menuCopierAdresseWeb.addActionListener(new MenuCopierAdresseWebListener());
		menuWeb.add(menuVoirSiteWeb);
		menuWeb.add(menuCopierAdresseWeb);
		menuBar.add(menuWeb);
		
		
		menuOnglet=new JMenu(CheesyKM.getLabel("tabs"));
		menuOnglet.setMnemonic(KeyEvent.VK_T);
		menuOnglet.setEnabled(false);
		
		
		menuFermerOnglet=new JMenuItem(CheesyKM.getLabel("closeTab"),KeyEvent.VK_O);
		menuFermerOnglet.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K,ActionEvent.ALT_MASK));
		menuFermerOnglet.setEnabled(false);
		menuFermerTousLesOnglets=new JMenuItem(CheesyKM.getLabel("closeAllTabs"),KeyEvent.VK_L);
		menuFermerTousLesOnglets.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,ActionEvent.ALT_MASK));
		menuFermerTousLesOnglets.setEnabled(false);
		class MenuFermerOngletListener implements ActionListener{
			public void actionPerformed(ActionEvent ae){
				CheesyKMAPI.this.hideTopic(CheesyKMAPI.this.getDisplayedTopic());
				((JTPDChangeListener)(CheesyKMAPI.this.jtpD.getChangeListeners()[0])).stateChanged(null);
			}
		}
		menuFermerOnglet.addActionListener(new MenuFermerOngletListener());
		
		class MenuFermerTousLesOngletsListener implements ActionListener{
			public void actionPerformed(ActionEvent ae){
				CheesyKMAPI.this.hideAllTopics();
				((JTPDChangeListener)(CheesyKMAPI.this.jtpD.getChangeListeners()[0])).stateChanged(null);
			}
		}
		menuFermerTousLesOnglets.addActionListener(new MenuFermerTousLesOngletsListener());
		menuOnglet.add(menuFermerOnglet);
		menuOnglet.add(menuFermerTousLesOnglets);
		menuOnglet.addSeparator();
		menuBar.add(menuOnglet);
		
		JMenu menuConfig=new JMenu(CheesyKM.getLabel("configuration"));
		menuConfig.setMnemonic(KeyEvent.VK_O);
		JMenuItem menuConfigItem=new JMenuItem(CheesyKM.getLabel("viewConfig"));
		class MenuConfigListener implements ActionListener {
			public void actionPerformed(ActionEvent e){
				CheesyKM.config.displayConfig();
			}
		}
		menuConfigItem.addActionListener(new MenuConfigListener());
		menuConfigItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,ActionEvent.ALT_MASK));
		menuConfig.add(menuConfigItem);
		menuBar.add(menuConfig);
		
		
		setJMenuBar(menuBar);
		
		
		JToolBar jtb=new JToolBar();
		
		jtbStop=new JButton(CheesyKM.loadIcon("./ressources/UnPlug.gif"));
		jtbStop.addActionListener(new menuDeconnecterListener());
		jtbStop.setEnabled(false);
		jtbStop.setToolTipText(CheesyKM.getLabel("disconnect")+" (Ctrl+D)");
		jtb.add(jtbStop);

		jtbQuitter=new JButton(CheesyKM.loadIcon("./ressources/Delete.gif"));
		jtbQuitter.addActionListener(new menuExitListener());
		jtbQuitter.setToolTipText(CheesyKM.getLabel("quit")+" (Ctrl+Q)");
		jtb.add(jtbQuitter);
		
		jtb.addSeparator();
		
		jtbVoirDocument=new JButton(CheesyKM.loadIcon("./ressources/Sheet.gif"));
		jtbVoirDocument.addActionListener(new MenuVoirDocumentListener());
		jtbVoirDocument.setEnabled(false);
		jtbVoirDocument.setToolTipText(CheesyKM.getLabel("seeDocument")+" (Ctrl+O)");
		jtb.add(jtbVoirDocument);
		
		jtbTelechargerDocument=new JButton(CheesyKM.loadIcon("./ressources/DocToFolder.gif"));
		jtbTelechargerDocument.addActionListener(new MenuTelechargerDocumentListener());
		jtbTelechargerDocument.setEnabled(false);
		jtbTelechargerDocument.setToolTipText(CheesyKM.getLabel("downloadDocument")+" (Ctrl+S)");
		jtb.add(jtbTelechargerDocument);
		
		jtbMettreAJourDocument=new JButton(CheesyKM.loadIcon("./ressources/PlusPlus.gif"));
		jtbMettreAJourDocument.addActionListener(new MenuMettreAJourDocumentListener());
		jtbMettreAJourDocument.setEnabled(false);
		jtbMettreAJourDocument.setToolTipText(CheesyKM.getLabel("updateThisDoc")+" (Ctrl+R)");
		jtb.add(jtbMettreAJourDocument);
		
		jtbModifierDocument=new JButton(CheesyKM.loadIcon("./ressources/EditComp.gif"));
		jtbModifierDocument.addActionListener(new MenuModifierDocumentListener());
		jtbModifierDocument.setEnabled(false);
		jtbModifierDocument.setToolTipText(CheesyKM.getLabel("editDocument")+" (Ctrl+E)");
		jtb.add(jtbModifierDocument);
		
		jtbSupprimerDocument=new JButton(CheesyKM.loadIcon("./ressources/DeleteSheet.gif"));
		jtbSupprimerDocument.addActionListener(new MenuSupprimerDocumentListener());
		jtbSupprimerDocument.setEnabled(false);
		jtbSupprimerDocument.setToolTipText(CheesyKM.getLabel("deleteDocument")+" (Ctrl+Delete)");
		jtb.add(jtbSupprimerDocument);
		
		jtb.addSeparator();
		
		jtbVoirSiteWeb=new JButton(CheesyKM.loadIcon("./ressources/web32.png"));
		jtbVoirSiteWeb.addActionListener(new MenuVoirSiteWebListener());
		jtbVoirSiteWeb.setEnabled(false);
		jtbVoirSiteWeb.setToolTipText(CheesyKM.getLabel("visitSite")+" (Ctrl+W)");
		jtb.add(jtbVoirSiteWeb);
		
		jtbCopierAddresseWeb=new JButton(CheesyKM.loadIcon("./ressources/Copy0.gif"));
		jtbCopierAddresseWeb.addActionListener(new MenuCopierAdresseWebListener());
		jtbCopierAddresseWeb.setEnabled(false);
		jtbCopierAddresseWeb.setToolTipText(CheesyKM.getLabel("copySiteURL")+" (Ctrl+U)");
		jtb.add(jtbCopierAddresseWeb);
		
		jtb.addSeparator();
		
		jtbFermerOnglet=new JButton(CheesyKM.loadIcon("./ressources/NewProj.gif"));
		jtbFermerOnglet.addActionListener(new MenuFermerOngletListener());
		jtbFermerOnglet.setEnabled(false);
		jtbFermerOnglet.setToolTipText(CheesyKM.getLabel("closeTab")+" (Alt+K)");
		jtb.add(jtbFermerOnglet);
		
		jtbFermerTousLesOnglets=new JButton(CheesyKM.loadIcon("./ressources/Folder2bliz.gif"));
		jtbFermerTousLesOnglets.addActionListener(new MenuFermerTousLesOngletsListener());
		jtbFermerTousLesOnglets.setEnabled(false);
		jtbFermerTousLesOnglets.setToolTipText(CheesyKM.getLabel("closeAllTabs")+" (Alt+L)");
		jtb.add(jtbFermerTousLesOnglets);
		
		jtb.addSeparator();
		
		JButton jtbConfig=new JButton(CheesyKM.loadIcon("./ressources/Hammer.gif"));
		jtbConfig.addActionListener(new MenuConfigListener());
		jtbConfig.setToolTipText(CheesyKM.getLabel("viewConfig")+" (Alt+P)");
		jtb.add(jtbConfig);
		jtb.setBorderPainted(false);
		jtb.setFloatable(true);
		if(CheesyKM.BUTTONTOOLBARLOCATION.equals("West")||CheesyKM.BUTTONTOOLBARLOCATION.equals("East")){
			jtb.setOrientation(JToolBar.VERTICAL);
		}
		
		
		
		grandPanel=new JPanel(new BorderLayout());
		grandPanel.add(jtb,CheesyKM.BUTTONTOOLBARLOCATION);
		
		petitPanel=new JPanel(new BorderLayout());
		qstb=new QuickSearchToolBar();
		petitPanel.add(qstb,CheesyKM.SEARCHTOOLBARLOCATION);
		
		grandPanel.add(petitPanel,"Center");
		
		cp.add(grandPanel,"Center");
		
		jtpG=new JTabbedPane();
		
		class JTPDMouseListener extends MouseAdapter{
			public void mouseClicked(MouseEvent e){
				if(e.getButton()==MouseEvent.BUTTON3){
					TopicPane tp=(TopicPane)((JTabbedPane)(e.getComponent())).getSelectedComponent();
					if(tp!=null)new TopicPopupMenu(e.getComponent(),e.getX(),e.getY(),tp.topic,false);
				}
			}
		}
		
		jtpD=new JTabbedPane();
		jtpD.addMouseListener(new JTPDMouseListener());
		jtpD.addChangeListener(new JTPDChangeListener());
		int split=JSplitPane.HORIZONTAL_SPLIT;
		if(CheesyKM.VERTICAL)split=JSplitPane.VERTICAL_SPLIT;
		JSplitPane jsp=new JSplitPane(split,jtpG,jtpD);
		jsp.setDividerSize(5);
		jsp.setContinuousLayout(true);
		
		petitPanel.add(jsp,"Center");
		
		setTitle(CheesyKM.getLabel("titleDisconnected"));
		setVisible(true);
		
		jsp.setDividerLocation((double)0.25);
		
		login=new Login(this);
	}
	/**
	*Returns the location of the main Toolbar in the main Frame.<br>
	*Returns "North" if the toolbar is floating (detached from the frame)
	*@return String ("North","South","East","West").
	*/
	public String buttonToolBarLocation(){
		BorderLayout layout = (BorderLayout)this.grandPanel.getLayout();// get layout somewhere;
		Component west=null;
		Component east=null;
		Component north=null;
		Component south=null;
		try {           
			Method method = BorderLayout.class.getDeclaredMethod("getChild", new Class[]{String.class, Boolean.TYPE});
			method.setAccessible(true);
			
			west = (Component)method.invoke(layout, new Object[]{BorderLayout.WEST, Boolean.TRUE});
			east = (Component)method.invoke(layout, new Object[]{BorderLayout.EAST, Boolean.TRUE});
			north = (Component)method.invoke(layout, new Object[]{BorderLayout.NORTH, Boolean.TRUE});
			south = (Component)method.invoke(layout, new Object[]{BorderLayout.SOUTH, Boolean.TRUE});
		} catch (NoSuchMethodException ex) {
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		} catch (java.lang.reflect.InvocationTargetException ex) {
			ex.printStackTrace();
		}
		if(east!=null){
			return "East";
		}else if(south!=null){
			return "South";
		} else if(west!=null){
			return "West";
		} else {
			return "North";
		}
	}
	/**
	*Returns the location of the search Toolbar in the main Frame.<br>
	*Returns "North" if the toolbar is floating (detached from the frame)
	*@return String ("North","South").
	*/
	public String searchToolBarLocation(){
		BorderLayout layout = (BorderLayout)this.petitPanel.getLayout();// get layout somewhere;
		Component north=null;
		Component south=null;
		try {           
			Method method = BorderLayout.class.getDeclaredMethod("getChild", new Class[]{String.class, Boolean.TYPE});
			method.setAccessible(true);
			north = (Component)method.invoke(layout, new Object[]{BorderLayout.NORTH, Boolean.TRUE});
			south = (Component)method.invoke(layout, new Object[]{BorderLayout.SOUTH, Boolean.TRUE});
		} catch (NoSuchMethodException ex) {
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		} catch (java.lang.reflect.InvocationTargetException ex) {
			ex.printStackTrace();
		}if(south!=null){
			return "South";
		} else {
			return "North";
		}
	}
	
	/**
	*Updates the GUI at disconnect, and shows the login prompt.
	*/
	void deconnecter(){
		deconnecter(false);
	}
	/**
	*Updates the GUI at disconnect, and shows the login prompt.
	*/
	void deconnecter(boolean relog){
		if(!relog)
		setTitle(CheesyKM.getLabel("titleDisconnected"));
		qstb.setEnabled(false);
		menuDeconnecter.setEnabled(false);
		menuDocument.setEnabled(false);
		jtbStop.setEnabled(false);
		jtpG.removeAll();
		hideAllTopics();
		String user=null;
		String pass=null;
		if(relog){
			user=CheesyKM.login;
			pass=CheesyKM.pass;
		}
		CheesyKM.watchFolder.kill();
		CheesyKM.deconnecter();
		login=new Login(this,relog,user,pass);
	}
	
	/**
	*Overrides JFrames <code>setVisible</code>, used by the "DoYouReallyWantToQuit" thing.
	*/
	public void setVisible(boolean b){
		if(!nePasFermer){
			super.setVisible(b);
		}
		nePasFermer=false;
	}
	
	/**
	*Fill the GUI at successfull logon.<br>
	*Calls constructors of {@link Thematique} and {@link Nouveaute} and puts them in the Left tabbed pane.
	*/
	public void initAtLogon(){
		jtbStop.setEnabled(true);
		qstb.setEnabled(true);
		menuDocument.setEnabled(true);
		setTitle(CheesyKM.getLabel("titleConnected")+" "+CheesyKM.easyKMConfig.get("title")+" : "+CheesyKM.login);
		
		nouveaute=new Nouveaute();
		JScrollPane scrollNouveaute=new JScrollPane(nouveaute);
		JPanel leftPanel=new JPanel(new BorderLayout());
		
		JButton reloadNews=new JButton(CheesyKM.getLabel("reloadNews"));
		if(CheesyKM.SHOWTOOLTIPS)reloadNews.setToolTipText(CheesyKM.getLabel("toolTipReloadNews"));
		class reloadNewsButtonListener implements ActionListener{
			public void actionPerformed(ActionEvent e){
				((JPanel)CheesyKM.api.jtpG.getComponent(0)).remove(1);
				((JPanel)CheesyKM.api.jtpG.getComponent(0)).add(new JScrollPane(new Nouveaute()),"Center");
				CheesyKM.api.jtpG.repaint();
			}
		}
		reloadNews.addActionListener(new reloadNewsButtonListener());
		leftPanel.add(reloadNews,"South");
		leftPanel.add(scrollNouveaute,"Center");
		this.jtpG.addTab(CheesyKM.getLabel("news"),null,leftPanel,CheesyKM.NOMBREDENOUVEAUTES+CheesyKM.getLabel("lastNews"));
		
		thematique=new Thematique();
		JScrollPane scrollThematique = new JScrollPane(thematique);
		this.jtpG.addTab(CheesyKM.getLabel("topics"),null,scrollThematique,CheesyKM.getLabel("topicTreeView"));

		if(CheesyKM.maximumRightLevel<Topic.RIGHT_RW){
			menuDocument.remove(menuDeposerDocument);
			menuDocument.remove(menuImporterDossier);
		} else {
			menuDocument.add(menuDeposerDocument);
			menuDocument.add(menuImporterDossier);
		}
		
		CheesyKM.watchFolder=new WatchFolder();
		CheesyKM.watchFolder.start();
		
	}
	/**
	*Return the index in the right tabbed pane for a {@link Topic}.
	*@param tid topicID as an <code>int</code>.
	*@return index of the tab, or -1 if the {@link Topic} is not in the TabbedPane.
	*/
	public int getIndexForDisplayedTopic(int tid){
		boolean trouve=false;
		int i=0;
		while(!trouve&&i<jtpD.getTabCount()){
			trouve=(((TopicPane)jtpD.getComponent(i)).topic.id==tid);
			i++;
		}
		if(trouve){
			return i-1;
		} else {
			return -1;
		}
	}
	
	/**
	*Displays a {@link Topic} in a tab in the right TabbedPane.<br>
	*If the topic is already displayed, no new tabs will be created and this Topics tab will become selected.
	*If a new Tab is created, its title length will be shortened if necessary (see {@link CheesyKM#MAXDOCTABSTITLESIZE CheesyKM.MAXDOCTABSTITLESIZE}),
	*and a new Menu Item in the "Tab" menu will be added.
	*@param topic {@link Topic} to display.
	*/
	public void displayTopic(Topic topic){
		int i=isAffiche(topic);
		if(i==-1){
			if(!CheesyKM.MULTIPLETABSDOCS) hideAllTopics();
			String title=topic.toString();
			if(title.length()>CheesyKM.MAXDOCTABSTITLESIZE) title=title.substring(0,CheesyKM.MAXDOCTABSTITLESIZE-3)+"...";
			jtpD.addTab(title,null,new TopicPane(topic),topic.toString());
			jtpD.setSelectedIndex(jtpD.getTabCount()-1);
			JMenuItem item=new JMenuItem(topic.toString());
			item.setName(topic.getNodeType()+new Integer(topic.id).toString());
			class OngletMenuItemListener implements ActionListener {
				public void actionPerformed(ActionEvent e){
					jtpD.setSelectedIndex(CheesyKM.api.getIndexForDisplayedTopic(Integer.parseInt(((JMenuItem)e.getSource()).getName().substring(1))));
				}
			}
			item.addActionListener(new OngletMenuItemListener());
			this.menuOnglet.add(item);
		} else {
			jtpD.setSelectedIndex(i);
		}
	}
	/**
	*Removes a Topics tab from the right TabbedPane.<br>
	*If a tab is removed from the TabbedPane, the matching menu item will be removed from the "Tabs" menu.
	*@param index index in the right TabbedPane of the tab to remove.
	*@see CheesyKMAPI#getIndexForDisplayedTopic(int)
	*@see CheesyKMAPI#isAffiche(Topic)
	*/
	public void hideTopic(int index){
		String cle=""+((TopicPane)jtpD.getComponent(index)).topic.getNodeType()+((TopicPane)jtpD.getComponent(index)).topic.id;
		boolean trouve=false;
		int i=3;
		while(!trouve){
			if((this.menuOnglet.getItem(i)).getName().equals(cle)){
				trouve=true;
				this.menuOnglet.remove(i);
			}
			i++;
		}
		jtpD.removeTabAt(index);
		System.gc();
	}
	
	
	/**
	*Return the index in the right tabbed pane for a {@link Topic}.
	*@param topic the topic to get the index from.
	*@return index of the tab, or -1 if the {@link Topic} is not in the TabbedPane.
	*/
	public int isAffiche(Topic topic){
		if(topic.id==-1) return -1;
		int i=0;
		boolean trouve=false;
		while(i<jtpD.getTabCount()&&!trouve){
			if(((TopicPane)(jtpD.getComponent(i))).topic.equals(topic)){
				trouve=true;
			}
			i++;
		}
		if(!trouve){
			return -1;
		} else {
			return i-1;
		}
	}
	
	/**
	*Removes a Topics tab from the right TabbedPane.<br>
	*If a tab is removed from the TabbedPane, the matching menu item will be removed from the "Tabs" menu.
	*@param topic {@link Topic} to remove.
	*/
	public void hideTopic(Topic topic){
		int i=isAffiche(topic);
		if(i!=-1) hideTopic(i);
	}
	
	/**
	*Removes all tabs from the right TabbedPane, and all matching "Tabs" menu items.
	*/
	public void hideAllTopics(){
		for(int i=3;i<3+jtpD.getTabCount();i++){
			this.menuOnglet.remove(3);
		}
		jtpD.removeAll();
	}
	/**
	*Return the {@link Topic} for the currently selected Tab in the right TabbedPane.
	*@return selected {@link Topic}, or <code>null</code> if no Topic is selected.
	*/
	public Topic getDisplayedTopic(){
		if(jtpD.getSelectedComponent()!=null){
			return ((TopicPane)(jtpD.getSelectedComponent())).topic;
		} else {
			return null;
		}
	}
	
	/**
	*Check if a file transfer Tab is showing or not.<br>
	*Used to enable or disable the "Close all tabs" function.
	*@return <code>true</code> if one ore more file transfer tabs are displayed, <code>false</code> else.
	*/
	public boolean runningFileTransferIsDisplayed(){
		for(int i=0;i<jtpD.getTabCount();i++){
			if(((TopicPane)jtpD.getComponent(i)).topic.getNodeType()=='F'){
				if(((FileTransferTopic)((TopicPane)jtpD.getComponent(i)).topic).pi.isRunning){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	*Used to tell the UI that something has been modified within a topic, and hides it if it was displayed.
	*@param topics Vector of modified Topics TIDs as Integers.
	*@param docID docID of a modified Doc, as an int.
	*/
	public void modifiedTopics(Vector topics,int docID){
		((JPanel)CheesyKM.api.jtpG.getComponent(0)).remove(1);
		((JPanel)CheesyKM.api.jtpG.getComponent(0)).add(new JScrollPane(new Nouveaute()),"Center");
		((JPanel)CheesyKM.api.jtpG.getComponent(0)).getComponent(1).repaint();
		for(int i=0;i<topics.size();i++){
			((Topic)((DefaultMutableTreeNode)thematique.topics.get(topics.get(i))).getUserObject()).decharger();
		}
		int i=this.getIndexForDisplayedTopic(docID);
		if(i!=-1){
			this.hideTopic(i);
		}
		if(CheesyKM.api.jtpG.getTabCount()==3) CheesyKM.api.jtpG.removeTabAt(2);    
	}
}
