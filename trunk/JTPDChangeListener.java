import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.tree.*;
import javax.swing.event.*;
/**
*Listens to right tabbed pane selection changes, and enables or disables the matching buttons/menu items.
*/
class JTPDChangeListener implements ChangeListener{
	public void stateChanged(ChangeEvent e){
		CheesyKM.api.menuOnglet.setEnabled(CheesyKM.api.jtpD.getSelectedComponent()!=null);
		CheesyKM.api.menuFermerOnglet.setEnabled(CheesyKM.api.jtpD.getSelectedComponent()!=null);
		CheesyKM.api.jtbFermerOnglet.setEnabled(CheesyKM.api.jtpD.getSelectedComponent()!=null);
		CheesyKM.api.menuFermerTousLesOnglets.setEnabled(CheesyKM.api.jtpD.getSelectedComponent()!=null&&!CheesyKM.api.runningFileTransferIsDisplayed());
		CheesyKM.api.jtbFermerTousLesOnglets.setEnabled(CheesyKM.api.jtpD.getSelectedComponent()!=null&&!CheesyKM.api.runningFileTransferIsDisplayed());
		Topic t=CheesyKM.api.getDisplayedTopic();
		if(t==null){
			CheesyKM.api.menuDocument.setEnabled(false);
			CheesyKM.api.menuVoirDocument.setEnabled(false);
			CheesyKM.api.menuTelechargerDocument.setEnabled(false);
			CheesyKM.api.jtbVoirDocument.setEnabled(false);
			CheesyKM.api.jtbVoirDocument.setIcon(CheesyKM.loadIcon("./ressources/Sheet.gif"));
			CheesyKM.api.jtbVoirDocument.setToolTipText(CheesyKM.getLabel("seeDocument"));
			CheesyKM.api.jtbTelechargerDocument.setEnabled(false);
			CheesyKM.api.jtbTelechargerDocument.setToolTipText(CheesyKM.getLabel("downloadDocument"));
			CheesyKM.api.menuWeb.setEnabled(false);
			CheesyKM.api.menuVoirSiteWeb.setEnabled(false);
			CheesyKM.api.jtbVoirSiteWeb.setEnabled(false);
			CheesyKM.api.jtbVoirSiteWeb.setToolTipText(CheesyKM.getLabel("visitSite"));
			CheesyKM.api.menuCopierAdresseWeb.setEnabled(false);
			CheesyKM.api.jtbCopierAddresseWeb.setEnabled(false);
		} else if(t.getNodeType()=='D'){
			Doc d=(Doc)t;
			CheesyKM.api.menuVoirDocument.setEnabled(!d.file.equals(""));
			CheesyKM.api.menuTelechargerDocument.setEnabled(!d.file.equals(""));
			CheesyKM.api.jtbVoirDocument.setEnabled(!d.file.equals(""));
			CheesyKM.api.jtbTelechargerDocument.setEnabled(!d.file.equals(""));
			CheesyKM.api.menuDocument.setEnabled(!d.file.equals(""));
			if(!d.file.equals("")){
				CheesyKM.api.jtbVoirDocument.setIcon(CheesyKM.loadIcon("./ressources/"+d.ftype+"32.png"));
				CheesyKM.api.jtbVoirDocument.setToolTipText(CheesyKM.getLabel("seeDocument")+" : "+d.format+" - "+d.getFSize());
				CheesyKM.api.jtbTelechargerDocument.setToolTipText(CheesyKM.getLabel("downloadDocument")+" : "+d.format+" - "+d.getFSize());
			}
			CheesyKM.api.menuWeb.setEnabled(!d.url.equals(""));
			CheesyKM.api.menuVoirSiteWeb.setEnabled(!d.url.equals(""));
			CheesyKM.api.jtbVoirSiteWeb.setEnabled(!d.url.equals(""));
			CheesyKM.api.menuCopierAdresseWeb.setEnabled(!d.url.equals(""));
			CheesyKM.api.jtbCopierAddresseWeb.setEnabled(!d.url.equals(""));
			if(!d.url.equals("")){
				CheesyKM.api.jtbVoirSiteWeb.setToolTipText(CheesyKM.getLabel("browse")+" ["+d.url+"]");
			}
		} else {
			CheesyKM.api.menuDocument.setEnabled(false);
			CheesyKM.api.menuVoirDocument.setEnabled(false);
			CheesyKM.api.menuTelechargerDocument.setEnabled(false);
			CheesyKM.api.jtbVoirDocument.setEnabled(false);
			CheesyKM.api.jtbVoirDocument.setIcon(CheesyKM.loadIcon("./ressources/Sheet.gif"));
			CheesyKM.api.jtbVoirDocument.setToolTipText(CheesyKM.getLabel("seeDocument"));
			CheesyKM.api.jtbTelechargerDocument.setEnabled(false);
			CheesyKM.api.jtbTelechargerDocument.setToolTipText(CheesyKM.getLabel("downloadDocument"));
			CheesyKM.api.menuWeb.setEnabled(false);
			CheesyKM.api.menuVoirSiteWeb.setEnabled(false);
			CheesyKM.api.jtbVoirSiteWeb.setEnabled(false);
			CheesyKM.api.jtbVoirSiteWeb.setToolTipText(CheesyKM.getLabel("visitSite"));
			CheesyKM.api.menuCopierAdresseWeb.setEnabled(false);
			CheesyKM.api.jtbCopierAddresseWeb.setEnabled(false);
			
			if(t.getNodeType()=='F'){
				CheesyKM.api.menuFermerOnglet.setEnabled(false);
				CheesyKM.api.jtbFermerOnglet.setEnabled(false);
			}
		}
		
		
	}
}


