/**
 * 
 */
package uk.bl.wa.interject.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * Starting from:
 *   http://stackoverflow.com/a/8972088/6689
 * 
 * You need to create a Filter wherein you wrap the ServletResponse argument with a custom HttpServletResponseWrapper 
 * implementation wherein you override the getOutputStream() and getWriter() to return a custom ServletOutputStream 
 * implementation wherein you copy the written byte(s) in the base abstract OutputStream#write(int b) method. 
 * Then, you pass the wrapped custom HttpServletResponseWrapper to the FilterChain#doFilter() call instead 
 * and finally you should be able to get the copied response after the the call.
 * 
 * @author Andrew Jackson <Andrew.Jackson@bl.uk>
 *
 */
//@WebFilter("/*")
public class ResponseLogger implements Filter {

    @Override
    public void init(FilterConfig config) throws ServletException {
        // NOOP.
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        if (response.getCharacterEncoding() == null) {
            response.setCharacterEncoding("UTF-8"); // Or whatever default. UTF-8 is good for World Domination.
        }

        HttpServletResponseCopier responseCopier = new HttpServletResponseCopier((HttpServletResponse) response);

        try {
            chain.doFilter(request, responseCopier);
            responseCopier.flushBuffer();
        } finally {
            byte[] copy = responseCopier.getCopy();
            System.out.println(new String(copy, response.getCharacterEncoding())); // Do your logging job here. This is just a basic example.
        }
    }

    @Override
    public void destroy() {
        // NOOP.
    }

}