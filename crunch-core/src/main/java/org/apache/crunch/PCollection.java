/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.crunch;

import java.util.Collection;

import org.apache.crunch.types.PTableType;
import org.apache.crunch.types.PType;
import org.apache.crunch.types.PTypeFamily;

/**
 * A representation of an immutable, distributed collection of elements that is
 * the fundamental target of computations in Crunch.
 *
 */
public interface PCollection<S> {
  /**
   * Returns the {@code Pipeline} associated with this PCollection.
   */
  Pipeline getPipeline();

  /**
   * Returns a {@code PCollection} instance that acts as the union of this
   * {@code PCollection} and the given {@code PCollection}.
   */
  PCollection<S> union(PCollection<S> other);
  
  /**
   * Returns a {@code PCollection} instance that acts as the union of this
   * {@code PCollection} and the input {@code PCollection}s.
   */
  PCollection<S> union(PCollection<S>... collections);

  /**
   * Applies the given doFn to the elements of this {@code PCollection} and
   * returns a new {@code PCollection} that is the output of this processing.
   *
   * @param doFn
   *          The {@code DoFn} to apply
   * @param type
   *          The {@link PType} of the resulting {@code PCollection}
   * @return a new {@code PCollection}
   */
  <T> PCollection<T> parallelDo(DoFn<S, T> doFn, PType<T> type);

  /**
   * Applies the given doFn to the elements of this {@code PCollection} and
   * returns a new {@code PCollection} that is the output of this processing.
   *
   * @param name
   *          An identifier for this processing step, useful for debugging
   * @param doFn
   *          The {@code DoFn} to apply
   * @param type
   *          The {@link PType} of the resulting {@code PCollection}
   * @return a new {@code PCollection}
   */
  <T> PCollection<T> parallelDo(String name, DoFn<S, T> doFn, PType<T> type);
  
  /**
   * Applies the given doFn to the elements of this {@code PCollection} and
   * returns a new {@code PCollection} that is the output of this processing.
   *
   * @param name
   *          An identifier for this processing step, useful for debugging
   * @param doFn
   *          The {@code DoFn} to apply
   * @param type
   *          The {@link PType} of the resulting {@code PCollection}
   * @param options
   *          Optional information that is needed for certain pipeline operations
   * @return a new {@code PCollection}
   */
  <T> PCollection<T> parallelDo(String name, DoFn<S, T> doFn, PType<T> type,
      ParallelDoOptions options);

  /**
   * Similar to the other {@code parallelDo} instance, but returns a
   * {@code PTable} instance instead of a {@code PCollection}.
   *
   * @param doFn
   *          The {@code DoFn} to apply
   * @param type
   *          The {@link PTableType} of the resulting {@code PTable}
   * @return a new {@code PTable}
   */
  <K, V> PTable<K, V> parallelDo(DoFn<S, Pair<K, V>> doFn, PTableType<K, V> type);

  /**
   * Similar to the other {@code parallelDo} instance, but returns a
   * {@code PTable} instance instead of a {@code PCollection}.
   *
   * @param name
   *          An identifier for this processing step
   * @param doFn
   *          The {@code DoFn} to apply
   * @param type
   *          The {@link PTableType} of the resulting {@code PTable}
   * @return a new {@code PTable}
   */
  <K, V> PTable<K, V> parallelDo(String name, DoFn<S, Pair<K, V>> doFn, PTableType<K, V> type);
  
  /**
   * Similar to the other {@code parallelDo} instance, but returns a
   * {@code PTable} instance instead of a {@code PCollection}.
   *
   * @param name
   *          An identifier for this processing step
   * @param doFn
   *          The {@code DoFn} to apply
   * @param type
   *          The {@link PTableType} of the resulting {@code PTable}
   * @param options
   *          Optional information that is needed for certain pipeline operations
   * @return a new {@code PTable}
   */
  <K, V> PTable<K, V> parallelDo(String name, DoFn<S, Pair<K, V>> doFn, PTableType<K, V> type,
      ParallelDoOptions options);

  /**
   * Write the contents of this {@code PCollection} to the given {@code Target},
   * using the storage format specified by the target.
   *
   * @param target
   *          The target to write to
   */
  PCollection<S> write(Target target);

  /**
   * Write the contents of this {@code PCollection} to the given {@code Target},
   * using the given {@code Target.WriteMode} to handle existing
   * targets.
   * 
   * @param target
   *          The target
   * @param writeMode
   *          The rule for handling existing outputs at the target location
   */
  PCollection<S> write(Target target, Target.WriteMode writeMode);
  
  /**
   * Returns a reference to the data set represented by this PCollection that
   * may be used by the client to read the data locally.
   */
  Iterable<S> materialize();

  /**
   * Marks this data as cached using the default {@link CachingOptions}. Cached {@code PCollection}s will only
   * be processed once, and then their contents will be saved so that downstream code can process them many times.
   *
   * @return this {@code PCollection} instance
   */
  PCollection<S> cache();

  /**
   * Marks this data as cached using the given {@code CachingOptions}. Cached {@code PCollection}s will only
   * be processed once and then their contents will be saved so that downstream code can process them many times.
   *
   * @param options the options that control the cache settings for the data
   * @return this {@code PCollection} instance
   */
  PCollection<S> cache(CachingOptions options);

  /**
   * @return A {@code PObject} encapsulating an in-memory {@link Collection} containing the values
   * of this {@code PCollection}.
   */
  PObject<Collection<S>> asCollection();

  /**
   * @return The first element of this {@code PCollection}.
   */
  PObject<S> first();

  /**
   * @return A reference to the data in this instance that can be read from a job running
   * on a cluster.
   *
   * @param materialize If true, materialize this data before returning a reference to it
   */
  ReadableData<S> asReadable(boolean materialize);

  /**
   * Returns the {@code PType} of this {@code PCollection}.
   */
  PType<S> getPType();

  /**
   * Returns the {@code PTypeFamily} of this {@code PCollection}.
   */
  PTypeFamily getTypeFamily();

  /**
   * Returns the size of the data represented by this {@code PCollection} in
   * bytes.
   */
  long getSize();

  /**
   * Returns the number of elements represented by this {@code PCollection}.
   *
   * @return An {@code PObject} containing the number of elements in this {@code PCollection}.
   */
  PObject<Long> length();

  /**
   * Returns a shorthand name for this PCollection.
   */
  String getName();

  /**
   * Apply the given filter function to this instance and return the resulting
   * {@code PCollection}.
   */
  PCollection<S> filter(FilterFn<S> filterFn);

  /**
   * Apply the given filter function to this instance and return the resulting
   * {@code PCollection}.
   *
   * @param name
   *          An identifier for this processing step
   * @param filterFn
   *          The {@code FilterFn} to apply
   */
  PCollection<S> filter(String name, FilterFn<S> filterFn);

  /**
   * Apply the given map function to each element of this instance in order to
   * create a {@code PTable}.
   */
  <K> PTable<K, S> by(MapFn<S, K> extractKeyFn, PType<K> keyType);

  /**
   * Apply the given map function to each element of this instance in order to
   * create a {@code PTable}.
   *
   * @param name
   *          An identifier for this processing step
   * @param extractKeyFn
   *          The {@code MapFn} to apply
   */
  <K> PTable<K, S> by(String name, MapFn<S, K> extractKeyFn, PType<K> keyType);

  /**
   * Returns a {@code PTable} instance that contains the counts of each unique
   * element of this PCollection.
   */
  PTable<S, Long> count();

  /**
   * Returns a {@code PObject} of the maximum element of this instance.
   */
  PObject<S> max();

  /**
   * Returns a {@code PObject} of the minimum element of this instance.
   */
  PObject<S> min();
  
  /**
   * Returns a {@code PCollection} that contains the result of aggregating all values in this instance.
   */
  PCollection<S> aggregate(Aggregator<S> aggregator);

}
