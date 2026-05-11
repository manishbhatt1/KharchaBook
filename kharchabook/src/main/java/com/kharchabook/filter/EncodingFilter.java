package com.kharchabook.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Filter that enforces UTF-8 character encoding for every request and response.
 *
 * This helps ensure that form data, request parameters, and generated response
 * content are interpreted correctly for Unicode characters.
 */
public class EncodingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
        // No initialization needed for this filter.
    }

    @Override
    public void destroy() {
        // No cleanup required when the filter is destroyed.
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // Force UTF-8 encoding on incoming requests and outgoing responses.
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        // Continue processing the request through the filter chain.
        chain.doFilter(request, response);
    }
}

