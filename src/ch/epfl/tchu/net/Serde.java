package ch.epfl.tchu.net;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import ch.epfl.tchu.SortedBag;

/**
 * A serializer-deserializer
 *
 * @param <T> The type to {@link Serde#serialize}-{@link Serde#deserialize}
 * @author <a href="https://people.epfl.ch/jad.tala">Jad Tala (310821)</a>
 * @author <a href="https://people.epfl.ch/sofiya.malamud">Sofiya Malamud (313789)</a>
 */
public interface Serde<T> {
    /**
     * Serializes a given object
     *
     * @param toSerialize The object of type {@code T} to serialize
     * @return the corresponding serialized {@link String} of the object of type {@code T}
     */
    String serialize(T toSerialize);

    /**
     * Deserializes an object using its corresponding string
     *
     * @param toDeserialize The {@link String} to deserialize
     * @return the object of type {@code T} corresponding to the given {@link String}
     */
    T deserialize(String toDeserialize);

    /**
     * Returns a serde using the given serialize and deserialize functions
     *
     * @param <T>                 The type of the resulting {@link Serde}
     * @param serializeFunction   The {@link Serde}'s {@link Serde#serialize} function
     * @param deserializeFunction The {@link Serde}'s {@link Serde#deserialize} function
     * @return a {@link Serde} of type {@code T} using the given {@link Serde#serialize} and {@link Serde#deserialize} functions
     */
    static <T> Serde<T> of(Function<T, String> serializeFunction, Function<String, T> deserializeFunction) {
        return new Serde<>() {
            @Override
            public String serialize(T toSerialize) {
                return serializeFunction.apply(toSerialize);
            }

            @Override
            public T deserialize(String toDeserialize) {
                return deserializeFunction.apply(toDeserialize);
            }
        };
    }

    /**
     * Returns a serde using the given list of all values of a set of enumerated values
     *
     * @param <T> The type of values
     * @param all The list of all values of a set of enumerated values
     * @return A {@link Serde} using the given list of all values of a set of enumerated values
     */
    static <T> Serde<T> oneOf(List<T> all) {
        return Serde.of(
                i -> Objects.isNull(i) ? "" : String.valueOf(all.indexOf(i)),
                i -> i.isEmpty() ? null : all.get(Integer.parseInt(i))
        );
    }

    /**
     * Returns a serde able to serialize-deserialize a list of values
     * serialized-deserialized by the given serde
     *
     * @param <T>       The value type of the given {@link Serde}
     * @param serde     The {@link Serde}
     * @param delimiter The delimiting {@link String}
     * @return a {@link Serde} able to {@link Serde#serialize}/{@link Serde#deserialize}  a list of values
     * serialized/deserialized by the given serde
     */
    static <T> Serde<List<T>> listOf(Serde<T> serde, String delimiter) {
        return Serde.of(
                t -> {
                    StringJoiner stringJoiner = new StringJoiner(delimiter);
                    t.forEach(e -> stringJoiner.add(serde.serialize(e)));
                    return stringJoiner.toString();
                },
                t -> t.isEmpty() ? List.of() : Arrays.stream(t.split(Pattern.quote(delimiter), -1))
                        .map(serde::deserialize)
                        .collect(Collectors.toList())
        );
    }

    /**
     * Returns a serde able to serialize-deserialize a list of values
     * serialized-deserialized by the given serde
     *
     * @param <T>       The value type of the given {@link Serde}
     * @param serde     The {@link Serde}
     * @param delimiter The delimiting {@link String}
     * @return a {@link Serde} able to {@link Serde#serialize}/{@link Serde#deserialize} a list of values
     * serialized/deserialized by the given serde
     */
    static <T extends Comparable<T>> Serde<SortedBag<T>> bagOf(Serde<T> serde, String delimiter) {
        return Serde.of(
                t -> {
                    StringJoiner stringJoiner = new StringJoiner(delimiter);
                    t.forEach(e -> stringJoiner.add(serde.serialize(e)));
                    return stringJoiner.toString();
                },
                t -> t.isEmpty() ? SortedBag.of() : SortedBag.of(Arrays.stream(t.split(Pattern.quote(delimiter), -1))
                        .map(serde::deserialize)
                        .collect(Collectors.toList()))
        );
    }
}