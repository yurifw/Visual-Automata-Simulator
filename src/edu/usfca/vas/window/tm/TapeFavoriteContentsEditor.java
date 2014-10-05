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

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.*;
import edu.usfca.vas.app.Localized;
import edu.usfca.xj.appkit.frame.XJDialog;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class TapeFavoriteContentsEditor extends XJDialog {

    private List favorites = null;
    private AbstractTableModel tableModel = null;

	public TapeFavoriteContentsEditor(Container parent) {
        super(parent, true);
        
		initComponents();
        center();

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tapeContentTable.clearSelection();
                close();
            }
        });

        buttonAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                favorites.add(new TapeConfiguration());
                tableModel.fireTableDataChanged();
                tapeContentTable.setRowSelectionAllowed(true);
                tapeContentTable.setRowSelectionInterval(tableModel.getRowCount()-1, tableModel.getRowCount()-1);
            }
        });

        buttonRemove.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = tapeContentTable.getSelectedRow();
                if(row>=0) {
                    favorites.remove(row);
                    tableModel.fireTableDataChanged();
                }
            }
        });

	}

    public void setFavorites(List favorites) {
        this.favorites = favorites;
    }

    public void dialogWillDisplay() {
        tapeContentTable.setModel(tableModel = new AbstractTableModel() {
            public int getColumnCount() {
                return 1;
            }

            public int getRowCount() {
                return favorites.size();
            }

            public boolean isCellEditable(int row, int col) {
                return true;
            }

            public String getColumnName(int column) {
                return Localized.getString("tmTapeContent");
            }

            public Object getValueAt(int row, int col) {
                TapeConfiguration conf = (TapeConfiguration)favorites.get(row);
                return conf.content;
            }

            public void setValueAt(Object value, int row, int col) {
                TapeConfiguration conf = (TapeConfiguration)favorites.get(row);
                conf.content = (String)value;
            }
        });
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPane = new JPanel();
        scrollPane1 = new JScrollPane();
        tapeContentTable = new JTable();
        buttonAdd = new JButton();
        buttonRemove = new JButton();
        label1 = new JLabel();
        buttonOK = new JButton();
        CellConstraints cc = new CellConstraints();

        //======== this ========
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setTitle("Tape Favorites Content");
        jDialog.getRootPane().setDefaultButton(buttonOK);
        Container contentPane2 = getContentPane();
        contentPane2.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setAutoscrolls(false);
            dialogPane.setBorder(Borders.DIALOG_BORDER);
            dialogPane.setDoubleBuffered(true);
            dialogPane.setEnabled(true);
            dialogPane.setLayout(new BorderLayout());

            //======== contentPane ========
            {
                contentPane.setAutoscrolls(true);
                contentPane.setLayout(new FormLayout(
                    new ColumnSpec[] {
                        new ColumnSpec("max(default;40dlu):grow"),
                        FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                        new ColumnSpec(Sizes.dluX(40))
                    },
                    new RowSpec[] {
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        new RowSpec(RowSpec.CENTER, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
                        FormFactory.DEFAULT_ROWSPEC
                    }));

                //======== scrollPane1 ========
                {
                    scrollPane1.setPreferredSize(new Dimension(204, 200));

                    //---- tapeContentTable ----
                    tapeContentTable.setModel(new DefaultTableModel(
                        new Object[][] {
                            {"a"},
                            {"a"},
                            {"e"},
                            {"unh asdasd "},
                            {"a"},
                            {"c"},
                        },
                        new String[] {
                            "Tape Content"
                        }
                    ) {
                        Class[] columnTypes = new Class[] {
                            String.class
                        };
                        public Class getColumnClass(int columnIndex) {
                            return columnTypes[columnIndex];
                        }
                    });
                    tapeContentTable.setPreferredScrollableViewportSize(new Dimension(200, 400));
                    tapeContentTable.setShowHorizontalLines(false);
                    tapeContentTable.setShowVerticalLines(false);
                    scrollPane1.setViewportView(tapeContentTable);
                }
                contentPane.add(scrollPane1, cc.xywh(1, 1, 1, 5));

                //---- buttonAdd ----
                buttonAdd.setText("Add");
                contentPane.add(buttonAdd, cc.xy(3, 1));

                //---- buttonRemove ----
                buttonRemove.setText("Remove");
                contentPane.add(buttonRemove, cc.xy(3, 3));

                //---- label1 ----
                label1.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
                label1.setText("Double-clic to edit the content");
                contentPane.add(label1, cc.xy(1, 6));

                //---- buttonOK ----
                buttonOK.setText("OK");
                contentPane.add(buttonOK, cc.xy(3, 6));
            }
            dialogPane.add(contentPane, BorderLayout.CENTER);
        }
        contentPane2.add(dialogPane, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPane;
    private JScrollPane scrollPane1;
    private JTable tapeContentTable;
    private JButton buttonAdd;
    private JButton buttonRemove;
    private JLabel label1;
    private JButton buttonOK;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

}
