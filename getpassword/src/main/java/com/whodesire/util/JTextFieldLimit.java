package com.whodesire.util;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class JTextFieldLimit extends PlainDocument{
	private static final long serialVersionUID = -2670005099307922406L;
	
	private int limit;
	private boolean upper = false;
	
	public JTextFieldLimit(int limit){
		super();
		this.limit = limit;
	}
	
	public JTextFieldLimit(int limit, boolean upper){
		super();
		this.limit = limit;
		this.upper = upper;
	}
	
	@Override
	public void insertString(final int offset, String str, final AttributeSet attr) throws BadLocationException{
		if (str == null)
			return;
		if(upper)
			str = str.toUpperCase();
		if((getLength() + str.length()) <= limit){
			super.insertString(offset, str, attr);
		}
	}
	
	@Override
	public void replace(final int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
		if (text == null)
			return;
		if(upper)
			text = text.toUpperCase();
		if((getLength()+ text.length()) <= limit){
			super.insertString(offset, text, attrs);
		}
	}
}
