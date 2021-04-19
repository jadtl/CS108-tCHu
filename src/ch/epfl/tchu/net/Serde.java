package ch.epfl.tchu.net;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import ch.epfl.tchu.SortedBag;

/**
 * 
 * @param <T>
 * 
 */
public interface Serde<T> {
  /**
   * 
   * @param toSerialize
   * 
   * @return
   */
  String serialize(T toSerialize);

  /**
   * 
   * @param toDeserialize
   * 
   * @return
   */
  T deserialize(String toDeserialize);

  /**
   * 
   * @param <T>
   * 
   * @param serializeFunction
   * 
   * @param deserializeFunction
   * 
   * @return
   */
  static <T> Serde<T> of(Function<T, String> serializeFunction, Function<String, T> deserializeFunction) {
    return new Serde<T>() {
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
   * 
   * @param <T>
   * 
   * @param all
   * 
   * @return
   */
  static <T> Serde<T> oneOf(List<T> all) {
    return Serde.of(
      i -> String.valueOf(all.indexOf(i)), 
      i -> all.get(Integer.parseInt(i))
    );
  }

  /**
   * 
   * @param <T>
   * 
   * @param serde
   * 
   * @param delimiter
   * 
   * @return
   */
  static <T> Serde<List<T>> listOf(Serde<T> serde, String delimiter) {
    return Serde.of(
      t -> {
        StringJoiner stringJoiner = new StringJoiner(delimiter);
        t.forEach(e -> stringJoiner.add(serde.serialize(e)));
        return stringJoiner.toString();
      },
      t -> {
          return Arrays.asList(t.split(Pattern.quote(delimiter), -1)).stream()
          .map(ds -> serde.deserialize(ds))
          .collect(Collectors.toList());
      }
    );
  }

  /**
   * 
   * @param <T>
   * 
   * @param serde
   * 
   * @param delimiter
   * 
   * @return
   */
  static <T extends Comparable<T>> Serde<SortedBag<T>> bagOf(Serde<T> serde, String delimiter) {
    return Serde.of(
      t -> {
        StringJoiner stringJoiner = new StringJoiner(delimiter);
        t.forEach(e -> stringJoiner.add(serde.serialize(e)));
        return stringJoiner.toString();
      }, 
      t -> {
        return SortedBag.of(Arrays.asList(t.split(Pattern.quote(delimiter), -1)).stream()
        .map(ds -> serde.deserialize(ds))
        .collect(Collectors.toList()));
      }
    );
  }
}
