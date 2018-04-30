package sysdata.it.androidarchitecture.repository;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static sysdata.it.androidarchitecture.repository.Resource.Status.FAILED;
import static sysdata.it.androidarchitecture.repository.Resource.Status.RUNNING;
import static sysdata.it.androidarchitecture.repository.Resource.Status.SUCCESS;

/**
 * Created by Brando Baldassarre on 26/02/2018.
 */

//a generic class that describes a data with a status
public class Resource<T> {
    @NonNull
    public final Status status;
    @Nullable public final T data;
    @Nullable public final Throwable throwable;
    @Nullable
    public final String message;
    @NonNull public ErrorObject errorObject;

    private Resource(@NonNull Status status, @Nullable T data, @Nullable String message, @Nullable Throwable throwable, @NonNull ErrorObject errorObject) {
        this.status = status;
        this.data = data;
        this.message = message;
        this.throwable = throwable;
        this.errorObject = errorObject;
    }

    public static <T> Resource<T> success(@NonNull T data) {
        return new Resource<>(SUCCESS, data, null, null, null);
    }

    public static <T> Resource<T> error(String msg, @Nullable Throwable throwable) {
        return error(msg, null, throwable);
    }

    public static <T> Resource<T> error(String msg, T data, @Nullable Throwable throwable) {
        return new Resource<>(FAILED, data, msg, throwable, null);
    }

    public static <T> Resource<T> loading(@Nullable T data) {
        return new Resource<>(RUNNING, data, null, null, null);
    }

    public static <T> Resource<T> mappingData(Resource resource, @Nullable T data) {
        return new Resource<>(resource.status, data, resource.message, resource.throwable, resource.errorObject);
    }

    public enum Status {
        RUNNING,
        SUCCESS,
        FAILED
    }

    public static class ErrorObject{
        private String message;

        public ErrorObject() {
        }

        public ErrorObject(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

}
