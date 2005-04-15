import javax.swing.filechooser.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.File;

class FileChooserDialog {
	public static String showChooser(JFrame parent,String title,FileFilter filtre,boolean enregistrer,String nomSuggere){
		//FileChooserDialog fcd=new FileChooserDialog(parent,title,filtre,enregistrer,nomSuggere);
		JFileChooser jfc=new JFileChooser();
		if(filtre!=null)jfc.addChoosableFileFilter(filtre);
		if(nomSuggere!=null)jfc.setSelectedFile(new File(nomSuggere));
		int resu;
		if(enregistrer){
			resu=jfc.showSaveDialog(parent);
		} else {
			resu=jfc.showOpenDialog(parent);
		}
		if(resu==JFileChooser.APPROVE_OPTION){
			return jfc.getSelectedFile().getAbsolutePath();
		} else {
			return null;
		}
	}
}
