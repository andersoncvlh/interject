interject
=========

A web application to interject with some preservation options.

API
---

The idea is to provide a RESTful API for identifying items, presenting options, and performing transformations. e.g.

API to perform identification of the item:
    /id/{original_url}

Interject by examining the item and offering access options;
    /interject/{original_url}

Perform actions on item:
    /action/{actor}/{parameters}/{original_url}
    /action/{actor}/{original_url}?{parameters}

Allow proxied access to remote resource:
    /action/passthrough/{original_url}


Format conversion actions
-------------------------

Look for 'difficult' image formats (e.g. X-BitMap etc.) and transform to PNG on the server side?

Offer to convert tape image formats, e.g. [taper](3)?


Emulation-based actions
-----------------------

Look for .tap files, e.g. the current JISC Search contains [121 .tap 0x13000000 files](1) (out of 318,836,024 resources), e.g. [Wheelie.tap](2) from 1999. Then, push them into a JavaScript-based emulator:

* [jsspeccy](https://github.com/gasman/jsspeccy2/blob/master/Embedding.txt) (quick win)
* [JSMESS](http://jsmess.textfiles.com/) (more platforms)

e.g. given

    http://web.archive.org/web/19991001051504/http://wkweb1.cableinet.co.uk:80/malkc/Wheelie.tap

We turn to an appropriately URL-encoded request like this:

    /interject/http://web.archive.org/web/19991001051504/http://wkweb1.cableinet.co.uk:80/malkc/Wheelie.tap

This presents options for different actions we could take. Once chosen, this directs the user to:

    /action/actor/http://web.archive.org/web/19991001051504/http://wkweb1.cableinet.co.uk:80/malkc/Wheelie.tap
    /action/taper/tzx/http://web.archive.org/web/19991001051504/http://wkweb1.cableinet.co.uk:80/malkc/Wheelie.tap

[1]: http://www.webarchive.org.uk/aadda-discovery/browse?f[0]=content_type_ext%3A%22.tap%22&f[1]=content_ffb%3A%2213000000%22
[2]: http://web.archive.org/web/19991001051504/http://wkweb1.cableinet.co.uk:80/malkc/Wheelie.tap
[3]: http://www.worldofspectrum.org/taper.html
