package sysdata.it.androidarchitecture.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;

import java.io.InvalidObjectException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import sysdata.it.androidarchitecture.BuildConfig;
import sysdata.it.androidarchitecture.usecase.BaseUseCase;
import sysdata.it.androidarchitecture.usecase.BaseUsecaseObserver;

/**
 * Created by Brando Baldassarre on 08/12/2017.
 */

public abstract class BaseArchitectureViewModel extends ViewModel {

    protected String TAG = this.getClass().getSimpleName();

    Map<BaseUsecaseObserver, BaseUseCase> singleInstanceUsecaseMap = new HashMap<>();

    Map<String, ObserverMetaData> observers;
    Map<String, PrepareBundleMetaData> preActions;

    private void loadAnnotations() {
        observers = new HashMap<>();
        preActions = new HashMap<>();

        Field[] declaredFields = getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            Annotation[] annotationList = field.getDeclaredAnnotations();
            populatePropertyAnnotationMap(field, annotationList);
        }

        Method[] declaredMethods = getClass().getDeclaredMethods();
        if(declaredMethods != null) {
            for (Method declaredMethod : declaredMethods) {
                Annotation[] annotationList = declaredMethod.getDeclaredAnnotations();
                populateMethodsAnnotationMap(declaredMethod, annotationList);
            }
        }
    }

    private void populatePropertyAnnotationMap(Field field, Annotation[] annotationList) {
        for (Annotation annotation : annotationList) {
            if(annotation instanceof UCAction) {
                populateObserversCollection((UCAction) annotation, field);
            }else if(annotation instanceof UCActionsSet) {
                UCAction[] actionsSet = ((UCActionsSet) annotation).value();
                if(actionsSet != null) {
                    for (UCAction ucAction : actionsSet)
                        populateObserversCollection(ucAction, field);
                }
            }
        }
    }

    private void populateMethodsAnnotationMap(Method declaredMethod, Annotation[] annotationList) {
        for (Annotation annotation : annotationList) {
            if(annotation instanceof UCAction) {
                populateObserversCollection((UCAction) annotation, declaredMethod);
            }else if(annotation instanceof UCActionsSet) {
                UCAction[] actionsSet = ((UCActionsSet) annotation).value();
                if(actionsSet != null) {
                    for (UCAction ucAction : actionsSet)
                        populateObserversCollection(ucAction, declaredMethod);
                }
            }else if(annotation instanceof UCPrepareBundle) {
                populatePreActionCollection((UCPrepareBundle) annotation, declaredMethod);
            }
        }
    }

    private void populateObserversCollection(UCAction annotation, Object field) {
        ObserverMetaData data = null;
        if(field instanceof Field) {
            data = new ObserverMetaData(annotation, (Field) field, null);
        }else if(field instanceof Method) {
            data = new ObserverMetaData(annotation,null, (Method) field);
        }

        String[] actions = data.actions;
        for (String action : actions) {
            observers.put(action, data);
        }
    }

    private void populatePreActionCollection(UCPrepareBundle annotation, Object field) {
        PrepareBundleMetaData data = null;
        if(field instanceof Field) {
            data = new PrepareBundleMetaData(annotation, (Field) field, null);
        }else if(field instanceof Method) {
            data = new PrepareBundleMetaData(annotation,null, (Method) field);
        }

        preActions.put(annotation.actionName(), data);
    }

    protected Bundle onPreparingBundle(String actionName, Object[] parameters) {
        Bundle returnValue = null;

        if(preActions != null) {
            PrepareBundleMetaData prepareBundleMetaData = preActions.get(actionName);
            if (prepareBundleMetaData != null) {
                try {
                    returnValue = (Bundle) prepareBundleMetaData.retrieveObject(this, parameters);
                } catch (Exception e) {
                    Log.e(TAG, "Exception preparing bundle of action ( actionName = "+actionName+" , parameters = "+logObjectParameters(parameters)+" ) . "
                            + "Probably you are passing wrong parameters to method annotated with @UCPrepareBundle in you ViewModel or the line where you call execute() has some unexpected parameter (maybe null).", e);
                }
            }
        }

        return returnValue;
    }

    public void execute(String actionName) {
        execute(actionName, null);
    }

    public void execute(String actionName, Object... parameters) {

        Class<? extends BaseUseCase> usecaseClass = null;
        BaseUseCase usecase = null;
        BaseUsecaseObserver usecaseObserver = null;

        if(observers == null || preActions == null) {
            loadAnnotations();
        }

        if(!TextUtils.isEmpty(actionName)) {
            ObserverMetaData observerMetaData = observers.get(actionName);
            if (observerMetaData != null) {
                try {
                    usecaseObserver = (BaseUsecaseObserver) observerMetaData.retrieveObject(this);
                    usecaseClass = usecaseObserver.getObservedUsecaseClass();
                }  catch (Exception e) {
                    Log.e(TAG, "Exception retrieving observer linked to action = "+actionName, e);
                }
            }
        }

        if(usecaseClass == null) Log.e(TAG, "Warning no usecase associated with action "+actionName);

        if(usecaseClass != null ) {
            usecase = singleInstanceUsecaseMap.get(usecaseObserver);
            if(usecase == null) {
                try {
                    usecase = usecaseClass.getDeclaredConstructor(Scheduler.class, Scheduler.class).newInstance(Schedulers.io(), Schedulers.computation());
                    singleInstanceUsecaseMap.put(usecaseObserver, usecase);
                } catch (Exception e) {
                    Log.e(TAG, "Usecase cannot be instantiated by reflection! ", e);
                }
            }
        }

        if(usecase == null)  Log.e(TAG, "Warning no usecase associated with action "+actionName);
        if(usecaseObserver == null) Log.e(TAG, "Warning no observer associated with action "+actionName);

        if(usecase != null && usecaseObserver != null){
            // first unsubscribe last one
            if(usecase != null) {
                usecase.unsubscribe();
            }
            // then execute other one
            Bundle bundle = onPreparingBundle(actionName, parameters);
            try {
                Log.d(TAG, "Executing action ( actionName = "+actionName+" , parameters = "+logBundleParameters(bundle) +" ) ");
                usecase.execute(usecaseObserver, bundle);
            }catch (Exception e){
                Log.e(TAG, "Exception executing action with details ( actionName = "+actionName+" , parameters = "+logBundleParameters(bundle)+" ). Probably something went wrong inside your current UseCase logic, check if Bundle are correct",e);
            }
        }
    }

    private String logObjectParameters(Object... parameters) {
        if(parameters == null) return "null";

        StringBuilder builder = new StringBuilder("{\n");
        for (Object object : parameters) {
            builder.append(""+object+",");
        }
        builder.append("\n} ");
        return null;
    }

    private String logBundleParameters(Bundle parameters) {
        if(parameters == null) return "null";

        StringBuilder builder = new StringBuilder("{\n");
        Set<String> keySet = parameters.keySet();
        for (String key : keySet) {
                builder.append("\n\t"+key+" : "+parameters.get(key));
        }
        builder.append("\n} ");
        return null;
    }

    /**
     * In this method you must call release(...) passing the usecase observer
     *
     */
    protected abstract void onReleaseObservers();

    /**
     * This method should be called on cleared
     */
    protected void releaseObservers(BaseUsecaseObserver... usecaseObserverList){
        // disposableObserver.remove(usecaseObserver);
        if(usecaseObserverList != null && usecaseObserverList.length > 0) {
            for (BaseUsecaseObserver usecaseObserver : usecaseObserverList) {
                BaseUseCase baseUseCase = singleInstanceUsecaseMap.get(usecaseObserver);
                if (baseUseCase != null) {
                    baseUseCase.unsubscribe();
                }
                singleInstanceUsecaseMap.remove(usecaseObserver);
            }
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();


        onReleaseObservers();

        if(singleInstanceUsecaseMap != null && !singleInstanceUsecaseMap.isEmpty()){
            for (BaseUsecaseObserver usecaseObserver : singleInstanceUsecaseMap.keySet()) {
                Log.e(TAG, "Forgot to call clear() method in for object " + usecaseObserver.getClass().getSimpleName());
            }
        }
    }

    /**
     * Object used to handle parameters
     */
    private static class BaseMetaData {
        Method method;
        Field field;

        public BaseMetaData(Field actionField, Method method) {
            this.method = method;
            this.field = actionField;
        }

        public boolean isFieldAnnotation(){
            return field != null;
        }

        public Object retrieveObject(Object parentObject) throws InvocationTargetException, IllegalAccessException {
            Object returnValue = null;
            if(isFieldAnnotation()) {
                field.setAccessible(true);
                returnValue = field.get(parentObject);
                field.setAccessible(false);
            } else {
                returnValue = method.invoke(parentObject);
            }

            return returnValue;
        }

        public Object retrieveObject(Object parentObject, Object[] parameters) throws InvocationTargetException, IllegalAccessException {
            Object returnValue = null;
            if(isFieldAnnotation()) {
                field.setAccessible(true);
                returnValue = field.get(parentObject);
                field.setAccessible(false);
            } else {
                returnValue = method.invoke(parentObject, parameters);
            }

            return returnValue;
        }
    }

    private static final class ObserverMetaData extends BaseMetaData {
        String[] actions;

        ObserverMetaData(UCAction annotation, Field field, Method method){
            super(field, method);
            actions = annotation.actions();
        }
    }

    private static final class PrepareBundleMetaData extends BaseMetaData {
        String actionName;

        public PrepareBundleMetaData(UCPrepareBundle annotation, Field field, Method method) {
            super(field, method);
            this.actionName = annotation.actionName();
        }
    }
}
