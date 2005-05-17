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
	/**
	*Called by the JFileChooser, checks if a file has to be displayed or not.
	*@param f the File to check.
	*/
	public boolean accept(File f) {
	    if (f.isDirectory()) {
		return true;
	    }
		String nomfic=f.getName();
		return nomfic.endsWith(this.extension);
	}
	
	/**
	*Called by some Look & Feel.
	*/
	public String getDescription() {
		 return "*"+this.extension;
	}
}