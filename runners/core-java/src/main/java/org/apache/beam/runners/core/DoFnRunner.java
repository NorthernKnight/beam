/*
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
package org.apache.beam.runners.core;

import org.apache.beam.sdk.transforms.Aggregator;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.windowing.BoundedWindow;
import org.apache.beam.sdk.util.TimeDomain;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.sdk.values.KV;
import org.joda.time.Instant;

/**
 * An wrapper interface that represents the execution of a {@link DoFn}.
 */
public interface DoFnRunner<InputT, OutputT> {
  /**
   * Prepares and calls a {@link DoFn DoFn's} {@link DoFn.StartBundle @StartBundle} method.
   */
  void startBundle();

  /**
   * Calls a {@link DoFn DoFn's} {@link DoFn.ProcessElement @ProcessElement} method with a
   * {@link DoFn.ProcessContext} containing the provided element.
   */
  void processElement(WindowedValue<InputT> elem);

  /**
   * Calls a {@link DoFn DoFn's} {@link DoFn.OnTimer @OnTimer} method for the given timer
   * in the given window.
   */
  void onTimer(String timerId, BoundedWindow window, Instant timestamp, TimeDomain timeDomain);

  /**
   * Calls a {@link DoFn DoFn's} {@link DoFn.FinishBundle @FinishBundle} method and performs
   * additional tasks, such as flushing in-memory states.
   */
  void finishBundle();

  /**
   * An internal interface for signaling that a {@link OldDoFn} requires late data dropping.
   */
  public interface ReduceFnExecutor<K, InputT, OutputT, W> {
    /**
     * Gets this object as a {@link OldDoFn}.
     *
     * <p>Most implementors of this interface are expected to be {@link OldDoFn} instances, and will
     * return themselves.
     */
    OldDoFn<KeyedWorkItem<K, InputT>, KV<K, OutputT>> asDoFn();

    /**
     * Returns an aggregator that tracks elements that are dropped due to being late.
     */
    Aggregator<Long, Long> getDroppedDueToLatenessAggregator();
  }
}
