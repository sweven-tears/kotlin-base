package pers.sweven.common.app

/**
 * [PageFactory] 的默认单例实现，可直接通过 [PageFactory.getInstance] 获取，
 * 无需自行继承。已内置路由表与全局拦截器管理，适合大多数业务直接复用。
 *
 * 使用示例：
 * ```kotlin
 * PageFactory.getInstance().addRoute("/user/profile", UserProfileActivity::class.java)
 * PageFactory.getInstance()
 *     .build("/user/profile")
 *     .withString("userId", "12345")
 *     .navigation(context)
 * ```
 */
object DefaultPageFactory : PageFactory<DefaultPageFactory.DefaultNav>() {

    override fun createNavigationBuilder(path: String): DefaultNav = DefaultNav(this, path)

    /**
     * 默认导航构建器实现，仅作为 [DefaultPageFactory] 的建造者载体。
     */
    class DefaultNav(
        factory: PageFactory<*>,
        path: String,
    ) : NavigationBuilder<DefaultNav>(factory, path)
}
