package cn.com.mustache.plugins.smart.setter.entity

import com.intellij.psi.PsiClass

data class SetterInfo(val psiClass: PsiClass?, val className: String, val leadingSpaces: Int, val hasBuilder: Boolean)
