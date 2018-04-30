package sysdata.it.androidarchitecture.viewmodel;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface UCPrepareBundle {
    String actionName();
}
