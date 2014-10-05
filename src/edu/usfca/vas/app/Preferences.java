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

package edu.usfca.vas.app;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.*;
import edu.usfca.xj.appkit.app.XJApplication;
import edu.usfca.xj.appkit.app.XJPreferences;
import edu.usfca.xj.appkit.frame.XJPanel;

import javax.swing.*;
import java.awt.*;

public class Preferences extends XJPanel {

    public static final String PREF_STARTUP_ACTION = "PREF_STARTUP_ACTION";
    public static final String PREF_ALERT_CLOSE_MACHINE = "PREF_ALERT_CLOSE_MACHINE";
    public static final String PREF_DEFAULT_GRAPHIC_WIDTH = "PREF_DEFAULT_GRAPHIC_WIDTH";
    public static final String PREF_DEFAULT_GRAPHIC_HEIGHT = "PREF_DEFAULT_GRAPHIC_HEIGHT";
    public static final String PREF_EPSILON_TRANSITION = "PREF_EPSILON_TRANSITION";
    public static final String PREF_INFINITE_TM_STEPS = "PREF_INFINITE_TM_STEPS";

    public static final int STARTUP_DO_NOTHING = 0;
    public static final int STARTUP_NEW_FA_DOC = 1;
    public static final int STARTUP_NEW_TM_DOC = 2;
    public static final int STARTUP_OPEN_LAST_DOC = 3;

    public static final boolean DEFAULT_ALERT_CLOSE_MACHINE = true;
    public static final int DEFAULT_GRAPHIC_WIDTH = 1024;
    public static final int DEFAULT_GRAPHIC_HEIGHT = 768;
    public static final String DEFAULT_EPSILON_TRANSITION = "e";
    public static final int DEFAULT_INFINITE_TM_STEPS = 10000;

	public Preferences() {
		initComponents();

        // General TAB

        getPreferences().bindToPreferences(startupActionComboBox, PREF_STARTUP_ACTION, STARTUP_OPEN_LAST_DOC);
        getPreferences().bindToPreferences(alertCloseMachineCheckBox, PREF_ALERT_CLOSE_MACHINE, DEFAULT_ALERT_CLOSE_MACHINE);
        getPreferences().bindToPreferences(defaultWidthField, PREF_DEFAULT_GRAPHIC_WIDTH, DEFAULT_GRAPHIC_WIDTH);
        getPreferences().bindToPreferences(defaultHeightField, PREF_DEFAULT_GRAPHIC_HEIGHT, DEFAULT_GRAPHIC_HEIGHT);

        // Machine TAB

        getPreferences().bindToPreferences(epsilonTransitionField, PREF_EPSILON_TRANSITION, DEFAULT_EPSILON_TRANSITION);
        getPreferences().bindToPreferences(infiniteTMStepsField, PREF_INFINITE_TM_STEPS, DEFAULT_INFINITE_TM_STEPS);
	}

    public void close() {
        dialogPane.requestFocus();
        getPreferences().applyPreferences();
        super.close();
    }

    public static int getStartupAction() {
        return getPreferences().getInt(PREF_STARTUP_ACTION, STARTUP_OPEN_LAST_DOC);
    }

    public static boolean getAlertOnCloseMachine() {
        return getPreferences().getBoolean(PREF_ALERT_CLOSE_MACHINE, DEFAULT_ALERT_CLOSE_MACHINE);
    }

    public static int getDefaultGraphicWidth() {
        return getPreferences().getInt(PREF_DEFAULT_GRAPHIC_WIDTH, DEFAULT_GRAPHIC_WIDTH);
    }

    public static int getDefaultGraphicHeight() {
        return getPreferences().getInt(PREF_DEFAULT_GRAPHIC_HEIGHT, DEFAULT_GRAPHIC_HEIGHT);
    }

    public static String getEpsilonTransition() {
        return getPreferences().getString(PREF_EPSILON_TRANSITION, DEFAULT_EPSILON_TRANSITION);
    }

    public static int getInfiniteTMSteps() {
        return getPreferences().getInt(PREF_INFINITE_TM_STEPS, DEFAULT_INFINITE_TM_STEPS);
    }

    private static XJPreferences getPreferences() {
        return XJApplication.shared().getPreferences();
    }

    // ** JFormDesigner

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPane = new JPanel();
        tabbedPane1 = new JTabbedPane();
        panel1 = new JPanel();
        label1 = new JLabel();
        startupActionComboBox = new JComboBox();
        alertCloseMachineCheckBox = new JCheckBox();
        label6 = new JLabel();
        label2 = new JLabel();
        defaultWidthField = new JTextField();
        label3 = new JLabel();
        defaultHeightField = new JTextField();
        panel3 = new JPanel();
        label7 = new JLabel();
        epsilonTransitionField = new JTextField();
        label4 = new JLabel();
        infiniteTMStepsField = new JTextField();
        label5 = new JLabel();
        CellConstraints cc = new CellConstraints();

        //======== this ========
        setTitle("Preferences");
        Container contentPane2 = getContentPane();
        contentPane2.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(Borders.DIALOG_BORDER);
            dialogPane.setMinimumSize(new Dimension(500, 300));
            dialogPane.setPreferredSize(new Dimension(650, 300));
            dialogPane.setRequestFocusEnabled(true);
            dialogPane.setLayout(new BorderLayout());

            //======== contentPane ========
            {
                contentPane.setLayout(new FormLayout(
                    "default:grow, 39dlu",
                    "fill:default:grow"));

                //======== tabbedPane1 ========
                {
                    tabbedPane1.setBorder(null);

                    //======== panel1 ========
                    {
                        panel1.setBorder(null);
                        panel1.setLayout(new FormLayout(
                            new ColumnSpec[] {
                                new ColumnSpec(Sizes.dluX(10)),
                                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                new ColumnSpec("max(default;50dlu)"),
                                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                new ColumnSpec(ColumnSpec.FILL, Sizes.dluX(40), FormSpec.DEFAULT_GROW),
                                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
                            },
                            new RowSpec[] {
                                new RowSpec(Sizes.dluY(10)),
                                FormFactory.LINE_GAP_ROWSPEC,
                                FormFactory.DEFAULT_ROWSPEC,
                                FormFactory.LINE_GAP_ROWSPEC,
                                new RowSpec(Sizes.dluY(10)),
                                FormFactory.LINE_GAP_ROWSPEC,
                                new RowSpec(Sizes.dluY(10)),
                                FormFactory.LINE_GAP_ROWSPEC,
                                new RowSpec(RowSpec.BOTTOM, Sizes.DEFAULT, FormSpec.NO_GROW),
                                FormFactory.LINE_GAP_ROWSPEC,
                                FormFactory.DEFAULT_ROWSPEC,
                                FormFactory.LINE_GAP_ROWSPEC,
                                FormFactory.DEFAULT_ROWSPEC
                            }));

                        //---- label1 ----
                        label1.setHorizontalAlignment(SwingConstants.RIGHT);
                        label1.setText("At startup:");
                        panel1.add(label1, cc.xy(3, 3));

                        //---- startupActionComboBox ----
                        startupActionComboBox.setModel(new DefaultComboBoxModel(new String[] {
                            "Do nothing",
                            "Create a new FA document",
                            "Create a new TM document",
                            "Open the last used document"
                        }));
                        startupActionComboBox.setSelectedIndex(0);
                        panel1.add(startupActionComboBox, cc.xy(5, 3));

                        //---- alertCloseMachineCheckBox ----
                        alertCloseMachineCheckBox.setText("Display alert when closing a machine");
                        panel1.add(alertCloseMachineCheckBox, cc.xywh(5, 5, 3, 1));

                        //---- label6 ----
                        label6.setText("Default Graphics Zone Size:");
                        panel1.add(label6, cc.xywh(3, 9, 3, 1));

                        //---- label2 ----
                        label2.setHorizontalAlignment(SwingConstants.RIGHT);
                        label2.setText("Width:");
                        panel1.add(label2, cc.xy(3, 11));
                        panel1.add(defaultWidthField, cc.xy(5, 11));

                        //---- label3 ----
                        label3.setHorizontalAlignment(SwingConstants.RIGHT);
                        label3.setText("Height:");
                        panel1.add(label3, cc.xy(3, 13));
                        panel1.add(defaultHeightField, cc.xy(5, 13));
                    }
                    tabbedPane1.addTab("General", panel1);

                    //======== panel3 ========
                    {
                        panel3.setLayout(new FormLayout(
                            new ColumnSpec[] {
                                new ColumnSpec(Sizes.dluX(10)),
                                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                new ColumnSpec(ColumnSpec.RIGHT, Sizes.DEFAULT, FormSpec.NO_GROW),
                                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
                                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                FormFactory.DEFAULT_COLSPEC,
                                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                new ColumnSpec(Sizes.dluX(10))
                            },
                            new RowSpec[] {
                                new RowSpec(Sizes.dluY(10)),
                                FormFactory.LINE_GAP_ROWSPEC,
                                FormFactory.DEFAULT_ROWSPEC,
                                FormFactory.LINE_GAP_ROWSPEC,
                                FormFactory.DEFAULT_ROWSPEC
                            }));

                        //---- label7 ----
                        label7.setText("An epsilon transition is indicated by the letter");
                        panel3.add(label7, cc.xy(3, 3));

                        //---- epsilonTransitionField ----
                        epsilonTransitionField.setText("e");
                        panel3.add(epsilonTransitionField, cc.xy(5, 3));

                        //---- label4 ----
                        label4.setText("Consider that a TM will run forever after more than");
                        panel3.add(label4, cc.xy(3, 5));

                        //---- infiniteTMStepsField ----
                        infiniteTMStepsField.setHorizontalAlignment(JTextField.RIGHT);
                        infiniteTMStepsField.setText("200");
                        panel3.add(infiniteTMStepsField, cc.xy(5, 5));

                        //---- label5 ----
                        label5.setText("continuous steps");
                        panel3.add(label5, cc.xy(7, 5));
                    }
                    tabbedPane1.addTab("Machine", panel3);
                }
                contentPane.add(tabbedPane1, cc.xywh(1, 1, 2, 1));
            }
            dialogPane.add(contentPane, BorderLayout.CENTER);
        }
        contentPane2.add(dialogPane, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPane;
    private JTabbedPane tabbedPane1;
    private JPanel panel1;
    private JLabel label1;
    private JComboBox startupActionComboBox;
    private JCheckBox alertCloseMachineCheckBox;
    private JLabel label6;
    private JLabel label2;
    private JTextField defaultWidthField;
    private JLabel label3;
    private JTextField defaultHeightField;
    private JPanel panel3;
    private JLabel label7;
    private JTextField epsilonTransitionField;
    private JLabel label4;
    private JTextField infiniteTMStepsField;
    private JLabel label5;
    // JFormDesigner - End of variables declaration  //GEN-END:variables



}
