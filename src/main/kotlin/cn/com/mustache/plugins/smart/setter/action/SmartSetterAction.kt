package cn.com.mustache.plugins.smart.setter.action

import cn.com.mustache.plugins.smart.setter.entity.SetterInfo
import cn.com.mustache.plugins.smart.setter.util.NotificationUtil
import cn.com.mustache.plugins.smart.setter.util.hasBuilderAnnotation
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.PsiTypesUtil

class SmartSetterAction : AnAction() {
    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = e.getData(CommonDataKeys.EDITOR) != null
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getRequiredData(CommonDataKeys.EDITOR)
        val psiFile = e.getData(CommonDataKeys.PSI_FILE)
        val project = e.project
        if (psiFile == null || project == null) {
            thisLogger().error("System error Please contact the developer!")
            return
        }

        val offset = editor.caretModel.offset
        val result = getPsiClass(editor, psiFile, offset)
        if (result.psiClass == null) {
            NotificationUtil.notify(
                project,
                "Only classes can be generated. Select a normal class!",
                NotificationType.WARNING
            )
            return
        }
        val setFields: Map<String, Any?> = if (result.hasBuilder) {
            getFields(result.psiClass)
        } else {
            getSetMethods(result.psiClass)
        }
        generateSetter(editor, offset, result, setFields)
        NotificationUtil.notify(project, "", "Generate Success", NotificationType.INFORMATION)
    }

    private fun getPsiClass(editor: Editor, psiFile: PsiFile, offset: Int): SetterInfo {
        val elementAtCaret = psiFile.findElementAt(offset)
        val variable = PsiTreeUtil.getParentOfType(elementAtCaret, PsiLocalVariable::class.java)
        val type = variable?.type
        val psiClass = type?.let { PsiTypesUtil.getPsiClass(it) }
        val hasBuilder = psiClass.hasBuilderAnnotation()
        val spaceOffset = if (hasBuilder) (psiClass?.name?.length ?: 0) + 1 else 0

        return psiClass?.let { clazz ->
            editor.document.run {
                val lineNumber = getLineNumber(variable.textRange.startOffset)
                val lineStartOffset = getLineStartOffset(lineNumber)
                val textToVariable = getText(TextRange(lineStartOffset, variable.textRange.startOffset))
                val leadingSpaces = textToVariable.takeWhile { it.isWhitespace() }.length + spaceOffset
                SetterInfo(clazz, variable.name, leadingSpaces, hasBuilder)
            }
        } ?: SetterInfo(null, "", 0, false)
    }

    private fun getSetMethods(psiClass: PsiClass): Map<String, PsiMethod?> {
        val setMethods = psiClass.methods
            .filter {
                it.hasModifierProperty(PsiModifier.PUBLIC)
                        && !it.hasModifierProperty(PsiModifier.STATIC)
                        && it.name.startsWith("set")
            }
            .associateBy { it.name.replaceFirst("set", "") }
        return setMethods;
    }

    private fun getFields(psiClass: PsiClass): Map<String, PsiField?> {
        val setFields = psiClass.fields
            .filter {
                it.hasModifierProperty(PsiModifier.PRIVATE)
                        && !it.hasModifierProperty(PsiModifier.STATIC)
            }
            .associateBy { it.name }
        return setFields;
    }

    private fun generateSetter(editor: Editor, offset: Int, setterInfo: SetterInfo, setMap: Map<String, Any?>) {
        WriteCommandAction.runWriteCommandAction(editor.project) {
            val blankSpace = " ".repeat(setterInfo.leadingSpaces)
            var lineNumber = editor.document.getLineNumber(offset) + 1
            for ((key, value) in setMap) {
                val isMethod = value is PsiMethod
                val name = if (isMethod) (value as PsiMethod?)?.name else key
                val lineStartOffset = editor.document.getLineStartOffset(lineNumber++)
                editor.document.insertString(
                    lineStartOffset,
                    if (isMethod) "$blankSpace${setterInfo.className}.${name}();\n" else "$blankSpace.${name}()\n"
                )

                if (key == setMap.keys.lastOrNull() && value is PsiField) {
                    editor.document.insertString(
                        editor.document.getLineStartOffset(lineNumber),
                        "$blankSpace.build();"
                    )
                }
                editor.caretModel.moveToOffset(lineStartOffset + 2)
                editor.scrollingModel.scrollToCaret(ScrollType.MAKE_VISIBLE)
            }
        }
    }

}