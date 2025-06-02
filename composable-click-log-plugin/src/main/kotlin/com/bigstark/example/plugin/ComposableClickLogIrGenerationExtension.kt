package com.bigstark.example.plugin

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclarationBase
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrConstImpl
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.ir.visitors.IrElementVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptVoid
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName

@OptIn(org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI::class)
class ComposableClickLogIrGenerationExtension(
    private val messageCollector: MessageCollector
) : IrGenerationExtension {

    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        moduleFragment.transformChildrenVoid(
            ComposableClickLogTransformer(pluginContext, messageCollector)
        )
    }
}

@OptIn(org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI::class)
private class ComposableClickLogTransformer(
    private val pluginContext: IrPluginContext,
    private val messageCollector: MessageCollector
) : IrElementTransformerVoid() {

    private var currentClass: String = ""
    private var clickableMethodFound = false

    override fun visitClass(declaration: IrClass): IrClass {
        if (isTargetActivity(declaration)) {
            currentClass = declaration.name.asString()
        }

        return super.visitClass(declaration) as IrClass
    }


    override fun visitCall(expression: IrCall): IrExpression {
        // ComposableClickCountActivity 내부에서만 감지
        if (currentClass == "ComposableClickLogActivity") {
            val functionName = expression.symbol.owner.name.asString()

            // clickable 함수 호출 감지
            if (functionName == "clickable") {
                clickableMethodFound = true
                messageCollector.report(
                    CompilerMessageSeverity.INFO,
                    "[composable] clickable method found - ${expression.symbol.owner.name}"
                )
            }
        }

        return super.visitCall(expression)
    }

    override fun visitFunction(declaration: IrFunction): IrStatement {
        if (clickableMethodFound.not()) {
            return super.visitFunction(declaration)
        }
        messageCollector.report(
            CompilerMessageSeverity.INFO,
            "[composable] transform clickable function block"
        )

        clickableMethodFound = false
        declaration.transformChildrenVoid(object : IrElementTransformerVoid() {
            // clickable 익명 함수 진입 시점
            override fun visitDeclaration(declaration: IrDeclarationBase): IrStatement {
                declaration.acceptVoid(object : IrElementVisitorVoid {

                    // clickable 익명 함수 진입 시점
                    override fun visitSimpleFunction(declaration: IrSimpleFunction) {
                        super.visitSimpleFunction(declaration)
                        generateLogFunction(pluginContext, declaration)
                    }
                })
                return super.visitDeclaration(declaration)
            }
        })

        return super.visitFunction(declaration)
    }

    private fun generateLogFunction(
        pluginContext: IrPluginContext,
        declaration: IrSimpleFunction
    ) {
        // Log 클래스 참조 가져오기
        val logClass = pluginContext.referenceClass(
            ClassId.topLevel(FqName("android.util.Log"))
        )

        // Log.v 메소드 심볼 가져오기 (String, String) -> Int
        val logVMethod = logClass?.owner?.declarations?.filterIsInstance<IrFunction>()
            ?.find { function ->
                function.name.asString() == "v" && function.valueParameters.size == 2
            }?.symbol as? IrSimpleFunctionSymbol
            ?: return

        // "TAG" 문자열 상수 생성
        val tagString = IrConstImpl.string(
            UNDEFINED_OFFSET,
            UNDEFINED_OFFSET,
            pluginContext.irBuiltIns.stringType,
            "TAG"
        )

        // "Hello World droid knights" 문자열 상수 생성
        val messageString = IrConstImpl.string(
            UNDEFINED_OFFSET,
            UNDEFINED_OFFSET,
            pluginContext.irBuiltIns.stringType,
            "Hello World droid knights 2025!"
        )

        // Log.v 호출 생성
        val logCall = IrCallImpl(
            UNDEFINED_OFFSET,
            UNDEFINED_OFFSET,
            pluginContext.irBuiltIns.intType,
            logVMethod,
            0, // 타입 인자 개수
            2   // 값 인자 개수
        ).apply {
            putValueArgument(0, tagString)
            putValueArgument(1, messageString)
        }

        // 기존 함수 본문을 가져와서 Log.v 호출을 첫 번째로 추가
        val originalBody = declaration.body
        if (originalBody is IrBlockBody) {
            // 기존 statements의 첫 번째에 Log.v 호출 삽입
            originalBody.statements.add(0, logCall)
            messageCollector.report(
                CompilerMessageSeverity.INFO,
                "[composable] generate log in clickable method"
            )
        }
    }

    private fun isTargetActivity(irClass: IrClass): Boolean {
        return irClass.name.asString() == "ComposableClickLogActivity"
    }
}