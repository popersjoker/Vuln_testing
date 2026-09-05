package com.ruoyi.afl.observer;

import com.ruoyi.afl.domain.AflCommand;

public interface AflObserver {
    public void update(AflCommand cmd);
}
