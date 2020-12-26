package org.apache.jmeter.protocol.dubbo.core;

import javax.swing.*;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.Vector;

/**
 * JAutoCompleteComboBox
 */
public class JAutoCompleteComboBox<T> extends JComboBox<T> {

    private AutoCompleter completer;

    public JAutoCompleteComboBox() {
        super();
        addCompleter();
    }

    public JAutoCompleteComboBox(ComboBoxModel cm) {
        super(cm);
        addCompleter();
    }

    public JAutoCompleteComboBox(ComboBoxModel cm,
                                 ItemListener itemListener) {
        super(cm);
        addCompleter(itemListener);
    }

    private void addCompleter(ItemListener itemListener) {
        setEditable(true);
        completer = new AutoCompleter(this, itemListener);

    }

    public JAutoCompleteComboBox(T[] items) {
        super(items);
        addCompleter();
    }

    public JAutoCompleteComboBox(List v) {
        super((Vector) v);
        addCompleter();
    }

    private void addCompleter() {
        setEditable(true);
        completer = new AutoCompleter(this);
    }

    public void autoComplete(String str) {
        this.completer.autoComplete(str, str.length());
    }

    public String getText() {
        return ((JTextField) getEditor().getEditorComponent()).getText();
    }

    public void setText(String text) {
        ((JTextField) getEditor().getEditorComponent()).setText(text);
    }

    public boolean containsItem(String itemString) {
        for (int i = 0; i < this.getModel().getSize(); i++) {
            String _item = " " + this.getModel().getElementAt(i);
            if (_item.equals(itemString))
                return true;
        }
        return false;
    }
}