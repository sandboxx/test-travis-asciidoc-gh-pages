/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.beryx.textio;

import java.util.List;
import java.util.function.Supplier;

/**
 * A reader for values of y type that implements {@link Comparable}.
 * Allows configuring the minimum and maximum permitted value.
 */
public abstract class ComparableInputReader<T extends Comparable<T>, B extends ComparableInputReader<T, B>> extends InputReader<T, B> {
    protected T minVal;
    protected T maxVal;

    protected abstract String typeNameWithIndefiniteArticle();

    public ComparableInputReader(Supplier<TextTerminal> textTerminalSupplier) {
        super(textTerminalSupplier);
    }

    /** Configures the minimum allowed value */
    public B withMinVal(T minVal) {
        this.minVal = minVal;
        return (B)this;
    }

    /** Configures the maximum allowed value */
    public B withMaxVal(T maxVal) {
        this.maxVal = maxVal;
        return (B)this;
    }

    @Override
    protected List<String> getDefaultErrorMessage(String s) {
        List<String> errList = super.getDefaultErrorMessage(s);
        if(minVal != null && maxVal != null) {
            errList.add("Expected " + typeNameWithIndefiniteArticle() + " value between " + minVal + " and " + maxVal + ".");
        } else if(minVal != null) {
            errList.add("Expected " + typeNameWithIndefiniteArticle() + " value greater than or equal to " + minVal + ".");
        } else if(maxVal != null) {
            errList.add("Expected " + typeNameWithIndefiniteArticle() + " value less than or equal to " + maxVal + ".");
        } else {
            errList.add("Expected " + typeNameWithIndefiniteArticle() + " value.");
        }
        return errList;
    }

    /** In addition to the checks performed by {@link InputReader#checkConfiguration()}, it checks if minVal &lt;= defaultVal &lt;= maxVal */
    @Override
    public void checkConfiguration() throws IllegalArgumentException {
        super.checkConfiguration();
        if(minVal != null && maxVal != null && minVal.compareTo(maxVal) > 0) throw new IllegalArgumentException("minVal = " + minVal + ", maxVal = " + maxVal);
        if(defaultValue != null) {
            if(minVal != null && defaultValue.compareTo(minVal) < 0) throw new IllegalArgumentException("minVal = " + minVal + ", defaultValue = " + defaultValue);
            if(maxVal != null && defaultValue.compareTo(maxVal) > 0) throw new IllegalArgumentException("maxVal = " + maxVal + ", defaultValue = " + defaultValue);
        }
    }

    /** Returns true if minVal &lt;= val &lt;= maxVal */
    public boolean isInRange(T val) {
        return (minVal == null || minVal.compareTo(val) <= 0) && (maxVal == null || maxVal.compareTo(val) >= 0);
    }
}
