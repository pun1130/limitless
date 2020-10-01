package user11681.limitless.config;

import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.AbstractListListEntry;
import me.shedaniel.clothconfig2.gui.entries.TooltipListEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import user11681.limitless.config.EnchantingBlockListListEntry.EnchantingBlockListCell;
import user11681.limitless.enchantment.EnchantingBlockEntry;

@SuppressWarnings("UnstableApiUsage")
public class EnchantingBlockListListEntry extends AbstractListListEntry<EnchantingBlockEntry, EnchantingBlockListCell, EnchantingBlockListListEntry> {
    public EnchantingBlockListListEntry(final Text fieldName, final List<EnchantingBlockEntry> value, final boolean defaultExpanded, final Supplier<Optional<Text[]>> tooltipSupplier, final Consumer<List<EnchantingBlockEntry>> saveConsumer, final Supplier<List<EnchantingBlockEntry>> defaultValue, final Text resetButtonKey, final boolean requiresRestart, final boolean deleteButtonEnabled, final boolean insertInFront, final BiFunction<EnchantingBlockEntry, EnchantingBlockListListEntry, EnchantingBlockListCell> createNewCell) {
        super(fieldName, value, defaultExpanded, tooltipSupplier, saveConsumer, defaultValue, resetButtonKey, requiresRestart, deleteButtonEnabled, insertInFront, createNewCell);
    }

    @Override
    @Nullable
    public Text getRemoveTooltip() {
        return super.getRemoveTooltip();
    }

    @Override
    public EnchantingBlockListListEntry self() {
        return this;
    }

    public static class EnchantingBlockListCell extends AbstractListCell<EnchantingBlockEntry, EnchantingBlockListCell, EnchantingBlockListListEntry> {
        protected static final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        protected final ReferenceArrayList<TooltipListEntry<?>> children = ReferenceArrayList.wrap(new TooltipListEntry[2], 0);

        protected EnchantingBlockEntry entry;

        public EnchantingBlockListCell(@Nullable EnchantingBlockEntry value, final EnchantingBlockListListEntry listListEntry) {
            super(value, listListEntry);

            if (value == null) {
                value = new EnchantingBlockEntry("bookshelf", 2);
            }

            this.entry = value;

            final ConfigEntryBuilder entryBuilder = ConfigEntryBuilder.create();

            listListEntry.widgets.add(entryBuilder.startSubCategory(new LiteralText("subcategory"), ReferenceArrayList.wrap(new AbstractConfigListEntry[]{
//                entryBuilder.startDropdownMenu(new LiteralText("1279866"), DropdownMenuBuilder.TopCellElementBuilder.ofBlockObject(value.getBlock()), DropdownMenuBuilder.CellCreatorBuilder.ofBlockObject())/*.setDefaultValue(Blocks.BOOKSHELF)*/.setSelections(Registry.BLOCK.stream().sorted(Comparator.comparing(Block::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build(),
//                entryBuilder.startFloatField(new LiteralText("test12345"), value.power).build()
            })).build());
        }

        @Override
        public EnchantingBlockEntry getValue() {
            return this.entry;
        }

        @Override
        public Optional<Text> getError() {
            return Optional.empty();
        }

        @Override
        public int getCellHeight() {
            return 20;
        }

        @Override
        public void render(final MatrixStack matrices, final int index, final int y, final int x, final int entryWidth, final int entryHeight, final int mouseX, final int mouseY, final boolean isSelected, final float delta) {
            textRenderer.draw(matrices, this.getValue().getBlock().getName(), x, y + 1, 0xFFFFFF);

            if (isSelected && listListEntry.isEditable()) {
                fill(matrices, x, y + 12, x + entryWidth - 12, y + 13, getConfigError().isPresent() ? 0xffff5555 : 0xffe0e0e0);
            }

            for (final TooltipListEntry<?> child : this.children) {
//                child.render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isSelected, delta);
            }
        }

        @Override
        public List<? extends Element> children() {
            return this.children;
        }
    }
}
