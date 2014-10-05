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

package edu.usfca.vas.window.batch;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.*;
import edu.usfca.vas.app.Localized;
import edu.usfca.vas.machine.tm.TMMachine;
import edu.usfca.vas.window.tm.TapeConfiguration;
import edu.usfca.vas.window.tm.WindowTM;
import edu.usfca.xj.appkit.frame.XJDialog;
import edu.usfca.xj.appkit.utils.XJFileChooser;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BatchTestTMSettings extends XJDialog {

    private BatchTestTM batch = new BatchTestTM(this);

    private WindowTM windowTM = null;
    private List tapeConfigurations = null;
    private AbstractTableModel tableModel = null;
    private List testItems = new ArrayList();

    public BatchTestTMSettings(WindowTM window) {
        super(window.getJavaContainer(), true);

        this.windowTM = window;
        this.tapeConfigurations = windowTM.getTapeConfigurations();

        initComponents();

        initBeforeTapeComponents();
        initAfterTapeComponents();

        initFileTableComponents();

        initRunComponents();

        setDefaultButton(runTestButton);        
        setSize(800, 600);
        center();
    }

    public void runTests() {
        batch.setTestItems(testItems);

        if(runMachineNamedRadio.isSelected()) {
            batch.setRunMachineName((String)runMachineNamedCombo.getSelectedItem());
            batch.setRunMachineAtIndex(-1);
        } else {
            batch.setRunMachineName(null);
            Integer i = (Integer)runMachineAtIndexSpinner.getValue();
            batch.setRunMachineAtIndex(i.intValue());
        }

        batch.setInitialTapeContent((String)beforeTapeContentCombo.getSelectedItem());
        if(beforeTapeBeginPositionRadio.isSelected())
            batch.setInitialTapeHeadPosition(0);
        else {
            Integer i = (Integer)beforeTapeCustomPositionSpinner.getValue();
            batch.setInitialTapeHeadPosition(i.intValue());
        }

        batch.setTestTapeContent(afterTapeContentTextField.getText());
        if(afterTapeBeginPositionRadio.isSelected())
            batch.setTestTapeHeadPosition(0);
        else if(afterTapeIgnorePositionRadio.isSelected())
            batch.setTestTapeHeadPosition(BatchTestTM.IGNORE);
        else {
            Integer i = (Integer)afterTapeCustomPositionSpinner.getValue();
            batch.setTestTapeHeadPosition(i.intValue());
        }

        batch.runTests();
    }

    private void initRunComponents() {
        runMachineNamedCombo.setEnabled(true);
        runMachineAtIndexSpinner.setEnabled(false);

        runMachineNamedCombo.removeAllItems();

        Iterator iterator = windowTM.getAllTMMachine().iterator();
        while(iterator.hasNext()) {
            TMMachine machine = (TMMachine)iterator.next();
            runMachineNamedCombo.addItem(machine.getName());
        }

        runTestButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                runTests();
                tableModel.fireTableDataChanged();
            }
        });

        runMachineNamedRadio.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(runMachineNamedRadio.isSelected()) {
                    runMachineNamedCombo.setEnabled(true);
                    runMachineAtIndexSpinner.setEnabled(false);
                }
            }
        });

        runMachineAtIndexRadio.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(runMachineAtIndexRadio.isSelected()) {
                    runMachineNamedCombo.setEnabled(false);
                    runMachineAtIndexSpinner.setEnabled(true);
                }
            }
        });

        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });
    }

    private void initBeforeTapeComponents() {
        beforeTapeCustomPositionSpinner.setEnabled(false);

        Iterator iterator = tapeConfigurations.iterator();
        while(iterator.hasNext()) {
            TapeConfiguration conf = (TapeConfiguration)iterator.next();
            beforeTapeContentCombo.addItem(conf.content);
        }

        beforeTapeContentCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int index = beforeTapeContentCombo.getSelectedIndex();
                if(index>=0) {
                    TapeConfiguration conf = (TapeConfiguration)tapeConfigurations.get(index);
                    if(conf.position == 0) {
                        beforeTapeBeginPositionRadio.setSelected(true);
                        afterTapeCustomPositionSpinner.setEnabled(false);
                    } else {
                        beforeTapeCustomPositionRadio.setSelected(true);
                        afterTapeCustomPositionSpinner.setEnabled(true);
                        beforeTapeCustomPositionSpinner.setValue(new Integer(conf.position));
                    }
                }
            }
        });

        beforeTapeBeginPositionRadio.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(beforeTapeBeginPositionRadio.isSelected())
                    beforeTapeCustomPositionSpinner.setEnabled(false);
            }
        });

        beforeTapeCustomPositionRadio.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(beforeTapeCustomPositionRadio.isSelected())
                    beforeTapeCustomPositionSpinner.setEnabled(true);
            }
        });
    }

    private void initAfterTapeComponents() {
        afterTapeCustomPositionSpinner.setEnabled(false);

        afterTapeBeginPositionRadio.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(afterTapeBeginPositionRadio.isSelected())
                    afterTapeCustomPositionSpinner.setEnabled(false);
            }
        });

        afterTapeCustomPositionRadio.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(afterTapeCustomPositionRadio.isSelected())
                    afterTapeCustomPositionSpinner.setEnabled(true);
            }
        });

        afterTapeIgnorePositionRadio.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(afterTapeIgnorePositionRadio.isSelected())
                    afterTapeCustomPositionSpinner.setEnabled(false);
            }
        });
    }

    private void initFileTableComponents() {
        filesTestTable.setDefaultRenderer(Object.class, new ColorRenderer());
        filesTestTable.setModel(tableModel = new AbstractTableModel() {
            public int getColumnCount() {
                return 5;
            }

            public int getRowCount() {
                return testItems == null?0:testItems.size();
            }

            public boolean isCellEditable(int row, int col) {
                return col == 0;
            }

            public String getColumnName(int column) {
                return BatchTestTMItem.getValueName(column);
            }

            public Object getValueAt(int row, int col) {
                return ((BatchTestTMItem)testItems.get(row)).getValue(col);
            }

            public void setValueAt(Object value, int row, int col) {
                ((BatchTestTMItem)testItems.get(row)).setValue(col, (String)value);
            }
        });

        addFilesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                XJFileChooser chooser = XJFileChooser.shared();
                chooser.displayOpenDialog(null, "*.tm", Localized.getString("documentTMDescription"), true);
                Iterator iterator = chooser.getSelectedFilePaths().iterator();
                while(iterator.hasNext()) {
                    testItems.add(new BatchTestTMItem((String)iterator.next()));
                }
                tableModel.fireTableDataChanged();
            }
        });

        removeFilesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int[] rows = filesTestTable.getSelectedRows();
                for(int i=rows.length-1; i>=0; i--)
                    testItems.remove(rows[i]);
                tableModel.fireTableDataChanged();
            }
        });

        removeAllFilesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                testItems.clear();
                tableModel.fireTableDataChanged();
            }
        });
    }

    public class ColorRenderer extends DefaultTableCellRenderer {

        public ColorRenderer() {
            setOpaque(true); //MUST do this for background to show up.
        }

        public Component getTableCellRendererComponent(
                JTable table, Object value,
                boolean isSelected, boolean hasFocus,
                int row, int column)
        {
            BatchTestTMItem item = (BatchTestTMItem)testItems.get(row);
            setForeground(item.failed?Color.red:Color.black);
            if(item.failed) {
                setToolTipText(item.result);
            } else {
                setToolTipText(null);
            }

            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        DefaultComponentFactory compFactory = DefaultComponentFactory.getInstance();
        dialogPane = new JPanel();
        contentPane = new JPanel();
        goodiesFormsSeparator4 = compFactory.createSeparator("Run Machine");
        panel4 = new JPanel();
        runMachineNamedRadio = new JRadioButton();
        runMachineNamedCombo = new JComboBox();
        runMachineAtIndexRadio = new JRadioButton();
        runMachineAtIndexSpinner = new JSpinner();
        separator3 = new JSeparator();
        goodiesFormsSeparator2 = compFactory.createSeparator("Tape Configuration Before Execution");
        scrollPane1 = new JScrollPane();
        filesTestTable = new JTable();
        panel1 = new JPanel();
        addFilesButton = new JButton();
        removeFilesButton = new JButton();
        removeAllFilesButton = new JButton();
        label1 = new JLabel();
        beforeTapeContentCombo = new JComboBox();
        label22 = new JLabel();
        panel2 = new JPanel();
        beforeTapeBeginPositionRadio = new JRadioButton();
        beforeTapeCustomPositionRadio = new JRadioButton();
        beforeTapeCustomPositionSpinner = new JSpinner();
        separator1 = new JSeparator();
        goodiesFormsSeparator3 = compFactory.createSeparator("Tape Configuration After Execution");
        label2 = new JLabel();
        afterTapeContentTextField = new JTextField();
        label222 = new JLabel();
        panel3 = new JPanel();
        afterTapeBeginPositionRadio = new JRadioButton();
        afterTapeCustomPositionRadio = new JRadioButton();
        afterTapeCustomPositionSpinner = new JSpinner();
        afterTapeIgnorePositionRadio = new JRadioButton();
        separator2 = new JSeparator();
        goodiesFormsSeparator1 = compFactory.createSeparator("File(s)");
        buttonBar = new JPanel();
        closeButton = new JButton();
        runTestButton = new JButton();
        CellConstraints cc = new CellConstraints();

        //======== this ========
        setTitle("Batch Tests");
        Container contentPane2 = getContentPane();
        contentPane2.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(Borders.DIALOG_BORDER);
            dialogPane.setLayout(new BorderLayout());

            //======== contentPane ========
            {
                contentPane.setLayout(new FormLayout(
                    new ColumnSpec[] {
                        FormFactory.DEFAULT_COLSPEC,
                        FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                        new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
                        FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                        FormFactory.DEFAULT_COLSPEC
                    },
                    new RowSpec[] {
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        new RowSpec(Sizes.dluY(10)),
                        FormFactory.LINE_GAP_ROWSPEC,
                        new RowSpec(Sizes.dluY(15)),
                        FormFactory.LINE_GAP_ROWSPEC,
                        new RowSpec(Sizes.dluY(10)),
                        FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        new RowSpec(Sizes.dluY(10)),
                        FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        new RowSpec(Sizes.dluY(15)),
                        FormFactory.LINE_GAP_ROWSPEC,
                        new RowSpec(Sizes.dluY(10)),
                        FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        new RowSpec(Sizes.dluY(10)),
                        FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        new RowSpec(Sizes.dluY(15)),
                        FormFactory.LINE_GAP_ROWSPEC,
                        new RowSpec(Sizes.dluY(10)),
                        FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        new RowSpec(Sizes.dluY(10)),
                        FormFactory.LINE_GAP_ROWSPEC,
                        new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
                        FormFactory.LINE_GAP_ROWSPEC,
                        new RowSpec(Sizes.dluY(10))
                    }));
                contentPane.add(goodiesFormsSeparator4, cc.xywh(1, 1, 5, 1));

                //======== panel4 ========
                {
                    panel4.setPreferredSize(new Dimension(0, 32));
                    panel4.setLayout(new BoxLayout(panel4, BoxLayout.X_AXIS));

                    //---- runMachineNamedRadio ----
                    runMachineNamedRadio.setSelected(true);
                    runMachineNamedRadio.setText("Named:");
                    panel4.add(runMachineNamedRadio);

                    //---- runMachineNamedCombo ----
                    runMachineNamedCombo.setEditable(true);
                    runMachineNamedCombo.setMaximumSize(new Dimension(32767, 22));
                    runMachineNamedCombo.setMinimumSize(new Dimension(80, 22));
                    runMachineNamedCombo.setModel(new DefaultComboBoxModel(new String[] {
                        "main"
                    }));
                    runMachineNamedCombo.setToolTipText("Name of the machine to run");
                    panel4.add(runMachineNamedCombo);

                    //---- runMachineAtIndexRadio ----
                    runMachineAtIndexRadio.setText("At index:");
                    runMachineAtIndexRadio.setToolTipText("Index of the machine to run (0-based index)");
                    panel4.add(runMachineAtIndexRadio);

                    //---- runMachineAtIndexSpinner ----
                    runMachineAtIndexSpinner.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
                    panel4.add(runMachineAtIndexSpinner);

                    //---- separator3 ----
                    separator3.setBackground(SystemColor.window);
                    separator3.setForeground(SystemColor.window);
                    panel4.add(separator3);
                }
                contentPane.add(panel4, cc.xywh(1, 5, 4, 1));
                contentPane.add(goodiesFormsSeparator2, cc.xywh(1, 9, 5, 1));

                //======== scrollPane1 ========
                {
                    scrollPane1.setPreferredSize(new Dimension(454, 100));

                    //---- filesTestTable ----
                    filesTestTable.setModel(new DefaultTableModel(
                        new Object[][] {
                            {"", "", null},
                            {null, null, null},
                        },
                        new String[] {
                            "File", "Tape Content", "Results"
                        }
                    ) {
                        Class[] columnTypes = new Class[] {
                            String.class, String.class, String.class
                        };
                        boolean[] columnEditable = new boolean[] {
                            true, true, false
                        };
                        public Class getColumnClass(int columnIndex) {
                            return columnTypes[columnIndex];
                        }
                        public boolean isCellEditable(int rowIndex, int columnIndex) {
                            return columnEditable[columnIndex];
                        }
                    });
                    filesTestTable.setPreferredScrollableViewportSize(new Dimension(450, 200));
                    filesTestTable.setSurrendersFocusOnKeystroke(false);
                    scrollPane1.setViewportView(filesTestTable);
                }
                contentPane.add(scrollPane1, cc.xywh(1, 33, 3, 1));

                //======== panel1 ========
                {
                    panel1.setLayout(new FormLayout(
                        ColumnSpec.decodeSpecs("default"),
                        new RowSpec[] {
                            FormFactory.DEFAULT_ROWSPEC,
                            FormFactory.LINE_GAP_ROWSPEC,
                            new RowSpec(Sizes.dluY(10)),
                            FormFactory.LINE_GAP_ROWSPEC,
                            FormFactory.DEFAULT_ROWSPEC,
                            FormFactory.LINE_GAP_ROWSPEC,
                            FormFactory.DEFAULT_ROWSPEC
                        }));

                    //---- addFilesButton ----
                    addFilesButton.setText("Add...");
                    panel1.add(addFilesButton, cc.xy(1, 1));

                    //---- removeFilesButton ----
                    removeFilesButton.setText("Remove");
                    panel1.add(removeFilesButton, cc.xy(1, 5));

                    //---- removeAllFilesButton ----
                    removeAllFilesButton.setText("Remove All");
                    panel1.add(removeAllFilesButton, cc.xy(1, 7));
                }
                contentPane.add(panel1, cc.xy(5, 33));

                //---- label1 ----
                label1.setHorizontalAlignment(SwingConstants.RIGHT);
                label1.setText("Content:");
                contentPane.add(label1, cc.xy(1, 13));

                //---- beforeTapeContentCombo ----
                beforeTapeContentCombo.setEditable(true);
                contentPane.add(beforeTapeContentCombo, cc.xy(3, 13));

                //---- label22 ----
                label22.setHorizontalAlignment(SwingConstants.RIGHT);
                label22.setText("Head position:");
                contentPane.add(label22, cc.xy(1, 15));

                //======== panel2 ========
                {
                    panel2.setPreferredSize(new Dimension(200, 30));
                    panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));

                    //---- beforeTapeBeginPositionRadio ----
                    beforeTapeBeginPositionRadio.setSelected(true);
                    beforeTapeBeginPositionRadio.setText("Beginning");
                    panel2.add(beforeTapeBeginPositionRadio);

                    //---- beforeTapeCustomPositionRadio ----
                    beforeTapeCustomPositionRadio.setText("Custom:");
                    panel2.add(beforeTapeCustomPositionRadio);

                    //---- beforeTapeCustomPositionSpinner ----
                    beforeTapeCustomPositionSpinner.setMaximumSize(new Dimension(50, 32767));
                    beforeTapeCustomPositionSpinner.setMinimumSize(new Dimension(50, 24));
                    beforeTapeCustomPositionSpinner.setModel(new SpinnerNumberModel(new Integer(2), new Integer(0), null, new Integer(1)));
                    beforeTapeCustomPositionSpinner.setPreferredSize(new Dimension(50, 24));
                    panel2.add(beforeTapeCustomPositionSpinner);

                    //---- separator1 ----
                    separator1.setBackground(SystemColor.window);
                    separator1.setBorder(null);
                    separator1.setForeground(SystemColor.window);
                    panel2.add(separator1);
                }
                contentPane.add(panel2, cc.xy(3, 15));
                contentPane.add(goodiesFormsSeparator3, cc.xywh(1, 19, 5, 1));

                //---- label2 ----
                label2.setHorizontalAlignment(SwingConstants.RIGHT);
                label2.setText("Content:");
                contentPane.add(label2, cc.xy(1, 23));
                contentPane.add(afterTapeContentTextField, cc.xy(3, 23));

                //---- label222 ----
                label222.setHorizontalAlignment(SwingConstants.RIGHT);
                label222.setText("Head position:");
                contentPane.add(label222, cc.xy(1, 25));

                //======== panel3 ========
                {
                    panel3.setPreferredSize(new Dimension(0, 30));
                    panel3.setLayout(new BoxLayout(panel3, BoxLayout.X_AXIS));

                    //---- afterTapeBeginPositionRadio ----
                    afterTapeBeginPositionRadio.setSelected(true);
                    afterTapeBeginPositionRadio.setText("Beginning");
                    panel3.add(afterTapeBeginPositionRadio);

                    //---- afterTapeCustomPositionRadio ----
                    afterTapeCustomPositionRadio.setText("Custom:");
                    panel3.add(afterTapeCustomPositionRadio);

                    //---- afterTapeCustomPositionSpinner ----
                    afterTapeCustomPositionSpinner.setMaximumSize(new Dimension(50, 32767));
                    afterTapeCustomPositionSpinner.setMinimumSize(new Dimension(50, 24));
                    afterTapeCustomPositionSpinner.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
                    afterTapeCustomPositionSpinner.setPreferredSize(new Dimension(50, 24));
                    panel3.add(afterTapeCustomPositionSpinner);

                    //---- afterTapeIgnorePositionRadio ----
                    afterTapeIgnorePositionRadio.setText("Ignore");
                    panel3.add(afterTapeIgnorePositionRadio);

                    //---- separator2 ----
                    separator2.setBackground(SystemColor.window);
                    separator2.setForeground(SystemColor.window);
                    panel3.add(separator2);
                }
                contentPane.add(panel3, cc.xy(3, 25));
                contentPane.add(goodiesFormsSeparator1, cc.xywh(1, 29, 5, 1));
            }
            dialogPane.add(contentPane, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
                buttonBar.setLayout(new FormLayout(
                    new ColumnSpec[] {
                        FormFactory.GLUE_COLSPEC,
                        FormFactory.BUTTON_COLSPEC,
                        FormFactory.RELATED_GAP_COLSPEC,
                        FormFactory.BUTTON_COLSPEC
                    },
                    RowSpec.decodeSpecs("pref")));

                //---- closeButton ----
                closeButton.setText("Close");
                buttonBar.add(closeButton, cc.xy(2, 1));

                //---- runTestButton ----
                runTestButton.setText("Run Test");
                buttonBar.add(runTestButton, cc.xy(4, 1));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane2.add(dialogPane, BorderLayout.CENTER);

        //---- buttonGroup3 ----
        ButtonGroup buttonGroup3 = new ButtonGroup();
        buttonGroup3.add(runMachineNamedRadio);
        buttonGroup3.add(runMachineAtIndexRadio);

        //---- buttonGroup1 ----
        ButtonGroup buttonGroup1 = new ButtonGroup();
        buttonGroup1.add(beforeTapeBeginPositionRadio);
        buttonGroup1.add(beforeTapeCustomPositionRadio);

        //---- buttonGroup2 ----
        ButtonGroup buttonGroup2 = new ButtonGroup();
        buttonGroup2.add(afterTapeBeginPositionRadio);
        buttonGroup2.add(afterTapeCustomPositionRadio);
        buttonGroup2.add(afterTapeIgnorePositionRadio);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPane;
    private JComponent goodiesFormsSeparator4;
    private JPanel panel4;
    private JRadioButton runMachineNamedRadio;
    private JComboBox runMachineNamedCombo;
    private JRadioButton runMachineAtIndexRadio;
    private JSpinner runMachineAtIndexSpinner;
    private JSeparator separator3;
    private JComponent goodiesFormsSeparator2;
    private JScrollPane scrollPane1;
    private JTable filesTestTable;
    private JPanel panel1;
    private JButton addFilesButton;
    private JButton removeFilesButton;
    private JButton removeAllFilesButton;
    private JLabel label1;
    private JComboBox beforeTapeContentCombo;
    private JLabel label22;
    private JPanel panel2;
    private JRadioButton beforeTapeBeginPositionRadio;
    private JRadioButton beforeTapeCustomPositionRadio;
    private JSpinner beforeTapeCustomPositionSpinner;
    private JSeparator separator1;
    private JComponent goodiesFormsSeparator3;
    private JLabel label2;
    private JTextField afterTapeContentTextField;
    private JLabel label222;
    private JPanel panel3;
    private JRadioButton afterTapeBeginPositionRadio;
    private JRadioButton afterTapeCustomPositionRadio;
    private JSpinner afterTapeCustomPositionSpinner;
    private JRadioButton afterTapeIgnorePositionRadio;
    private JSeparator separator2;
    private JComponent goodiesFormsSeparator1;
    private JPanel buttonBar;
    private JButton closeButton;
    private JButton runTestButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
