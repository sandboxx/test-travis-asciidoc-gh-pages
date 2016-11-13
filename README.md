[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](http://makeapullrequest.com)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/beryx/text-io/blob/master/LICENSE)
[![Build Status](https://img.shields.io/travis/beryx/text-io/master.svg?label=Build)](https://travis-ci.org/beryx/text-io)
## Text-IO ##


Text-IO is a Java library for creating text-based user interfaces.
It can be used in applications that need to read interactive input from the user.

**Features**

- supports reading values with various data types.
- allows masking the input when reading sensitive data.
- allows selecting a value from a list.
- allows to specify constraints on the input values (format patterns, value ranges, length constraints etc.).
- provides different terminal implementations and offers a Service Provider Interface (SPI) for configuring additional text terminals.

By default, Text-IO tries to use text terminals backed by [java.io.Console](http://docs.oracle.com/javase/8/docs/api/java/io/Console.html).
If no console device is present (which may happen, for example, when running the application in your IDE),
a Swing-based terminal is used instead.

*Example*

```
TextIO textIO = TextIoFactory.getTextIO();

String user = textIO.newStringInputReader()
        .withDefaultValue("admin")
        .read("Username");

String password = textIO.newStringInputReader()
        .withMinLength(6)
        .withInputMasking(true)
        .read("Password");

int age = textIO.newIntInputReader()
        .withMinVal(13)
        .read("Age");

Month month = textIO.newEnumInputReader(Month.class)
        .read("What month were you born in?");

TextTerminal terminal = textIO.getTextTerminal();
terminal.println("\nUser " + user + " is " + age + " years old, " +
        "was born in " + month + " and has the password " + password + ".");
```

Click on the image below to see the output of the above example in a Swing-based terminal.

<a href="https://github.com/beryx/textio/raw/master/doc/img/swing-terminal-animated.gif"><img src="https://github.com/beryx/textio/raw/master/doc/img/swing-terminal-thumb.gif"></a>
