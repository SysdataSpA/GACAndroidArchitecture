package sysdata.it.androidarchitecture.viewmodel;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Andrea Guitto on 29/03/2018.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface UCActionsSet {
    UCAction[] value();
}
