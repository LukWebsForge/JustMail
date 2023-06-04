# JustMail
An unfinished attempt at creating a fully integrated mail server supporting SMTP and IMAP in Java.

![SMTP](https://img.shields.io/badge/SMTP-supported-brightgreen.svg)
![IMAP](https://img.shields.io/badge/IMAP-in_progress-yellow.svg)

## Building

You can compile the mail server using [Maven](https://maven.apache.org).

```bash
mvn package
```

The compilation process outputs the build artifact `target/JustMail.jar` which contains all dependencies.

The mail server can be started in the command line.
```
java -jar JustMail.jar
```

## Implementation Status

Take a look at the project's [issues](https://github.com/LukWebsForge/JustMail/issues) to see which parts of the relevant mail standards are implemented.

