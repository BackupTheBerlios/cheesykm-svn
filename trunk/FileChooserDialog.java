import javax.swing.filechooser.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.File;
/**
*Manages file choosers.
*/
class FileChooserDialog {
	/**
	*Shows a file chooser dialog and returns a String representation of the selected file.
	*@param parent Dialogs parent Frame.
	*@param title Title of the Dialog.
	*@param filtre FileFilter to apply to the displayed files. (see {@link FiltreFics})
	*@param enregistrer <code>true</code> to show a "Save" dialog, <code>false</code> to show a "Open" dialog.
	*@param nomSuggere Suggested file name.
	*@return String representation of the full path to the selected file.
	*/
	public static String showChooser(JFrame parent,String title,FileFilter filtre,boolean enregistrer,String nomSuggere){
		JFileChooser jfc=new JFileChooser();
		if(filtre!=null)jfc.addChoosableFileFilter(filtre);
		if(nomSuggere!=null)jfc.setSelectedFile(new File(nomSuggere));
		int resu;
		if(enregistrer){
			jfc.setDialogTitle(CheesyKM.getLabel("saveFileAs"));
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
