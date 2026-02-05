package ru.practicum.util;

import ru.practicum.ewm.stats.avro.ActionTypeAvro;

import java.util.Map;

public class ActionWeights {
    public static final Map<ActionTypeAvro, Double> WEIGHTS = Map.of(
            ActionTypeAvro.ACTION_LIKE, 1.0,
            ActionTypeAvro.ACTION_REGISTER, 0.8,
            ActionTypeAvro.ACTION_VIEW, 0.4
    );
}
