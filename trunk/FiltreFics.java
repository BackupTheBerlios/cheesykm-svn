import java.util.*;
import javax.swing.filechooser.*;
import java.io.*;
/**
*File filter, applicable to file choosers, to filter the displayed file types.
*@see FileChooserDialog
*/
public class FiltreFics extends javax.swing.filechooser.FileFilter{
	private String extension;
	/**
	*New file filter that accepts files with the specified extension.
	*@param extension File extension to accept as a <code>String</code>.
	*/
	public FiltreFics(String extension){
		this.extension=extension;
	}
	
	public boolean accept(File f) {
	    if (f.isDirectory()) {
		return true;
	    }
		String nomfic=f.getName();
		return nomfic.endsWith(this.extension);
	}
	
	public String getDescription() {
		 return "*"+this.extension;
	}
}