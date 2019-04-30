package de.saxsys.mvvmfx.utils.commands;

class DefaultParameterizedAction<T> extends Action<T> {

    private final ParameterizedRunnable<T> runnable;

    DefaultParameterizedAction(ParameterizedRunnable<T> runnable) {
        this.runnable = runnable;
    }

    @Override
    protected void action(T parameter) {
        runnable.run(parameter);
    }

}
