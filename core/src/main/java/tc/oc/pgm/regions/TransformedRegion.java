package tc.oc.pgm.regions;

import java.util.Random;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.region.Region;
import tc.oc.pgm.api.region.RegionDefinition;

public abstract class TransformedRegion implements RegionDefinition.Static {

  protected final Region region;
  protected @Nullable Bounds bounds;

  public TransformedRegion(Region region) {
    this.region = region;
  }

  @Override
  public boolean isBlockBounded() {
    return this.region.isBlockBounded();
  }

  @Override
  public boolean isStatic() {
    return region.isStatic();
  }

  @Override
  public boolean isEmpty() {
    return this.region.isEmpty();
  }

  @Override
  public Bounds getBounds() {
    if (this.bounds == null) {
      this.bounds = this.getTransformedBounds();
    }
    return this.bounds;
  }

  /**
   * Generic bounding box transform - transform all 8 vertices and find the minimum bounding box
   * containing them.
   */
  protected Bounds getTransformedBounds() {
    Vector[] oldVertices = this.region.getBounds().getVertices();
    Vector[] newVertices = new Vector[8];
    for (int i = 0; i < oldVertices.length; i++) {
      newVertices[i] = this.transform(oldVertices[i]);
    }
    return new Bounds(newVertices);
  }

  @Override
  public boolean contains(Vector point) {
    return this.region.getStatic().contains(this.untransform(point));
  }

  @Override
  public boolean contains(Location point) {
    if (isStatic()) return contains(point.toVector());
    var vec = untransform(point.toVector());
    return this.region.contains(new Location(point.getWorld(), vec.getX(), vec.getY(), vec.getZ()));
  }

  @Override
  public boolean canGetRandom() {
    return this.region.canGetRandom();
  }

  /**
   * Generic random point generator that simply transforms a point generated by the transformed
   * region. This will work at least with affine transformations, but other types may make the
   * random distribution uneven.
   */
  @Override
  public Vector getRandom(Random random) {
    return this.transform(this.region.getStatic().getRandom(random));
  }

  @Override
  public Vector getRandom(Match match) {
    return this.transform(this.region.getRandom(match));
  }

  protected abstract Vector transform(Vector point);

  protected abstract Vector untransform(Vector point);
}
