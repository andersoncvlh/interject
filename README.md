Interject
=========

A proof-of-concept on-demand format conversion system build with web archives in mind.

[![Build Status](https://travis-ci.org/ukwa/interject.png?branch=master)](https://travis-ci.org/ukwa/interject/)

* Characterisation with Tika

To Do
-----

* Remove/decode spaces etc. from filename.
* Finish hex view, splitting large files into head/tail views.
* Finish text view.
* Finish metadata view.
* Report numbers of metadata properties etc in summary view.
* Ensure download link is always last in the list.
* Add URL form to the homepage, a la Mementos.


More speculatively:

* Provide transformed version inside an iframe, for inline feedback.
* Highlight signature matching sections in the hex view.
* Link to related holdings via Solr?


We could also consider adding:

* For characterisation:
    * Nanite/DROID
    * FITS
    * [Jpylyzer](https://github.com/openplanets/jpylyzer) for the lone [JP2](http://www.webarchive.org.uk/interject/inspect/http://web.archive.org/web/20071005171934/http://www.wchc.org.uk/pics/disney%201.jp2)
* For actions:
    * JSMESS and more disk image types
    * OpenJPEG for JP2
