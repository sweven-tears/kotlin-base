package pers.sweven.common.binding.command;

/**
 * Created by Sweven on 2021/10/29.
 * Email:sweventears@Foxmail.com
 */
public interface BindingConsumer<T> {
    void call(T t);
}
