package net.minecraft.client.gui.screens.options;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;

public class FigEspSettingsScreen extends Screen {
   private final Screen previousScreen;
   private final Options options;
   private final String moduleName;
   private final KeyMapping keyMapping;
   private Button keyBindButton;
   private boolean waitingForKey;
   private int panelX;
   private int panelY;
   private int panelWidth;
   private int panelHeight;

   public FigEspSettingsScreen(final Screen previousScreen, final Options options, final String moduleName, final KeyMapping keyMapping) {
      super(Component.literal(moduleName.toUpperCase(java.util.Locale.ROOT)));
      this.previousScreen = previousScreen;
      this.options = options;
      this.moduleName = moduleName;
      this.keyMapping = keyMapping;
   }

   @Override
   protected void init() {
      this.panelWidth = Math.min(380, this.width - 24);
      this.panelHeight = Math.min(470, this.height - 24);
      this.panelX = (this.width - this.panelWidth) / 2;
      this.panelY = (this.height - this.panelHeight) / 2;
      int left = this.panelX + 14;
      int rowY = this.panelY + 76;
      int halfWidth = (this.panelWidth - 36) / 2;

      this.keyBindButton = this.addRenderableWidget(Button.builder(this.keyBindLabel(), pressed -> {
         this.waitingForKey = true;
         pressed.setMessage(Component.literal("Press a key or mouse button..."));
      }).bounds(left, this.panelY + 45, this.panelWidth - 28, 23).build());

      if (this.moduleName.equals("Entity ESP")) {
         this.addFilterRow("Players", this.options.figClientEspPlayers(), this.options.figClientEspPlayerColor(), left, rowY, halfWidth);
         this.addFilterRow("Animals", this.options.figClientEspAnimals(), this.options.figClientEspAnimalColor(), left, rowY + 31, halfWidth);
         this.addFilterRow("Mobs", this.options.figClientEspMobs(), this.options.figClientEspMobColor(), left, rowY + 62, halfWidth);
         this.addFilterRow("Other entities", this.options.figClientEspOther(), this.options.figClientEspOtherColor(), left, rowY + 93, halfWidth);
      } else if (this.moduleName.equals("Tracers")) {
         this.addColorButton("Player", this.options.figClientTracerPlayerColor(), left, rowY, this.panelWidth - 28);
         this.addColorButton("Animals", this.options.figClientTracerAnimalColor(), left, rowY + 27, this.panelWidth - 28);
         this.addColorButton("Mobs", this.options.figClientTracerMobColor(), left, rowY + 54, this.panelWidth - 28);
         this.addColorButton("Other", this.options.figClientTracerOtherColor(), left, rowY + 81, this.panelWidth - 28);
         this.addToggleButton("Player", this.options.figClientTracerPlayerTarget(), left, rowY + 116, this.panelWidth - 28);
         this.addToggleButton("Pig", this.options.figClientTracerPig(), left, rowY + 139, this.panelWidth - 28);
         this.addToggleButton("Cow", this.options.figClientTracerCow(), left, rowY + 162, this.panelWidth - 28);
         this.addToggleButton("Sheep", this.options.figClientTracerSheep(), left, rowY + 185, this.panelWidth - 28);
         this.addToggleButton("Chicken", this.options.figClientTracerChicken(), left, rowY + 208, this.panelWidth - 28);
         this.addToggleButton("Zombie", this.options.figClientTracerZombie(), left, rowY + 231, this.panelWidth - 28);
         this.addToggleButton("Skeleton", this.options.figClientTracerSkeleton(), left, rowY + 254, this.panelWidth - 28);
         this.addToggleButton("Creeper", this.options.figClientTracerCreeper(), left, rowY + 277, this.panelWidth - 28);
         this.addToggleButton("Enderman", this.options.figClientTracerEnderman(), left, rowY + 300, this.panelWidth - 28);
         this.addToggleButton("Ender Dragon", this.options.figClientTracerDragon(), left, rowY + 323, this.panelWidth - 28);
      } else if (this.moduleName.equals("Chest ESP")) {
         this.addToggleButton("Normal chests", this.options.figClientEspNormalChests(), left, rowY, this.panelWidth - 28);
         this.addToggleButton("Ender chests", this.options.figClientEspEnderChests(), left, rowY + 27, this.panelWidth - 28);
         this.addToggleButton("Trapped chests", this.options.figClientEspTrappedChests(), left, rowY + 54, this.panelWidth - 28);
         this.addToggleButton("Barrels", this.options.figClientEspBarrels(), left, rowY + 81, this.panelWidth - 28);
         this.addToggleButton("Shulker boxes", this.options.figClientEspShulkers(), left, rowY + 108, this.panelWidth - 28);
         this.addColorButton("Chest ESP", this.options.figClientChestEspColor(), left, rowY + 139, this.panelWidth - 28);
      } else if (this.moduleName.equals("City ESP")) {
         this.addColorButton("City ESP", this.options.figClientCityEspColor(), left, rowY, this.panelWidth - 28);
      }

      this.addRenderableWidget(Button.builder(Component.literal("Done"), button -> this.onClose())
         .bounds(this.panelX + this.panelWidth - 88, this.panelY + this.panelHeight - 31, 74, 20).build());
   }

   private Component keyBindLabel() {
      return Component.literal("Keybind: ").append(this.keyMapping.getTranslatedKeyMessage());
   }

   @Override
   public boolean keyPressed(final KeyEvent event) {
      if (this.waitingForKey) {
         this.keyMapping.setKey(event.isEscape() ? InputConstants.UNKNOWN : InputConstants.getKey(event));
         this.finishKeyBinding();
         return true;
      }
      return super.keyPressed(event);
   }

   @Override
   public boolean mouseClicked(final MouseButtonEvent event, final boolean doubleClick) {
      if (this.waitingForKey) {
         this.keyMapping.setKey(InputConstants.Type.MOUSE.getOrCreate(event.button()));
         this.finishKeyBinding();
         return true;
      }
      return super.mouseClicked(event, doubleClick);
   }

   private void finishKeyBinding() {
      this.waitingForKey = false;
      KeyMapping.resetMapping();
      this.options.save();
      this.keyBindButton.setMessage(this.keyBindLabel());
   }

   private void addFilterRow(final String label, final OptionInstance<Boolean> enabled, final OptionInstance<Integer> color,
                             final int x, final int y, final int width) {
      this.addToggleButton(label, enabled, x, y, width);
      this.addColorButton(label, color, x + width + 8, y, width);
   }

   private void addToggleButton(final String label, final OptionInstance<Boolean> option, final int x, final int y, final int width) {
      this.addRenderableWidget(Button.builder(this.toggleLabel(label, option), pressed -> {
         option.set(!option.get());
         this.options.save();
         pressed.setMessage(this.toggleLabel(label, option));
      }).bounds(x, y, width, 23).build());
   }

   private void addColorButton(final String label, final OptionInstance<Integer> option, final int x, final int y, final int width) {
      this.addRenderableWidget(Button.builder(this.colorLabel(label, option), pressed -> {
         option.set((option.get() + 1) % 5);
         this.options.save();
         pressed.setMessage(this.colorLabel(label, option));
      }).bounds(x, y, width, 23).build());
   }

   private Component toggleLabel(final String label, final OptionInstance<Boolean> option) {
      return Component.literal(label + (option.get() ? "  ON" : "  OFF"))
         .withStyle(option.get() ? ChatFormatting.AQUA : ChatFormatting.GRAY);
   }

   private Component colorLabel(final String label, final OptionInstance<Integer> option) {
      int color = option.get();
      return Component.literal(label + " color: " + FigEspColors.name(color)).withColor(FigEspColors.color(color));
   }

   @Override
   public void extractBackground(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float partialTick) {
      super.extractBackground(graphics, mouseX, mouseY, partialTick);
      graphics.fill(this.panelX, this.panelY, this.panelX + this.panelWidth, this.panelY + this.panelHeight, 0xF20B171E);
      graphics.fill(this.panelX, this.panelY, this.panelX + this.panelWidth, this.panelY + 2, 0xFF7CD8F3);
      graphics.fill(this.panelX, this.panelY + 37, this.panelX + this.panelWidth, this.panelY + 38, 0xFF31515E);
      graphics.outline(this.panelX, this.panelY, this.panelWidth, this.panelHeight, 0xFF7CD8F3);
   }

   @Override
   public void extractRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float partialTick) {
      super.extractRenderState(graphics, mouseX, mouseY, partialTick);
      graphics.text(this.font, this.title.copy().withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD), this.panelX + 14, this.panelY + 14, 0xFFFFFFFF);
      if (this.waitingForKey) {
         graphics.text(this.font, "Press Escape to unbind", this.panelX + 14, this.panelY + this.panelHeight - 20, 0xFF9ABAC7);
      }
   }

   @Override
   public void onClose() {
      Minecraft.getInstance().gui.setScreen(this.previousScreen);
   }
}