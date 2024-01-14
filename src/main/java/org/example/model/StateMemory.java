package org.example.model;

public class StateMemory {
    private boolean confirm;
    private boolean cancel;

    public StateMemory() {
    }

    public StateMemory(boolean confirm, boolean cancel) {
        this.confirm = confirm;
        this.cancel = cancel;
    }

    public boolean isConfirm() {
        return confirm;
    }

    public void setConfirm(boolean confirm) {
        this.confirm = confirm;
    }

    public boolean isCancel() {
        return cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }
}
