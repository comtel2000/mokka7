/*
 * PROJECT Mokka7 (fork of Moka7)
 *
 * Copyright (C) 2017 J.Zimmermann All rights reserved.
 *
 * Mokka7 is free software: you can redistribute it and/or modify it under the terms of the Lesser
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or under EPL Eclipse Public License 1.0.
 *
 * This means that you have to chose in advance which take before you import the library into your
 * project.
 *
 * SNAP7 is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE whatever license you
 * decide to adopt.
 */
package org.comtel2000.mokka7.client.service;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.EventTarget;

/**
 * Service wrapper for FX services
 *
 * @author comtel
 *
 * @param <T>
 */
public class CompletableService<T> extends Service<T> {

  private Task<T> task;

  @Override
  protected Task<T> createTask() {
    return task;
  }

  public static <T> CompletableService<T> supply(Callable<T> t) {
    CompletableService<T> instance = new CompletableService<>();
    return instance.setCallable(t);
  }

  public static <T> CompletableService<T> supply(Task<T> t) {
    CompletableService<T> instance = new CompletableService<>();
    return instance.setTask(t);
  }

  private CompletableService<T> setTask(Task<T> t) {
    task = Objects.requireNonNull(t);
    return this;
  }

  private CompletableService<T> setCallable(Callable<T> t) {
    return setTask(new Task<T>() {
      @Override
      protected T call() throws Exception {
        return t.call();
      }
    });
  }

  /**
   * {@link #runningProperty()}
   */
  public CompletableService<T> bindRunning(BooleanProperty running) {
    running.bind(runningProperty());
    return this;
  }

  /**
   * {@link #titleProperty()}
   */
  public CompletableService<T> bindTitle(StringProperty title) {
    title.bind(titleProperty());
    return this;
  }

  /**
   * {@link #progressProperty()}
   */
  public CompletableService<T> bindProgress(DoubleProperty progress) {
    progress.bind(progressProperty());
    return this;
  }

  /**
   * {@link Service#setOnSucceeded(javafx.event.EventHandler)}
   */
  public CompletableService<T> onSucceeded(Consumer<? super T> action) {
    setOnSucceeded(t -> action.accept(getValue()));
    return this;
  }

  /**
   * {@link Service#setOnFailed(javafx.event.EventHandler)}
   */
  public CompletableService<T> onFailed(Consumer<? super Throwable> action) {
    setOnFailed(t -> action.accept(getException()));
    return this;
  }

  /**
   * {@link Service#setOnCancelled(javafx.event.EventHandler)}
   */
  public CompletableService<T> onCancelled(Consumer<? super EventTarget> action) {
    setOnCancelled(t -> action.accept(t.getTarget()));
    return this;
  }

  public CompletableService<T> onComplete(BiConsumer<? super T, ? super Throwable> action) {
    stateProperty().addListener((l, a, b) -> {
      switch (b) {
        case CANCELLED:
        case SUCCEEDED:
        case FAILED:
          action.accept(getValue(), getException());
          break;
        default:
          break;
      }
    });
    return this;
  }
}
