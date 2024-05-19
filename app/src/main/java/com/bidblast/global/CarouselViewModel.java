package com.bidblast.global;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bidblast.model.HypermediaFile;

import java.util.ArrayList;
import java.util.List;

public class CarouselViewModel {
    private final MutableLiveData<List<HypermediaFile>> filesList =
        new MutableLiveData<>(new ArrayList<>());

    private final MutableLiveData<HypermediaFile> selectedFile = new MutableLiveData<>(null);

    public LiveData<List<HypermediaFile>> getFilesList() { return filesList; }

    public LiveData<HypermediaFile> getSelectedFile() { return selectedFile; }

    public void setFilesList(List<HypermediaFile> files) {
        if(files != null && !files.isEmpty()) {
            filesList.setValue(files);
            selectedFile.setValue(files.get(0));
        }
    }

    public void setSelectedFile(HypermediaFile file) {
        selectedFile.setValue(file);
    }
}
