package de.lukweb.justmail.utils.interfaces;

public interface CatchStreamCallback {

    /**
     * There can be only one active callback at the same time
     *
     * @return false if you want to contine the loop, true when you're finished
     */
    boolean callback(String data);
}
