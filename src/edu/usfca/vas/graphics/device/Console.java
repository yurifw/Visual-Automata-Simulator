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

package edu.usfca.vas.graphics.device;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;

public class Console implements OutputDevice {

    private JScrollPane component;
    private JTextPane textArea;

    public Console() {
        textArea = new JTextPane();
        component = new JScrollPane(textArea);
    }

    public JComponent getComponent() {
        return component;
    }

    public void clear() {
        textArea.setText("");
    }

    public void print(String s) {
        String ps = textArea.getText();
        textArea.setText(ps+s);
        System.out.print(s);
    }

    public void println(String s) {
        String ps = textArea.getText();
        textArea.setText(ps+s+"\n");
        System.out.println(s);
    }

    public void println(String t, SimpleAttributeSet attr) {
        String ps = textArea.getText();
        textArea.setCaretPosition(ps.length());
        textArea.setCharacterAttributes(attr, true);
        if(ps.length()>0)
            textArea.replaceSelection("\n"+t);
        else
            textArea.setText(t);
    }

}
