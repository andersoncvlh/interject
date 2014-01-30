Interject
=========

A proof-of-concept on-demand format conversion system build with web archives in mind.

[![Build Status](https://travis-ci.org/ukwa/interject.png?branch=master)](https://travis-ci.org/ukwa/interject/)

* Characterisation with Tika

To Do
-----

* Should cache properly: http://www.playframework.com/documentation/2.2.1/JavaCache
* Should async properly: http://www.playframework.com/documentation/2.2.1/JavaAsync
* Make front-page look good.
* Finish VRML support methods.
    * Implement wine/ivvrml conversion.
    * Add VRML97/X3D renderer.
* Ensure download link is always last in the list.

More speculatively:

* Provide transformed version inside an iframe, for inline feedback.
* Highlight signature matching sections in the hex view. (Need Tika API changes to access Magic Clause classes)
* Link to related holdings via Solr?
* For characterisation:
    * Nanite/DROID
    * FITS
    * [Jpylyzer](https://github.com/openplanets/jpylyzer) for the lone [JP2](http://www.webarchive.org.uk/interject/inspect/http://web.archive.org/web/20071005171934/http://www.wchc.org.uk/pics/disney%201.jp2)
* For actions:
    * JSMESS and more disk image types
    * fuse-utils tapconv to support TZX screenshotting via TAP
    * OpenJPEG for JP2
