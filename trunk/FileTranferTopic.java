import javax.swing.*;
/**
*Special {@link Topic}, to display informations about a file transfer in the right tabbed pane.
*/
class FileTransferTopic extends Topic{
	/**path to the destination file*/
	String fileName;
	/**Associated {@link ProgressBarMonitor}*/
	ProgressBarMonitor pi;
	/**Associated "Abort transfer" button*/
	JButton button;
	/**
	*Creates a new FileTransferTopic (id=-1,nodeType='F')
	*@param fileName Path to the destination file.
	*@param pi Associated {@link ProgressBarMonitor}.
	*/
	FileTransferTopic(String fileName,ProgressBarMonitor pi){
		super(-1);
		this.setNodeType('F');
		this.fileName=fileName;
		this.pi=pi;
		this.button=null;
	}
	
	/**
	*@return the filename of this FileTransferTopic.
	*/
	public String toString(){
		return this.fileName;
	}
}
