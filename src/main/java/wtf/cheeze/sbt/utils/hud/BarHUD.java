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
package wtf.cheeze.sbt.utils.hud;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import wtf.cheeze.sbt.utils.RenderUtils;

/**
 * A HUD that displays a bar, code liberally inspired by SBA, but way simpler thanks to modern mc, bar textures taken directly from SBA
 */
public abstract class BarHUD extends HUD{
    public static final Identifier UNFILLED = Identifier.of("skyblocktweaks", "unfill.png");
    public static final Identifier FILLED = Identifier.of("skyblocktweaks", "fill.png");
    public static final int BAR_WIDTH = 71;
    public static final int BAR_HEIGHT = 5;

    @Override
    public void render(DrawContext context, boolean fromHudScreen, boolean hovered) {
        if (!shouldRender(fromHudScreen)) return;
        var bounds = getCurrentBounds();
        if (fromHudScreen) {
            drawBackground(context, hovered ? BACKGROUND_HOVERED : BACKGROUND_NOT_HOVERED);
        }
        var colors = RenderUtils.getColor3f((int) INFO.getColor.get());
        if (bounds.scale == 1.0f) {
            context.setShaderColor(colors.red, colors.green, colors.blue, 1.0f);
            context.drawTexture(UNFILLED, bounds.x, bounds.y, 0, 0, BAR_WIDTH, BAR_HEIGHT, BAR_WIDTH, BAR_HEIGHT);
            context.drawTexture(FILLED, bounds.x, bounds.y, 0, 0, calculateFill((float) INFO.getFillNum.get(), (float) INFO.getMaxNum.get()), BAR_HEIGHT, BAR_WIDTH, BAR_HEIGHT);
            context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        } else {
            RenderUtils.beginScale(context, bounds.scale);
            context.setShaderColor(colors.red, colors.green, colors.blue, 1.0f);
            context.drawTexture(UNFILLED, (int)(bounds.x/bounds.scale), (int)(bounds.y/bounds.scale), 0, 0, BAR_WIDTH, BAR_HEIGHT, BAR_WIDTH, BAR_HEIGHT);
            context.drawTexture(FILLED, (int)(bounds.x/bounds.scale), (int)(bounds.y/bounds.scale), 0, 0, calculateFill((float) INFO.getFillNum.get(), (float) INFO.getMaxNum.get()), BAR_HEIGHT, BAR_WIDTH, BAR_HEIGHT);
            context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderUtils.endScale(context);
        }
    }
    @Override
    public Bounds getCurrentBounds() {
        var scale = (float) INFO.getScale.get();
        return new Bounds(getActualX((float) INFO.getX.get()), getActualY((float) INFO.getY.get()), BAR_WIDTH * scale, BAR_HEIGHT * scale, scale);
    }
    @Override
    public BoundsRelative getCurrentBoundsRelative() {
        var scale = (float) INFO.getScale.get();
        return new BoundsRelative((float) INFO.getX.get(), (float) INFO.getY.get(), BAR_WIDTH * scale, BAR_HEIGHT * scale, scale);
    }

//    @Override
//    public void drawBackground(DrawContext context, int color) {
//        var bounds = getCurrentBounds();
//        int i = (int) (1 * bounds.scale);
//        //int i = 1;
//        context.fill(bounds.x , bounds.y, (int) (bounds.x + bounds.width), (int) (bounds.y + bounds.height), color);
//    }



    private static int calculateFill(float current, float max) {
        if (current >= max) return BAR_WIDTH;
        var i = (int) (current / max * BAR_WIDTH);
        return i;
    }

}
