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
			ProgressBarMonitor pbm=new ProgressBarMonitor(new Long(size).intValue(),nomComplet,this);
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
				//CheesyKM.echo("2048 octet");
				//CheesyKM.echo(pbm.actuel+"/"+size+"[++]");
			}
			
			byte[] b=new byte[1];
			int j=0;
			while(bif.read(b,0,1)!=-1&&!stop){
			//for(int k=toskip*65536;k<size;k++){
			//	bif.read(b,0,1);
				j++;
				bof.write(b,0,1);
				if(j==100){
					j=0;
					pbm.setValue(pbm.actuel+100);
					//CheesyKM.echo(pbm.actuel+"/"+size+"[]");
				}
			}
			
			bof.flush();
			fos.flush();
			bof.close();
			fos.close();
			bif.close();
			pbm.setValue(new Long(size).intValue());
			//echo("DL fini: toskip="+toskip+"  lefttoskip="+lefttoskip);
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
		}
		
	}
}
