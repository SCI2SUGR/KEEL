package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Boosting;

public class pair_gg {
    // GenotipoBoostings pair
    public GenotypeBoosting first, second;
    public pair_gg(GenotypeBoosting a, GenotypeBoosting b) {
        first=a.duplica(); second=b.duplica();
    }
}
