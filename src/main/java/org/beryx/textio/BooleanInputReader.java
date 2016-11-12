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
 * A reader for boolean values.
 * Allows configuring which string value should be interpreted as <i>true</i> and which as <i>false</i>.
 */
public class BooleanInputReader extends InputReader<Boolean, BooleanInputReader> {
    public BooleanInputReader(Supplier<TextTerminal> textTerminalSupplier) {
        super(textTerminalSupplier);
    }
    private String trueInput = "true";
    private String falseInput = "false";

    /** Configures the string value that corresponds to <i>true</i>. */
    public BooleanInputReader withTrueInput(String trueInput) {
        if(trueInput == null || trueInput.trim().isEmpty()) throw new IllegalArgumentException("trueInput is empty");
        this.trueInput = trueInput;
        return this;
    }

    /** Configures the string value that corresponds to <i>false</i>. */
    public BooleanInputReader withFalseInput(String falseInput) {
        if(falseInput == null || falseInput.trim().isEmpty()) throw new IllegalArgumentException("falseInput is empty");
        this.falseInput = falseInput;
        return this;
    }

    @Override
    protected List<String> getDefaultErrorMessage(String s) {
        List<String> errList = super.getDefaultErrorMessage(s);
        errList.add("Expected: " + trueInput + " / " + falseInput);
        return errList;
    }

    @Override
    public ParseResult<Boolean> parse(String s) {
        if(trueInput.equalsIgnoreCase(s)) return new ParseResult<>(true);
        if(falseInput.equalsIgnoreCase(s)) return new ParseResult<>(false);
        return new ParseResult<>(null, getErrorMessage(s));
    }
}
