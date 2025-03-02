package tc.oc.pgm.platform.modern.material;

import static tc.oc.pgm.util.platform.Supports.Variant.PAPER;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.entity.GlowItemFrame;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LeashHitch;
import org.bukkit.entity.Painting;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.Nullable;
import tc.oc.pgm.util.chunk.ChunkVector;
import tc.oc.pgm.util.material.BlockMaterialData;
import tc.oc.pgm.util.material.ItemMaterialData;
import tc.oc.pgm.util.material.MaterialMatcher;
import tc.oc.pgm.util.material.MaterialUtils;
import tc.oc.pgm.util.platform.Supports;
import tc.oc.pgm.util.xml.InvalidXMLException;
import tc.oc.pgm.util.xml.Node;

@Supports(value = PAPER, minVersion = "1.20.6")
@SuppressWarnings("deprecation")
public class ModernMaterialUtils implements MaterialUtils {

  @Override
  public BlockMaterialData createBlockData(Material material) {
    if (!material.isBlock()) {
      throw new IllegalStateException("Material " + material + " is not a block");
    }
    return new ModernBlockData(material.createBlockData());
  }

  @Override
  public BlockMaterialData createBlockData(BlockState block) {
    return new ModernBlockData(block.getBlockData());
  }

  @Override
  public BlockMaterialData createBlockData(ChunkSnapshot chunk, BlockVector chunkPos) {
    return new ModernBlockData(
        chunk.getBlockData(chunkPos.getBlockX(), chunkPos.getBlockY(), chunkPos.getBlockZ()));
  }

  @Override
  public BlockMaterialData getTo(EntityChangeBlockEvent event) {
    return new ModernBlockData(event.getBlockData());
  }

  @Override
  public ItemMaterialData createItemData(ItemStack item) {
    return new ModernItemData(item.getType());
  }

  @Override
  public ItemMaterialData createItemData(Material material, short data) {
    return ModernMaterialParser.parseItem(material, data);
  }

  @Override
  public ItemMaterialData createItemData(Hanging hanging) {
    return new ModernItemData(
        switch (hanging) {
          case Painting ignored -> Material.PAINTING;
          case GlowItemFrame ignored -> Material.GLOW_ITEM_FRAME;
          case ItemFrame ignored -> Material.ITEM_FRAME;
          case LeashHitch ignored -> Material.LEAD;
          default -> null;
        });
  }

  @Override
  public BlockMaterialData decode(int encoded) {
    return ModernEncodeUtil.decode(encoded);
  }

  @Override
  public Iterator<tc.oc.pgm.util.block.BlockData> iterator(
      Map<ChunkVector, ChunkSnapshot> chunks, Iterator<BlockVector> vectors) {
    return new BlockDataIterator(chunks, vectors);
  }

  @Override
  public Material parseMaterial(String text, @Nullable Node node) throws InvalidXMLException {
    return ModernMaterialParser.parseMaterial(text, node);
  }

  @Override
  public ItemMaterialData parseItemMaterialData(String text, @Nullable Node node)
      throws InvalidXMLException {
    return ModernMaterialParser.parseItem(text, node);
  }

  @Override
  public ItemMaterialData parseItemMaterialData(String text, short dmg, @Nullable Node node)
      throws InvalidXMLException {
    return ModernMaterialParser.parseItem(text, dmg, node);
  }

  @Override
  public BlockMaterialData parseBlockMaterialData(String text, @Nullable Node node)
      throws InvalidXMLException {
    var md = ModernMaterialParser.parseBlock(text, node);
    if (!md.getItemType().isBlock()) {
      throw new InvalidXMLException(
          "Material " + md.getItemType().name() + " is not a block", node);
    }
    return md;
  }

  @Override
  public BlockMaterialData fromLegacyBlock(Material material, byte data) {
    return new ModernBlockData(Bukkit.getUnsafe().fromLegacy(material, data));
  }

  @Override
  public Set<BlockMaterialData> getPossibleBlocks(Material material) {
    // Get all possible blockstates off of nms
    var block = CraftMagicNumbers.getBlock(material);
    var states = block.getStateDefinition().getPossibleStates();
    Set<BlockMaterialData> materials = new HashSet<>(states.size());
    for (var state : states) {
      materials.add(new ModernBlockData(state.createCraftBlockData()));
    }
    return materials;
  }

  @Override
  public boolean hasBlockStates(Material material) {
    var block = CraftMagicNumbers.getBlock(material);
    return !block.getStateDefinition().getPossibleStates().isEmpty();
  }

  @Override
  public MaterialMatcher.Builder matcherBuilder() {
    return new MaterialMatcherBuilderImpl();
  }

  private static class MaterialMatcherBuilderImpl extends MaterialMatcher.BuilderImpl
      implements ModernMaterialParser.Adapter<MaterialMatcher.Builder> {

    @Override
    public MaterialMatcher.Builder visit(Material material) {
      return addAll(ModernMaterialParser.flatten(material));
    }

    @Override
    public MaterialMatcher.Builder visit(Material material, short data) {
      BlockData bd = Bukkit.getUnsafe().fromLegacy(material, (byte) data);
      if (MATERIAL_UTILS.hasBlockStates(bd.getMaterial())) {
        return add(new BlockStateMaterialMatcher(bd));
      } else {
        return add(bd.getMaterial());
      }
    }

    @Override
    public MaterialMatcher.Builder add(Material material, boolean flatten) {
      // TODO: PLATFORM 1.20 - flatten non-legacy itemstack into list of modern
      return add(material);
    }

    @Override
    public MaterialMatcher.Builder add(ItemStack item, boolean flatten) {
      // TODO: PLATFORM 1.20 - flatten non-legacy itemstack into list of modern
      return add(item.getType());
    }

    @Override
    protected void parseSingle(String text, @Nullable Node node) throws InvalidXMLException {
      ModernMaterialParser.parse(text, node, materialsOnly, this);
    }
  }
}
