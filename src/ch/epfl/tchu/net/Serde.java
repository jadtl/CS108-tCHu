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
 * @param <T>
 *        The type to serialize-deserialize
 *
 * @author Sofiya Malamud (313789)
 * @author Jad Tala (310821)
 */
public interface Serde<T> {
  /**
   * Serializes a given object
   * 
   * @param toSerialize
   *        The object to serialize
   * 
   * @return the corresponding serialized string of the object
   */
  String serialize(T toSerialize);

  /**
   * Deserializes an object using its corresponding string
   * 
   * @param toDeserialize
   *        The string to deserialize
   * 
   * @return the object corresponding to the given string
   */
  T deserialize(String toDeserialize);

  /**
   * Returns a serde using the given serialize and deserialize functions
   * 
   * @param <T>
   *        The type of the resulting serde
   * 
   * @param serializeFunction
   *        The serdes's serialize function
   * 
   * @param deserializeFunction
   *        The serde's serialize function
   * 
   * @return a serde using the given serialize and deserialize functions
   */
  static <T> Serde<T> of(Function<T, String> serializeFunction, Function<String, T> deserializeFunction) {
    return new Serde<T>() {
      @Override
      public String serialize(T toSerialize) { return serializeFunction.apply(toSerialize); }

      @Override
      public T deserialize(String toDeserialize) { return deserializeFunction.apply(toDeserialize); }
    };
  }

  /**
   * Returns a serde using the given list of all values of a set of enumerated values
   * 
   * @param <T>
   *        The type of values
   * 
   * @param all
   *        The list of all values of a set of enumerated values
   * 
   * @return a serde using the given list of all values of a set of enumerated values
   */
  static <T> Serde<T> oneOf(List<T> all) {
    return Serde.of(
      i -> Objects.isNull(i) ? "" : String.valueOf(all.indexOf(i)), 
      i -> all.get(Integer.parseInt(i))
    );
  }

  /**
   * Returns a serde able to serialize-deserialize a list of values
   * serialized-deserialized by the given serde
   * 
   * @param <T>
   *        The value type of the given serde
   * 
   * @param serde
   *        The serde
   * 
   * @param delimiter
   *        The delimiting character
   * 
   * @return a serde able to serialize-deserialize a list of values
   * serialized-deserialized by the given serde
   */
  static <T> Serde<List<T>> listOf(Serde<T> serde, String delimiter) {
    return Serde.of(
      t -> {
        StringJoiner stringJoiner = new StringJoiner(delimiter);
        t.forEach(e -> stringJoiner.add(serde.serialize(e)));
        return stringJoiner.toString();
      },
      t -> t.isEmpty() ? List.of() : Arrays.asList(t.split(Pattern.quote(delimiter), -1)).stream()
          .map(ds -> serde.deserialize(ds))
          .collect(Collectors.toList())
    );
  }

  /**
   * Returns a serde able to serialize-deserialize a list of values
   * serialized-deserialized by the given serde
   * 
   * @param <T>
   *        The value type of the given serde
   * 
   * @param serde
   *        The serde
   * 
   * @param delimiter
   *        The delimiting character
   * 
   * @return a serde able to serialize-deserialize a list of values
   * serialized-deserialized by the given serde
   */
  static <T extends Comparable<T>> Serde<SortedBag<T>> bagOf(Serde<T> serde, String delimiter) {
    return Serde.of(
      t -> {
        StringJoiner stringJoiner = new StringJoiner(delimiter);
        t.forEach(e -> stringJoiner.add(serde.serialize(e)));
        return stringJoiner.toString();
      }, 
      t -> {
        return t.isEmpty() ? SortedBag.of() : SortedBag.of(Arrays.asList(t.split(Pattern.quote(delimiter), -1)).stream()
        .map(ds -> serde.deserialize(ds))
        .collect(Collectors.toList()));
      }
    );
  }
}