[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/beryx/streamplify/blob/master/LICENSE)
[![Build Status](https://img.shields.io/travis/beryx/handlebars-java-helpers/master.svg?label=Build)](https://travis-ci.org/beryx/streamplify)
## Streamplify ##


The goal of this library is to provide useful Java 8 streams and to assist you in building new streams that allow efficient parallel processing.

The utilities offered by Streamplify include:

- combinatorics streams: permutations, combinations, cartesian product etc.
- classes that help you implement your own efficient parallel streams.

**Example**
```
System.out.println(new Permutations(10)
    .parallelStream()
    .filter(perm -> {
        for(int i = 0; i < perm.length - 1; i++) {
            for(int j = i + 1; j < perm.length; j++) {
                if(Math.abs(perm[j] - perm[i]) == j - i) return false;
            }
        }
        return true;
    })
    .map(perm -> Arrays.toString(perm))
    .collect(Collectors.joining("\n")));
```


Please read the **[documentation](http://streamplify.beryx.org)** and the javadoc.

**Contribute to this project!**

We welcome all kind of contributions and there are many ways in which you can help this project.

Read the **[how to contribute](CONTRIBUTING.md)** and jump in!
