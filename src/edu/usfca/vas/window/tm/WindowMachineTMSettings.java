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
import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.*;
import edu.usfca.vas.app.Localized;
import edu.usfca.xj.appkit.frame.XJDialog;
import edu.usfca.xj.appkit.utils.XJAlert;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WindowMachineTMSettings extends XJDialog {

    private WindowMachineTM wm = null;

    public WindowMachineTMSettings(WindowMachineTM wm) {
        super(wm.getWindow().getJavaContainer(), true);
        
        this.wm = wm;

        initComponents();

        setResizable(false);
        resize();

        setDefaultButton(okButton);

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });

        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                WindowMachineTM wm = WindowMachineTMSettings.this.wm;
                if(wm.getWindowTM().alreadyExistsMachineWithName(nameField.getText(), wm)) {
                    XJAlert.display(getJavaComponent(), Localized.getString("tmWMCannotRenameTitle"),
                                    Localized.getString("tmWMCannotRenameMessage"));
                } else {
                    popValues();
                    close();
                }
            }
        });
    }

    public void resize() {
        Dimension d = dialogPane.getPreferredSize();
        d.height += 30;
        setSize(d);
    }

    public void pushValues() {
        nameField.setText(wm.getTitle());
        nameField.selectAll();
        
        widthField.setText(String.valueOf(wm.getGraphicsSize().width));
        heightField.setText(String.valueOf(wm.getGraphicsSize().height));

        verticalSpinner.setValue(new Integer(wm.getVerticalMagnetics()));
        horizontalSpinner.setValue(new Integer(wm.getHorizontalMagnetics()));
    }

    public void popValues() {
        wm.setTitle(nameField.getText());
        wm.setGraphicsSize(Integer.parseInt(widthField.getText()), Integer.parseInt(heightField.getText()));
        Integer horizontal = (Integer)horizontalSpinner.getValue();
        Integer vertical = (Integer)horizontalSpinner.getValue();
        wm.setMagnetics(horizontal.intValue(), vertical.intValue());
    }

    public void display() {
        pushValues();
        center();
        runModal();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        DefaultComponentFactory compFactory = DefaultComponentFactory.getInstance();
        dialogPane = new JPanel();
        contentPane = new JPanel();
        label1 = new JLabel();
        nameField = new JTextField();
        goodiesFormsSeparator2 = compFactory.createSeparator("Machine Size");
        label2 = new JLabel();
        widthField = new JTextField();
        label3 = new JLabel();
        heightField = new JTextField();
        goodiesFormsSeparator1 = compFactory.createSeparator("Magnetic Layout Grid");
        label6 = new JLabel();
        verticalSpinner = new JSpinner();
        label4 = new JLabel();
        horizontalSpinner = new JSpinner();
        buttonBar = new JPanel();
        okButton = new JButton();
        cancelButton = new JButton();
        CellConstraints cc = new CellConstraints();

        //======== this ========
        setTitle("Machine Settings");
        Container contentPane2 = getContentPane();
        contentPane2.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(Borders.DIALOG_BORDER);
            dialogPane.setPreferredSize(new Dimension(350, 300));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPane ========
            {
                contentPane.setLayout(new FormLayout(
                    new ColumnSpec[] {
                        new ColumnSpec(Sizes.dluX(10)),
                        FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                        FormFactory.DEFAULT_COLSPEC,
                        FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                        new ColumnSpec(Sizes.dluX(30)),
                        FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                        new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
                        FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                        new ColumnSpec("10px")
                    },
                    new RowSpec[] {
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        new RowSpec(Sizes.dluY(10)),
                        FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        new RowSpec(Sizes.dluY(10)),
                        FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC
                    }));

                //---- label1 ----
                label1.setHorizontalAlignment(SwingConstants.RIGHT);
                label1.setText("Name:");
                contentPane.add(label1, cc.xy(3, 1));
                contentPane.add(nameField, cc.xywh(5, 1, 3, 1));
                contentPane.add(goodiesFormsSeparator2, cc.xywh(3, 5, 5, 1));

                //---- label2 ----
                label2.setHorizontalAlignment(SwingConstants.RIGHT);
                label2.setText("Width:");
                contentPane.add(label2, cc.xy(3, 7));
                contentPane.add(widthField, cc.xywh(5, 7, 3, 1));

                //---- label3 ----
                label3.setHorizontalAlignment(SwingConstants.RIGHT);
                label3.setText("Height:");
                contentPane.add(label3, cc.xy(3, 9));
                contentPane.add(heightField, cc.xywh(5, 9, 3, 1));
                contentPane.add(goodiesFormsSeparator1, cc.xywh(3, 13, 5, 1));

                //---- label6 ----
                label6.setHorizontalAlignment(SwingConstants.RIGHT);
                label6.setText("Vertical:");
                contentPane.add(label6, cc.xy(3, 15));

                //---- verticalSpinner ----
                verticalSpinner.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
                contentPane.add(verticalSpinner, cc.xy(5, 15));

                //---- label4 ----
                label4.setHorizontalAlignment(SwingConstants.RIGHT);
                label4.setText("Horizontal:");
                contentPane.add(label4, cc.xy(3, 17));

                //---- horizontalSpinner ----
                horizontalSpinner.setModel(new SpinnerNumberModel(new Integer(1), new Integer(0), null, new Integer(1)));
                contentPane.add(horizontalSpinner, cc.xy(5, 17));
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

                //---- okButton ----
                okButton.setText("OK");
                buttonBar.add(okButton, cc.xy(2, 1));

                //---- cancelButton ----
                cancelButton.setText("Cancel");
                buttonBar.add(cancelButton, cc.xy(4, 1));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane2.add(dialogPane, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPane;
    private JLabel label1;
    private JTextField nameField;
    private JComponent goodiesFormsSeparator2;
    private JLabel label2;
    private JTextField widthField;
    private JLabel label3;
    private JTextField heightField;
    private JComponent goodiesFormsSeparator1;
    private JLabel label6;
    private JSpinner verticalSpinner;
    private JLabel label4;
    private JSpinner horizontalSpinner;
    private JPanel buttonBar;
    private JButton okButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables


}
