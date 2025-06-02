package com.bigstark.example.plugin.count

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Type

class ClickCountClassVisitor(
    apiVersion: Int,
    cv: ClassVisitor
) : ClassVisitor(apiVersion, cv) {

    private var countableMethods = mutableSetOf<CountableMethod>()

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        countableMethods.find { it.name == name && it.descriptor == descriptor }?.let {
            val methodType = Type.getMethodType(it.descriptor)
            val viewVarIndex = methodType.argumentTypes.indexOfFirst { type ->
                type.descriptor == "Landroid/view/View;"
            }.takeIf { index -> index >= 0 } ?: 0
            return IncreaseCountMethodVisitor(
                api = api,
                next = super.visitMethod(access, name, descriptor, signature, exceptions),
                viewVarIndex = viewVarIndex
            )
        }

        return ClickCountLambdaMethodVisitor(
            api = api,
            next = super.visitMethod(access, name, descriptor, signature, exceptions)
        ) { name, descriptor ->
            countableMethods.add(CountableMethod(name, descriptor))
        }
    }

    data class CountableMethod(
        val name: String,
        val descriptor: String
    )
}