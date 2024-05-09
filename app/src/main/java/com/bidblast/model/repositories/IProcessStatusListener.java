package com.bidblast.model.repositories;

public interface IProcessStatusListener<T> {
    void onSuccess(T data);
    void onError();
}
