package com.sun.tools.xjc.addon.krasa.validations;

import org.apache.maven.plugin.logging.Log;

/**
 * A log that writes nothing, given to the mojo the fixture runner drives: one run per fixture would
 * otherwise print the whole configuration of the mojo and the debug log of XJC, which is thousands
 * of lines per build and nothing a reader of a failing test needs.
 */
class SilentMojoLog implements Log {

    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public void debug(CharSequence content) {
    }

    @Override
    public void debug(CharSequence content, Throwable error) {
    }

    @Override
    public void debug(Throwable error) {
    }

    @Override
    public boolean isInfoEnabled() {
        return false;
    }

    @Override
    public void info(CharSequence content) {
    }

    @Override
    public void info(CharSequence content, Throwable error) {
    }

    @Override
    public void info(Throwable error) {
    }

    @Override
    public boolean isWarnEnabled() {
        return false;
    }

    @Override
    public void warn(CharSequence content) {
    }

    @Override
    public void warn(CharSequence content, Throwable error) {
    }

    @Override
    public void warn(Throwable error) {
    }

    @Override
    public boolean isErrorEnabled() {
        return false;
    }

    @Override
    public void error(CharSequence content) {
    }

    @Override
    public void error(CharSequence content, Throwable error) {
    }

    @Override
    public void error(Throwable error) {
    }
}
