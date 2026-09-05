package com.ruoyi.afl.subject;

import com.ruoyi.afl.domain.AflCommand;
import com.ruoyi.afl.observer.AflObserver;

import java.util.ArrayList;

public interface AflSubject {
    //发布者
    public ArrayList<AflObserver> observers = new ArrayList<AflObserver>();
    public void addObserver(AflObserver observer) ;
    public void removeObserver(AflObserver observer);
    public void notifyObserves(AflCommand cmd);
}
