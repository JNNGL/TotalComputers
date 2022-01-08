/*
    Computers are now in minecraft!
    Copyright (C) 2021  JNNGL

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

package com.jnngl.totalcomputers.system;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Common functions
 */
public class Utils {

    private static Graphics2D g;

    /**
     * Creates font metrics
     * @param f Font
     * @return {@link java.awt.FontMetrics} of a font
     */
    public static FontMetrics getFontMetrics(Font f) {
        if(g == null) g = new BufferedImage(1,1, BufferedImage.TYPE_BYTE_GRAY).createGraphics();
        return g.getFontMetrics(f);
    }

}
