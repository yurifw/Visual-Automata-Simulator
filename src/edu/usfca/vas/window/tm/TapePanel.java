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
import edu.usfca.vas.machine.tm.TMMachine;
import edu.usfca.vas.machine.tm.TMTape;
import edu.usfca.vas.machine.tm.exception.TMTapeException;
import edu.usfca.xj.appkit.frame.XJView;
import edu.usfca.xj.appkit.utils.XJAlert;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class TapePanel extends XJView {

    public static final int TAPE_HEIGHT = 60;
    public static final int TAPE_OFFSET_X = 20;
    public static final int TAPE_CELL_WIDTH = 40;
    public static final int TAPE_CELL_HEIGHT = 40;
    public static final int TAPE_INFINITE_LEN = 100;

    private TMTape tape = null;
    private int selectedSymbolIndex = -1;

    public TapePanel() {
        setBackground(Color.white);
        setMaximumSize(new Dimension(99999, TAPE_HEIGHT));
        setMinimumSize(new Dimension(TAPE_OFFSET_X+TAPE_INFINITE_LEN*TAPE_CELL_WIDTH, TAPE_HEIGHT));
        setPreferredSize(new Dimension(TAPE_OFFSET_X+TAPE_INFINITE_LEN*TAPE_CELL_WIDTH, TAPE_HEIGHT));

        setFocusable(true);

        getInputMap().put(KeyStroke.getKeyStroke("LEFT"), "moveCursorLeft");
        getInputMap().put(KeyStroke.getKeyStroke("KP_LEFT"), "moveCursorLeft");
        getInputMap().put(KeyStroke.getKeyStroke("RIGHT"), "moveCursorRight");
        getInputMap().put(KeyStroke.getKeyStroke("KP_RIGHT"), "moveCursorRight");

        getActionMap().put("moveCursorLeft", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if(selectedSymbolIndex>0) {
                    selectedSymbolIndex--;
                    repaint();
                }
            }
        });

        getActionMap().put("moveCursorRight", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                selectedSymbolIndex++;
                repaint();
            }
        });
    }

    public void setTape(TMTape tape) {
        this.tape = tape;
    }

    public TMTape getTape() {
        return tape;
    }

    public void setContent(String content) {
        getTape().setContent(content);
        repaint();
    }

    public String getContent() {
        return tape.getContent();
    }

    public void clear() {
        tape.clear();
        repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

        int y = ySymbol();
        int dx = TAPE_CELL_WIDTH;
        int dy = TAPE_CELL_HEIGHT;

        String tapeContent = tape.getContent();
        int tapeLen = tapeContent.length();

        g2d.setColor(Color.black);
        g2d.fillRect(TAPE_OFFSET_X-10, y, 5, TAPE_CELL_HEIGHT);

        for(int i=0; i<TAPE_INFINITE_LEN; i++) {
            int x = TAPE_OFFSET_X+i*dx;

            g2d.drawRect(x, y, dx, dy);
            if(i == selectedSymbolIndex) {
                g2d.setColor(Color.blue);
                g2d.setStroke(strokeBold);
                g2d.drawRect(x, y, dx, dy);
                g2d.setStroke(strokeNormal);
                g2d.setColor(Color.black);
            }

            String s = TMMachine.SYMBOL_BLANK;
            if(i<tapeLen)
                s = tapeContent.substring(i, i+1);

            drawCenteredString(s, (int)(x+0.5*dx), (int)(y+0.5*dy), g);

            if(tape.getPosition() == i) {
                Polygon p = new Polygon();
                p.addPoint(x+5, (int)(y+dy+dx*0.5)-2);
                p.addPoint(x+dx-5, (int)(y+dy+dx*0.5)-2);
                p.addPoint((int)(x+dx*0.5), y+dy+5);
                g2d.fillPolygon(p);
            }
        }
    }

    public int ySymbol() {
        return (int) (0.5*(TAPE_HEIGHT-TAPE_CELL_HEIGHT));
    }

    public static void drawCenteredString(String s, int x, int y, Graphics g) {
        FontMetrics fm = g.getFontMetrics();
        int xx = (int)(x-fm.stringWidth(s)*0.5);
        int yy = (int)(y+fm.getHeight()*0.5);
        g.drawString(s, xx, yy);
    }

    public void handleMousePressed(MouseEvent e) {
        Point p = e.getPoint();
        if(p.y>=ySymbol() && p.y<=ySymbol()+TAPE_CELL_HEIGHT && p.x>=TAPE_OFFSET_X)
            selectedSymbolIndex = (p.x-TAPE_OFFSET_X)/TAPE_CELL_WIDTH;
        else
            selectedSymbolIndex = -1;

        repaint();
    }

    public void handleKeyPressed(KeyEvent e) {
        if(selectedSymbolIndex>=0) {
            switch(e.getKeyChar()) {
                case KeyEvent.VK_ENTER:
                    try {
                        tape.setPosition(selectedSymbolIndex);
                    } catch (TMTapeException e1) {
                        XJAlert.display(this, Localized.getString("error"), Localized.getString("tmTapeException")+e1.message);
                    }
                    break;
            }
            repaint();
        }
    }

    public void handleKeyTyped(KeyEvent e) {
        if(selectedSymbolIndex>=0) {
            try {
                switch(e.getKeyChar()) {
                    case KeyEvent.VK_ENTER:
                        tape.setPosition(selectedSymbolIndex);
                        break;

                    case KeyEvent.VK_BACK_SPACE:
                        if(selectedSymbolIndex>0) {
                            selectedSymbolIndex--;
                            tape.writeSymbolAtPosition(String.valueOf(TMMachine.SYMBOL_BLANK), selectedSymbolIndex);
                        }
                        break;

                    case KeyEvent.VK_DELETE:
                        tape.writeSymbolAtPosition(String.valueOf(TMMachine.SYMBOL_BLANK), selectedSymbolIndex);
                        break;

                    case KeyEvent.VK_SPACE:
                        tape.writeSymbolAtPosition(TMMachine.SYMBOL_BLANK, selectedSymbolIndex);
                        selectedSymbolIndex++;
                        break;

                    default:
                        if(!((e.getModifiersEx() & InputEvent.META_DOWN_MASK) == InputEvent.META_DOWN_MASK)) {
                            tape.writeSymbolAtPosition(String.valueOf(e.getKeyChar()), selectedSymbolIndex);
                            selectedSymbolIndex++;
                        }
                        break;
                }
            } catch(TMTapeException e1) {
                XJAlert.display(this, Localized.getString("error"), Localized.getString("tmTapeException")+e1.message);
            }
            repaint();
        }
    }

}
