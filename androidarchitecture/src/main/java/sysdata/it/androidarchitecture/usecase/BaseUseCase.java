/*
 * Copyright (C) 2016 Sysdata Digital, S.r.l.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sysdata.it.androidarchitecture.usecase;

import android.os.Bundle;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;

/**
 * A UseCase represents and performs a single, atomic work unit.
 * <p>
 * Inspiration taken from <a href="https://github.com/richardradics/RxAndroidBootstrap/blob/master/core/src/main/java/com/richardradics/core/interactor/UseCase.java">here</a>.
 * </p>
 *
 * @author Stefano Ciarcia'
 *         created on 22/07/2015.
 */
public abstract class BaseUseCase<T> {

    /**
     * Indicates on what thread to perform our work
     */
    private final Scheduler mThreadExecutor;
    /**
     * Indicates on what thread to give back the result of our work
     */
    private final Scheduler mPostExecutionThread;

    /**
     * A Subscription is a convenience object used to be able to unsubscribe from an observable.
     */
    protected Disposable mSubscription = null;

    /**
     * Will setup the 'execution' Scheduler and the 'emission' Scheduler to use.
     * Then it will perform two operations:
     *
     * @param threadExecutor      the {@link Scheduler} on which to execute the work
     * @param postExecutionThread the {@link Scheduler} on which to emit notifications
     */
    public BaseUseCase(Scheduler threadExecutor, Scheduler postExecutionThread) {
        this.mThreadExecutor = threadExecutor;
        this.mPostExecutionThread = postExecutionThread;
    }

    /**
     * Builds an {@link Observable} which will be used when executing the current {@link
     * BaseUseCase}.
     */
    protected abstract Observable<T> buildUseCaseObservable(Bundle b);

    /**
     * Executes the current use case.
     *
     * @param useCaseSubscriber The guy who will be listen to the observable build with {@link
     *                          #buildUseCaseObservable(Bundle)}.
     */
    @SuppressWarnings("unchecked")
    public void execute(Observer<T> useCaseSubscriber) {
        execute(useCaseSubscriber, null);
    }

    /**
     * Executes the current use case.
     *
     * @param useCaseSubscriber The guy who will be listen to the observable build with {@link
     *                          #buildUseCaseObservable(Bundle)}.
     * @param b                 A bundle containing parameters needed to generate the Observable
     */
    @SuppressWarnings("unchecked")
    public void execute(final Observer<T> useCaseSubscriber, Bundle b) {

        // don't waste subscriptions
        if(mSubscription != null && !mSubscription.isDisposed()){
            mSubscription.dispose();
        }

        if (useCaseSubscriber != null) {
            // in this case we need to define what thread should execute the subscription logic
            this.mSubscription = mThreadExecutor.createWorker()
                    .schedule(() -> buildUseCaseObservable(b)
                            .observeOn(mPostExecutionThread)
                            .subscribe(useCaseSubscriber));
        }
    }

    /**
     * This method will return the {@link Scheduler} on which to execute the work.
     *
     * @return the {@link Scheduler} set via class constructor.
     */
    public Scheduler getThreadExecutor() {
        return mThreadExecutor;
    }

    /**
     * This method will return the {@link Scheduler} on which to emit notifications.
     *
     * @return the {@link Scheduler} set via class constructor.
     */
    public Scheduler getPostExecutionThread() {
        return mPostExecutionThread;
    }

    /**
     * Unsubscribes from current {@link Disposable}.
     * <p>
     * Subclasses should override this method to remove all the Subscribers added to (if any)
     * Repository they hold a reference to, and then release such Repositories.
     * </p>
     */
    public void unsubscribe() {
        if (mSubscription != null && !mSubscription.isDisposed()) {
            mSubscription.dispose();
        }
    }
}
