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
import javax.swing.*;
import java.io.*;
import java.awt.*;
import java.net.*;
/**
*CheesyKMs internal http download manager.
*/
class Download extends Thread{
	/**path to the destination file.*/
	String nomComplet;
	/**source URL*/
	String url;
	/**Size of the file*/
	long size;
	
	boolean stop;
	ProgressBarMonitor pbm;
	/**
	*Creates and starts a new Download Thread.
	*/
	Download(String nomComplet,String url,long size){
		this.nomComplet=nomComplet;
		this.url=url;
		this.size=size;
		stop=false;
		start();
		
	}
	
	/**
	*Creates a new {@link ProgressBarMonitor} and downloads the file.<br>
	*Shows an error dialog if an execption occured during the transfer.
	*/
	public void run(){
		try{	
			pbm=new ProgressBarMonitor(new Long(size).intValue(),nomComplet,this);
			File fichierDest=new File(nomComplet);
			if(!fichierDest.exists())fichierDest.createNewFile();
			URL urlFic=new URL(url);
			BufferedInputStream bif=new BufferedInputStream(urlFic.openStream(),2048);
			FileOutputStream fos=new FileOutputStream(fichierDest,false);
			BufferedOutputStream bof=new BufferedOutputStream(fos,2048);
			byte[] bskip=new byte[2048];
			int toskip=new Double(size/(2048)).intValue();
			for(int i=0;i<toskip;i++){
				if(stop)break;
				bif.read(bskip,0,2048);
				bof.write(bskip,0,2048);
				pbm.setValue(pbm.actuel+2048);
			}
			
			byte[] b=new byte[1];
			int j=0;
			while(bif.read(b,0,1)!=-1&&!stop){
				j++;
				bof.write(b,0,1);
				if(j==100){
					j=0;
					pbm.setValue(pbm.actuel+100);
				}
			}
			
			bof.flush();
			fos.flush();
			bof.close();
			fos.close();
			bif.close();
			pbm.setValue(new Long(size).intValue());
			if(!stop){
				JOptionPane.showMessageDialog(CheesyKM.api,CheesyKM.getLabel("downloadOf")+nomComplet+CheesyKM.getLabel("successFullyDone"),CheesyKM.getLabel("downloadComplete"), JOptionPane.INFORMATION_MESSAGE);
			} else {
				File f=new File(nomComplet);
				f.delete();
				f=null;
				JOptionPane.showMessageDialog(CheesyKM.api,CheesyKM.getLabel("downloadOf")+nomComplet+CheesyKM.getLabel("hasBeenCanceled"),CheesyKM.getLabel("downloadCanceled"), JOptionPane.INFORMATION_MESSAGE);
			}
		} catch(Exception e){
			JOptionPane.showMessageDialog(null, CheesyKM.getLabel("error")+e, CheesyKM.getLabel("errorWhileDownloading")+nomComplet, JOptionPane.ERROR_MESSAGE);
			pbm.setValue(new Long(size).intValue());
			File f=new File(nomComplet);
			f.delete();
			f=null;
			JOptionPane.showMessageDialog(CheesyKM.api,CheesyKM.getLabel("downloadOf")+nomComplet+CheesyKM.getLabel("hasBeenCanceled"),CheesyKM.getLabel("downloadCanceled"), JOptionPane.INFORMATION_MESSAGE);
		}
		
	}
}
