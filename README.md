Interject
=========

A proof-of-concept on-demand format conversion system build with web archives in mind. Developed at the <a href="http://www.webarchive.org.uk/ukwa/">UK Web Archive</a>, as part of the <a href="http://www.scape-project.eu/">SCAPE Project</a>.

[![Build Status](https://travis-ci.org/ukwa/interject.png?branch=master)](https://travis-ci.org/ukwa/interject/)


Installation
------------

Perhaps the clumsiest aspect of the current implementation is the way the underlying tools are handled. The shell commands being invoked are not declared very cleanly, and the invocation code contains a number of assumptions about what is installed, and where. For example, view3dscene needs to be installed separately and symlinked into the execution folder to work, and some of the tools need a X session to function (e.g. via a Xvfb).

Development Ideas
-----------------

The packaging of tools and the declaration of how to invoke them is probably the area that needs most work in order to carry these ideas forward. For example, if would be better to package up the various tools and their dependencies along with the web service, perhaps in the form of one or more virtual machine images.

Other possible improvements are:

* Add a URL form to /inspect/ when no URL parameter is present, and add bookmarklet a la Mementos.
* Use a single parameterised route to invoke actions.
* List all known actions when no action is passed.
* Much better error handling.
    * It's not clear if/when an action goes wrong, or when type detection fails.
    * Little/no debug information is passed back.
* Some actions require proper extension to succeed, so need to use ID result to fix up input and output extensions.
* Properly generalise output format specification, including setting the content-disposition filename appropriately following conversions.
* Ensure download link is always last in the list.
* Should async properly: http://www.playframework.com/documentation/2.2.1/JavaAsync
* Provide transformed version inside an iframe, for in-line feedback? How to cope with use via transclusion?
* Highlight signature matching sections in the hex view. (Need Tika API changes to access Magic Clause classes).
* Link to related holdings via Solr?
* For characterisation:
    * FITS
    * [Jpylyzer](https://github.com/openplanets/jpylyzer) for the lone [JP2](http://www.webarchive.org.uk/interject/inspect/http://web.archive.org/web/20071005171934/http://www.wchc.org.uk/pics/disney%201.jp2)
* For actions:
    * JSMESS and more disk image types
    * fuse-utils tapconv to support TZX screenshotting via TAP
    * OpenJPEG for JP2
    * PDF/TIFF Summaries:  montage -density 150x150 -geometry 200x200+0+0 -tile 4 ~/Downloads/aghsecondannaulreport.pdf tiledoverview.jpg
    * http://www.imagemagick.org/discourse-server/viewtopic.php?f=3&t=23010
