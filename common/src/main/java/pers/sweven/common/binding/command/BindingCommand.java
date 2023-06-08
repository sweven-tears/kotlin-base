package pers.sweven.common.binding.command;

/**
 * Created by Sweven on 2021/10/29.
 * Email:sweventears@Foxmail.com
 */
public class BindingCommand<T> {
    private BindingAction action;
    private BindingConsumer<T> consumer;
    private BindingFunction<Boolean> canExecute;

    public BindingCommand(BindingAction action) {
        this.action = action;
    }

    public BindingCommand(int arg0, BindingConsumer<T> consumer) {
        this.consumer = consumer;
    }

    public BindingCommand(BindingAction action, BindingConsumer<T> consumer) {
        this.action = action;
        this.consumer = consumer;
    }

    public BindingCommand(BindingAction action, BindingFunction<Boolean> canExecute) {
        this.action = action;
        this.canExecute = canExecute;
    }

    public void execute() {
        if (action != null && canExecute()) {
            action.call();
        }
    }

    public void execute(T t) {
        if (consumer != null && canExecute()) {
            consumer.call(t);
        }
    }

    private boolean canExecute() {
        if (canExecute == null) {
            return true;
        }
        return canExecute.call();
    }
}
