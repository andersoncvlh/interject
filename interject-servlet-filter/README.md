Interject Servlet Request Filter
================================

This is a Servlet Request Filter that you can add to any Servlet-based web application to implmement content type conversion on demand.

To see how it works, launch the test service using:

    mvn jetty:run

...and go to http://localhost:8989/

That page will load two images, one in PNG format, and another is referenced in the HTML document as a ".bmp" version of the same image. The [InterjectRequestFilter][1] is [configured][1] to interject when the web application attempts to respond with a BMP, and replaces the response with a redirect to a conversion service.

A simple example conversion service is included, based on Java's built-in ImageIO conversion functionality.

An integration test that tests the filter and the conversion services together is provided. This demonstrates how conversion-on-demand can be used with web-based resources as a preservation action without having to modify the other resources that reference a given resource, also acts as an example of how to deploy the request filter.

[1]: src/test/java/uk/bl/wa/interject/servlet/integration/InterjectRequestFilterTest.java
[2]: src/test/resources/interject-filter.properties

