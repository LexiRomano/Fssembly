# Fssembly

`Fssembly` is a proto-assembly language written for the `FGC-16` computer architecture. The purpose of this project was to try to design an
assembly language with little prior knowledge and learn by failing... a lot. This is only one part of a much larger project named F-Suite
where I will try to build everything from the ground up with little to no help or training.

This is the "`Fssembler`" for the `Fssembly` language.

## Usage

`Fssembly` was originally written in Java 17 and it can *probably* be used with anything later than Java 10. I don't really understand how Java versions
work or what's different between them so don't sue me if it breaks.

### The basics

If you do manage to be able to launch the `Fssembler.jar` file, you will notice that it's quite simple. I designed it to be something like a widget
that sits in the corner of your screen and you just hit enter every time you want it to fssemble. The top window is where you enter a file name to be
fssembled. The bottom is where the results of the run are output. Every a file is fssembled, an attempts counter will increment. This is done partially
to provide feedback that it actually did something if the output happens to match the last run's output, and partially to mock you of your never ending
failures. To get started, take a look at the datasheet below. The first page contains information about the `FGC-16` architecture which will be useful
for stuff like memory and I/O. For more information about the `FGC-16` architecture, take a look at its repo. The second page in the datasheet contains
the syntax for the `Fssembly` language. Once you have your program written in a text file in the same folder as the `Fssembler.jar` file or a subfolder
therein, enter its file path, name, and extension into the top window. Once you hit enter, a folder named `fbn` will appear containing your binary (ascii
hex) files. The files and folders are managed in the following manner:

        a.txt
            goes to
        fbn/a.fbn
        
        src/b.txt
            goes to
        fbn/b.fbn
        
        src/stuff/c.txt
            goes to
        fbn/stuff/c.fbn
        
        src/stuff/things/d.txt
            goes to
        fbn/stuff/things/d.fbn
        
        ...

### Advanced Programs

Beyond the simple address header described in the datasheet, there are a few more advanced options. To start, you can add a file name after the address
space signifier which will be prepended to the file. The name can be a maximum of 32 characters and is padded with 0s if it's shorter. The number of 256 byte
chunks the file will take up is added to the 33rd byte, just after the name, and then the program will start on the 34th byte. The header will look something
like this:

        # 0x9000 fileName

If there's something already written in binary you want to put in a src folder and be passed through using the `Fssembler`, you can make a raw file
that will just take numbers and turn them directly into an `fbn` file. A raw file can look something like this:

        # R
        0xa2
        212
        0b1110010
        0xFF
        \h // ascii representation of characters
        \i
        ...

You can also pad a file with zeros to ensure it's length is a multple of 256. To do this, simply add a P to the end of the first part of the address header.
Here's what that looks like:

        # AP [opt filename]
            or
        # RP

Padding is not currently supported for manual variable address declaration.

Once you start writing larger programs that may span multiple files, you can fssemble them all in one go using a batch file. A batch file can look something
like this:

       # B
       src/rom.txt
       src/disks/0/s0.txt
       src/disks/0/s1.txt
       ...

Simply type in the path, name, and file extension to the batch file and the `Fssembler` will do the rest.

## Datasheet

View datasheet [here](https://docs.google.com/spreadsheets/d/1bagL_yX_ullKfEMETFIMV0RIFsRq73ULrNGZyCADKzc/edit?usp=sharing)
