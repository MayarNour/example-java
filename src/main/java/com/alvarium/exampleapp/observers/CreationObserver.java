/*******************************************************************************
 * Copyright 2021 Dell Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/
package com.alvarium.exampleapp.observers;

import com.alvarium.Sdk;
import com.alvarium.annotators.AnnotatorException;
import com.alvarium.exampleapp.SampleData;
import com.alvarium.streams.StreamException;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.exceptions.Exceptions;

/**
 * This class takes the role of the host responsible for data creation and calling the sdk.create()
 * method.
 * 
 * It takes an (Alvarium) SDK and a destination Channel on which the data being annotated should be
 * published to next. 
 */
public class CreationObserver implements Observer<SampleData> {
  private final Sdk sdk;
  private final Channel<SampleData> destinationChannel; 

  public CreationObserver(Sdk sdk, Channel<SampleData> destinationChannel) {
    this.sdk = sdk;
    this.destinationChannel = destinationChannel;
  } 

  
  @Override
  public void onNext(SampleData data) {
    try {
      sdk.create(data.toJson().getBytes());
      destinationChannel.publish(data);
    } catch(AnnotatorException e) {
      // Exceptions are propagated because onNext's signature does not throw any exception
      Exceptions.propagate( 
          new ObserverException("Could not annotate data during create", e)
      );
    } catch(StreamException e) {
      Exceptions.propagate(
          new ObserverException("Could not publish data during create", e)
      );
    }
  }

  @Override
  public void onError(Throwable d) {
    final Exception e = new Exception(d);
    Exceptions.propagate(
        new ObserverException("Could not publish data during create", e)
    );
  }

  @Override
  public void onComplete() {}

  @Override
  public void onSubscribe(Disposable d) {}

}
