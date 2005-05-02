import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
*Displays a JSlider (0-100) in a JDialog.
*/
class RateDocDialog extends JDialog{
	JSlider ratingS;
	Doc doc;
	/**
	*Displays a JSlider (0-100) in a modal JDialog with OK/Cancel buttons. The RPC "rateDoc" method is called when the user clicks "OK".
	*@param doc the document to rate.
	*/
	RateDocDialog(Doc doc){
		super(CheesyKM.api);
		this.doc=doc;
		ratingS=new JSlider(JSlider.HORIZONTAL,0,100,doc.score);
		ratingS.setMajorTickSpacing(20);
		ratingS.setPaintTicks(true);
		ratingS.setPaintLabels(true);
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(ratingS,"Center");
		JPanel south=new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton oki=new JButton(CheesyKM.getLabel("okidoki"));
		oki.setIcon(CheesyKM.loadIcon("./ressources/Check.gif"));
		class OkiButtonListener implements ActionListener{
			public void actionPerformed(ActionEvent e){
				RateDocDialog.this.doc.score=CheesyKM.rateDoc(RateDocDialog.this.doc,RateDocDialog.this.ratingS.getValue());
				int i=CheesyKM.api.getIndexForDisplayedTopic(RateDocDialog.this.doc.id);
				if(i!=-1){
					CheesyKM.api.hideTopic(i);
					CheesyKM.api.displayTopic(RateDocDialog.this.doc);
				}
				RateDocDialog.this.dispose();
			}
		}
		oki.addActionListener(new OkiButtonListener());
		JButton cancel=new JButton(CheesyKM.getLabel("cancel"));
		cancel.setIcon(CheesyKM.loadIcon("./ressources/Delete20.gif"));
		class CancelButtonListener implements ActionListener{
			public void actionPerformed(ActionEvent e){
				RateDocDialog.this.dispose();
			}
		}
		cancel.addActionListener(new CancelButtonListener());
		south.add(oki);
		south.add(cancel);
		this.getContentPane().add(south,"South");
		this.getContentPane().add(new JLabel(CheesyKM.getLabel("rateDoc"),JLabel.CENTER),"North");
		this.setModal(true);
		this.setUndecorated(false);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.pack();
		this.setBounds(CheesyKM.api.getX()+((CheesyKM.api.getWidth()-this.getWidth())/2),CheesyKM.api.getY()+((CheesyKM.api.getHeight()-this.getHeight())/2),this.getWidth(),this.getHeight());
		this.setVisible(true);
	}
}
