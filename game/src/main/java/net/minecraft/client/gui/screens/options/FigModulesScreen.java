package net.minecraft.client.gui.screens.options;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class FigModulesScreen extends Screen {
   private static final Component TITLE = Component.literal("FIG CLIENT").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD);
   private static final int TOP_BAR_HEIGHT = 24;
   private static final int COLUMN_GAP = 4;
   private static final int PANEL_GAP = 8;
   private static final int EDGE_MARGIN = 8;
   private static final int MIN_COLUMN_WIDTH = 60;
   private static final int PANEL_HEADER_HEIGHT = 17;
   private static final int MAX_COLUMN_WIDTH = 84;
   private static final int ROW_HEIGHT = 14;
   private final Screen previousScreen;
   private final Options options;
   private final List<ModuleEntry> modules = new ArrayList<>();
   private final List<PlainTextButton> moduleButtons = new ArrayList<>();
   private final List<PlainTextButton> actionButtons = new ArrayList<>();
   private final List<PlainTextButton> navigationButtons = new ArrayList<>();
   private String filter = "";
   private EditBox searchBox;
   private int columnWidth;
   private int firstColumnX;
   private int visibleModules;
   private int columnsPerRow;
   private int gridRowCount;
   private int[] panelHeights = new int[Category.values().length + 1];
   private int[] panelRowTops = new int[1];
   private int[] panelRowHeights = new int[1];
   private int rowHeight;
   private int visibleRowsPerPanel;
   private int[] scrollOffsets = new int[Category.values().length + 1];

   private enum Category {
      COMBAT("Combat"),
      PLAYER("Player"),
      MOVEMENT("Movement"),
      RENDER("Render"),
      WORLD("World"),
      TOOLS("Tools"),
      MISC("Misc");

      private final String label;

      Category(final String label) {
         this.label = label;
      }
   }

   private record ModuleEntry(String name, Category category, OptionInstance<Boolean> option, KeyMapping keyMapping) {
   }

   public FigModulesScreen(final Screen lastScreen, final Options options) {
      super(TITLE);
      this.previousScreen = lastScreen;
      this.options = options;
      this.modules.add(new ModuleEntry("Auto Totem", Category.COMBAT, options.figClientAutoTotem(), options.keyFigAutoTotem));
      this.modules.add(new ModuleEntry("Entity ESP", Category.COMBAT, options.figClientEntityEsp(), options.keyFigEntityEsp));
      this.modules.add(new ModuleEntry("Inventory Alerts", Category.PLAYER, options.figClientInventoryAlerts(), options.keyFigInventoryAlerts));
      this.modules.add(new ModuleEntry("Food Info", Category.PLAYER, options.figClientFoodInfo(), options.keyFigFoodInfo));
      this.modules.add(new ModuleEntry("Air Place", Category.PLAYER, options.figClientAirPlace(), options.keyFigAirPlace));
      this.modules.add(new ModuleEntry("Auto Refill", Category.PLAYER, options.figClientAutoRefill(), options.keyFigAutoRefill));
      this.modules.add(new ModuleEntry("Inventory Sort", Category.TOOLS, options.figClientInventorySort(), options.keyFigInventorySort));
      this.modules.add(new ModuleEntry("Flight", Category.MOVEMENT, options.figClientFlight(), options.keyFigFlight));
      this.modules.add(new ModuleEntry("Freecam", Category.MOVEMENT, options.figClientFreecam(), options.keyFigFreecam));
      this.modules.add(new ModuleEntry("Fullbright", Category.RENDER, options.figClientFullbright(), options.keyFigFullbright));
      this.modules.add(new ModuleEntry("Zoom", Category.RENDER, options.figClientZoom(), options.keyFigZoom));
      this.modules.add(new ModuleEntry("Equipment HUD", Category.RENDER, options.figClientEquipmentHud(), options.keyFigEquipmentHud));
      this.modules.add(new ModuleEntry("Entity Culling", Category.RENDER, options.figClientEntityCulling(), options.keyFigEntityCulling));
      this.modules.add(new ModuleEntry("Chest ESP", Category.TOOLS, options.figClientChestEsp(), options.keyFigChestEsp));
      this.modules.add(new ModuleEntry("Tracers", Category.RENDER, options.figClientEspTracers(), options.keyFigTracers));
      this.modules.add(new ModuleEntry("X-Ray", Category.RENDER, options.figClientXray(), options.keyFigXray));
      this.modules.add(new ModuleEntry("Chunk Saver", Category.WORLD, options.figClientChunkSaver(), options.keyToggleChunkSaver));
      this.modules.add(new ModuleEntry("City ESP", Category.WORLD, options.figClientCityEsp(), options.keyFigCityEsp));
      this.modules.add(new ModuleEntry("Compass", Category.MISC, options.figClientCompass(), options.keyFigCompass));
   }

   @Override
   protected void init() {
      this.moduleButtons.clear();
      this.actionButtons.clear();
      this.navigationButtons.clear();
      this.calculateLayout();

      int navigationGap = 8;
      String[] navigationLabels = {"Modules", "Config", "GUI", "HUD", "Friends", "Macros", "Profiles"};
      int navigationWidth = navigationGap * (navigationLabels.length - 1);
      for (String label : navigationLabels) {
         navigationWidth += this.font.width(label) + 8;
      }
      int navigationX = Math.max(EDGE_MARGIN, (this.width - navigationWidth) / 2);
      this.addNavigationButton("Modules", navigationX, () -> {});
      navigationX += this.font.width("Modules") + 8 + navigationGap;
      this.addNavigationButton("Config", navigationX, () -> this.minecraft.gui.setScreen(new OptionsScreen(this, this.options, true)));
      navigationX += this.font.width("Config") + 8 + navigationGap;
      this.addNavigationButton("GUI", navigationX, () -> this.minecraft.gui.setScreen(new FigHudLayoutScreen(this, this.options)));
      navigationX += this.font.width("GUI") + 8 + navigationGap;
      this.addNavigationButton("HUD", navigationX, () -> this.minecraft.gui.setScreen(new FigHudLayoutScreen(this, this.options)));
      navigationX += this.font.width("HUD") + 8 + navigationGap;
      this.addNavigationButton("Friends", navigationX, () -> {});
      navigationX += this.font.width("Friends") + 8 + navigationGap;
      this.addNavigationButton("Macros", navigationX, () -> {});
      navigationX += this.font.width("Macros") + 8 + navigationGap;
      this.addNavigationButton("Profiles", navigationX, () ->
         this.minecraft.gui.setScreen(new net.lax1dude.eaglercraft.v1_8.profile.EaglerProfileScreen26(this)));

      int searchColumn = Category.values().length;
      int searchX = this.columnX(searchColumn);
      int searchTop = this.panelTop(searchColumn) + PANEL_HEADER_HEIGHT + 4;
      int searchHeight = Math.max(12, Math.min(20, this.rowHeight + 3));
      this.searchBox = new EditBox(this.font, searchX + 4, searchTop, Math.max(1, this.columnWidth - 8), searchHeight, Component.literal("Search"));
      this.searchBox.setMaxLength(64);
      this.searchBox.setValue(this.filter);
      this.searchBox.setResponder(value -> {
         this.filter = value;
         this.updateVisibleModules();
      });
      this.addRenderableWidget(this.searchBox);

      for (ModuleEntry module : this.modules) {
         PlainTextButton button = this.addRenderableWidget(new PlainTextButton(0, 0, this.columnWidth - 8, ROW_HEIGHT,
            this.moduleLabel(module), pressed -> {
               module.option().set(!module.option().get());
               if (module.name().equals("Flight")) {
                  Minecraft.getInstance().setFigFlightEnabled(module.option().get());
               } else if (module.name().equals("Freecam")) {
                  Minecraft.getInstance().setFigFreecamEnabled(module.option().get());
               }
               this.options.save();
               this.refreshModuleLabels();
            }, this.font));
         this.moduleButtons.add(button);
         button.setWidth(Math.max(1, this.columnWidth - 8));
         button.setHeight(this.rowHeight);
      }

      this.actionButtons.add(this.addRenderableWidget(new PlainTextButton(0, 0, this.columnWidth - 8, ROW_HEIGHT,
         Component.literal("Waypoints"), button -> this.minecraft.gui.setScreen(new FigWaypointsScreen(this, this.options)), this.font)));
      this.actionButtons.add(this.addRenderableWidget(new PlainTextButton(0, 0, this.columnWidth - 8, ROW_HEIGHT,
         Component.literal("Block Search"), button -> this.minecraft.gui.setScreen(new FigBlockSearch(this, this.options)), this.font)));
      this.actionButtons.add(this.addRenderableWidget(new PlainTextButton(0, 0, this.columnWidth - 8, ROW_HEIGHT,
         Component.literal("HUD Layout"), button -> this.minecraft.gui.setScreen(new FigHudLayoutScreen(this, this.options)), this.font)));
      this.actionButtons.add(this.addRenderableWidget(new PlainTextButton(0, 0, this.columnWidth - 8, ROW_HEIGHT,
         this.totemHealthLabel(), button -> {
            int health = this.options.figClientAutoTotemHealth().get();
            this.options.figClientAutoTotemHealth().set(health >= 20 ? 2 : health + 2);
            button.setMessage(this.totemHealthLabel());
            this.options.save();
         }, this.font)));
      this.updateVisibleModules();
   }

   private void calculateLayout() {
      int columnCount = Category.values().length + 1;
      int availableWidth = Math.max(1, this.width - EDGE_MARGIN * 2);
      this.columnsPerRow = Math.min(columnCount,
         Math.max(1, (availableWidth + COLUMN_GAP) / (MIN_COLUMN_WIDTH + COLUMN_GAP)));
      this.columnWidth = Math.min(MAX_COLUMN_WIDTH,
         Math.max(1, (availableWidth - COLUMN_GAP * (this.columnsPerRow - 1)) / this.columnsPerRow));
      int totalWidth = this.columnsPerRow * this.columnWidth + (this.columnsPerRow - 1) * COLUMN_GAP;
      this.firstColumnX = Math.max(EDGE_MARGIN, (this.width - totalWidth) / 2);
      this.gridRowCount = (columnCount + this.columnsPerRow - 1) / this.columnsPerRow;
      int verticalGaps = PANEL_GAP * (this.gridRowCount - 1);
      int availableHeight = Math.max(1, this.height - (TOP_BAR_HEIGHT + 24) - 22 - verticalGaps - EDGE_MARGIN);
      int panelRowSpace = Math.max(1, availableHeight / this.gridRowCount);
      int compactRowSpace = Math.max(9, panelRowSpace - PANEL_HEADER_HEIGHT - 8);
      this.rowHeight = Math.min(ROW_HEIGHT, compactRowSpace);
      this.visibleRowsPerPanel = Math.max(1, (panelRowSpace - PANEL_HEADER_HEIGHT - 8) / this.rowHeight);
      if (this.scrollOffsets.length != columnCount) {
         this.scrollOffsets = new int[columnCount];
      }

      this.panelHeights = new int[columnCount];
      this.panelRowTops = new int[this.gridRowCount];
      this.panelRowHeights = new int[this.gridRowCount];
      int searchColumn = Category.values().length;
      int searchHeight = Math.max(12, Math.min(20, this.rowHeight + 3));
      for (int column = 0; column < columnCount; column++) {
         int count = this.moduleCountForColumn(column);
         int rows = Math.min(count, this.visibleRowsPerPanel);
         int searchExtra = column == searchColumn ? searchHeight + 8 : 0;
         this.panelHeights[column] = PANEL_HEADER_HEIGHT + 8 + searchExtra + rows * this.rowHeight;
         this.panelRowHeights[column / this.columnsPerRow] = Math.max(
            this.panelRowHeights[column / this.columnsPerRow], this.panelHeights[column]);
      }
      int top = TOP_BAR_HEIGHT + 24;
      for (int row = 0; row < this.gridRowCount; row++) {
         this.panelRowTops[row] = top;
         top += this.panelRowHeights[row] + PANEL_GAP;
      }
   }

   private int moduleCountForColumn(final int column) {
      if (column == Category.values().length) {
         return 0;
      }
      Category category = Category.values()[column];
      int count = 0;
      for (ModuleEntry module : this.modules) {
         if (module.category() == category) {
            count++;
         }
      }
      if (category == Category.TOOLS) {
         count += 3;
      } else if (category == Category.COMBAT && this.options.figClientAutoTotem().get()) {
         count++;
      }
      return count;
   }

   private int columnX(final int column) {
      int wrappedColumn = column % this.columnsPerRow;
      return this.firstColumnX + wrappedColumn * (this.columnWidth + COLUMN_GAP);
   }

   private int panelTop(final int column) {
      int panelRow = column / this.columnsPerRow;
      return this.panelRowTops[panelRow];
   }

   private int panelHeight(final int column) {
      return this.panelHeights[column];
   }

   private int panelColumnForY(final double y) {
      for (int row = 0; row < this.gridRowCount; row++) {
         int top = this.panelRowTops[row];
         int bottom = top + this.panelRowHeights[row];
         if (y >= top && y < bottom) {
            return row;
         }
      }
      return -1;
   }

   private void addNavigationButton(final String label, final int x, final Runnable action) {
      PlainTextButton button = this.addRenderableWidget(new PlainTextButton(x, 5, this.font.width(label) + 8, 15,
         Component.literal(label).withStyle(label.equals("Modules") ? ChatFormatting.AQUA : ChatFormatting.WHITE), pressed -> action.run(), this.font));
      this.navigationButtons.add(button);
   }

   private Component moduleLabel(final ModuleEntry module) {
      String label = switch (module.name()) {
         case "Inventory Alerts" -> "Inv Alerts";
         case "Inventory Sort" -> "Inv Sort";
         case "Equipment HUD" -> "Equip HUD";
         default -> module.name();
      };
      return Component.literal(label).withStyle(module.option().get() ? ChatFormatting.AQUA : ChatFormatting.GRAY);
   }

   private Component totemHealthLabel() {
      return Component.literal("Totem " + this.options.figClientAutoTotemHealth().get() / 2.0 + " hearts");
   }

   private void refreshModuleLabels() {
      for (int i = 0; i < this.modules.size(); i++) {
         this.moduleButtons.get(i).setMessage(this.moduleLabel(this.modules.get(i)));
      }
   }

   private void updateVisibleModules() {
      String query = this.filter.trim().toLowerCase(Locale.ROOT);
      int[] rows = new int[Category.values().length + 1];
      int matches = 0;
      for (int i = 0; i < this.modules.size(); i++) {
         ModuleEntry module = this.modules.get(i);
         Button button = this.moduleButtons.get(i);
         boolean visible = query.isEmpty() || module.name().toLowerCase(Locale.ROOT).contains(query);
         int column = module.category().ordinal();
         if (visible) {
            int row = rows[column]++;
            button.visible = row >= this.scrollOffsets[column] && row < this.scrollOffsets[column] + this.visibleRowsPerPanel;
            button.setWidth(Math.max(1, this.columnWidth - 8));
            button.setHeight(this.rowHeight);
            button.setPosition(this.columnX(column) + 4,
               this.panelTop(column) + PANEL_HEADER_HEIGHT + 4 + (row - this.scrollOffsets[column]) * this.rowHeight);
            matches++;
         } else {
            button.visible = false;
         }
      }
      this.visibleModules = 0;
      for (int i = 0; i < this.actionButtons.size(); i++) {
         PlainTextButton button = this.actionButtons.get(i);
         boolean visible = query.isEmpty();
         if (i == 3) {
            visible = query.isEmpty() && this.options.figClientAutoTotem().get();
         }
         button.visible = visible;
         if (visible) {
            int column = i == 3 ? Category.COMBAT.ordinal() : Category.TOOLS.ordinal();
            int row = rows[column]++;
            button.visible = row >= this.scrollOffsets[column] && row < this.scrollOffsets[column] + this.visibleRowsPerPanel;
            button.setWidth(Math.max(1, this.columnWidth - 8));
            button.setHeight(this.rowHeight);
            button.setPosition(this.columnX(column) + 4,
               this.panelTop(column) + PANEL_HEADER_HEIGHT + 4 + (row - this.scrollOffsets[column]) * this.rowHeight);
         }
      }
      this.visibleModules = matches;
   }

   @Override
   public boolean mouseScrolled(final double x, final double y, final double scrollX, final double scrollY) {
      if (scrollY == 0.0 || x < this.firstColumnX || x >= this.firstColumnX + this.columnsPerRow * this.columnWidth
            + (this.columnsPerRow - 1) * COLUMN_GAP) {
         return super.mouseScrolled(x, y, scrollX, scrollY);
      }
      int columnInRow = Math.min(this.columnsPerRow - 1,
         (int)(x - this.firstColumnX) / (this.columnWidth + COLUMN_GAP));
      int panelRow = this.panelColumnForY(y);
      if (panelRow < 0 || panelRow >= this.gridRowCount) {
         return super.mouseScrolled(x, y, scrollX, scrollY);
      }
      int column = panelRow * this.columnsPerRow + columnInRow;
      if (column >= this.scrollOffsets.length || y < this.panelTop(column) + PANEL_HEADER_HEIGHT) {
         return super.mouseScrolled(x, y, scrollX, scrollY);
      }
      int rowCount = column == Category.values().length
         ? this.filter.isEmpty() ? 0 : this.visibleModules
         : this.moduleCountForColumn(column);
      int maximumOffset = Math.max(0, rowCount - this.visibleRowsPerPanel);
      this.scrollOffsets[column] = Math.max(0, Math.min(maximumOffset,
         this.scrollOffsets[column] + (scrollY < 0.0 ? 1 : -1)));
      this.updateVisibleModules();
      return true;
   }

   @Override
   public boolean mouseClicked(final MouseButtonEvent event, final boolean doubleClick) {
      if (event.button() == 1) {
         for (int i = 0; i < this.moduleButtons.size(); i++) {
            PlainTextButton button = this.moduleButtons.get(i);
            if (button.visible && button.isMouseOver(event.x(), event.y())) {
               String moduleName = this.modules.get(i).name();
               this.minecraft.gui.setScreen(new FigEspSettingsScreen(this, this.options, moduleName, this.modules.get(i).keyMapping()));
               return true;
            }
         }
      }
      return super.mouseClicked(event, doubleClick);
   }

   @Override
   public void extractBackground(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float partialTick) {
      super.extractBackground(graphics, mouseX, mouseY, partialTick);
      for (int column = 0; column < Category.values().length + 1; column++) {
         int x = this.columnX(column);
         int y = this.panelTop(column);
         graphics.fill(x, y, x + this.columnWidth, y + this.panelHeight(column), 0xB50B171E);
         graphics.fill(x, y, x + this.columnWidth, y + PANEL_HEADER_HEIGHT, 0xD22B5666);
         graphics.fill(x, y, x + this.columnWidth, y + 1, 0xFF54DDF4);
         graphics.outline(x, y, this.columnWidth, this.panelHeight(column), 0xA531515E);
      }
   }

   @Override
   public void extractRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float partialTick) {
      super.extractRenderState(graphics, mouseX, mouseY, partialTick);
      for (int i = 0; i < Category.values().length; i++) {
         int x = this.columnX(i) + 7;
         int y = this.panelTop(i) + 5;
         graphics.text(this.font, Category.values()[i].label, x, y, 0xFFFFFFFF);
      }
      int searchColumn = Category.values().length;
      graphics.text(this.font, "Search", this.columnX(searchColumn) + 7, this.panelTop(searchColumn) + 5, 0xFFFFFFFF);
      graphics.text(this.font, "Fig Client 26.2", 7, this.height - 12, 0xFFB9D8E8);
      graphics.centeredText(this.font, "Scroll panels", this.width / 2, this.height - 12, 0xFF9ABAC7);
      graphics.text(this.font, "Right click to edit", this.width - this.font.width("Right click to edit") - 7, this.height - 12, 0xFFB9D8E8);
      if (!this.filter.isEmpty() && this.visibleModules == 0) {
         graphics.centeredText(this.font, "No matching modules", this.columnX(searchColumn) + this.columnWidth / 2,
            this.panelTop(searchColumn) + PANEL_HEADER_HEIGHT + 12, 0xFFB9D8E8);
      }
   }

   @Override
   public void onClose() {
      this.minecraft.gui.setScreen(this.previousScreen);
   }
}
