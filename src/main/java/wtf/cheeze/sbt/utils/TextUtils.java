/*
 * Copyright (C) 2024 MisterCheezeCake
 *
 * This file is part of SkyblockTweaks.
 *
 * SkyblockTweaks is free software: you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * SkyblockTweaks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with SkyblockTweaks. If not, see <https://www.gnu.org/licenses/>.
 */
package wtf.cheeze.sbt.utils;

import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;

public class TextUtils {
    public static final String SECTION  = "§";
    public static String removeColorCodes(String text) {
        return text.replaceAll("§[a-f0-9k-o]", "");
    }
    public static int parseIntWithKorM(String text) {
        text = text.toLowerCase();
        if (text.endsWith("k")) {
            return Integer.parseInt(text.substring(0, text.length() - 1)) * 1000;
        } else if (text.endsWith("m")) {
            return Integer.parseInt(text.substring(0, text.length() - 1)) * 1000000;
        } else {
            return Integer.parseInt(text);
        }
    }
    public static String formatNumber(int number, String seperator) {
        String str = Integer.toString(number);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            sb.append(str.charAt(i));
            if ((str.length() - i - 1) % 3 == 0 && i != str.length() - 1) {
                sb.append(seperator);
            }
        }
        return sb.toString();
    }
    public static String addKOrM(int number, String separator) {
        if (number >= 1000000) {
            return formatNumber(number / 1000000, separator) + "M";
        } else if (number >= 1000) {
            return formatNumber(number / 1000, separator) + "K";
        } else {
            return formatNumber(number, separator);
        }
    }
    public static Text getTextThatLinksToURL(String text,String hovered, String url) {
        return Text.literal(text).styled(style -> {
            style = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(hovered)));
            style = style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
            return style;
        });
    }

}
