import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
/**
*A Wuffable Object is an Object on which you can call the "sayWuff()" method (pronounce "woof!"). Used by the {@link EditableField} to tell their {@link EditableFieldGroup} that they became (in)valid.
*/
interface Wuffable {
	/**
	*Used by the {@link EditableField} to tell their {@link EditableFieldGroup} that they became (in)valid.
	*/
	public void sayWuff();
}
/**
*Group of {@link EditableField}, used to build forms.
*/
public class EditableFieldGroup extends JPanel implements Wuffable{
	Vector fields;
	JComponent toEnable=null;
	/**
	*Creates a new EditableFieldGroup, with an attached compenent to enable(true) if all the fields are valid, and to enable(false) else.
	*/
	EditableFieldGroup(JComponent toEnable){
		super();
		this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		fields=new Vector();
		this.toEnable=toEnable;
	}
	/**
	*Adds an {@link EditableField} to this group and calls its {@link EditableField#registerForGroup(Wuffable)} method.
	*@param field the {@link EditableField} to add to this group.
	*/
	void addEditableField(EditableField field){
		field.registerForGroup(this);
		fields.add(field);
		add((JPanel)field);
	}
	
	/**
	*Adds an {@link EditableField} to this group, and calls its {@link EditableField#registerForGroup(Wuffable)} and {@link EditableField#setHasToBeSet(boolean)} methods.
	*@param field the {@link EditableField} to add to this group.
	*@param hasToBeSet true if field has to be set to be valid, false else.
	*/
	void addEditableField(EditableField field,boolean hasToBeSet){
		field.setHasToBeSet(hasToBeSet);
		addEditableField(field);
	}
	
	/**
	*Checks if all the fields in this group are valid.
	*@return true if all the fields of this group are valid, false else.
	*/
	boolean fieldsAreValid(){
		Enumeration e=fields.elements();
		boolean resu=true;
		while(e.hasMoreElements()){
			if(!(((EditableField)e.nextElement()).isValid())) resu=false;
			//CheesyKM.echo("VALID");
		}
		return resu;
	}
	/**
	*Called by the members (fields) of this group to tell this EditableFieldGroup that they became valid or invalid. It permits to enable or disable the JComponent attaches to this Group.
	*/
	public void sayWuff(){
		//CheesyKM.echo("WUFF!");
		if(toEnable!=null){
			toEnable.setEnabled(fieldsAreValid());
		}
	}
	/**
	*Returns all the values of the Fields in this group, in the same order as they were added to this group.
	*@return the values of all the fields in this group as a Vector.
	*/
	Vector getValues(){
		Vector resu=new Vector();
		for(int i=0;i<fields.size();i++){
			resu.add(((EditableField)fields.get(i)).value());
		}
		return resu;
	}
}
