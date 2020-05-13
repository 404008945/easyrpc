package com.lxs.easyrpc.proxy;

public interface Result {
    /**
     * Get invoke result.
     *
     * @return result. if no result return null.
     */
    Object getResult();

    void setResult(Object o);

    /**
     * Get exception.
     *
     * @return exception. if no exception return null.
     */
    Throwable getException();

    /**
     * Has exception.
     *
     * @return has exception.
     */
    boolean hasException();

    void setException(Exception e);

}
