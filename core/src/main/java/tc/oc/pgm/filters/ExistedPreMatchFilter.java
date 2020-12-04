package tc.oc.pgm.filters;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import org.bukkit.Material;
import tc.oc.pgm.api.filter.query.BlockQuery;
import tc.oc.pgm.api.filter.query.PlayerQuery;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.killreward.KillRewardMatchModule;
import tc.oc.pgm.snapshot.SnapshotMatchModule;

public class ExistedPreMatchFilter extends TypedFilter<BlockQuery> {

  @Override
  public Class<? extends BlockQuery> getQueryType() {
    return BlockQuery.class;
  }

  @Override
  protected QueryResponse queryTyped(BlockQuery query) {
    return QueryResponse.fromBoolean(query.getBlock().getBlock().getType() != Material.AIR &&
        query.getMatch().needModule(SnapshotMatchModule.class)
            .getOriginalMaterial(query.getLocation().toVector())
            .equals(query.getBlock().getMaterialData())
    );
  }
}
