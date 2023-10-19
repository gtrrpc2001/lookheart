package com.mcuhq.simplebluetooth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

public class SharedViewModel extends ViewModel {

    // Arr 갱신
    private final MutableLiveData<ArrayList<String>> arrList = new MutableLiveData<>(new ArrayList<>());

    private final MutableLiveData<String> age = new MutableLiveData<>();
    private final MutableLiveData<String> gender = new MutableLiveData<>();
    private final MutableLiveData<String> height = new MutableLiveData<>();
    private final MutableLiveData<String> weight = new MutableLiveData<>();
    private final MutableLiveData<String> sleep = new MutableLiveData<>();
    private final MutableLiveData<String> wakeup = new MutableLiveData<>();



    private final MutableLiveData<String> bpm = new MutableLiveData<>();
    private final MutableLiveData<String> tCal = new MutableLiveData<>();
    private final MutableLiveData<String> eCal = new MutableLiveData<>();
    private final MutableLiveData<String> step = new MutableLiveData<>();
    private final MutableLiveData<String> distance = new MutableLiveData<>();



    private final MutableLiveData<Boolean> emergency = new MutableLiveData<>();
    private final MutableLiveData<Boolean> arr = new MutableLiveData<>();
    private final MutableLiveData<Boolean> myo = new MutableLiveData<>();
    private final MutableLiveData<Boolean> nonContact = new MutableLiveData<>();
    private final MutableLiveData<Boolean> fastarr = new MutableLiveData<>();
    private final MutableLiveData<Boolean> slowarr = new MutableLiveData<>();
    private final MutableLiveData<Boolean> irregular = new MutableLiveData<>();


    private final MutableLiveData<Boolean> summaryRefreshCheck = new MutableLiveData<>();
    private final MutableLiveData<Boolean> arrRefreshCheck = new MutableLiveData<>();

    public void setSummaryRefreshCheck(Boolean check) {
        summaryRefreshCheck.setValue(check);
    }
    public void setArrRefreshCheck(Boolean check) {
        arrRefreshCheck.setValue(check);
    }

    public LiveData<Boolean> getSummaryRefreshCheck() {
        return summaryRefreshCheck;
    }

    public LiveData<Boolean> getArrRefreshCheck() {
        return arrRefreshCheck;
    }

    // set
    public void setAge(String newText) {
        age.setValue(newText);
    }
    public void setGender(String newText) {
        gender.setValue(newText);
    }
    public void setHeight(String newText) {
        height.setValue(newText);
    }
    public void setWeight(String newText) {
        weight.setValue(newText);
    }
    public void setSleep(String newText) {
        sleep.setValue(newText);
    }
    public void setWakeup(String newText) {
        wakeup.setValue(newText);
    }


    public void setBpm(String newText) {
        bpm.setValue(newText);
    }
    public void setTCalText(String newText) {
        tCal.setValue(newText);
    }
    public void setECalText(String newText) {
        eCal.setValue(newText);
    }

    public void setStep(String newText) {
        step.setValue(newText);
    }

    public void setDistance(String newText) {
        distance.setValue(newText);
    }


    public void setEmergency(Boolean check) {
        emergency.setValue(check);
    }
    public void setArr(Boolean check) { arr.setValue(check);}
    public void setMyo(Boolean check) {
        myo.setValue(check);
    }
    public void setNonContact(Boolean check) {
        nonContact.setValue(check);
    }
    public void setFastarr(Boolean check) { fastarr.setValue(check);}
    public void setSlowarr(Boolean check) {
        slowarr.setValue(check);
    }
    public void setIrregular(Boolean check) {
        irregular.setValue(check);
    }


    public void addArrList(String arrDate) {
        ArrayList<String> currentList = arrList.getValue();
        if (currentList != null) {
            currentList.add(arrDate);
            arrList.postValue(currentList);
        }
    }

    public void removeArrList(int index) {
        ArrayList<String> currentList = arrList.getValue();
        if (currentList != null && index >= 0 && index < currentList.size()) {
            currentList.remove(index);
            arrList.setValue(currentList); // LiveData 업데이트
        }
    }

    public void removeAllArrList() {
        ArrayList<String> currentList = arrList.getValue();
        if (currentList != null) {
            currentList.clear(); // 모든 요소 지우기
            arrList.setValue(currentList); // LiveData 업데이트
        }
    }

    // get
    public LiveData<String> getAge() {
        return age;
    }
    public LiveData<String> getGender() {
        return gender;
    }
    public LiveData<String> getHeight() {
        return height;
    }
    public LiveData<String> getWeight() {
        return weight;
    }
    public LiveData<String> getSleep() {
        return sleep;
    }
    public LiveData<String> getWakeup() { return wakeup; }

    public LiveData<String> getBpm() {
        return bpm;
    }
    public LiveData<String> getTCalText() {
        return tCal;
    }

    public LiveData<String> getECalText() {
        return eCal;
    }


    public LiveData<String> getStepText() {
        return step;
    }
    public LiveData<String> getDistanceText() {
        return distance;
    }


    public LiveData<Boolean> getEmergency() {
        return emergency;
    }
    public LiveData<Boolean> getArr() {
        return arr;
    }
    public LiveData<Boolean> getMyo() {
        return myo;
    }
    public LiveData<Boolean> getNonContact() {
        return nonContact;
    }
    public LiveData<Boolean> getFastArr() {
        return fastarr;
    }
    public LiveData<Boolean> getSlowarr() {
        return slowarr;
    }
    public LiveData<Boolean> getIrregular() {
        return irregular;
    }

    public MutableLiveData<ArrayList<String>> getArrList() {
        return arrList;
    }

}