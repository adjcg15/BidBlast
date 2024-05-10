package com.bidblast.repositories;

public interface IEmptyProcessStatusListener {
    void onSuccess();
    void onError(ProcessErrorCodes errorCode);
}
