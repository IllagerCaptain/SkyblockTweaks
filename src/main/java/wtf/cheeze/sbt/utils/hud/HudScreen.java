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

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import wtf.cheeze.sbt.SkyBlockTweaks;

import java.util.ArrayList;
import java.util.function.Predicate;

public class HudScreen extends Screen {

        private ArrayList<HUD> huds;

        @Nullable
        private HUD selectedElement = null;

        @Nullable
        private HUD hoveredElement = null;

        @Nullable
        private HUD selectedViaTheKeyboard = null;

        private TextFieldWidget widgetX;
        private TextFieldWidget widgetY;
        private ButtonWidget doneButton;
        private ButtonWidget resetModeButton;
        private ButtonWidget cancelButton;
        private ButtonWidget resetButton;
        private Mode mode = Mode.DRAG;
        private int resetModeIndex = 0;

        private Screen parent;
        public HudScreen(Text title, ArrayList<HUD> huds, Screen parent) {
            super(title);
            this.huds = huds;
            MinecraftClient mc = MinecraftClient.getInstance();
            var centerX = (mc.getWindow().getScaledWidth() / 2) - 50;
            widgetX = new TextFieldWidget(mc.textRenderer, centerX, 55, 100, 20, Text.literal(""));
            widgetY = new TextFieldWidget(mc.textRenderer, centerX, 85, 100, 20, Text.literal(""));
            widgetX.setTextPredicate(new NumberPredicate());
            widgetY.setTextPredicate(new NumberPredicate());

            widgetX.setChangedListener(s -> {
                if (selectedElement != null) {
                    if (s.isEmpty()) return;
                    selectedElement.updatePosition(Float.parseFloat(s), selectedElement.getCurrentBoundsRelative().y);
                }
            });
            widgetY.setChangedListener(s -> {
                if (selectedElement != null) {
                    if (s.isEmpty()) return;
                    selectedElement.updatePosition(selectedElement.getCurrentBoundsRelative().x, Float.parseFloat(s));
                }
            });
            doneButton = ButtonWidget.builder(Text.literal("Done"), button -> {
                this.selectedElement = null;
                setMode(Mode.DRAG);
            }).dimensions(centerX, 110, 100, 20).build();
            resetModeButton = ButtonWidget.builder(Text.literal(this.huds.getFirst().getName()), button -> {
                this.resetModeIndex++;
                if (this.resetModeIndex >= this.huds.size()) {
                    this.resetModeIndex = 0;
                }
                resetModeButton.setMessage(Text.literal(this.huds.get(this.resetModeIndex).getName()));

            }).dimensions(centerX-25, 35, 150, 20).build();

            cancelButton = ButtonWidget.builder(Text.literal("Exit"), button -> {
                this.selectedElement = null;
                setMode(Mode.DRAG);
            }).dimensions(centerX-25, 95, 150, 20).build();

            resetButton = ButtonWidget.builder(Text.literal("Reset"), button -> {
                    this.huds.get(this.resetModeIndex).updatePosition(0f, 0f);

            }).dimensions(centerX-25, 65, 150, 20).build();

            setMode(Mode.DRAG);
            this.addDrawableChild(widgetX);
            this.addDrawableChild(widgetY);
            this.addDrawableChild(doneButton);
            this.addDrawableChild(resetModeButton);
            this.addDrawableChild(cancelButton);
            this.addDrawableChild(resetButton);
            this.parent = parent;

        }


        private void setMode(Mode newMode) {
            this.mode = newMode;
            if (newMode == Mode.DRAG) {
                widgetX.setVisible(false);
                widgetY.setVisible(false);
                doneButton.visible = false;
                resetModeButton.visible = false;
                cancelButton.visible = false;
                resetButton.visible = false;
            } else if (newMode == Mode.TEXT) {
                widgetX.setVisible(true);
                widgetY.setVisible(true);
                doneButton.visible = true;
                resetModeButton.visible = false;
                cancelButton.visible = false;
                resetButton.visible = false;
            } else if (newMode == Mode.RESET) {
                widgetX.setVisible(false);
                widgetY.setVisible(false);
                doneButton.visible = false;
                resetModeButton.visible = true;
                cancelButton.visible = true;
                resetButton.visible = true;
            }
        }
        private void enableTextMode(HUD hud) {
            this.selectedElement = hud;
            setMode(Mode.TEXT);
            var bounds = hud.getCurrentBoundsRelative();
            widgetX.setText(""+bounds.x);
            widgetY.setText(""+bounds.y);
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            super.render(context, mouseX, mouseY, delta);

            MinecraftClient mc = MinecraftClient.getInstance();
            var centerX = mc.getWindow().getScaledWidth() / 2;
            if (!this.hasAltDown() && this.mode == Mode.DRAG) {
                context.drawCenteredTextWithShadow(mc.textRenderer, "Drag or use the arrow keys to move items" , centerX, 5, 0xFFFFFF);
                context.drawCenteredTextWithShadow(mc.textRenderer, "Scroll or use the + and - keys to scale items" , centerX, 15, 0xFFFFFF);
                context.drawCenteredTextWithShadow(mc.textRenderer, "Press shift and hover to see the name of the item" , centerX, 25, 0xFFFFFF);
                context.drawCenteredTextWithShadow(mc.textRenderer, "Control click to edit the positions in a text field" , centerX, 35, 0xFFFFFF);
                context.drawCenteredTextWithShadow(mc.textRenderer, "Control + R to enter Reset Mode" , centerX, 45, 0xFFFFFF);
                context.drawCenteredTextWithShadow(mc.textRenderer, "Press alt to hide this text" , centerX, 55, 0xFFFFFF);
            } else if (this.mode == Mode.TEXT) {
                context.drawCenteredTextWithShadow(mc.textRenderer, "You are now in exact positioning mode, editing " + selectedElement.getName() , centerX, 5, 0xFFFFFF);
                context.drawCenteredTextWithShadow(mc.textRenderer, "Enter the x and y positions in the text fields below" , centerX, 15, 0xFFFFFF);
                context.drawCenteredTextWithShadow(mc.textRenderer, "The number is relative, so 0 is fully up/left and 1 is fully up/right" , centerX, 25, 0xFFFFFF);
                context.drawCenteredTextWithShadow(mc.textRenderer, "Press the done button to exit" , centerX, 35, 0xFFFFFF);
                context.drawCenteredTextWithShadow(mc.textRenderer, "X: " , centerX, 45, 0xFFFFFF);
                context.drawCenteredTextWithShadow(mc.textRenderer, "Y: " , centerX, 76, 0xFFFFFF);
            } else if (this.mode == Mode.RESET) {
                context.drawCenteredTextWithShadow(mc.textRenderer, "You are now in reset mode", centerX, 5, 0xFFFFFF);
                context.drawCenteredTextWithShadow(mc.textRenderer, "Press the reset button to reset the selected item to the default position", centerX, 15, 0xFFFFFF);
                context.drawCenteredTextWithShadow(mc.textRenderer, "Press the first button to cycle between elements", centerX, 25, 0xFFFFFF);
            }
            HUD hovered = null;
            for (HUD hud : huds) {
                boolean b = clickInBounds(hud, mouseX, mouseY);
                hud.render(context, true, b);
                if (b) {
                    hovered = hud;
                    if (this.hasShiftDown()) {
                        this.setTooltip(Tooltip.of(Text.literal(hud.getName())), HoveredTooltipPositioner.INSTANCE, false);
                    }
                }
            }
            hoveredElement = hovered;
            if (hovered == null) {
                this.clearTooltip();
            }


        }

        @Override
        public void close() {
            MinecraftClient.getInstance().setScreen(parent);
            SkyBlockTweaks.CONFIG.HANDLER.save();
        }
        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            boolean b = super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
            if (this.mode != Mode.DRAG) return b;
            if (selectedElement != null) {
                selectedElement.updatePosition(HUD.getRelativeX(mouseX), HUD.getRelativeY(mouseY));
            }
            return b;
        }
        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            boolean b = super.mouseClicked(mouseX, mouseY, button);
            selectedViaTheKeyboard = null;
            for (HUD hud : huds) {
                if (clickInBounds(hud, mouseX, mouseY)) {
                    if (this.hasControlDown() || this.mode == Mode.TEXT) {
                        enableTextMode(hud);
                        return b;
                    }
                    if (this.mode == Mode.DRAG) selectedElement = hud;
                }
            }
            return b;



        }
        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            boolean x = super.mouseReleased(mouseX, mouseY, button);
            if (this.mode == Mode.DRAG) selectedElement = null;
            return x;
        }
        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {

            if (keyCode == GLFW.GLFW_KEY_R && this.hasControlDown()) {
                setMode(Mode.RESET);
            }
            if (this.mode == Mode.DRAG) {
                if (this.hoveredElement != null) {
                    if (keyCode == GLFW.GLFW_KEY_UP) {
                        var bounds = hoveredElement.getCurrentBoundsRelative();
                        hoveredElement.updatePosition(bounds.x, bounds.y - getMoveAmmount());
                        selectedViaTheKeyboard = hoveredElement;
                    } else if (keyCode == GLFW.GLFW_KEY_DOWN) {
                        var bounds = hoveredElement.getCurrentBoundsRelative();
                        hoveredElement.updatePosition(bounds.x, bounds.y + getMoveAmmount());
                        selectedViaTheKeyboard = hoveredElement;
                    } else if (keyCode == GLFW.GLFW_KEY_LEFT) {
                        var bounds = hoveredElement.getCurrentBoundsRelative();
                        hoveredElement.updatePosition(bounds.x - getMoveAmmount(), bounds.y);
                        selectedViaTheKeyboard = hoveredElement;
                    } else if (keyCode == GLFW.GLFW_KEY_RIGHT) {
                        var bounds = hoveredElement.getCurrentBoundsRelative();
                        hoveredElement.updatePosition(bounds.x + getMoveAmmount(), bounds.y);
                        selectedViaTheKeyboard = hoveredElement;
                    } else if (keyCode == GLFW.GLFW_KEY_EQUAL) {
                        hoveredElement.updateScale(hoveredElement.getCurrentBoundsRelative().scale + 0.1f);
                        selectedViaTheKeyboard = hoveredElement;
                    } else if (keyCode == GLFW.GLFW_KEY_MINUS) {
                        hoveredElement.updateScale(hoveredElement.getCurrentBoundsRelative().scale - 0.1f);
                        selectedViaTheKeyboard = hoveredElement;
                    }
                } else if (this.selectedViaTheKeyboard != null) {
                    if (keyCode == GLFW.GLFW_KEY_UP) {
                        var bounds = selectedViaTheKeyboard.getCurrentBoundsRelative();
                        selectedViaTheKeyboard.updatePosition(bounds.x, bounds.y - getMoveAmmount());
                    } else if (keyCode == GLFW.GLFW_KEY_DOWN) {
                        var bounds = selectedViaTheKeyboard.getCurrentBoundsRelative();
                        selectedViaTheKeyboard.updatePosition(bounds.x, bounds.y + getMoveAmmount());
                    } else if (keyCode == GLFW.GLFW_KEY_LEFT) {
                        var bounds = selectedViaTheKeyboard.getCurrentBoundsRelative();
                        selectedViaTheKeyboard.updatePosition(bounds.x - getMoveAmmount(), bounds.y);
                    } else if (keyCode == GLFW.GLFW_KEY_RIGHT) {
                        var bounds = selectedViaTheKeyboard.getCurrentBoundsRelative();
                        selectedViaTheKeyboard.updatePosition(bounds.x + getMoveAmmount(), bounds.y);
                    } else if (keyCode == GLFW.GLFW_KEY_EQUAL) {
                        selectedViaTheKeyboard.updateScale(selectedViaTheKeyboard.getCurrentBoundsRelative().scale + 0.1f);
                    } else if (keyCode == GLFW.GLFW_KEY_MINUS) {
                        selectedViaTheKeyboard.updateScale(selectedViaTheKeyboard.getCurrentBoundsRelative().scale - 0.1f);
                    }
                }
            }
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
            boolean b = super.mouseScrolled(mouseX, mouseY, horizontal, vertical);
            if (hoveredElement != null) {
                hoveredElement.updateScale(hoveredElement.getCurrentBoundsRelative().scale + (float) vertical / 10);
            }
            return b;
        }

        public static boolean clickInBounds(HUD hud, double mouseX, double mouseY) {
            var bounds = hud.getCurrentBounds();
            return mouseX >= bounds.x && mouseX <= bounds.x + bounds.width && mouseY >= bounds.y && mouseY <= bounds.y + bounds.height;
        }

        public static float getMoveAmmount() {
            return 2.0f / MinecraftClient.getInstance().getWindow().getScaledWidth();
        }

        private static class NumberPredicate implements Predicate<String> {
            @Override
            public boolean test(String s) {
                if (s.isEmpty()) return true; // allow empty strings
                if (s == "." || s.startsWith(".")) return false; // don't allow just a dot
                if (s.endsWith(".")) s= s + "0"; // allow trailing dots
                try {
                    var f = Float.parseFloat(s);
                    if (f < 0 || f > 1) return false;
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        }
        private static enum Mode {
            DRAG,
            TEXT,
            RESET
        }




}
