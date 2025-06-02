package com.bigstark.example.plugin.log

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor

class ClickLogClassVisitor(
    apiVersion: Int,
    cv: ClassVisitor
) : ClassVisitor(apiVersion, cv) {

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        if (name == "onClick" && descriptor == "(Landroid/view/View;)V") {
            return ClickLogMethodVisitor(
                api = api,
                next = super.visitMethod(access, name, descriptor, signature, exceptions)
            )
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions)
    }


}