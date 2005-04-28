import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class EditableFieldGroup extends JPanel{
	Vector fields;
	JComponent toEnable=null;
	
	EditableFieldGroup(JComponent toEnable){
		super();
		this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		fields=new Vector();
		this.toEnable=toEnable;
	}
	
	void addEditableField(EditableField field){
		field.registerForGroup(this);
		fields.add(field);
		add((JPanel)field);
	}
	
	void addEditableField(EditableField field,boolean hasToBeSet){
		field.setHasToBeSet(hasToBeSet);
		addEditableField(field);
	}
	
	boolean fieldsAreValid(){
		Enumeration e=fields.elements();
		while(e.hasMoreElements()){
			if(!(((EditableField)e.nextElement()).isValid())) return false;
			//CheesyKM.echo("VALID");
		}
		return true;
	}
	
	void sayWuff(){
		//CheesyKM.echo("WUFF!");
		if(toEnable!=null){
			toEnable.setEnabled(fieldsAreValid());
		}
	}
	
	Vector getValues(){
		Vector resu=new Vector();
		for(int i=0;i<fields.size();i++){
			resu.add(((EditableField)fields.get(i)).value());
		}
		return resu;
	}
}
