package sysdata.it.androidarchitecture;

import android.content.Context;
import android.os.Bundle;

public interface BaseClassProvider {
    Object provideObject(Bundle param, Context context);
}