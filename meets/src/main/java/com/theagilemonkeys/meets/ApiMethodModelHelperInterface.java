package com.theagilemonkeys.meets;

import com.theagilemonkeys.meets.models.base.MeetsListener;

/**
 * Android Meets SDK
 * Original work Copyright (c) 2014 [TheAgileMonkeys]
 *
 * @author Álvaro López Espinosa
 *
 * Class that let us work with asynchronous methods and listeners
 */
public interface ApiMethodModelHelperInterface<MODEL> {
    /**
     * Waits for all asynchronous methods to be finished and then notify the listener. After
     * that, the listener is removed from this model's listeners. If there is no pending methods,
     * the listener is called immediately
     * @param listener The listener to be notified
     * @return This model
     */
    MODEL await(MeetsListener<MODEL> listener);

    /**
     * It works the same as {@link #await(com.theagilemonkeys.meets.models.base.MeetsListener)} method, but if you pass true in keepListening
     * parameter, do not extract the listener so future changes through async methods in this model
     * will be notified.
     * @param listener The listener to be notified
     * @param keepListening
     * @return This model
     */
    MODEL await(MeetsListener<MODEL> listener, boolean keepListening);

    /**
     * Remove a previously added listener. Useful to avoid memory leaks when an activity is recreated or destroyed.
     * If the listener is not found, nothing happens
     * @param listener The listener to be removed
     * @return This model
     */
    MODEL noLongerWait(MeetsListener<MODEL> listener);

    /**
     * Remove all attached listeners.
     * @return This model
     */
    MODEL allNoLongerWait();

    /**
     * By default, all asynchronous methods starts immediately. If you want next methods starts
     * after all previous ones are finished, call this method. Only affects the next call to an async
     * method.
     * @return This model
     */
    MODEL nextWaitForPrevious();
}
