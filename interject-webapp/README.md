interject-webapp
================

A web application to interject with some preservation options.


Developer Notes
---------------
This web application uses a git submodule to inherit the UKWA Bootstrap theme. Therefore, to get a fully working copy, you may need to do this:

    $ git submodule init
    Submodule 'interject-webapp/public/bootstrap' (https://github.com/ukwa/bootstrap.git) registered for path 'interject-webapp/public/bootstrap'
    $ git submodule update
    Cloning into 'interject-webapp/public/bootstrap'...
    remote: Counting objects: 24606, done.
    remote: Compressing objects: 100% (8591/8591), done.
    remote: Total 24606 (delta 15835), reused 24603 (delta 15833)
    Receiving objects: 100% (24606/24606), 20.18 MiB | 502 KiB/s, done.
    Resolving deltas: 100% (15835/15835), done.
    Submodule path 'interject-webapp/public/bootstrap': checked out 'eb93fc5604bf745aeefe72567f6d6a3d942c97e0':


OR

   $ git clone --recursive ...

Running with a proxy
--------------------

    $ mvn install
    $ cd interject-webapp
    $ play clean run -Dhttp.proxyHost=explorer.bl.uk -Dhttp.proxyPort=3127


API
---

The idea is to provide a RESTful API for identifying items, presenting options, and performing transformations. e.g.

Interject by examining the item and offering access options;
    /interject/inspect/{original_url}

Perform actions on item:
    /interject/action/{actor}/{parameters}/{original_url}
or
    /interject/action/{actor}/{original_url}?{parameters}

Allow proxied access to remote resource:
    /interject/action/passthrough/{original_url}


Format conversion actions
-------------------------

Look for 'difficult' image formats (e.g. X-BitMap etc.) and transform to PNG on the server side?

Offer to convert tape image formats, e.g. [taper](3)?


Emulation-based actions
-----------------------

Look for .tap files, e.g. the current JISC Search contains [121 .tap 0x13000000 files](1) (out of 318,836,024 resources), e.g. [Wheelie.tap](2) from 1999.
Also, http://www.webarchive.org.uk/aadda-discovery/formats?f[0]=content_type_ext%3A%22.tzx%22 857 of those.
And http://www.webarchive.org.uk/aadda-discovery/formats?f[0]=content_type_ext%3A%22.z80%22 -
132 .z80, but this looks a bit confused (also getting image/cgm as a match) and a large range of FFBs.



Then, push them into a JavaScript-based emulator:

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
