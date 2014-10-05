/*

[The "BSD licence"]
Copyright (c) 2005 Jean Bovet
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

1. Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.
3. The name of the author may not be used to endorse or promote products
derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/

package edu.usfca.xj.appkit.app;

import edu.usfca.xj.appkit.frame.XJDialog;
import edu.usfca.xj.appkit.swing.XJColorChooser;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.prefs.Preferences;

public class XJPreferences {

    protected Preferences prefs = null;
    protected Map bindings = new HashMap();

    public XJPreferences(Class c) {
        this.prefs = Preferences.userNodeForPackage(c);
    }

    public void setString(String key, String value) {
        prefs.put(key, value);
    }

    public String getString(String key, String def) {
        return prefs.get(key, def);
    }

    public void setInt(String key, int value) {
        prefs.putInt(key, value);
    }

    public void setInt(String key, Integer value) {
        prefs.putInt(key, value.intValue());
    }

    public int getInt(String key, int def) {
        return prefs.getInt(key, def);
    }

    public void setBoolean(String key, boolean value) {
        prefs.putBoolean(key, value);
    }

    public boolean getBoolean(String key, boolean def) {
        return prefs.getBoolean(key, def);
    }

    public void setColor(String key, Color value) {
        setObject(key, value);
    }

    public Color getColor(String key, Color def) {
        return (Color) getObject(key, def);
    }

    public void setList(String key, List array) {
        setObject(key, array);
    }

    public List getList(String key) {
        return (List)getObject(key, null);
    }

    public void setObject(String key, Object obj) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(obj);
            out.close();
            prefs.putByteArray(key, bos.toByteArray());
        } catch(Exception e) {
            System.err.println("Cannot set the object associated with key "+key+": "+e);
        }
    }

    public Object getObject(String key, Object defaultObject) {
        try {
            byte[] bytes = prefs.getByteArray(key, null);
            if(bytes == null)
                return defaultObject;

            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes));
            Object o = in.readObject();
            in.close();
            return o;
        } catch(Exception e) {
            System.err.println("Cannot get the object associated with key "+key+": "+e);
        }
        return defaultObject;
    }

    public void remove(String key) {
        prefs.remove(key);
    }

    public Preferences getPreferences() {
        return prefs;
    }

    // *** Bindings

    public void bindToPreferences(JComboBox component, String key, int defaultValue) {
        try {
            component.setSelectedIndex(getInt(key, defaultValue));
        } catch(IllegalArgumentException e) {
            e.printStackTrace();
        }
        setInt(key, component.getSelectedIndex());

        JComboBoxBindingAction action = new JComboBoxBindingAction(component, key, true);
        bindings.put(key, action);
        component.addActionListener(action);
    }

    public void bindToPreferences(JComboBox component, String key, String defaultValue) {
        component.setSelectedItem(getString(key, defaultValue));
        setString(key, (String)component.getSelectedItem());

        JComboBoxBindingAction action = new JComboBoxBindingAction(component, key, false);
        bindings.put(key, action);
        component.addActionListener(action);
    }

    public void bindToPreferences(JSpinner component, String key, int defaultValue) {
        component.setValue(new Integer(getInt(key, defaultValue)));
        setInt(key, (Integer)component.getValue());

        JSpinnerBindingAction action = new JSpinnerBindingAction(component, key);
        bindings.put(key, action);
        component.addChangeListener(action);
    }

    public void bindToPreferences(JTextField component, String key, int defaultValue) {
        bindToPreferences(component, key, String.valueOf(defaultValue));
    }

    public void bindToPreferences(JTextField component, String key, String defaultValue) {
        component.setText(getString(key, defaultValue));
        setString(key, component.getText());

        JTextFieldBindingAction action = new JTextFieldBindingAction(component, key);
        bindings.put(key, action);
        component.addActionListener(action);
    }

    public void defaultPreference(JCheckBox component, String key, boolean defaultValue) {
        component.setSelected(defaultValue);
        setBoolean(key, component.isSelected());
    }

    public void bindToPreferences(JCheckBox component, String key, boolean defaultValue) {
        component.setSelected(getBoolean(key, defaultValue));
        setBoolean(key, component.isSelected());

        JCheckBoxBindingAction action = new JCheckBoxBindingAction(component, key);
        bindings.put(key, action);
        component.addActionListener(action);
    }

    public void bindToPreferences(JToggleButton component, String key, boolean defaultValue) {
        component.setSelected(getBoolean(key, defaultValue));
        setBoolean(key, component.isSelected());

        JToggleButtonBindingAction action = new JToggleButtonBindingAction(component, key);
        bindings.put(key, action);
        component.addActionListener(action);
    }

    public void bindToPreferences(ButtonGroup component, String key, String defaultValue) {
        component.setSelected(getButtonWithActionCommand(component, getString(key, defaultValue)).getModel(), true);
        setString(key, component.getSelection().getActionCommand());

        ButtonGroupBindingAction action = new ButtonGroupBindingAction(component, key);
        bindings.put(key, action);

        Enumeration elements = component.getElements();
        while (elements.hasMoreElements()) {
            AbstractButton button = (AbstractButton) elements.nextElement();
            button.addActionListener(action);
        }
    }

    /* Used to select a color using a JPanel as visual feedback */

    public void defaultPreference(JPanel component, String key, Color defaultValue) {
        component.setBackground(defaultValue);
        setColor(key, component.getBackground());
    }

    public void bindToPreferences(JPanel component, String key, Color defaultValue) {
        component.setBackground(getColor(key, defaultValue));
        setColor(key, component.getBackground());

        ColorChooserBindingMouseListener listener = new ColorChooserBindingMouseListener(component, key);
        bindings.put(key, listener);
        component.addMouseListener(listener);
    }

    public void applyPreferences() {
        Iterator iterator = bindings.keySet().iterator();
        while(iterator.hasNext()) {
            applyPreference((String)iterator.next());
        }
    }

    public void applyPreference(String key) {
        Object o = bindings.get(key);
        if(o instanceof ActionListener) {
            ActionListener action = (ActionListener)o;
            action.actionPerformed(null);
        } else if(o instanceof ChangeListener) {
            ChangeListener action = (ChangeListener)o;
            action.stateChanged(null);
        } else if(o instanceof MouseListener) {
            MouseListener listener = (MouseListener)o;
            listener.mousePressed(null);
        }
    }

    protected AbstractButton getButtonWithActionCommand(ButtonGroup group, String actionCommand) {
        Enumeration elements = group.getElements();
        while (elements.hasMoreElements()) {
            AbstractButton button = (AbstractButton) elements.nextElement();
            if(button.getActionCommand().equalsIgnoreCase(actionCommand))
                return button;
        }
        return null;
    }

    protected class JComboBoxBindingAction implements ActionListener {

        JComboBox component = null;
        String key = null;
        boolean index = false;

        public JComboBoxBindingAction(JComboBox component, String key, boolean index) {
            this.component = component;
            this.key = key;
            this.index = index;
        }

        public void actionPerformed(ActionEvent e) {
            if(index)
                setInt(key, component.getSelectedIndex());
            else
                setString(key, (String)component.getSelectedItem());
        }
    }

    protected class JTextFieldBindingAction implements ActionListener {

        JTextField component = null;
        String key = null;

        public JTextFieldBindingAction(JTextField component, String key) {
            this.component = component;
            this.key = key;
        }

        public void actionPerformed(ActionEvent e) {
            setString(key, component.getText());
        }
    }

    protected class JCheckBoxBindingAction implements ActionListener {

        JCheckBox component = null;
        String key = null;

        public JCheckBoxBindingAction(JCheckBox component, String key) {
            this.component = component;
            this.key = key;
        }

        public void actionPerformed(ActionEvent e) {
            setBoolean(key, component.isSelected());
        }
    }

    protected class JToggleButtonBindingAction implements ActionListener {

        JToggleButton component = null;
        String key = null;

        public JToggleButtonBindingAction(JToggleButton component, String key) {
            this.component = component;
            this.key = key;
        }

        public void actionPerformed(ActionEvent e) {
            setBoolean(key, component.isSelected());
        }
    }

    protected class ButtonGroupBindingAction implements ActionListener {

        ButtonGroup component = null;
        String key = null;

        public ButtonGroupBindingAction(ButtonGroup component, String key) {
            this.component = component;
            this.key = key;
        }

        public void actionPerformed(ActionEvent e) {
            setString(key, component.getSelection().getActionCommand());
        }
    }

    protected class JSpinnerBindingAction implements ChangeListener {

        JSpinner component = null;
        String key = null;

        public JSpinnerBindingAction(JSpinner component, String key) {
            this.component = component;
            this.key = key;
        }

        public void stateChanged(ChangeEvent e) {
            setInt(key, (Integer)component.getValue());
        }
    }

    protected class ColorChooserBindingMouseListener extends MouseAdapter {

        JPanel component = null;
        String key = null;

        public ColorChooserBindingMouseListener(JPanel component, String key) {
            this.component = component;
            this.key = key;
        }

        public void mousePressed(MouseEvent e) {
            if(e == null) {
                setColor(key, component.getBackground());
            } else {
                XJColorChooser cc = new XJColorChooser(component.getParent(), true, component);
                if(cc.runModal() == XJDialog.BUTTON_OK) {
                    setColor(key, cc.getColor());
                }
            }
        }
    }

}
