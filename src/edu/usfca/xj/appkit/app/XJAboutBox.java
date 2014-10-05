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

import edu.usfca.xj.appkit.frame.XJPanel;
import edu.usfca.xj.appkit.utils.XJLocalizable;
import edu.usfca.xj.foundation.XJLib;

import javax.swing.*;
import java.awt.*;

public class XJAboutBox extends XJPanel {

    public static String getAboutSoftwareVersion() {
        return XJLocalizable.getString("about", "ABOUT_SOFTWARE_VERSION");
    }

    public static String getAboutSoftwareVersionDate() {
        return XJLocalizable.getString("about", "ABOUT_SOFTWARE_VERSION_DATE");
    }

    public static String getAboutURL() {
        return XJLocalizable.getString("about", "ABOUT_URL");        
    }

    public XJAboutBox() {
        setTitle(XJLocalizable.getString("about", "ABOUT_TITLE"));
        setSize(400, 200);
        jFrame.setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel label = new JLabel(XJLocalizable.getString("about", "ABOUT_SOFTWARE_TITLE"));
        label.setFont(new Font(label.getFont().getName(), Font.BOLD, 18));

        gbc.gridx = 0;
        gbc.gridy = 0;

        panel.add(label, gbc);

        gbc.gridy = 1;
        panel.add(new JLabel(getAboutSoftwareVersion()+" "+getAboutSoftwareVersionDate()), gbc);

        gbc.gridy = 2;
        panel.add(new JLabel(" "), gbc);

        gbc.gridy = 3;
        label = new JLabel(XJLocalizable.getString("about", "ABOUT_AUTHOR"));
        panel.add(label, gbc);

        gbc.gridy = 4;
        label = new JLabel(XJLocalizable.getString("about", "ABOUT_URL"));
        panel.add(label, gbc);

        gbc.gridy = 5;
        panel.add(new JLabel(" "), gbc);

        gbc.gridy = 6;
        label = new JLabel("Built using XJLib "+XJLib.stringVersion()+" ("+XJLib.stringDate()+")");
        panel.add(label, gbc);

        jFrame.getContentPane().add(panel, BorderLayout.CENTER);

        center();
    }
}
