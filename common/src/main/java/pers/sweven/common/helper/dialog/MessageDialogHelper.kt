package com.shop.core.sdk.helper.dialog

/**
 * Created by Sweven on 2025/4/2--17:21.
 * Email: sweventears@163.com
 */
class MessageDialogHelper(private val factory: Factory) {
    fun setTitle(title: CharSequence): MessageDialogHelper {
        factory.title = title
        return this
    }

    fun setMessage(message: CharSequence): MessageDialogHelper {
        factory.message = message
        return this
    }

    fun setCanceledOnTouchOutside(canceledOnTouchOutside: Boolean): MessageDialogHelper {
        factory.canceledOnTouchOutside = canceledOnTouchOutside
        return this
    }

    fun setConfirm(
        confirmText: CharSequence,
        confirmListener: (factory: Factory) -> Unit = { factory.dismiss() },
    ): MessageDialogHelper {
        factory.confirmText = confirmText
        factory.confirmListener = confirmListener
        return this
    }

    fun setCancel(
        cancelText: CharSequence,
        cancelListener: (factory: Factory) -> Unit = { factory.dismiss() },
    ): MessageDialogHelper {
        factory.cancelText = cancelText
        factory.cancelListener = cancelListener
        return this
    }

    fun show() {
        factory.create().show()
    }

    fun dismiss() {
        factory.dismiss()
    }

    interface Factory {
        var title: CharSequence
        var message: CharSequence
        var cancelText: CharSequence?
        var confirmText: CharSequence?
        var canceledOnTouchOutside: Boolean

        var cancelListener: (Factory) -> Unit
        var confirmListener: (Factory) -> Unit

        fun create(): Factory
        fun show()
        fun dismiss()
    }
}