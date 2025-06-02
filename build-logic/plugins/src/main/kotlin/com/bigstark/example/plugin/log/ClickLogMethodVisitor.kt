package com.bigstark.example.plugin.log

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class ClickLogMethodVisitor(
    api: Int,
    next: MethodVisitor
) : MethodVisitor(api, next) {

    private var loggableName = ""
    private var maxLocals = 0 // 메서드의 로컬 변수 개수 (나중에 설정)

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        if (descriptor == "Lcom/bigstark/example/log/Loggable;") {
            return ClickLogAnnotationVisitor(
                api = api,
                next = super.visitAnnotation(descriptor, visible)
            ) {
                loggableName = it
            }
        }
        return super.visitAnnotation(descriptor, visible)
    }

    override fun visitMaxs(maxStack: Int, maxLocals: Int) {
        this.maxLocals = maxLocals
        super.visitMaxs(maxStack + 4, maxLocals + 2) // 로그 코드에 필요한 추가 스택 공간
    }

    override fun visitInsn(opcode: Int) {
        if (loggableName.isEmpty()) {
            super.visitInsn(opcode)
            return
        }

        when (opcode) {
            Opcodes.IRETURN,
            Opcodes.LRETURN,
            Opcodes.FRETURN,
            Opcodes.DRETURN,
            Opcodes.ARETURN,
            Opcodes.RETURN -> {
                // 리턴 값을 임시 변수에 저장 (리턴 타입에 따라 다름)
                val tmpVarIndex = maxLocals // 새로운 로컬 변수 인덱스 사용
                when (opcode) {
                    Opcodes.IRETURN -> {
                        visitVarInsn(Opcodes.ISTORE, tmpVarIndex)
                        insertLog()
                        visitVarInsn(Opcodes.ILOAD, tmpVarIndex)
                    }

                    Opcodes.LRETURN -> {
                        visitVarInsn(Opcodes.LSTORE, tmpVarIndex)
                        insertLog()
                        visitVarInsn(Opcodes.LLOAD, tmpVarIndex)
                    }

                    Opcodes.FRETURN -> {
                        visitVarInsn(Opcodes.FSTORE, tmpVarIndex)
                        insertLog()
                        visitVarInsn(Opcodes.FLOAD, tmpVarIndex)
                    }

                    Opcodes.DRETURN -> {
                        visitVarInsn(Opcodes.DSTORE, tmpVarIndex)
                        insertLog()
                        visitVarInsn(Opcodes.DLOAD, tmpVarIndex)
                    }

                    Opcodes.ARETURN -> {
                        visitVarInsn(Opcodes.ASTORE, tmpVarIndex)
                        insertLog()
                        visitVarInsn(Opcodes.ALOAD, tmpVarIndex)
                    }

                    else -> { // RETURN (void)
                        insertLog()
                    }
                }
            }
        }

        super.visitInsn(opcode)
    }

    private fun insertLog() {
        // 기존 로그 코드
        visitLdcInsn("TAG")

        // StringBuilder 생성
        visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder")
        visitInsn(Opcodes.DUP)
        visitMethodInsn(
            Opcodes.INVOKESPECIAL,
            "java/lang/StringBuilder",
            "<init>",
            "()V",
            false
        )

        // 로그 메시지 추가
        visitLdcInsn("log name: $loggableName")
        visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "java/lang/StringBuilder",
            "append",
            "(Ljava/lang/String;)Ljava/lang/StringBuilder;",
            false
        )

        // StringBuilder를 문자열로 변환
        visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "java/lang/StringBuilder",
            "toString",
            "()Ljava/lang/String;",
            false
        )

        // Log.v 호출
        visitMethodInsn(
            Opcodes.INVOKESTATIC,
            "android/util/Log",
            "v",
            "(Ljava/lang/String;Ljava/lang/String;)I",
            false
        )

        // Log.v의 반환값(int) 제거
        visitInsn(Opcodes.POP)
    }

    class ClickLogAnnotationVisitor(
        api: Int,
        next: AnnotationVisitor,
        private val onLoggableName: (String) -> Unit
    ) : AnnotationVisitor(api, next) {
        override fun visit(name: String?, value: Any?) {
            super.visit(name, value)
            if (name == "name") {
                onLoggableName.invoke(value as? String ?: return)
            }
        }
    }
}
