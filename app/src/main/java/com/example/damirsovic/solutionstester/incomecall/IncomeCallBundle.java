package com.example.damirsovic.solutionstester.incomecall;

public class IncomeCallBundle {
    private int callAction;
    private boolean triggerAction;

    public IncomeCallBundle() {
        this.triggerAction = false;
        this.callAction = 0;
    }

    public IncomeCallBundle(boolean triggerAction, int callAction) {
        this.triggerAction = triggerAction;
        this.callAction = callAction;
    }

    public boolean getTriggerAction() {
        return triggerAction;
    }

    public void setTriggerAction(boolean triggerAction) {
        this.triggerAction = triggerAction;
    }

    public int getCallAction() {
        return callAction;
    }

    public void setCallAction(int callAction) {
        this.callAction = callAction;
    }
}
