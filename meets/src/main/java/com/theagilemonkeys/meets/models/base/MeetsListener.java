package com.theagilemonkeys.meets.models.base;

/**
 * Android Meets SDK
 * Original work Copyright (c) 2014 [TheAgileMonkeys]
 *
 * @author Álvaro López Espinosa
 */
public interface MeetsListener<RESULT> {
    void onDone(RESULT result);
    void onFail(RESULT result, Exception e);
    void onAlways(RESULT result, Exception e);

    // Convenience class to use as Null object pattern or to implement only the method you want.
    public static class Empty<RESULT> implements MeetsListener<RESULT> {
        @Override public void onDone(RESULT result) {}
        @Override public void onFail(RESULT result, Exception e) {}
        @Override public void onAlways(RESULT result, Exception e) {}
    }

    public static interface Collection<COLLECTION extends MeetsCollection<MODEL>, MODEL> extends MeetsListener<COLLECTION> {
        void onReset(COLLECTION modelCollection);
        void onPush(COLLECTION modelCollection, MODEL model);
        void onExtract(COLLECTION modelCollection, MODEL model);

        // Convenience class to use as Null object pattern or to implement only the method you want.
        public static class Empty<COLLECTION extends MeetsCollection<MODEL>, MODEL> implements Collection<COLLECTION, MODEL> {
            @Override public void onDone(COLLECTION result) {}
            @Override public void onFail(COLLECTION result, Exception e) {}
            @Override public void onAlways(COLLECTION result, Exception e) {}
            @Override public void onReset(COLLECTION modelCollection) {}
            @Override public void onPush(COLLECTION modelCollection, MODEL model) {}
            @Override public void onExtract(COLLECTION modelCollection, MODEL model) {}
        }
    }
}