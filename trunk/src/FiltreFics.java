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