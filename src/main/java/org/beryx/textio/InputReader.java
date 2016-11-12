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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A reader for values of type T.
 * It offers a fluent interface for configuring various settings such as input masking, possible values, default value, error messages etc.
 * @param <T> the type of the values that can be read by this InputReader
 * @param <B> the type of this InputReader
 */
public abstract class InputReader<T, B extends InputReader<T, B>> {
    /** Functional interface for providing error messages */
    @FunctionalInterface
    public static interface ErrorMessageProvider {
        /**
         * Returns the list of error messages for the given string representation of the value
         * @param sVal the string representation of the value
         * @param propertyName the name of the property corresponding to this value. May be null.
         * @return - the list of error messages or null in no error has been detected.
         */
        List<String> getErrorMessage(String sVal, String propertyName);
    }

    /**
     * A holder object returned by the {@link #parse(String)} method, containing the parsed value and/or the error messages.
     * @param <T>
     */
    public static class ParseResult<T> {
        private final T value;
        private final List<String> errorMessages;

        public ParseResult(T value) {
            this.value = value;
            this.errorMessages = null;
        }

        public ParseResult(T value, List<String> errorMessages) {
            this.value = value;
            this.errorMessages = (errorMessages != null && errorMessages.isEmpty()) ? null : errorMessages;
        }

        public ParseResult(T value, String... errorMessages) {
            this.value = value;
            this.errorMessages = (errorMessages.length == 0) ? null : Arrays.asList(errorMessages);
        }

        public T getValue() {
            return value;
        }

        public List<String> getErrorMessages() {
            return errorMessages;
        }
    }

    /** Supplier of {@ling TextTerminal}s */
    protected final Supplier<TextTerminal> textTerminalSupplier;

    /** null, if there is no default value */
    protected T defaultValue;

    /** Non-null and non-empty, if the value to be read must be chosen from a list of allowed values. */
    protected List<T> possibleValues;

    /** If true, the list of possible values will be numbered and the desired value will be selected by choosing the corresponding number. */
    protected boolean numberedPossibleValues = false;

    /** The provider of error messages. If null, the {@link #getDefaultErrorMessage(String)} will be used. */
    protected ErrorMessageProvider errorMessageProvider;

    /** The name of the property corresponding to the value to be read. May be null. */
    protected String propertyName;

    /** If true, the input will be masked (useful for example when reading passwords) */
    protected boolean inputMasking = false;

    /** If true, the input will be trimmed. Default: true */
    protected boolean inputTrimming = true;

    /** The formatter used when displaying values of type T. Default: use {@link String#valueOf(Object)} */
    protected Function<T, String> valueFormatter = val -> String.valueOf(val);

    /**
     * Parses the input string
     * @param s the input string
     * @return a {@link ParseResult} that holds the parsed value and/or the error messages, if errors occurred.
     */
    public abstract ParseResult<T> parse(String s);

    public InputReader(Supplier<TextTerminal> textTerminalSupplier) {
        this.textTerminalSupplier = textTerminalSupplier;
    }

    public B withDefaultValue(T defaultValue) {
        this.defaultValue = defaultValue;
        return (B)this;
    }

    public B withPossibleValues(T... possibleValues) {
        this.possibleValues = null;
        if(possibleValues.length > 0) {
            this.possibleValues = new ArrayList<>();
            for(T val : possibleValues) {
                this.possibleValues.add(val);
            }
        }
        return (B)this;
    }

    public B withPossibleValues(List<T> possibleValues) {
        this.possibleValues = (possibleValues != null && possibleValues.isEmpty()) ? null : possibleValues;
        return (B)this;
    }

    public B withNumberedPossibleValues(boolean numbered) {
        this.numberedPossibleValues = numbered;
        return (B)this;
    }

    public B withInputMasking(boolean inputMasking) {
        this.inputMasking = inputMasking;
        return (B)this;
    }

    public B withInputTrimming(boolean inputTrimming) {
        this.inputTrimming = inputTrimming;
        return (B)this;
    }

    public B withPropertyName(String propertyName) {
        this.propertyName = "".equals(propertyName) ? null : propertyName;
        return (B)this;
    }

    public B withValueFormatter(Function<T, String> valueFormatter) {
        this.valueFormatter = valueFormatter;
        return (B)this;
    }

    public B withErrorMessageProvider(ErrorMessageProvider errorMessageProvider) {
        this.errorMessageProvider = errorMessageProvider;
        return (B)this;
    }

    /**
     * Returns a generic error message.
     */
    protected String getDefaultErrorMessage() {
        StringBuilder errBuilder = new StringBuilder("Invalid value");
        if(propertyName != null) errBuilder.append(" for '" + propertyName + "'");
        errBuilder.append('.');
        return errBuilder.toString();
    }

    /**
     * If no <tt>errorMessageProvider</tt> exists, this method is used to provide the list of error messages for the input string <tt>s</tt>.
     * It should return a non-empty list of messages.
     */
    protected List<String> getDefaultErrorMessage(String s) {
        return new ArrayList<>(Collections.singleton(getDefaultErrorMessage()));
    }

    /**
     * Provides the list of error messages for the input string <tt>s</tt>.
     * If an <tt>errorMessageProvider</tt> exists, it will be used. Otherwise, {@link #getDefaultErrorMessage(String)} will be called.
     */
    public final List<String> getErrorMessage(String s) {
        ErrorMessageProvider provider = (this.errorMessageProvider != null) ? this.errorMessageProvider : (sVal, pVal) -> getDefaultErrorMessage(sVal);
        return provider.getErrorMessage(s, propertyName);
    }

    /**
     * Reads a value of type T.
     * It repeatedly prompts the users to enter the value, until they provide a valid input string.
     * @param prompt the messages to be displayed for prompting the user to enter the value
     * @return the value of type T parsed from the input string
     */
    public T read(String... prompt) {
        return read(Arrays.asList(prompt));
    }

    /**
     * Reads a value of type T.
     * It repeatedly prompts the users to enter the value, until they provide a valid input string.
     * @param prompt the list of messages to be displayed for prompting the user to enter the value
     * @return the value of type T parsed from the input string
     */
    public T read(List<String> prompt) {
        checkConfiguration();
        TextTerminal textTerminal = textTerminalSupplier.get();
        while(true) {
            printPrompt(prompt, textTerminal, possibleValues);
            String sVal = textTerminal.read(inputMasking);
            if(sVal != null && inputTrimming) sVal = sVal.trim();
            if(sVal == null || sVal.isEmpty()) {
                if(defaultValue != null) return defaultValue;
            }
            if(possibleValues == null || !numberedPossibleValues) {
                ParseResult<T> result = parse(sVal);
                List<String> errMessages = result.getErrorMessages();
                if(errMessages == null) {
                    if(possibleValues == null || possibleValues.contains(result.getValue())) {
                        return result.getValue();
                    }
                    textTerminal.print(getDefaultErrorMessage());
                    textTerminal.println(" You must enter one of the displayed values.");
                    textTerminal.println( );
                } else {
                    textTerminal.println(errMessages);
                    textTerminal.println();
                }
            } else {
                try {
                    int optIndex = Integer.parseInt(sVal);
                    if(optIndex > 0 && optIndex <= possibleValues.size()) {
                        return possibleValues.get(optIndex - 1);
                    }
                } catch (NumberFormatException e) {
                    // Continue the execution. The next statement will print the error message.
                }
                textTerminal.print(getDefaultErrorMessage());
                textTerminal.println(" Enter a value between 1 and " + possibleValues.size() + ".");
                textTerminal.println();
            }
        }
    }

    /**
     * Checks if the reader is correctly configured.
     * This default implementation checks if the defaultValue is among the possibleValues.
     * @throws java.lang.IllegalArgumentException
     */
    public void checkConfiguration() throws java.lang.IllegalArgumentException {
        if(possibleValues != null && defaultValue != null) {
            if(!possibleValues.contains(defaultValue)) {
                throw new IllegalArgumentException("Invalid default value: " + defaultValue + ". Allowed values: " + possibleValues);
            }
        }
    }

    /**
     * Displays a prompt inviting the user to enter a value.
     * @param prompt the list of prompt messages. May be null.
     * @param textTerminal the text terminal to which the messages are sent.
     * @param options the list of options from which the user can choose a value. May be null.
     */
    protected void printPrompt(List<String> prompt, TextTerminal textTerminal, List<T> options) {
        textTerminal.print(prompt);
        boolean useColon = false;
        if(prompt != null && !prompt.isEmpty()) {
            String lastLine = prompt.get(prompt.size() - 1);
            useColon = !lastLine.isEmpty() && Character.isJavaIdentifierPart(lastLine.charAt(lastLine.length() - 1));
        }
        if(options == null) {
            if(defaultValue != null) textTerminal.print(" [" + defaultValue + "]");
            textTerminal.print(useColon ? ": " : " ");
        } else {
            textTerminal.println(useColon ? ":" : "");
            for(int i = 0; i < options.size(); i++) {
                T option = options.get(i);
                boolean isDefault = (defaultValue != null) && defaultValue.equals(option);
                textTerminal.println((isDefault ? "* ": "  ")
                        + (numberedPossibleValues ? ((i + 1) + ": ") : "")
                        + valueFormatter.apply(option));
            }
            textTerminal.print("Enter your choice: ");
        }
    }
}
