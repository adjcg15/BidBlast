package com.bidblast.repositories;

public interface IEmptyProcessWithBusinessErrorListener<E> {
    void onSuccess();
    void onError(E errorCode);
}
