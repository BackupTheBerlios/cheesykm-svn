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
		return showChooser(parent,title,filtre,enregistrer,nomSuggere,false,false);
	}
	
	/**
	*Shows a file chooser dialog and returns a String representation of the selected file.
	*@param parent Dialogs parent Frame.
	*@param title Title of the Dialog.
	*@param filtre FileFilter to apply to the displayed files. (see {@link FiltreFics})
	*@param enregistrer <code>true</code> to show a "Save" dialog, <code>false</code> to show a "Open" dialog.
	*@param nomSuggere Suggested file name.
	*@param foldersToo <code>true</code> to allow folder selection.
	*@return String representation of the full path to the selected file.
	*/
	public static String showChooser(JFrame parent,String title,FileFilter filtre,boolean enregistrer,String nomSuggere,boolean foldersToo,boolean onlyFolders){
		JFileChooser jfc=new JFileChooser();
		if(foldersToo)jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		if(onlyFolders)jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
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
