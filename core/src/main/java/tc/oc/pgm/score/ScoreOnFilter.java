package tc.oc.pgm.score;

import tc.oc.pgm.api.feature.FeatureDefinition;
import tc.oc.pgm.api.filter.Filter;
import tc.oc.pgm.api.party.Competitor;
import tc.oc.pgm.filters.dynamic.FilterMatchModule;

public class ScoreOnFilter implements FeatureDefinition {

  protected final Filter trigger;
  protected final double score;

  public ScoreOnFilter(Filter trigger, double score) {
    this.trigger = trigger;
    this.score = score;
  }

  public void load(ScoreMatchModule smm, FilterMatchModule fmm) {
    fmm.onRise(
        Competitor.class,
        trigger,
        competitor -> {
          smm.incrementScore(competitor, score, ScoreCause.FILTER);
        });
  }
}
