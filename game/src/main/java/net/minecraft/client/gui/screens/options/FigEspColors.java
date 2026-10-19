package net.minecraft.client.gui.screens.options;

public final class FigEspColors {
   private static final int[] COLORS = {0xFF54DDF4, 0xFFFF657A, 0xFF7FE08A, 0xFFFFC857, 0xFFFFFFFF};
   private static final String[] NAMES = {"Cyan", "Red", "Green", "Gold", "White"};

   private FigEspColors() {
   }

   public static int color(final int index) {
      return COLORS[Math.floorMod(index, COLORS.length)];
   }

   public static String name(final int index) {
      return NAMES[Math.floorMod(index, NAMES.length)];
   }
}