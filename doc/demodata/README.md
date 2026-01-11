# MockSMTP demo data

This is the source material for the demo data included with MockSMTP.
I've left a few notes in case you would like to edit or recreate these messages for yourself.

(Note that shell commands are designed to run on a Mac.
You might need minor modifications to get them to run on your Linux distro of choice.)

You can start MockSMTP with this data pre-loaded by adding the `demo` flag at the command line:
```shell
java -jar release/MockSMTP.jar --demo
```

## File structure

The demo data files are stored in `src/main/resources/demodata`.

Each message is a self-contained email message, containing headers, body text, and attachments.
MockSMTP doesn't validate character encodings of these files and presumes they are all UTF-8.

Note that the files use CRLF line endings, as per the SMTP protocol,
so make sure your text editor or IDE can preserve them.
Since the application's source code uses Unix-style LF line endings,
Git will complain when you're committing changes to the demo data,
since text files should consistently use one line ending or another;
this warning can be ignored.

## Loading messages from the command line

To load messages from a file, you can use the standard NetCat command (or `nc` for short)
to send the SMTP connection commands, the message itself, and the SMTP quit command.
Some pre-defined examples of the SMTP commands are hard-coded into MockSMTP,
but they are included in this documentation as separate files that can be used externally.

For example, if your server is listening to port 25:
```shell
cat doc/demodata/email-headers.txt src/main/resources/demodata/email-message2.txt doc/demodata/email-footers.txt | nc -c localhost 25
```

## Recreating attachments

Some of the demo data are multi-part emails containing attachments, stored as Base64-encoded binaries.
To convert a binary file into a Base64-encoded text file, you can use the standard `base64` command:
```shell
base64 -b 72 -i doc/demodata/lizardman1.jpeg -o output.jpeg.base64
```

The resulting file can be added to a multi-part email by adding another content boundary,
along with some metadata for the multi-part section you are adding to the message,
and adding the Base64-encoded data directly into the new section.
See the end results in `src/main/resources/demodata` for some examples.

To convert a Base64 text file back to its original binary:
```shell
base64 -d -i output.jpeg.base64 -o output.jpeg
```
