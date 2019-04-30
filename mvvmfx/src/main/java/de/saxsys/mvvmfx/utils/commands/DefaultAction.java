package de.saxsys.mvvmfx.utils.commands;

class DefaultAction extends Action<Void> {

    private final Runnable runnable;

    DefaultAction(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    protected void action(Void parameter) {
        runnable.run();
    }

}
