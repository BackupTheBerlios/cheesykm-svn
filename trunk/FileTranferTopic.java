import javax.swing.*;

class FileTransferTopic extends Topic{
	String fileName;
	ProgressBarMonitor pi;
	JButton button;
	FileTransferTopic(String fileName,ProgressBarMonitor pi){
		super(-1);
		this.setNodeType('F');
		this.fileName=fileName;
		this.pi=pi;
		this.button=null;
	}
	
	public String toString(){
		return this.fileName;
	}
}
