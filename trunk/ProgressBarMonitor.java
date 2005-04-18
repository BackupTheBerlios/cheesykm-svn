import javax.swing.*;
import java.awt.*;
/**
*Monitors a file transfer, and updates its associated progress bar.
*/
class ProgressBarMonitor{
	/**Total file size*/
	int total;
	/**Current transferred size*/
	int actuel;
	/**Transfer in progress ?*/
	boolean isRunning=true;
	/**Associated progress bar*/
	JProgressBar bar;
	/**destination file name*/
	String fileName;
	/**Associated {@link Download}*/
	Download d;
	/**Associated {@link FileTransferTopic}*/
	FileTransferTopic topic;
	/**
	*New ProgressBarMonitor.
	*@param total Total file size.
	*@param fileName Destination file name.
	*@param d Associated {@link Download}.
	*/
	ProgressBarMonitor(int total,String fileName,Download d){
		this.total=total;
		this.d=d;
		this.actuel=0;
		this.bar=new JProgressBar(0,total);
		bar.setStringPainted(true);
		bar.setIndeterminate(true);
		bar.setPreferredSize(new Dimension(200,50));
		this.fileName=fileName;
		topic=new FileTransferTopic(fileName,this);
		CheesyKM.api.displayTopic(topic);
		
	}
	/**
	*Updates the progress of the transfer, and the progress bar.
	*@param actuel Current transferred size.
	*/
	public void setValue(int actuel){
		bar.setIndeterminate(false);
		this.actuel=actuel;
		bar.setValue(actuel);
		bar.setString(new Float(((float)((actuel+0.0)/(total+0.0)))*100).intValue()+"%");
		if(actuel>=total){
			isRunning=false;
			topic.button.setText(CheesyKM.getLabel("closeTab"));
		}
	}
	
}
