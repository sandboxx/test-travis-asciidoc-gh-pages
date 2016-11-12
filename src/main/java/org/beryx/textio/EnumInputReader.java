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

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Supplier;

/**
 * A reader for enum values.
 * It initializes {@link #possibleValues} with the constants returned by the <tt>values()</tt> method.
 * It is not allowed to call {@link #withPossibleValues(List)} or {@link #withPossibleValues(Enum[])} for this reader.
 * By default, it uses a numbered list for displaying the possible values.
 */
public class EnumInputReader<T extends Enum<T>> extends InputReader<T, EnumInputReader<T>> {
    private final Map<String, T> enumValues = new LinkedHashMap<String, T>();

    public EnumInputReader(Supplier<TextTerminal> textTerminalSupplier, Class<T> enumClass) {
        super(textTerminalSupplier);
        try {
            Method mValues = enumClass.getMethod("values");
            T[] values = (T[])mValues.invoke(null);
            for(T value : values) enumValues.put(value.toString(), value);
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot create EnumInputReader<" + enumClass.getName() + ">", e);
        }
        this.possibleValues = new ArrayList<>(enumValues.values());
        this.numberedPossibleValues = true;
    }

    /** Always throws UnsupportedOperationException. */
    @Override
    public EnumInputReader<T> withPossibleValues(T... possibleValues) {
        throw new UnsupportedOperationException();
    }

    /** Always throws UnsupportedOperationException. */
    @Override
    public EnumInputReader<T> withPossibleValues(List<T> possibleValues) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ParseResult<T> parse(String s) {
        T value = enumValues.get(s);
        if(value != null) return new ParseResult<>(value);
        return new ParseResult<T>(null, getErrorMessage(s));
    }
}
