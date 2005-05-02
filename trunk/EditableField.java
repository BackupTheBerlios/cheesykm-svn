import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
/**
*An EditableField is used, within an {@link EditableFieldGroup} to build forms.<br>
*The EditableField combines a JLabel (usually the name of the property) and a user-editable component (JTextField, etc...).
*It is possible to specify that a field has to be set. If a field is invalid (ex:invalid date format, or blank&&hatToBeSet), its
*background becomes red and the {@link EditableFieldGroup} it belongs to is not valid().
*/
public abstract class EditableField extends JPanel{
	boolean hasToBeSet=false;
	Wuffable efg=null;
	/**Default constructor, calls JPanels constructor.*/
	EditableField(LayoutManager m){
		super(m);
	}
	/**Default constructor, calls JPanels constructor.*/
	EditableField(){
		super();
	}
	/**
	*Specify that this field has to be set to be valid.
	*@param hasToBeSet boolean, true if the field has to be set, false else.
	*/
	public abstract void setHasToBeSet(boolean hasToBeSet);
	
	/**
	*Check if this field has to be set to be valid.
	*@return true if this field has to be set.
	*/
	public boolean getHasToBeSet(){
		return this.hasToBeSet;
	}
	/**
	*Checks if this field content is valid
	*@return true if this field is valid, false else.
	*/
	public abstract boolean isValid();
	/**
	*Returns the value of this field.
	*@return the value of this field, as an Object.
	*/
	public abstract Object value();
	/**
	*Register this field as member of an {@link EditableFieldGroup}.
	*@param efg {@link Wuffable}, usually an {@link EditableFieldGroup} to register this EditableField to.
	*/
	public void registerForGroup(Wuffable efg){
		this.efg=efg;
	}
}
/**
*Displays an JLabel and a JTextField, only Integers can be typed in the JTextField.
*/
class IntegerValue extends EditableField{
	public JTextField tf;
	IntegerValue(String title,int value){
		super(new FlowLayout(FlowLayout.LEFT));
		add(new JLabel(title));
		tf=new JTextField();
		tf.setText(new Integer(value).toString());
		tf.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();      
				if (!((Character.isDigit(c) ||(c == KeyEvent.VK_BACK_SPACE) ||(c == KeyEvent.VK_DELETE)))) {
					e.getComponent().getToolkit().beep();
					e.consume();
				}
			}
			public void keyReleased(KeyEvent e) {
				if(IntegerValue.this.hasToBeSet&&IntegerValue.this.tf.getText().length()==0){
					IntegerValue.this.tf.setBackground(Color.red);
				} else {
					IntegerValue.this.tf.setBackground(new JTextField().getBackground());
				}
				if(IntegerValue.this.efg!=null){
					IntegerValue.this.efg.sayWuff();
				}
			}
		});
		tf.setColumns(5);
		add(tf);
	}
	public boolean isValid(){
		if(tf.getText().length()==0&&hasToBeSet){
			return false;
		} else {
			return true;
		}
	}
	public Object value(){
		return new Integer(tf.getText());
	}
	public void setHasToBeSet(boolean b){
		this.hasToBeSet=b;
		if(this.hasToBeSet&&tf.getText().length()==0){
			tf.setBackground(Color.red);
		} else {
			tf.setBackground(new JTextField().getBackground());
		}
	}
}
/**
*Displays a label and a String in a textfield.
*/
class StringValue extends EditableField{
	public JTextField tf;
	StringValue(String title,String value){
		super(new FlowLayout(FlowLayout.LEFT));
		add(new JLabel(title));
		tf=new JTextField();
		tf.setText(value);
		tf.setColumns(20);
		
		tf.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if(StringValue.this.hasToBeSet&&StringValue.this.tf.getText().length()==0){
					StringValue.this.tf.setBackground(Color.red);
				} else {
					StringValue.this.tf.setBackground(new JTextField().getBackground());
				}
				if(StringValue.this.efg!=null)
					StringValue.this.efg.sayWuff();
			}
		});
		add(tf);
	}
	public boolean isValid(){
		if(tf.getText().length()==0&&hasToBeSet){
			return false;
		} else {
			return true;
		}
	}
	public Object value(){
		return tf.getText();
	}
	public void setHasToBeSet(boolean b){
		this.hasToBeSet=b;
		if(this.hasToBeSet&&tf.getText().length()==0){
			tf.setBackground(Color.red);
		} else {
			tf.setBackground(new JTextField().getBackground());
		}
	}
}

/**
*Displays a label and a Password in a JPasswordField.
*/
class PasswordValue extends EditableField{
	public JPasswordField tf;
	PasswordValue(String title,String value){
		super(new FlowLayout(FlowLayout.LEFT));
		add(new JLabel(title));
		tf=new JPasswordField();
		tf.setText(value);
		tf.setColumns(20);
		
		tf.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if(PasswordValue.this.hasToBeSet&&PasswordValue.this.tf.getPassword().length==0){
					PasswordValue.this.tf.setBackground(Color.red);
				} else {
					PasswordValue.this.tf.setBackground(new JTextField().getBackground());
				}
				if(PasswordValue.this.efg!=null)
					PasswordValue.this.efg.sayWuff();
			}
		});
		add(tf);
	}
	public boolean isValid(){
		if(tf.getPassword().length==0&&hasToBeSet){
			return false;
		} else {
			return true;
		}
	}
	public Object value(){
		return new String(tf.getPassword());
	}
	public void setHasToBeSet(boolean b){
		this.hasToBeSet=b;
		if(this.hasToBeSet&&tf.getPassword().length==0){
			tf.setBackground(Color.red);
		} else {
			tf.setBackground(new JTextField().getBackground());
		}
	}
}

/**
*Displays a label and a checkbox.
*/
class BooleanValue extends EditableField{
	public JCheckBox value;
	BooleanValue(String title,boolean state){
		super(new FlowLayout(FlowLayout.LEFT));
		value=new JCheckBox(title,state);
		add(value);
	}
	public boolean isValid(){
		return true;
	}
	public Object value(){
		return new Boolean(value.isSelected());
	}
	public void setHasToBeSet(boolean b){
		this.hasToBeSet=b;
	}
}
/**
*Displays a label and a non-editable combobox.
*/
class DropDownListValue extends EditableField{
	public JComboBox value;
	DropDownListValue(String title,Vector possibleValues){
		super(new FlowLayout(FlowLayout.LEFT));
		add(new JLabel(title));
		value=new JComboBox(possibleValues);
		value.setEditable(false);
		add(value);
	}
	public boolean isValid(){
		return true;
	}
	public Object value(){
		return value.getSelectedItem();
	}
	public void setHasToBeSet(boolean b){
		this.hasToBeSet=b;
	}
}
/**
*Displays a label, a file path in a textfield, and a "browse..." button.
*/
class OpenFileNameValue extends EditableField{
	public JTextField fileName;
	OpenFileNameValue(String title,String value){
		super(new FlowLayout(FlowLayout.LEFT));
		add(new JLabel(title));
		fileName=new JTextField();
		fileName.setColumns(20);
		fileName.setText(value);
		fileName.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if(OpenFileNameValue.this.hasToBeSet&&OpenFileNameValue.this.fileName.getText().length()==0){
					OpenFileNameValue.this.fileName.setBackground(Color.red);
				} else {
					OpenFileNameValue.this.fileName.setBackground(new JTextField().getBackground());
				}
				if(OpenFileNameValue.this.efg!=null)
					OpenFileNameValue.this.efg.sayWuff();
			}
		});
		add(fileName);
		JButton browse=new JButton("...");
		class BrowseButtonListener implements ActionListener {
			JTextField fileName;
			BrowseButtonListener(JTextField tf){
				this.fileName=tf;
			}
			public void actionPerformed(ActionEvent e){
				fileName.setText(FileChooserDialog.showChooser(CheesyKM.api,CheesyKM.getLabel("selectFile"),null,false,null));
				if(OpenFileNameValue.this.efg!=null)
					OpenFileNameValue.this.efg.sayWuff();
			}
		}
		browse.addActionListener(new BrowseButtonListener(fileName));
		add(browse);
	}
	
	public boolean isValid(){
		if(fileName.getText().length()==0&&hasToBeSet){
			fileName.setBackground(Color.red);
			return false;
		} else if(fileName.getText().length()==0&&!hasToBeSet){
			fileName.setBackground(new JTextField().getBackground());
			return true;
		} else {
			boolean resu=new File(fileName.getText()).exists();
			if(resu) resu=new File(fileName.getText()).isFile();
			if(!resu){
				fileName.setBackground(Color.red);
			} else {
				fileName.setBackground(new JTextField().getBackground());
			}
			return resu;
		}
	}
	public Object value(){
		return fileName.getText();
	}
	public void setHasToBeSet(boolean b){
		this.hasToBeSet=b;
		if(this.hasToBeSet&&fileName.getText().length()==0){
			fileName.setBackground(Color.red);
		} else {
			fileName.setBackground(new JTextField().getBackground());
		}
	}
}
/**
*Displays a date field, {@link CheesyKM#isdate(String)} is called to check if the date is valid.
*/
class DateValue extends EditableField{
	JTextField tf;
	DateValue(String title,String value){
		super(new FlowLayout(FlowLayout.LEFT));
		add(new JLabel(title));
		tf=new JTextField(value);
		tf.setColumns(10);
		class DateListener extends KeyAdapter{
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();      
				if (!((Character.isDigit(c) ||(c == KeyEvent.VK_BACK_SPACE) ||(c == KeyEvent.VK_DELETE)||(c==KeyEvent.VK_MINUS)))) {
					e.getComponent().getToolkit().beep();
					e.consume();
				}
			}
			public void keyReleased(KeyEvent e) {
				if((DateValue.this.hasToBeSet&&DateValue.this.tf.getText().length()==0)||(DateValue.this.tf.getText().length()>0&&!CheesyKM.isDate(DateValue.this.tf.getText()))){
					DateValue.this.tf.setBackground(Color.red);
				} else {
					DateValue.this.tf.setBackground(new JTextField().getBackground());
				}
				if(DateValue.this.efg!=null)
					DateValue.this.efg.sayWuff();
			}
		}
		tf.addKeyListener(new DateListener());
		add(tf);
	}
	
	public boolean isValid(){
		if(tf.getText().length()==0&&hasToBeSet){
			return false;
		} else if(tf.getText().length()>0){
			return CheesyKM.isDate(tf.getText());
		} else {
			return true;
		}
	}
	
	public Object value(){
		return tf.getText();
	}
	public void setHasToBeSet(boolean b){
		this.hasToBeSet=b;
		if(this.hasToBeSet&&tf.getText().length()==0){
			tf.setBackground(Color.red);
		} else {
			tf.setBackground(new JTextField().getBackground());
		}
	}
}

/**
*Displays a label and a String in a textarea.
*/
class BigStringValue extends EditableField{
	public JTextArea tf;
	BigStringValue(String title,String value){
		super();
		this.setLayout(new BorderLayout());
		add(new JLabel(title),"North");
		tf=new JTextArea();
		tf.setText(value);
		tf.setColumns(40);
		tf.setRows(7);
		tf.setWrapStyleWord(true);
		tf.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if(BigStringValue.this.hasToBeSet&&BigStringValue.this.tf.getText().length()==0){
					BigStringValue.this.tf.setBackground(Color.red);
				} else {
					BigStringValue.this.tf.setBackground(new JTextField().getBackground());
				}
				if(BigStringValue.this.efg!=null)
					BigStringValue.this.efg.sayWuff();
			}
		});
		add(new JScrollPane(tf),"Center");
	}
	public boolean isValid(){
		if(tf.getText().length()==0&&hasToBeSet){
			return false;
		} else {
			return true;
		}
	}
	public Object value(){
		return tf.getText();
	}
	public void setHasToBeSet(boolean b){
		this.hasToBeSet=b;
		if(this.hasToBeSet&&tf.getText().length()==0){
			tf.setBackground(Color.red);
		} else {
			tf.setBackground(new JTextField().getBackground());
		}
	}
}
