package edu.usfca.vas.window.fa;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextLayout;
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

public class WindowMachineFAOverlay {

    public static final int DEFAULT_WIDTH = 300;
    public static final int DEFAULT_HEIGHT = 40;

    public static final int ALIGN_TOP_LEFT = 0;
    public static final int ALIGN_TOP_RIGHT = 1;
    public static final int ALIGN_BOTTOM_LEFT = 2;
    public static final int ALIGN_BOTTOM_RIGHT = 3;
    public static final int ALIGN_CENTER = 4;

    public static final int OFFSET = 10;
    public static final int INSET = -4;

    protected JFrame parentFrame;
    protected JComponent parentComponent;

    protected InfoPanel contentPanel;
    protected Point dragOffset;
    protected Point customLocation;
    protected JTextField stringField;
    protected int stringIndex;

    public WindowMachineFAOverlay(JFrame parentFrame, JComponent parentComponent) {

        this.parentFrame = parentFrame;
        this.parentComponent = parentComponent;

        contentPanel = new InfoPanel();
        contentPanel.setOpaque(false);
        contentPanel.setVisible(false);

        parentFrame.getLayeredPane().add(contentPanel, JLayeredPane.MODAL_LAYER);

        createListeners();
    }

    private void createListeners() {
        parentComponent.addComponentListener(new ComponentAdapter() {
            public void componentHidden(ComponentEvent e) {
                if(contentPanel.isVisible())
                    contentPanel.setVisible(false);
            }

            public void componentMoved(ComponentEvent e) {
                if(contentPanel.isVisible())
                    resize();
            }

            public void componentResized(ComponentEvent e) {
                if(contentPanel.isVisible())
                    resize();
            }
        });

        contentPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                dragOffset = e.getPoint();
            }

            public void mouseEntered(MouseEvent e) {
                //buttonsPanel.setVisible(true);
            }

            public void mouseExited(MouseEvent e) {
                //buttonsPanel.setVisible(false);
            }
        });

        contentPanel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                customLocation = SwingUtilities.convertPoint(contentPanel, e.getPoint(), parentFrame);
                // @todo why 21 ?
                customLocation.translate(-dragOffset.x, -dragOffset.y-21);
                contentPanel.setLocation(customLocation);
            }

        });
    }

    public void hide() {
        if(contentPanel.isVisible()) {
            contentPanel.setVisible(false);
            parentComponent.requestFocus();
        }
    }

    public void resize() {
        contentPanel.resize();
        Rectangle r = parentComponent.getVisibleRect();
        Point p = SwingUtilities.convertPoint(parentComponent, new Point(r.x, r.y), parentFrame.getRootPane());
        int x = 0;
        int y = 0;
        switch(overlayDefaultAlignment()) {
            case ALIGN_CENTER:
                x = p.x+r.width/2-overlayDefaultWidth()/2;
                y = p.y+r.height/2-overlayDefaultHeight()/2;
                break;
            case ALIGN_TOP_LEFT:
                x = p.x+OFFSET;
                y = p.y+OFFSET;
                break;
            case ALIGN_TOP_RIGHT:
                x = p.x+r.width-overlayDefaultWidth()-2*OFFSET;
                y = p.y+OFFSET;
                break;
            case ALIGN_BOTTOM_LEFT:
                x = p.x+OFFSET;
                y = p.y+r.height-overlayDefaultHeight()-OFFSET;
                break;
            case ALIGN_BOTTOM_RIGHT:
                x = p.x+r.width-overlayDefaultWidth()-2*OFFSET;
                y = p.y+r.height-overlayDefaultHeight()-OFFSET;
                break;
        }
        if(customLocation == null)
            contentPanel.setLocation(x, y);
    }

    public void display() {
        if(overlayWillDisplay()) {
            resize();
            contentPanel.setVisible(true);
        } else {
            contentPanel.setVisible(false);
        }
    }

    public void setVisible(boolean visible) {
        if(visible)
            display();
        else
            hide();
    }

    public boolean isVisible() {
        return contentPanel.isVisible();
    }

    public boolean overlayWillDisplay() {
        return true;
    }

    public int overlayDefaultWidth() {
        return contentPanel.getWidth();
    }

    public int overlayDefaultHeight() {
        return contentPanel.getHeight()+50;
    }

    public int overlayDefaultAlignment() {
        return ALIGN_TOP_RIGHT;
    }

    public void setString(String string, int index) {
        this.stringIndex = index;
        contentPanel.repaint();
    }

    public void setStringField(JTextField field) {
        this.stringField = field;
    }

    public String getText() {
        return stringField.getText();
    }

    public void textChanged() {
        resize();
    }

    public class InfoPanel extends JPanel {

        public InfoPanel() {
            super(new BorderLayout());
            setFont(new Font("Courier", Font.PLAIN, 36));
        }

        public void resize() {
            if(getGraphics() == null)
                return;

            if(getText() == null || getText().length() == 0)
                return;

            TextLayout layout = new TextLayout(getText(), getFont(), ((Graphics2D)getGraphics()).getFontRenderContext());
            int width = (int)layout.getBounds().getWidth() - INSET;
            int height = 2*(int)layout.getBounds().getHeight() - INSET;
            setSize(new Dimension(width, height));
            setPreferredSize(new Dimension(width, height));
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            int x = 0;
            int y = getHeight()/2;

            g.setColor(Color.black);
            g.drawString(getText(), x, y);
            g.setColor(Color.red);
            g.drawString("^", x+stringIndex*getWidth()/getText().length(), y+getHeight()/2);
        }
    }
}
