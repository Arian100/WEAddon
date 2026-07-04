package me.arian.wea.util;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class NonNulls {

    public static <T> T nonNull(T obj, String msg) {
        if (obj != null) {
            return obj;
        } else {
            throw new NullPointerException(msg);
        }
    }

    public static <T> T nonNull(T obj, String msg, Consumer<T> action) {
        if (obj != null) {
            action.accept(obj);
            return obj;
        } else {
            throw new NullPointerException(msg);
        }
    }

    public static <T, S> T nonNullWithAction(T obj, S actionObject, String msg, Consumer<S> actionIfNull) {
        if (obj != null) {
            return obj;
        } else {
            actionIfNull.accept(actionObject);
            throw new NullPointerException(msg);
        }
    }

    public static <T> boolean checkNonNull(T obj, String msg, Consumer<T> action) {
        if (obj != null) {
            action.accept(obj);
            return true;
        } else {
            return false;
        }
    }

    public static <T> boolean checkNonNull(T obj, String msg) {
        return obj != null;
    }

    @Nullable
    public static <T> T nonNullWithReturn(T obj, Consumer<T> action) {
        if (obj != null) {
            action.accept(obj);
        }
        return obj;
    }

    public static <T> boolean nonNull(T obj) {
        return obj != null;
    }
}
