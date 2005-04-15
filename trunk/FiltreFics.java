import java.util.*;
import javax.swing.filechooser.*;
import java.io.*;

public class FiltreFics extends javax.swing.filechooser.FileFilter{
	private String extension;
	
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