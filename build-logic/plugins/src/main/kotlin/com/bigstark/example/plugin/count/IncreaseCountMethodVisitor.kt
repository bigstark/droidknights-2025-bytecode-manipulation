package com.bigstark.example.plugin.count

import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class IncreaseCountMethodVisitor(
    api: Int,
    next: MethodVisitor,
    private val viewVarIndex: Int
) : MethodVisitor(api, next) {


    override fun visitCode() {
        super.visitCode()

        val tagNullLabel = Label()
        val tagCastFailLabel = Label()

        // View 객체 로드 (it)
        visitVarInsn(Opcodes.ALOAD, viewVarIndex)

        // 현재 태그 값 가져오기
        visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "android/view/View",
            "getTag",
            "()Ljava/lang/Object;",
            false
        )

        // null 체크
        visitInsn(Opcodes.DUP)
        visitJumpInsn(Opcodes.IFNULL, tagNullLabel)

        // tag 가 null이 아니면 Integer로 형변환 시도
        visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Integer")
        visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "java/lang/Integer",
            "intValue",
            "()I",
            false
        )
        visitJumpInsn(Opcodes.GOTO, tagCastFailLabel)

        // null인 경우 0 사용
        visitLabel(tagNullLabel)
        visitInsn(Opcodes.POP)  // null 제거
        visitInsn(Opcodes.ICONST_0)

        // 값에 1 추가
        visitLabel(tagCastFailLabel)
        visitInsn(Opcodes.ICONST_1)
        visitInsn(Opcodes.IADD)

        // 결과값 복제 (하나는 태그 설정용, 하나는 로깅용)
        visitInsn(Opcodes.DUP)

        // Integer로 박싱 (로깅용)
        visitMethodInsn(
            Opcodes.INVOKESTATIC,
            "java/lang/Integer",
            "valueOf",
            "(I)Ljava/lang/Integer;",
            false
        )

        // 임시 로컬 변수에 저장 (로깅용)
        visitVarInsn(Opcodes.ASTORE, 3)

        // View 객체 다시 로드
        visitVarInsn(Opcodes.ALOAD, viewVarIndex)

        // Integer로 박싱하여 스택 맨 위로 가져오기 (태그 설정용)
        visitInsn(Opcodes.SWAP)
        visitMethodInsn(
            Opcodes.INVOKESTATIC,
            "java/lang/Integer",
            "valueOf",
            "(I)Ljava/lang/Integer;",
            false
        )

        // 새 태그 값 설정
        visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "android/view/View",
            "setTag",
            "(Ljava/lang/Object;)V",
            false
        )

        // 2. 로그 출력 코드 추가

        // Log 태그 문자열 로드
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

        // "count: " 문자열 추가
        visitLdcInsn("count: ")
        visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "java/lang/StringBuilder",
            "append",
            "(Ljava/lang/String;)Ljava/lang/StringBuilder;",
            false
        )

        // 저장해둔 태그 값 로드
        visitVarInsn(Opcodes.ALOAD, 3)

        // StringBuilder에 태그 값 추가
        visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "java/lang/StringBuilder",
            "append",
            "(Ljava/lang/Object;)Ljava/lang/StringBuilder;",
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

}