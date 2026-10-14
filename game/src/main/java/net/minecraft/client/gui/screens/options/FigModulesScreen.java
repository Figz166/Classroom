package net.minecraft.client.gui.screens.options;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class FigModulesScreen extends OptionsSubScreen {
   private static final Component TITLE = Component.literal("Fig Client Modules").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD);
   private static final Component VISUALS_HEADER = Component.literal("VISUALS").withStyle(ChatFormatting.AQUA);
   private static final Component PERFORMANCE_HEADER = Component.literal("PERFORMANCE").withStyle(ChatFormatting.AQUA);
   private static final Component QUALITY_OF_LIFE_HEADER = Component.literal("QUALITY OF LIFE").withStyle(ChatFormatting.AQUA);
   private static final Component SINGLEPLAYER_HEADER = Component.literal("SINGLEPLAYER ONLY").withStyle(ChatFormatting.AQUA);

   public FigModulesScreen(final Screen lastScreen, final Options options) {
      super(lastScreen, options, TITLE);
   }

   @Override
   protected void addOptions() {
      this.list.addHeader(VISUALS_HEADER);
      this.list.addBig(this.options.figClientFullbright());
      this.list.addBig(this.options.figClientZoom());
      this.list.addBig(this.options.figClientCompass());

      this.list.addHeader(PERFORMANCE_HEADER);
      this.list.addBig(this.options.figClientChunkSaver());
      this.list.addBig(this.options.figClientEntityCulling());

      this.list.addHeader(QUALITY_OF_LIFE_HEADER);
      this.list.addBig(this.options.figClientAutoRefill());
      this.list.addBig(this.options.figClientInventoryAlerts());
      this.list.addBig(this.options.figClientEquipmentHud());

      this.list.addHeader(SINGLEPLAYER_HEADER);
      this.list.addBig(this.options.figClientXray());

      this.list.addHeader(Component.literal("WAYPOINTS").withStyle(ChatFormatting.AQUA));
      this.list.addBig(
         Button.builder(Component.literal("Open Waypoint Manager"), button -> Minecraft.getInstance().gui.setScreen(new FigWaypointsScreen(this, this.options)))
            .width(310)
            .build()
      );

      this.list.addHeader(Component.literal("TOOLS").withStyle(ChatFormatting.AQUA));
      this.list.addBig(
         Button.builder(Component.literal("Open Block Search"), button -> Minecraft.getInstance().gui.setScreen(new FigBlockSearch(this, this.options)))
            .width(310)
            .build()
      );
   }

   @Override
   public void extractBackground(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      super.extractBackground(graphics, mouseX, mouseY, a);
      graphics.fill(0, 0, this.width, this.height, 0x26328CB8);
   }

   @Override
   public void extractRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      super.extractRenderState(graphics, mouseX, mouseY, a);
      graphics.fill(this.width / 2 - 100, 28, this.width / 2 + 100, 30, 0xFF7CD8F3);
   }
}
