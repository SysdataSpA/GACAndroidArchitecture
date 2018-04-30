package sysdata.it.androidarchitecture.viewmodel;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Andrea Guitto on 29/03/2018.
 */
@Repeatable(UCActionsSet.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface UCAction {

    String[] actions();
}
