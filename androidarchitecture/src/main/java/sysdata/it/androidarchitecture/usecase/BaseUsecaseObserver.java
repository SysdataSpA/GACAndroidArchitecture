package sysdata.it.androidarchitecture.usecase;

import android.arch.lifecycle.MutableLiveData;
import android.os.Bundle;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by Andrea Guitto on 29/03/2018.
 */

public abstract class BaseUsecaseObserver<RepositoryModel> implements Observer<RepositoryModel> {

    private Class<? extends BaseUseCase> observedUsecaseClass;

    public static <RepositoryModel> Builder<RepositoryModel> buildOn(Class<? extends BaseUseCase<RepositoryModel>> clazz) {
        return new Builder<>(clazz);
    }

    public Class<? extends BaseUseCase> getObservedUsecaseClass() {
        return observedUsecaseClass;
    }

    public void setObservedUsecaseClass(Class<? extends BaseUseCase> observedUsecaseClass) {
        this.observedUsecaseClass = observedUsecaseClass;
    }

    /**
     * A builder for BaseUsecaseObserver
     * @param <RepositoryModel>
     */
    public static final class Builder<RepositoryModel> {

        Class<? extends BaseUseCase<RepositoryModel>> observedUsecaseClass;
        MutableLiveData liveDataHandled;
        ActionMap<RepositoryModel, ?> actionMap;

        public Builder(Class<? extends BaseUseCase<RepositoryModel>> clazz) {
            observedUsecaseClass = clazz;
        }

        public BaseUsecaseObserver<RepositoryModel> build(){
            return new BuiltUsecaseObserver<>(observedUsecaseClass, liveDataHandled, actionMap);
        }

        public <UIModel> Builder<RepositoryModel> map(MutableLiveData<UIModel> liveData, ActionMap<RepositoryModel,UIModel> action) {
            liveDataHandled = liveData;
            actionMap = action;
            return this;
        }
    }

    /**
     * Generated classes used by buidler
     * @param <RepositoryModel>
     * @param <UIModel>
     */
    private static final class BuiltUsecaseObserver<RepositoryModel,UIModel> extends BaseUsecaseObserver<RepositoryModel> {
        MutableLiveData<UIModel> liveDataHandled;
        ActionMap<RepositoryModel,UIModel> actionMap;

        public BuiltUsecaseObserver(Class<? extends BaseUseCase<RepositoryModel>> observedUsecaseClass, MutableLiveData<UIModel> liveDataHandled, ActionMap<RepositoryModel, UIModel> actionMap) {
            super();
            setObservedUsecaseClass(observedUsecaseClass);
            this.liveDataHandled = liveDataHandled;
            this.actionMap = actionMap;
        }

        @Override
        public void onNext(RepositoryModel repositoryModel) {
            if(actionMap != null && liveDataHandled != null) {
                liveDataHandled.postValue(actionMap.onAction(repositoryModel));
            }
        }
    }

    public interface ActionMap<RepositoryModel, UIModel> {
        UIModel onAction(RepositoryModel model);
    }

    public interface UseCaseMap {
        Bundle onAction(Object... model);
    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onError(Throwable e) {
        Log.e("BaseObs", "onError: ", e);
    }

    @Override
    public void onComplete() {

    }
}
