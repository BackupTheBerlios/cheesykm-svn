import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.text.html.*;
import java.io.*;
import java.net.*;
/**
*Displays a {@link Topic} in a <code>JPanel</code><br>
*Can handle Topics type 'D' (Document), 'W' (Webpage), 'F' (Filetransfer)
*/
class TopicPane extends JPanel{
	Topic topic;
	/**
	*New panel to display a {@link Topic}.
	*@param topic {@link Topic} to display.
	*/
	TopicPane(Topic topic){
		super();
		this.topic=topic;
		
		setLayout(new BorderLayout());
		
		if(topic.getNodeType()=='D'){
			Doc doc=(Doc)topic;
			JTextPane textPane = new JTextPane();
			textPane.setEditable(false);
			
			class TPMouseListener extends MouseAdapter{
				public void mouseClicked(MouseEvent e){
					if(e.getButton()==MouseEvent.BUTTON3){
						new TopicPopupMenu(e.getComponent(),e.getX(),e.getY(),TopicPane.this.topic,false);
					}
				}
			}
			textPane.addMouseListener(new TPMouseListener());
			
			
			StyledDocument document = textPane.getStyledDocument();
			addStylesToDocument(document);
			try{
				document.insertString(document.getLength(),doc.toString(),document.getStyle("titre"));
				document.insertString(document.getLength()," "+CheesyKM.getLabel("ed")+" "+doc.version,document.getStyle("ed"));
				newLine(document);
				double scoreSurCinq=(((double)doc.score)/100)*5.0;
				Double scoreSurCinqD=new Double(scoreSurCinq);
				int scoreSurCinqI=scoreSurCinqD.intValue();
				for(int i=0;i<scoreSurCinqI;i++){
					document.insertString(document.getLength()," ",document.getStyle("iconF"+(i+1)));
					document.insertString(document.getLength(),"",document.getStyle("valeur"));
				}
				for(int i=scoreSurCinqI;i<5;i++){
					document.insertString(document.getLength()," ",document.getStyle("iconC"+(i+1)));
					document.insertString(document.getLength(),"",document.getStyle("valeur"));
				}
				
				newLine(document);
				if(!doc.author.equals("")){
					document.insertString(document.getLength(),CheesyKM.getLabel("by")+doc.author+" ",document.getStyle("auteur"));
				}
				if(!doc.creadate.equals("")){
					document.insertString(document.getLength(),CheesyKM.getLabel("at")+" "+doc.creadate.substring(0,10),document.getStyle("auteur"));
				}
				newLine(document);
				if(!doc.description.equals("")){
					newLine(document);
					document.insertString(document.getLength(),doc.description,document.getStyle("desc"));
					newLine(document);
					newLine(document);
				}
				if(!doc.kwords.equals("")){
					document.insertString(document.getLength(),CheesyKM.getLabel("keywords"),document.getStyle("propriete"));
					document.insertString(document.getLength(),doc.kwords,document.getStyle("valeurVerte"));
					newLine(document);
				}
				
				
				if(doc.topicList.size()!=0){
					document.insertString(document.getLength(),CheesyKM.getLabel("topics")+" : ",document.getStyle("propriete"));
					for(int i=0;i<doc.topicList.size();i++){
						document.insertString(document.getLength(),CheesyKM.getTopicFullName(doc.topicList.get(i).toString())+" ",document.getStyle("valeurOrange"));
						
						document.insertString(document.getLength(),"rien",document.getStyle("themeButton"+i));
						if(i!=doc.topicList.size()-1){
							document.insertString(document.getLength(),"  - ",document.getStyle("propriete"));
						}
					}
					newLine(document);
				}
				
				
				
				
				if(!doc.file.equals("")){
					document.insertString(document.getLength(),CheesyKM.getLabel("file")+" : ",document.getStyle("propriete"));
					document.insertString(document.getLength(),doc.format+" - "+doc.getFSize()+" ("+CheesyKM.getLabel("downloaded")+" "+doc.downloads+" "+CheesyKM.getLabel("times")+")  ",document.getStyle("valeur"));
					document.insertString(document.getLength(),"rien",document.getStyle("viewFileButton"));
					document.insertString(document.getLength(),"  ",document.getStyle("valeur"));
					document.insertString(document.getLength(),"rien",document.getStyle("dlButton"));
					newLine(document);
				}
				
				if(!doc.url.equals("")){
					document.insertString(document.getLength(),CheesyKM.getLabel("webSite")+" : ",document.getStyle("propriete"));
					document.insertString(document.getLength(),doc.url+" ("+CheesyKM.getLabel("browsed")+" "+doc.visits+" "+CheesyKM.getLabel("times")+")  ",document.getStyle("valeur"));
					document.insertString(document.getLength(),"rien",document.getStyle("wwwButton"));
					document.insertString(document.getLength(),"  ",document.getStyle("valeur"));
					document.insertString(document.getLength(),"rien",document.getStyle("wwwToClipBoardButton"));
					newLine(document);
				}
				
				Enumeration keys=doc.uf.keys();
				Vector nomsAttribs=new Vector();
				while(keys.hasMoreElements())nomsAttribs.add(keys.nextElement());
				for(int i=0;i<nomsAttribs.size();i++){
					if(!((String)doc.uf.get(nomsAttribs.get(i))).equals("")){
						document.insertString(document.getLength(),nomsAttribs.get(i)+" : ",document.getStyle("propriete"));
						document.insertString(document.getLength(),doc.uf.get(nomsAttribs.get(i)).toString(),document.getStyle("valeur"));
						newLine(document);
					}
				}
				
				document.insertString(document.getLength(),CheesyKM.getLabel("sentBy")+" : ",document.getStyle("propriete"));
				document.insertString(document.getLength(),doc.user+" ",document.getStyle("valeurBleue"));
				document.insertString(document.getLength(),CheesyKM.getLabel("at")+" "+doc.date.substring(0,10)+". ",document.getStyle("valeur"));
				//newLine(document);
				
				if(!doc.editdate.equals("")){
					document.insertString(document.getLength(),CheesyKM.getLabel("modifiedAt"),document.getStyle("propriete"));
					document.insertString(document.getLength(),doc.editdate.substring(0,10),document.getStyle("valeur"));
					if(!doc.editor.equals("")){
						document.insertString(document.getLength()," "+CheesyKM.getLabel("by")+" ",document.getStyle("valeur"));
						document.insertString(document.getLength(),doc.editor+".",document.getStyle("valeurBleue"));
					}
					newLine(document);
				} else if(!doc.editor.equals("")){
					document.insertString(document.getLength(),CheesyKM.getLabel("modifiedBy")+" ",document.getStyle("propriete"));
					document.insertString(document.getLength(),doc.editor+".",document.getStyle("valeurBleue"));
					newLine(document);
				}
				
				
		} catch(BadLocationException ble){CheesyKM.echo(ble);}
		textPane.setAutoscrolls(true);
		JScrollPane jspTP=new JScrollPane(textPane);
		jspTP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		add(jspTP,"Center");
		} else if(topic.getNodeType()=='W'){
			JEditorPane editor=new JEditorPane();
			editor.setEditable(false);
			try{
				URL url=new URL(((WebPageTopic)topic).counterURL);
				JEditorPane bidon=new JEditorPane();
				bidon.setPage(url);
				
				editor.setPage(((WebPageTopic)topic).realURL);
			} catch(IOException ioe){
				JOptionPane.showMessageDialog(null, CheesyKM.getLabel("butWhereDidYouPutTheBrowser")+ioe, CheesyKM.getLabel("error"), JOptionPane.ERROR_MESSAGE);
			}
			
			class Hyperactive implements HyperlinkListener {
				public void hyperlinkUpdate(HyperlinkEvent e) {
					if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
						JEditorPane pane = (JEditorPane) e.getSource();
						if (e instanceof HTMLFrameHyperlinkEvent) {
							HTMLFrameHyperlinkEvent  evt = (HTMLFrameHyperlinkEvent)e;
							HTMLDocument doc = (HTMLDocument)pane.getDocument();
							doc.processHTMLFrameHyperlinkEvent(evt);
						} else {
							try {
								pane.setPage(e.getURL());
							} catch (Throwable t) {
								t.printStackTrace();
							}
						}
					}
				}
			}
			editor.addHyperlinkListener(new Hyperactive());
			
			JScrollPane editorScrollPane = new JScrollPane(editor);
			editorScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			add(editorScrollPane,"Center");
			//CheesyKM.echo(topic+" a sa page web");
			
		} else if(topic.getNodeType()=='F'){
			FileTransferTopic ftt=(FileTransferTopic)topic;
			this.setLayout(new BorderLayout());
			JPanel barPanel=new JPanel(new GridLayout(2,0));
			barPanel.add(new JLabel(CheesyKM.getLabel("downloadProgress")+" "+ftt.fileName+" :"),"North");
			barPanel.add(ftt.pi.bar);
			
			add(barPanel,"North");
			
			barPanel.setMaximumSize(new Dimension(800,50));
			class AnnulerActionListener implements ActionListener{
				public void actionPerformed(ActionEvent e){
					if(((FileTransferTopic)TopicPane.this.topic).pi.isRunning){
						((FileTransferTopic)TopicPane.this.topic).pi.d.stop=true;
						((JButton)e.getSource()).setText(CheesyKM.getLabel("closeTab"));
					} else {
						CheesyKM.api.hideTopic(CheesyKM.api.jtpD.getSelectedIndex());
					}
				}
			}
			JButton annuler=new JButton(CheesyKM.getLabel("cancelDownload"));
			annuler.addActionListener(new AnnulerActionListener());
			JPanel buttonPanel=new JPanel(new BorderLayout());
			buttonPanel.add(annuler,"North");
			add(buttonPanel,"Center");
			ftt.button=annuler;
		} else if(topic.getNodeType()=='A'){
			add(new JScrollPane(new AdvancedSearchForm(CheesyKM.DEFAULTSEARCHFIELDNUMBER)));
		}
		
		setMinimumSize(new Dimension(100,100));
	}
	
	protected void newLine(StyledDocument doc){
		try{
			doc.insertString(doc.getLength(),"\n",doc.getStyle("regular"));
		} catch(BadLocationException ble){CheesyKM.echo(ble);}
	}
	
	/**
	*Creates the styles used to display the various properties of a document.(Fonts size, color, style, button, etc...)
	*/
	protected void addStylesToDocument(StyledDocument doc) {
		//Initialize some styles.
		Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
		
		//style de base
		Style regular = doc.addStyle("regular", def);
		StyleConstants.setFontFamily(def, "Lucida Sans");
		StyleConstants.setBold(def,false);
		StyleConstants.setItalic(def,false);
		StyleConstants.setFontSize(regular,14);
		
		//Titre
		Style s=doc.addStyle("titre",regular);
		StyleConstants.setFontSize(s,16);
		StyleConstants.setBold(s,true);
		
		//Edition
		s = doc.addStyle("ed", regular);
		StyleConstants.setItalic(s,true);
		StyleConstants.setForeground(s,Color.gray);
		StyleConstants.setFontSize(s,12);
		
		//Auteur
		s = doc.addStyle("auteur", regular);
		StyleConstants.setItalic(s,true);
		
		//Desription
		s=doc.addStyle("desc",regular);
		
		//Propriété (Libelllllllllllllé)
		s=doc.addStyle("propriete",regular);
		StyleConstants.setBold(s,true);
		StyleConstants.setFontSize(s,10);
		
		//Valeur (d'une propriété)
		s=doc.addStyle("valeur",regular);
		StyleConstants.setFontSize(s,11);
		
		//ValeurVerte (d'une propriété)
		s=doc.addStyle("valeurVerte",regular);
		StyleConstants.setFontSize(s,11);
		StyleConstants.setForeground(s,new Color(51,102,0));
		
		//ValeurOrange (d'une propriété)
		s=doc.addStyle("valeurOrange",regular);
		StyleConstants.setFontSize(s,11);
		StyleConstants.setForeground(s,new Color(204,102,0));
		
		//ValeurBleue (d'une propriété)
		s=doc.addStyle("valeurBleue",regular);
		StyleConstants.setFontSize(s,11);
		//StyleConstants.setForeground(s,new Color(0,0,255));
		
		//Bouton www
		s = doc.addStyle("wwwButton",regular);
		StyleConstants.setFontSize(s,11);
		StyleConstants.setAlignment(s,StyleConstants.ALIGN_LEFT);
		JButton wwwButton = new JButton();
		wwwButton.setIcon(CheesyKM.loadIcon("./ressources/WebComponent16.gif"));
		wwwButton.setText(CheesyKM.getLabel("visitSite"));
		wwwButton.setToolTipText(CheesyKM.getLabel("visitSite"));
		class wwwButtonActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e){
				CheesyKM.openURL((Doc)((TopicPane)(CheesyKM.api.jtpD.getSelectedComponent())).topic);
			}
		}
		wwwButton.addActionListener(new wwwButtonActionListener());
		wwwButton.setFont(new Font("arial",Font.PLAIN,11));
		wwwButton.setMaximumSize(new Dimension(110,19));
		wwwButton.setPreferredSize(new Dimension(110,19));
		StyleConstants.setComponent(s,wwwButton);
		
		//Bouton wwwToClipBoard
		s = doc.addStyle("wwwToClipBoardButton",regular);
		StyleConstants.setFontSize(s,11);
		StyleConstants.setAlignment(s,StyleConstants.ALIGN_LEFT);
		JButton wwwToClipBoardButton = new JButton();
		wwwToClipBoardButton.setIcon(CheesyKM.loadIcon("./ressources/Copy16.gif"));
		wwwToClipBoardButton.setText(CheesyKM.getLabel("copyURL"));
		wwwToClipBoardButton.setToolTipText(CheesyKM.getLabel("copyURLToSystemClipboard"));
		class wwwToClipBoardButtonActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e){
				String url=((Doc)((TopicPane)(CheesyKM.api.jtpD.getSelectedComponent())).topic).url;
				CheesyKM.copyToClipBoard(url);
			}
		}
		wwwToClipBoardButton.addActionListener(new wwwToClipBoardButtonActionListener());
		wwwToClipBoardButton.setFont(new Font("arial",Font.PLAIN,11));
		wwwToClipBoardButton.setMaximumSize(new Dimension(120,19));
		wwwToClipBoardButton.setPreferredSize(new Dimension(120,19));
		StyleConstants.setComponent(s,wwwToClipBoardButton);
		
		
		//Bouton DL
		s = doc.addStyle("dlButton",regular);
		StyleConstants.setFontSize(s,11);
		StyleConstants.setAlignment(s,StyleConstants.ALIGN_LEFT);
		JButton dlButton = new JButton();
		dlButton.setIcon(CheesyKM.loadIcon("./ressources/Import16.gif"));
		dlButton.setText(CheesyKM.getLabel("download"));
		dlButton.setToolTipText(CheesyKM.getLabel("downloadDocument"));
		class dlButtonActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e){
				CheesyKM.download((Doc)((TopicPane)(CheesyKM.api.jtpD.getSelectedComponent())).topic,true);
			}
		}
		dlButton.addActionListener(new dlButtonActionListener());
		dlButton.setFont(new Font("arial",Font.PLAIN,11));
		dlButton.setMaximumSize(new Dimension(120,19));
		dlButton.setPreferredSize(new Dimension(120,19));
		StyleConstants.setComponent(s,dlButton);
		
		//Bouton voir fichier
		s = doc.addStyle("viewFileButton",regular);
		StyleConstants.setFontSize(s,11);
		StyleConstants.setAlignment(s,StyleConstants.ALIGN_LEFT);
		JButton viewFileButton = new JButton();
		
		
		viewFileButton.setIcon(CheesyKM.loadIcon("./ressources/m"+((Doc)this.topic).ftype+".png"));
		
		
		viewFileButton.setText(CheesyKM.getLabel("display"));
		viewFileButton.setToolTipText(CheesyKM.getLabel("seeDocument"));
		class viewFileButtonActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e){
				CheesyKM.download((Doc)((TopicPane)(CheesyKM.api.jtpD.getSelectedComponent())).topic,false);
			}
		}
		viewFileButton.addActionListener(new viewFileButtonActionListener());
		viewFileButton.setFont(new Font("arial",Font.PLAIN,11));
		viewFileButton.setMaximumSize(new Dimension(125,19));
		viewFileButton.setPreferredSize(new Dimension(125,19));
		StyleConstants.setComponent(s,viewFileButton);
		
		//Icone etoile foncee
		for(int i=1;i<6;i++){
			s = doc.addStyle("iconF"+i, regular);
			StyleConstants.setAlignment(s, StyleConstants.ALIGN_LEFT);
			StyleConstants.setIcon(s,CheesyKM.loadIcon("./ressources/etoile-foncee.png"));
		}
		
		//Icone etoile claire
		for(int i=1;i<6;i++){
			s = doc.addStyle("iconC"+i, regular);
			StyleConstants.setAlignment(s, StyleConstants.ALIGN_LEFT);
			StyleConstants.setIcon(s,CheesyKM.loadIcon("./ressources/etoile-claire.png"));
		}
		
		//boutons voir themes
		
		if(this.topic.getNodeType()=='D'){
			for(int i=0;i<((Doc)this.topic).topicList.size();i++){
				s = doc.addStyle("themeButton"+i,regular);
				StyleConstants.setFontSize(s,11);
				StyleConstants.setAlignment(s,StyleConstants.ALIGN_LEFT);
				JButton themeButton = new JButton();
				themeButton.setIcon(CheesyKM.loadIcon("./ressources/ZoomIn16.gif"));
				themeButton.setText("");
				themeButton.setToolTipText(CheesyKM.getLabel("openTopic")+" "+CheesyKM.getTopicFullName(((Doc)(topic)).topicList.get(i).toString())+" "+CheesyKM.getLabel("inTopicTreeView"));
				class themeButtonActionListener implements ActionListener {
					int i;
					themeButtonActionListener(int i){
						this.i=i;
					}
					public void actionPerformed(ActionEvent e){
						CheesyKM.api.thematique.expandPathToTopic(((Doc)(TopicPane.this.topic)).topicList.get(i).toString());
						CheesyKM.api.jtpG.setSelectedIndex(1);
					}
				}
				themeButton.addActionListener(new themeButtonActionListener(i));
				themeButton.setFont(new Font("arial",Font.PLAIN,11));
				themeButton.setMaximumSize(new Dimension(19,19));
				themeButton.setPreferredSize(new Dimension(19,19));
				StyleConstants.setComponent(s,themeButton);
			}
		}
	}	
}
