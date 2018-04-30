package sysdata.it.androidarchitecture.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import java.lang.ref.WeakReference;

import io.reactivex.Observable;

/**
 * Created by Brando Baldassarre on 26/02/2018.
 * Edited by Andrea Guitto on 22/03/2018
 *
 * @param <ResultType> the type parameter
 */
// ResultType: Type for the Resource data
// RequestType: Type for the API response
public abstract class NetworkBoundResource<ResultType> {

    private final MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();

    /**
     * Instantiates a new Network bound resource.
     */
    public NetworkBoundResource() {
        this(false);
    }

    public NetworkBoundResource(boolean initLiveData){
        if(initLiveData) {
           initLiveData();
        }
    }

    protected void initLiveData(){
        result.setValue(Resource.loading(null));
        LiveData<ResultType> dbSource = loadFromDb();
        result.addSource(dbSource, data -> {
            result.removeSource(dbSource);
            if (shouldFetch(data)) {
                fetchFromNetwork(dbSource);
            } else {
                result.addSource(dbSource,
                        newData -> result.setValue(Resource.success(newData)));
            }
        });
    }

    /**
     * Save call result.
     *
     * @param item the item
     */
    // Called to save the result of the API response into the database
    @WorkerThread
    protected abstract void saveCallResult(@NonNull ResultType item);

    /**
     * Should fetch boolean.
     *
     * @param data the data
     * @return the boolean
     */
    // Called with the data in the database to decide whether it should be
    // fetched from the network.
    protected abstract boolean shouldFetch(@Nullable ResultType data);

    /**
     * Should save result boolean.
     *
     * @param data the data
     * @return the boolean
     */
    // Called with the data in the database to decide whether it should be
    // fetched from the network.
    protected abstract boolean shouldSaveResult(@Nullable ResultType data);

    /**
     * Load from db live data.
     *
     * @return the live data
     */
    // Called to get the cached data from the database
    @NonNull
    protected abstract LiveData<ResultType> loadFromDb();

    /**
     * Create call live data.
     *
     * @return the live data
     */
    // Called to create the API call.
    @NonNull
    protected abstract LiveData<Resource<ResultType>> createCall();

    /**
     * On fetch failed.
     */
    // Called when the fetch fails. The child class may want to reset components
    // like rate limiter.
    protected void onFetchFailed() {
    }

    private void fetchFromNetwork(final LiveData<ResultType> dbSource) {
        LiveData<Resource<ResultType>> apiResponse = createCall();
        // we re-attach dbSource as a new source,
        // it will dispatch its latest value quickly
        result.addSource(dbSource,
                newData -> result.setValue(Resource.loading(newData)));
        result.addSource(apiResponse, response -> {
            result.removeSource(apiResponse);
            result.removeSource(dbSource);
            //noinspection ConstantConditions
            if (response.status == Resource.Status.SUCCESS) {
                if(shouldSaveResult(response.data)) {
                    saveResultAndReInit(response);
                } else {
                    result.setValue(Resource.success(response.data));
                }
            } else {
                onFetchFailed();
                result.addSource(dbSource,
                        newData -> result.setValue(
                                Resource.error(response.message.toString(), newData, new Exception("db fetch failed"))));
            }
        });
    }

    private void saveResultAndReInit(Resource<ResultType> response) {
        Observable.just(response)
                .doOnNext(resp -> saveCallResult(resp.data))
                .doOnNext(b -> result.addSource(loadFromDb(),
                        newData -> result.setValue(Resource.success(newData))))
                .subscribe();
    }

    /**
     * Gets as live data.
     *
     * @return the as live data
     */
    // returns a LiveData that represents the resource, implemented
    // in the base class.
    public final LiveData<Resource<ResultType>> getAsLiveData() {
        if(result == null) initLiveData();
        return result;
    }

    /**
     * Builder class
     *
     * @param <ResultType> the type parameter
     */
    public static final class Builder<ResultType> {

        /**
         * The Generated network bound resource.
         */
        GeneratedNetworkBoundResource<ResultType> generatedNetworkBoundResource;

        /**
         * Instantiates a new Builder.
         */
        public Builder() {
            super();
            generatedNetworkBoundResource = new GeneratedNetworkBoundResource();
        }

        /**
         * Should fetch builder.
         *
         * @param action the action, default it will generate a false value
         * @return the builder
         */
        public Builder<ResultType> shouldFetch(Action4<ResultType> action) {
            generatedNetworkBoundResource.setShouldFetch(action);
            return this;
        }

        /**
         * Save call result builder.
         *
         * @param action the action
         * @return the builder
         */
        public Builder<ResultType> onSaveCallResult(Action1<ResultType> action) {
            generatedNetworkBoundResource.setSaveCallResultAction(action);
            return this;
        }

        /**
         * Load from db action builder.
         *
         * @param action the action
         * @return the builder
         */
        public Builder<ResultType> onLoadFromDBAction(Action2<ResultType> action) {
            generatedNetworkBoundResource.setLoadFromDBAction(action);
            return this;
        }

        /**
         * Create call builder.
         *
         * @param action the action
         * @return the builder
         */
        public Builder<ResultType> onCreateCall(Action3<ResultType> action) {
            generatedNetworkBoundResource.setCreateCallAction(action);
            return this;
        }

        /**
         * Should save result builder.
         *
         * @param action the action, default it will generate a false value
         * @return the builder
         */
        public Builder<ResultType> shouldSaveResult(Action4<ResultType> action) {
            generatedNetworkBoundResource.setShouldSaveResult(action);
            return this;
        }

        /**
         * Build network bound resource.
         *
         * @return the network bound resource
         */
        public NetworkBoundResource<ResultType> build() {
            return generatedNetworkBoundResource;
        }

        /**
         * Build live data live data.
         *
         * @return the live data
         */
        public LiveData<Resource<ResultType>> buildLiveData() {
            return generatedNetworkBoundResource.getAsLiveData();
        }

        /**
         * Build live data live data.
         *
         * @return the live data
         */
        public Observable<Resource<ResultType>> buildObservable() {
            return generatedNetworkBoundResource.getAsObservable();
        }

        /**
         * The interface Action 1.
         *
         * @param <ResultType> the type parameter
         */
        public interface Action1<ResultType> {
            /**
             * Do action.
             *
             * @param param the param
             */
            void doAction(ResultType param); }

        /**
         * The interface Action 2.
         *
         * @param <ResultType> the type parameter
         */
        public interface Action2<ResultType> {
            /**
             * Do action live data.
             *
             * @return the live data
             */
            Observable<ResultType> doAction(); }

        /**
         * The interface Action 3.
         *
         * @param <ResultType> the type parameter
         */
        public interface Action3<ResultType> {
            /**
             * Do action live data.
             *
             * @return the live data
             */
            Observable<Resource<ResultType>> doAction(); }

        /**
         * The interface Action 4.
         *
         * @param <ResultType> the type parameter
         */
        public interface Action4<ResultType> {
            /**
             * Do action boolean.
             *
             * @param param the param
             * @return the boolean
             */
            boolean doAction(ResultType param); }
    }

    /**
     * Class used with {@link NetworkBoundResource.Builder} class
     *
     * @param <ResultType> the type parameter
     */
    public static final class GeneratedNetworkBoundResource<ResultType> extends NetworkBoundResource<ResultType> {

        GeneratedNetworkBoundResource() {
            super(false);
        }

        /**
         * The Save call result action.
         */
        WeakReference<Builder.Action1<ResultType>> saveCallResultAction;
        /**
         * The Load from db action.
         */
        WeakReference<Builder.Action2<ResultType>> loadFromDBAction;
        /**
         * The Create call action.
         */
        WeakReference<Builder.Action3<ResultType>> createCallAction;
        /**
         * The Should fetch.
         */
        WeakReference<Builder.Action4<ResultType>> shouldFetch;
        /**
         * The Should save result.
         */
        WeakReference<Builder.Action4<ResultType>> shouldSaveResult;

        /**
         * Sets save call result action.
         *
         * @param saveCallResultAction the save call result action
         */
        public void setSaveCallResultAction(Builder.Action1<ResultType> saveCallResultAction) {
            this.saveCallResultAction = new WeakReference<>(saveCallResultAction);
        }

        /**
         * Sets load from db action.
         *
         * @param loadFromDBAction the load from db action
         */
        public void setLoadFromDBAction(Builder.Action2<ResultType> loadFromDBAction) {
            this.loadFromDBAction = new WeakReference<>(loadFromDBAction);
        }

        /**
         * Sets create call action.
         *
         * @param createCallAction the create call action
         */
        public void setCreateCallAction(Builder.Action3<ResultType> createCallAction) {
            this.createCallAction = new WeakReference<>(createCallAction);
        }

        /**
         * Sets should fetch.
         *
         * @param shouldFetch the should fetch
         */
        public void setShouldFetch(Builder.Action4<ResultType> shouldFetch) {
            this.shouldFetch =  new WeakReference<>(shouldFetch);
        }

        /**
         * Sets should save result.
         *
         * @param shouldSaveResult the should save result
         */
        public void setShouldSaveResult(Builder.Action4<ResultType> shouldSaveResult) {
            this.shouldSaveResult = new WeakReference<>(shouldSaveResult);
        }

        @Override
        protected void saveCallResult(@NonNull ResultType item) {
            if(saveCallResultAction != null && saveCallResultAction.get() != null) {
                saveCallResultAction.get().doAction(item);
            }
        }

        @Override
        protected boolean shouldFetch(@Nullable ResultType data) {
            boolean returnValue = false;
            if(shouldFetch != null && shouldFetch.get() != null) {
                returnValue = shouldFetch.get().doAction(data);
            }
            return returnValue;
        }

        @Override
        protected boolean shouldSaveResult(@Nullable ResultType data) {
            boolean returnValue = false;
            if(shouldSaveResult != null && shouldSaveResult.get() != null) {
                returnValue = shouldSaveResult.get().doAction(data);
            }
            return returnValue;
        }

        @NonNull
        @Override
        protected LiveData<ResultType> loadFromDb() {
            MutableLiveData<ResultType> returnValue = null;

            if(loadFromDBAction != null && loadFromDBAction.get() != null) {
                returnValue = new MutableLiveData<>();

                Observable<ResultType> resultTypeObservable = loadFromDBAction.get().doAction();
                resultTypeObservable.subscribe(returnValue::postValue);
            }
            return returnValue;
        }

        @NonNull
        @Override
        protected LiveData<Resource<ResultType>> createCall() {
            MutableLiveData<Resource<ResultType>> returnValue = null;
            if(createCallAction != null && createCallAction.get() != null) {
                returnValue = new MutableLiveData<>();

                Observable<Resource<ResultType>> resultTypeObservable = createCallAction.get().doAction();
                resultTypeObservable.subscribe(returnValue::postValue);
            }
            return returnValue;
        }

        /**
         * Gets as live data.
         *
         * @return the as live data
         */
        // returns a LiveData that represents the resource, implemented
        // in the base class.
        public Observable<Resource<ResultType>> getAsObservable() {

            Observable<Resource<ResultType>> networkResourceObservable = Observable.empty();
            Observable<ResultType> oLoadFromDbCall = Observable.empty();

            if(loadFromDBAction != null && loadFromDBAction.get() != null) {
                oLoadFromDbCall = loadFromDBAction.get().doAction();
            }

            if(createCallAction != null && createCallAction.get() != null) {
                networkResourceObservable = createCallAction.get().doAction().doOnNext(resultTypeResource -> {
                    if (shouldSaveResult != null && shouldSaveResult.get() != null) {
                        boolean shouldSave = shouldSaveResult.get().doAction(resultTypeResource.data);
                        if (saveCallResultAction != null && saveCallResultAction.get() != null && shouldSave) {
                            saveCallResultAction.get().doAction(resultTypeResource.data);
                        }
                    }
                });
            }


            Observable<Resource<ResultType>> finalNetworkResourceObservable = networkResourceObservable.switchIfEmpty(Observable.just(Resource.success(null)));
            Observable<Resource<ResultType>> resourceObservable = oLoadFromDbCall.flatMap(resultType -> {
                boolean shouldFetchValue = false;
                if (shouldFetch != null && shouldFetch.get() != null) {
                    shouldFetchValue = this.shouldFetch.get().doAction(resultType);
                }

                if (!shouldFetchValue)
                    return Observable.just(Resource.success(resultType));

                return finalNetworkResourceObservable;

            }).switchIfEmpty(finalNetworkResourceObservable);

            return Observable.concat(Observable.just(Resource.loading(null)) ,resourceObservable);
        }

    }

}