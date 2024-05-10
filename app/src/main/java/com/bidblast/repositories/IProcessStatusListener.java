package com.bidblast.repositories;

public interface IProcessStatusListener<T> {
    void onSuccess(T data);
    void onError();
}
