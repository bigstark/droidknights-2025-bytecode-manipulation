package com.bigstark.example.plugin.count

import org.objectweb.asm.Handle
import org.objectweb.asm.MethodVisitor

class ClickCountLambdaMethodVisitor(
    api: Int,
    next: MethodVisitor,
    private val callback: (lambdaName: String, lambdaDescriptor: String) -> Unit
) : MethodVisitor(api, next) {
    override fun visitInvokeDynamicInsn(
        name: String?,
        descriptor: String?,
        bootstrapMethodHandle: Handle?,
        vararg bootstrapMethodArguments: Any?
    ) {
        super.visitInvokeDynamicInsn(
            name,
            descriptor,
            bootstrapMethodHandle,
            *bootstrapMethodArguments
        )
        if (name == DYNAMIC_INVOKE_NAME && descriptor?.contains(DYNAMIC_INVOKE_DESC) == true) {
            bootstrapMethodArguments.forEach {
                if (it is Handle && it.desc.contains(BOOTSTRAP_ARGUMENT_HANDLE_DESC)) {
                    callback.invoke(it.name, it.desc)
                }
            }
        }
    }

    companion object {
        private const val DYNAMIC_INVOKE_NAME = "onClick"
        private const val DYNAMIC_INVOKE_DESC = "Landroid/view/View\$OnClickListener;"

        private const val BOOTSTRAP_ARGUMENT_HANDLE_DESC = "Landroid/view/View;"
    }
}