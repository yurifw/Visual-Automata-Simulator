/*

[The "BSD licence"]
Copyright (c) 2004 Jean Bovet
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

package edu.usfca.vas.window.fa;

import edu.usfca.vas.app.Localized;
import edu.usfca.vas.data.DataWrapperFA;
import edu.usfca.vas.graphics.fa.GViewFAMachine;
import edu.usfca.vas.machine.fa.FAMachine;
import edu.usfca.vas.window.WindowMachineAbstract;
import edu.usfca.vas.window.tools.DesignToolsFA;
import edu.usfca.xj.appkit.frame.XJFrame;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WindowMachineFA extends WindowMachineAbstract {

    protected WindowMachineFASettings settings = null;

    protected JTextField alphabetTextField;
    protected JTextField stringTextField;
    protected JComboBox typeComboBox;
    protected JPanel mainPanel;
    protected JSplitPane mainPanelSplit;
    protected JScrollPane mainPanelScrollPane;

    protected DesignToolsFA designToolFA;

    protected WindowMachineFAOverlay overlay;
    protected boolean overlayVisible;

    public WindowMachineFA(XJFrame parent) {
        super(parent);
    }

    public void init() {
        setGraphicPanel(new GViewFAMachine(parent));
        getFAGraphicPanel().setDelegate(this);
        getFAGraphicPanel().setMachine(getDataWrapperFA().getGraphicMachine());
        getFAGraphicPanel().setRealSize(getDataWrapperFA().getSize());

        setLayout(new BorderLayout());

        add(createUpperPanel(), BorderLayout.NORTH);
        add(createAutomataPanel(), BorderLayout.CENTER);

        overlay = new WindowMachineFAOverlay(parent.getJFrame(), mainPanel);
        overlay.setStringField(stringTextField);
    }

    public WindowFA getWindowFA() {
        return (WindowFA)getWindow();
    }

    public DataWrapperFA getDataWrapperFA() {
        return (DataWrapperFA)getDataWrapper();
    }

    public GViewFAMachine getFAGraphicPanel() {
        return (GViewFAMachine)getGraphicPanel();
    }

    public JPanel createUpperPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setMaximumSize(new Dimension(99999, 30));

        panel.add(designToolFA = new DesignToolsFA(), BorderLayout.WEST);
        panel.add(createControlPanel(), BorderLayout.EAST);

        getFAGraphicPanel().setDesignToolsPanel(designToolFA);

        return panel;
    }

    public JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setMaximumSize(new Dimension(99999, 30));

        panel.add(new JLabel(Localized.getString("faWMAutomaton")));
        typeComboBox = new JComboBox(new String[] { Localized.getString("DFA"),
                                                    Localized.getString("NFA") });
        typeComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int type = typeComboBox.getSelectedIndex();
                if(type !=getDataWrapperFA().getMachineType()) {
                    getDataWrapperFA().setMachineType(type);
                    changeOccured();
                }
            }
        });

        panel.add(typeComboBox);

        panel.add(new JLabel(Localized.getString("faWMAlphabet")));

        alphabetTextField = new JTextField(getDataWrapperFA().getSymbolsString());
        alphabetTextField.setPreferredSize(new Dimension(100, 20));
        alphabetTextField.addCaretListener(new CaretListener() {
            public void caretUpdate(CaretEvent e) {
                handleAlphabetTextFieldEvent();
            }

        });

        alphabetTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleAlphabetTextFieldEvent();
            }
        });

        panel.add(alphabetTextField);

        panel.add(new JLabel(Localized.getString("faWMString")));

        stringTextField = new JTextField("");
        stringTextField.setPreferredSize(new Dimension(100, 20));
        stringTextField.addCaretListener(new CaretListener() {
            public void caretUpdate(CaretEvent e) {
                handleStringTextFieldEvent();
            }

        });

        stringTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleStringTextFieldEvent();
            }
        });

        panel.add(stringTextField);

        JButton start = new JButton(Localized.getString("faWMRun"));
        start.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getWindowFA().run();
            }
        });

        panel.add(start);

        return panel;
    }

    public JComponent createAutomataPanel() {
        mainPanelScrollPane = new JScrollPane(getGraphicPanel());
        mainPanelScrollPane.setPreferredSize(new Dimension(640, 480));
        mainPanelScrollPane.setWheelScrollingEnabled(true);

        mainPanelSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainPanelSplit.setContinuousLayout(true);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(mainPanelScrollPane, BorderLayout.CENTER);

        return mainPanel;
    }

    public boolean supportsOverlay() {
        return true;
    }

    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if(visible && overlayVisible)
            overlay.setVisible(true);
        else if(!visible && overlayVisible)
            overlay.setVisible(false);
    }

    public boolean isOverlayVisible() {
        return overlay.isVisible();
    }

    public void toggleOverlayVisibility() {
        overlay.setVisible(!overlay.isVisible());
        overlayVisible = overlay.isVisible();
    }

    // *** Event methods

    public void handleAlphabetTextFieldEvent() {
        String s = alphabetTextField.getText();
        if(!s.equals(getDataWrapperFA().getSymbolsString())) {
            getDataWrapperFA().setSymbolsString(s);
            changeOccured();
        }
    }

    public void handleStringTextFieldEvent() {
        String s = stringTextField.getText();
        if(!s.equals(getDataWrapperFA().getString())) {
            getDataWrapperFA().setString(s);
            overlay.textChanged();
            changeOccured();
        }
    }

    public String getString() {
        return stringTextField.getText();
    }

    // *** Public methods

    public FAMachine convertNFA2DFA() {
        return getDataWrapperFA().getMachine().convertNFA2DFA();
    }

    public void setFAMachine(FAMachine machine) {
        getDataWrapperFA().setMachine(machine);
        getDataWrapperFA().getGraphicMachine().setMachine(machine);
        getDataWrapperFA().getGraphicMachine().reconstruct();

        getFAGraphicPanel().setMachine(getDataWrapperFA().getGraphicMachine());
        getFAGraphicPanel().centerAll();
        getFAGraphicPanel().repaint();
    }

    public void rebuild() {
        super.rebuild();
        getFAGraphicPanel().setMachine(getDataWrapperFA().getGraphicMachine());
        typeComboBox.setSelectedIndex(getDataWrapperFA().getMachineType());
        alphabetTextField.setText(getDataWrapperFA().getSymbolsString());
        stringTextField.setText(getDataWrapperFA().getString());
    }

    public void setTitle(String title) {
        getWindowFA().setWindowMachineTitle(this, title);
        getDataWrapperFA().setName(title);
        changeOccured();        
    }

    public String getTitle() {
        return getWindowFA().getWindowMachineTitle(this);
    }

    public void displaySettings() {
        if(settings == null)
            settings = new WindowMachineFASettings(this);

        settings.display();
    }

    public void setGraphicsSize(int dx, int dy) {
        getGraphicPanel().setRealSize(dx, dy);
        getDataWrapperFA().setSize(new Dimension(dx, dy));
        changeOccured();
    }

    public Dimension getGraphicSize() {
        return getGraphicPanel().getRealSize();
    }

    public void setDebugInfo(String remaining) {
        String original = stringTextField.getText();
        overlay.setString(original, original.length()-remaining.length());
    }

    public void viewSizeDidChange() {
        // do nothing
    }
}
