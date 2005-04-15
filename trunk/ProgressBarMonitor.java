import javax.swing.*;
import java.awt.*;

class ProgressBarMonitor{
	int total;
	int actuel;
	boolean isRunning=true;
	JProgressBar bar;
	String fileName;
	Download d;
	FileTransferTopic topic;
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
