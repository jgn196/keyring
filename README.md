# Key Ring

## What is it?

A portable password manager application written in Java.

It stores your passwords in an encrypted file that you can keep on removable media.

## Where to get it

Download the source code from GitHub:

https://github.com/jgn196/keyring

## How to build it

To build the project you will need:
* Maven
* Java JDK 1.8
* An Internet connection for downloading dependencies

Assuming you have all of these, issue this command from the project root directory:

`mvn package`

The built application packages will be created in the `target` directory.  

## How to install it

Take the built package of your choice (packages are built in Zip, TAR/GZ and TAR/BZ2 formats) 
and extract it into an installation directory.
This can be on removable media, such as a USB memory stick.

## How to use it

Run the `KeyRing.bat` batch file or `KeyRing` shell script depending on which OS you are using.
Basic usage is printed to the console.

To store a password for a user called "Bill" on a web site called "www.foo.com":

`KeyRing put www.foo.com Bill`

To see the stored password for the user on the site:
 
`KeyRing get www.foo.com Bill`

## Cryptography Notes

All passwords are encrypted using
 
* 256-bit AES 
* The encryption key is derived from the user supplied password using the scheme in PKCS12
* The password is salted with cryptographically random salt
* Cryptographic functions are used from the Bouncy Castle cryptographic library
