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

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.*;
import edu.usfca.vas.graphics.IconManager;
import edu.usfca.xj.appkit.app.XJAboutBox;
import edu.usfca.xj.appkit.frame.XJPanel;
import edu.usfca.xj.appkit.text.XJURLLabel;

import javax.swing.*;
import java.awt.*;

public class About extends XJPanel {

    public About() {
        initComponents();

        versionLabel.setText("Version "+XJAboutBox.getAboutSoftwareVersion());
        buildLabel.setText("Build "+XJAboutBox.getAboutSoftwareVersionDate());

        copyrightTextArea.setText("Image to Postscript conversion (c) 1999 Terence Parr\n" +
                "Icon by Matthew McClintock <matthew@mc.clintock.com>");

        appIconButton.setIcon(IconManager.getIcon(IconManager.ICON_APP));

        setSize(850, 300);
        jFrame.setResizable(false);
        center();
    }

// Note : put this when generating a new form
//    URLLabel = new XJURLLabel("http://www.cs.usfca.edu/~jbovet");
//    And make sure copyright date are correct

    private static final String url = "http://www.cs.usfca.edu/~jbovet";

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        appIconButton = new JButton();
        titleLabel = new JLabel();
        descriptionLabel = new JLabel();
        versionLabel = new JLabel();
        buildLabel = new JLabel();
        copyrightLabel = new JLabel();
        URLLabel = new XJURLLabel(url);
        copyrightTextArea = new JTextArea();
        CellConstraints cc = new CellConstraints();

        //======== this ========
        setResizable(false);
        setTitle("About");
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            new ColumnSpec[] {
                new ColumnSpec(Sizes.dluX(10)),
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                FormFactory.DEFAULT_COLSPEC,
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                new ColumnSpec(Sizes.dluX(10))
            },
            new RowSpec[] {
                new RowSpec(Sizes.dluY(10)),
                FormFactory.LINE_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC,
                FormFactory.LINE_GAP_ROWSPEC,
                new RowSpec("max(default;12dlu)"),
                FormFactory.LINE_GAP_ROWSPEC,
                new RowSpec(Sizes.dluY(10)),
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
                FormFactory.DEFAULT_ROWSPEC,
                FormFactory.LINE_GAP_ROWSPEC,
                new RowSpec(Sizes.dluY(10))
            }));

        //---- appIconButton ----
        appIconButton.setBorderPainted(false);
        appIconButton.setContentAreaFilled(false);
        appIconButton.setDefaultCapable(false);
        appIconButton.setEnabled(true);
        appIconButton.setFocusPainted(false);
        appIconButton.setFocusable(false);
        appIconButton.setIcon(new ImageIcon("/Users/bovet/Dev/Java/Automata/src/icons/app.png"));
        appIconButton.setMaximumSize(new Dimension(136, 144));
        appIconButton.setMinimumSize(new Dimension(136, 144));
        appIconButton.setPreferredSize(new Dimension(124, 144));
        contentPane.add(appIconButton, cc.xywh(3, 3, 1, 9));

        //---- titleLabel ----
        titleLabel.setFont(new Font("Lucida Grande", Font.BOLD, 36));
        titleLabel.setText("Visual Automata Simulator");
        contentPane.add(titleLabel, cc.xy(5, 3));

        //---- descriptionLabel ----
        descriptionLabel.setHorizontalAlignment(SwingConstants.LEFT);
        descriptionLabel.setHorizontalTextPosition(SwingConstants.LEFT);
        descriptionLabel.setText("A tool for simulating, visualizing and transforming finite state automata and Turing Machines");
        descriptionLabel.setVerticalAlignment(SwingConstants.TOP);
        descriptionLabel.setVerticalTextPosition(SwingConstants.TOP);
        contentPane.add(descriptionLabel, cc.xywh(5, 5, 1, 2));

        //---- versionLabel ----
        versionLabel.setText("Version 1.0");
        contentPane.add(versionLabel, cc.xy(5, 9));

        //---- buildLabel ----
        buildLabel.setText("Build 900");
        contentPane.add(buildLabel, cc.xy(5, 11));

        //---- copyrightLabel ----
        copyrightLabel.setText("Copyright (c) 2004-2006 Jean Bovet");
        contentPane.add(copyrightLabel, cc.xy(5, 15));

        //---- URLLabel ----
        URLLabel.setText("Visit www.cs.usfca.edu/~jbovet");
        contentPane.add(URLLabel, cc.xy(5, 17));

        //---- copyrightTextArea ----
        copyrightTextArea.setBackground(SystemColor.window);
        copyrightTextArea.setBorder(null);
        copyrightTextArea.setEditable(false);
        copyrightTextArea.setText("");
        contentPane.add(copyrightTextArea, cc.xy(5, 19));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JButton appIconButton;
    private JLabel titleLabel;
    private JLabel descriptionLabel;
    private JLabel versionLabel;
    private JLabel buildLabel;
    private JLabel copyrightLabel;
    private XJURLLabel URLLabel;
    private JTextArea copyrightTextArea;
    // JFormDesigner - End of variables declaration  //GEN-END:variables



}
