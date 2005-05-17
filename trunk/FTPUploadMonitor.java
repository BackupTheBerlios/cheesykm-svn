import com.enterprisedt.net.ftp.*;
import javax.swing.*;
/**
*Used to display a progressBar while a FTP transfer is in progress.
*/
public class FTPUploadMonitor extends JDialog implements FTPProgressMonitor{
	JProgressBar pb;
	long totalSize;
	/**
	*Builds a new FTPUploadMonitor.
	*@param totalSize total size of the transferred file.
	*/
	FTPUploadMonitor(long totalSize){
		super(CheesyKM.api);
		this.totalSize=totalSize;
		pb=new JProgressBar(0,new Long(totalSize).intValue());
		pb.setIndeterminate(true);
		pb.setString(CheesyKM.getLabel("transferInProgress"));
		pb.setStringPainted(true);
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.setUndecorated(true);
		this.setResizable(false);
		this.getContentPane().add(pb);
		int maHauteur=40;
		int maLargeur=300;
		this.setBounds(CheesyKM.api.getX()+((CheesyKM.api.getWidth()-maLargeur)/2),CheesyKM.api.getY()+((CheesyKM.api.getHeight()-maHauteur)/2),maLargeur,maHauteur);
		this.validate();
		this.setModal(true);
	}
	/**
	*Called by the FTPClient to monitor a file transfer.
	*/
	public void bytesTransferred(long count){
		if(pb.isIndeterminate()){
			pb.setIndeterminate(false);
		}
		pb.setValue(new Long(count).intValue());
	}
}
