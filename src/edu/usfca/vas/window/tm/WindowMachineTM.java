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

package edu.usfca.vas.window.tm;

import edu.usfca.vas.app.Localized;
import edu.usfca.vas.data.DataWrapperTM;
import edu.usfca.vas.graphics.tm.GViewTMMachine;
import edu.usfca.vas.machine.tm.TMMachine;
import edu.usfca.vas.window.WindowMachineAbstract;
import edu.usfca.vas.window.tools.DesignToolsTM;
import edu.usfca.xj.appkit.frame.XJFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WindowMachineTM extends WindowMachineAbstract {

    private WindowMachineTMSettings settings = null;

    private JScrollPane mainPanelScrollPane;
    private DesignToolsTM designToolTM;

    public WindowMachineTM(XJFrame parent) {
        super(parent);
    }

    public void init() {
        setGraphicPanel(new GViewTMMachine());
        getGraphicPanel().setDelegate(this);
        getTMGraphicPanel().setMachine(getDataWrapperTM().getGraphicMachine());
        getGraphicPanel().setRealSize(getDataWrapperTM().getSize());

        setLayout(new BorderLayout());
        add(createControlPanel(), BorderLayout.NORTH);
        add(createAutomataPanel(), BorderLayout.CENTER);
    }

    public WindowTM getWindowTM() {
        return (WindowTM)getWindow();
    }

    public DataWrapperTM getDataWrapperTM() {
        return (DataWrapperTM)getDataWrapper();
    }

    public GViewTMMachine getTMGraphicPanel() {
        return (GViewTMMachine)getGraphicPanel();
    }

    public JPanel createControlPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setMaximumSize(new Dimension(99999, 30));

        panel.add(designToolTM = new DesignToolsTM(), BorderLayout.WEST);
        panel.add(createRunPanel(), BorderLayout.EAST);

        getTMGraphicPanel().setDesignToolsPanel(designToolTM);

        return panel;
    }

    public JPanel createRunPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        JButton start = new JButton(Localized.getString("tmWMRun"));
        start.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getWindowTM().run();
            }
        });
        panel.add(start);
        return panel;
    }

    public JComponent createAutomataPanel() {
        mainPanelScrollPane = new JScrollPane(getGraphicPanel());
        mainPanelScrollPane.setPreferredSize(new Dimension(640, 480));
        mainPanelScrollPane.setWheelScrollingEnabled(true);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(mainPanelScrollPane, BorderLayout.CENTER);

        return panel;
    }

    // *** Event methods

    public TMMachine getTMMachine() {
        return getDataWrapperTM().getTMMachine();
    }

    // *** Public methods

    public void clear() {
        getTMGraphicPanel().repaint();
    }

    public void rebuild() {
        super.rebuild();
        getTMGraphicPanel().setMachine(getDataWrapperTM().getGraphicMachine());
    }

    public void setTitle(String title) {
        getWindowTM().refactorRenameMachine(getDataWrapperTM().getName(), title);

        getWindowTM().setWindowMachineTitle(this, title);
        getTMGraphicPanel().getMachine().getMachine().setName(title);
        getDataWrapperTM().setName(title);
        changeOccured();
    }

    public String getTitle() {
        return getWindowTM().getWindowMachineTitle(this);
    }

    public void machineDidRename(String oldName, String newName) {
        getTMGraphicPanel().getMachine().machineDidRename(oldName, newName);
    }

    public void displaySettings() {
        if(settings == null)
            settings = new WindowMachineTMSettings(this);

        settings.display();
    }

    public void setGraphicsSize(int dx, int dy) {
        getGraphicPanel().setRealSize(dx, dy);
        getDataWrapperTM().setSize(new Dimension(dx, dy));
        changeOccured();
    }

    public Dimension getGraphicsSize() {
        return getGraphicPanel().getRealSize();
    }

    public void viewSizeDidChange() {
        // do nothing
    }
}
