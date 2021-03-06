/*
    Plugin for computers in vanilla minecraft!
    Copyright (C) 2022  JNNGL

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.jnngl.totalcomputers.system.desktop;

import com.jnngl.totalcomputers.TotalComputers;
import com.jnngl.totalcomputers.system.TotalOS;
import com.jnngl.totalcomputers.system.overlays.Keyboard;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public abstract class ConsoleApplication extends WindowApplication {

    public class ConsoleOutput extends OutputStream {
        @Override
        public void write(int b) {
            if(b == 10) {
                putString("\n");
                return;
            }
            putString(""+((char) b));
        }

        public void write(String str) {
            putString(str);
        }
    }

    private final Vector<Character> chars;
    private Color background, textColor;
    private Font font;
    private boolean hasNext;
    public ConsoleOutput stdout, stderr;
    public InputStream stdin;

    public ConsoleApplication(TotalOS os, String title, int width, int height, String path) {
        this(os, title, os.screenWidth/2-width/2, os.screenHeight/2-height/2, width, height, path);
    }

    public ConsoleApplication(TotalOS os, String title, int x, int y, int width, int height, String path) {
        super(path, os, title, x, y, width, height);
        chars = new Vector<>();
        background = Color.BLACK;
        textColor = Color.WHITE;
        font = os.baseFont.deriveFont((float)os.screenHeight/128*3);
        stdout = new ConsoleOutput();
        stderr = new ConsoleOutput();
        stdin = new InputStream() {
            private int i;
            private byte[] bytes;

            @Override
            public int read() throws IOException {
                if(bytes == null) {
                    i = 0;
                    while(!hasNext);
                    bytes = line.getBytes();
                }
                try {
                    return bytes[i];
                } finally {
                    i++;
                    if(i >= bytes.length) {
                        bytes = null;
                    }
                }
            }
        };
        start();
        renderCanvas();
    }

    private String line = "";

    public ConsoleApplication putString(String str) {
        for(char c : str.toCharArray()) {
            chars.add(c);
        }
        return this;
    }

    @Override
    public void processInput(int x, int y, TotalComputers.InputInfo.InteractType type) {
        if(type == TotalComputers.InputInfo.InteractType.RIGHT_CLICK) {
            line = "";
            os.keyboard.invokeKeyboard((text, key, keyboard) -> {
                if(key == Keyboard.Keys.OK || key == Keyboard.Keys.ENTER) {
                    chars.add('\n');
                    os.keyboard.closeKeyboard();
                    renderCanvas();
                    hasNext = true;
                    return line;
                }
                if(key == Keyboard.Keys.BACKSPACE) {
                    if(line.length() > 0) {
                        line = line.substring(0, line.length()-1);
                        chars.remove(chars.size()-1);
                    }
                }
                if(key.text != null) {
                    for (char c : text.toCharArray()) {
                        chars.add(c);
                    }
                    line += text;
                }
                renderCanvas();
                return line;
            }, "");
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(background);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setFont(font);
        FontMetrics metrics = g.getFontMetrics();
        int y = 3;
        y += metrics.getMaxAscent();
        int x = 3;
        g.setColor(textColor);
        List<Character> printed = new ArrayList<>();
        List<Character> toDel = null;
        for(char c : chars) {
            if(y >= getHeight()) {
                y = 3;
                x = 3;
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(Color.WHITE);
                toDel = printed;
            }
            if(c == '\n') {
                y += metrics.getMaxAscent();
                x = 3;
            }
            String str = ""+c;
            g.drawString(str, x, y);
            x += metrics.charWidth(c);
            if(x >= getWidth()-3) {
                y += metrics.getMaxAscent();
                x = 3+metrics.charWidth(' ');
            }
        }

        if(toDel != null) chars.removeAll(toDel);
    }

    public boolean hasNext() {
        return hasNext;
    }

    public String next() {
        hasNext = false;
        return line;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public Font getFont() {
        return font;
    }

    public void setBackground(Color background) {
        this.background = background;
    }

    public Color getBackground() {
        return background;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    public Color getTextColor() {
        return textColor;
    }

}
