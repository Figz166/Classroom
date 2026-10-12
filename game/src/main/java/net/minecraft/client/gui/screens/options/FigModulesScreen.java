package net.minecraft.client.gui.screens.options;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class FigModulesScreen extends OptionsSubScreen {
   private static final Component TITLE = Component.literal("Fig Modules");

   public FigModulesScreen(final Screen lastScreen, final Options options) {
      super(lastScreen, options, TITLE);
   }

   @Override
   protected void addOptions() {
      this.list.addBig(this.options.figClientFullbright());
      this.list.addBig(this.options.figClientChunkSaver());
      this.list.addBig(this.options.figClientEntityCulling());
      this.list.addBig(this.options.figClientAutoRefill());
      Button dangerHeader = Button.builder(Component.literal("DANGER: Singleplayer Only"), button -> {})
         .width(310)
         .build();
      dangerHeader.active = false;
      this.list.addBig(dangerHeader);
      this.list.addBig(this.options.figClientXray());
      Button freecamButton = Button.builder(Component.literal("Freecam (WIP)"), button -> {})
         .width(310)
         .build();
      freecamButton.active = false;
      this.list.addBig(freecamButton);
      Button blueprintButton = Button.builder(Component.literal("Blueprints (WIP)"), button -> {})
         .width(310)
         .build();
      blueprintButton.active = false;
      this.list.addBig(blueprintButton);
      this.list.addBig(this.options.hideSplashTexts());
      this.list.addBig(this.options.vignette());
      this.list.addBig(this.options.gamma());

      this.list.addSmall(new OptionInstance[]{
         this.options.renderDistance(),
         this.options.simulationDistance(),
         this.options.framerateLimit(),
         this.options.cloudStatus(),
         this.options.particles(),
         this.options.ambientOcclusion(),
         this.options.eaglerOptimizedShader(),
         this.options.entityShadows(),
         this.options.entityDistanceScaling(),
         this.options.showPerformanceCounters()
      });

      this.list.addSmall(new OptionInstance[]{
         this.options.menuBackgroundBlurriness(),
         this.options.bobView(),
         this.options.textureFiltering(),
         this.options.maxAnisotropyBit(),
         this.options.cutoutLeaves(),
         this.options.improvedTransparency(),
         this.options.cloudRange(),
         this.options.weatherRadius(),
         this.options.hideLightningFlash(),
         this.options.toggleCrouch(),
         this.options.toggleSprint()
      });
   }
}
