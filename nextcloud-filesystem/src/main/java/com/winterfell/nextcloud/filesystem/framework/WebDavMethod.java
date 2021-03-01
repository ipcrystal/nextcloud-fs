package com.winterfell.nextcloud.filesystem.framework;

/**
 * @author zhuzhenjie
 */
public interface WebDavMethod {

    /**
     * GET
     */
    String GET = "GET";

    /**
     * POST
     */
    String POST = "POST";

    /**
     * PUT
     */
    String PUT = "PUT";

    /**
     * DELETE
     */
    String DELETE = "DELETE";

    /**
     * PROPFIND
     */
    String PROPFIND = "PROPFIND";

    /**
     * MKCOL
     */
    String MKCOL = "MKCOL";

    /**
     * SEARCH
     */
    String SEARCH = "SEARCH";

    /**
     * COPY
     */
    String MOVE = "MOVE";

}
