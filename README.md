# ImSter - <ins>Im</ins>age <ins>St</ins>eganograph<ins>er</ins>

ImSter is a tool that lets you hide and view encrypted text inside images securely. 

<p align="center">
<img src=images/mainView.png alt="Main GUI view">
 </p>

Text is password encrypted using 256-bit AES and encoded into the pixels of the image themselves
rather than any metadata. It is impossible for anyone to tell by eye that there is hidden content within
an image.

## Contents
- [Demo](#demo)
- [Quick Start](#quick-start)
- [Features](#features)
- [Purpose](#purpose)
- [Command Line Interface (CLI)](#command-line-interface-cli)
- [Requirements](#requirements)
- [License](#license)

## Demo
The image on the right contains the entirety of Shakespeare's Macbeth encrypted and hidden in its pixels.

Can you tell the difference?

Original Image            |  Image with Macbeth
:-------------------------:|:-------------------------:
![Original image](images/original.png)  |  ![Image with Macbeth hidden inside](images/hidden.png)

To see for yourself, download the image on the right and decode it with ImSter using the password `ronyon`.

## Quick Start

1. Download the latest [release](https://github.com/armytricks/ImSter/releases/latest)*
2. Make sure you have Java installed (at least JRE)
3. Ensure the jar file is executable (you may need to `chmod +x ImSter-*.jar` on Linux/Mac OS)
4. Run the jar file (if opening it fails, try `java -jar ImSter-*.jar`)

*For compatibility, [older releases](https://github.com/armytricks/ImSter/releases) are also available

## Features
- 256-bit AES encryption
- AEAD using GCM encryption mode
- Single-file multi-platform portable binary (no installation, just run the file)
- CLI and GUI
- Lightweight (entire application is just 10MB)

## Purpose
While many solutions exist for securely encrypting data, ImSter serves a slightly different purpose. The purpose
 of ImSter is to attemp to keep the fact that your information even exists confidential, in addition to providing security through encryption. Note that even though the data inside is secure, LSB toggling is a common technique, and as such the knowledge of the existance of this data is susceptable to steganalysis techniques that may detect the bit-patterns produced by the encryption algorithms used.

## Command Line Interface (CLI)

ImSter v0.4.3+ comes integrated with a CLI. For examples of usage in scripts for bulk encryption/decryption see the [examples](examples) folder.

To use it, specify commands as follows:

#### Encoding
`java -jar ImSter-xxx.jar encode -i input.png -o output.png -m "message" -p password`
#### Decoding
`java -jar ImSter-xxx.jar decode -i input.png -p password`

#### Legacy Support

To use legacy mode, supply the `-l` switch

#### Output

The decoded message goes to stdout. Any errors encountered will be sent to stderr.

## Requirements

- Operating System: Windows/Linux/Mac OS
- Java Version: 1.8+ (i.e. JRE 8+)
- Encode Input: PNG*/JPEG**
- Encode Output: PNG only (ImSter converts JPEG input to PNG for output)
- Decode Input: PNG only

*Indexed (Colormap) PNGs are supported by converting output to truecolor

**JPEG is supported by converting the input into PNG

## [License](LICENSE)

<!---
Add libraries, manual build?, and license, and how it works?
-->
