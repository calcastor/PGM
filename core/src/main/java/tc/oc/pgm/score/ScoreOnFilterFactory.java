package tc.oc.pgm.score;

import tc.oc.pgm.api.filter.Filter;
import tc.oc.pgm.api.match.Match;

public class ScoreOnFilterFactory {

  protected final Filter trigger;
  protected final double score;

  public ScoreOnFilterFactory(Filter trigger, double score) {
    this.trigger = trigger;
    this.score = score;
  }

  public ScoreOnFilter createScoreOnFilter(Match match) {
    return new ScoreOnFilter(trigger, score);
  }
}
