package pers.sweven.common.app;

/**
 * Created by Sweven on 2024/4/2--16:43.
 * Email: sweventears@163.com
 */
public interface PageInput<I> {
    I consume(PageManager manager);
}
